package io.basquiat.blockchain.transaction.util;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bouncycastle.crypto.RuntimeCryptoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.basquiat.blockchain.transaction.domain.Transaction;
import io.basquiat.blockchain.transaction.domain.TransactionIn;
import io.basquiat.blockchain.transaction.domain.TransactionOut;
import io.basquiat.blockchain.transaction.domain.TransactionOutMap;
import io.basquiat.blockchain.transaction.domain.UnspentTransactionOut;
import io.basquiat.blockchain.transaction.validator.TransactionValidator;
import io.basquiat.crypto.ECDSAUtil;
import io.basquiat.util.Base58;
import io.basquiat.util.Sha256Util;

/**
 * TransactionUtil
 * created by basquiat
 *
 */
@Component
public class TransactionUtil {
	
	private static final Logger LOG = LoggerFactory.getLogger(TransactionUtil.class);
	
	static String GENESIS_ADDRESS;
	
	static BigDecimal COINBASE_AMOUNT;
	
	@Value("${genesis.address}")
	private void setGenesisAddress(String genesisAddress) {
		GENESIS_ADDRESS = genesisAddress;
    }
	
	@Value("${coinbase.amount}")
	private void setCoinbaseAmount(BigDecimal coinbaseAmount) {
		COINBASE_AMOUNT = coinbaseAmount;
    }
	
	/**
	 * create transaction hash
	 * <pre>
	 * TransactionOut과 TransactionIn의 특정 값을 통해서 Sha256을 통해서 hash값을 생성한다.
	 * </pre>
	 * @See reference -> https://en.bitcoin.it/wiki/Protocol_documentation#tx
	 * @param transaction
	 * @return String
	 */
	public static String createTransactionHash(Transaction transaction) {
		// txInt --> forEach (txOutHash + txOutIndex)
		String txInValueConcat = transaction.getTxIns()
				.stream()
				.map(txIn -> txIn.getTxOutHash() + txIn.getTxOutIndex())
				.reduce((previous, next) -> previous+next).orElse("");

		// txOut --> forEach (address + coin amout)
		String txOutValueConcat = transaction.getTxOuts()
								  .stream()
								  .map(txOut -> txOut.getAddress() + txOut.getAmount().toString())
								  .reduce((previous, next) -> previous+next).orElse("");
		return Sha256Util.SHA256(txInValueConcat + txOutValueConcat);
	}
	
	/**
	 * 최초로 노드를 띄우고 특정 주소로 50을 보내는 txOut를 생성한다.
	 * @return Transaction
	 */
	public static Transaction genesisTransaction() {
		// genesisTxIns
		List<TransactionIn> txIns = Stream.of(TransactionIn.builder().signature("").txOutHash("").txOutIndex(0).build()).collect(Collectors.toList());
		// genesisTxOut
		List<TransactionOut> txOuts = Stream.of(TransactionOut.builder().address(GENESIS_ADDRESS).amount(COINBASE_AMOUNT).build()).collect(Collectors.toList());
		
		// Transaction
		Transaction transaction = Transaction.builder().txIns(txIns).txOuts(txOuts).build();
		
		// transaction hash create
		String transactionHash = TransactionUtil.createTransactionHash(transaction);
		transaction.setTxHash(transactionHash);
		return transaction;
	}
	
	/**
	 * create Signature
	 * private key는 base58 encoding된 값이다.
	 * @param transaction
	 * @param txInIndex
	 * @param privateKey
	 * @param utxOutList
	 * @return String
	 * @throws InvalidKeySpecException 
	 * @throws NoSuchAlgorithmException 
	 * @throws Exception 
	 */
	public static String createSignature(Transaction transaction, Integer txInIndex, String privateKey, List<UnspentTransactionOut> uTxOs) {
		// 1. transaction에서 txInIndex에 해당하는 TransactionIn 객체를 추출한다.
		TransactionIn txIn = transaction.getTxIns().get(txInIndex);
		// 2. sigantrue에 사용될 transaction hash값을 추출한다.
		String signatureData = transaction.getTxHash();
		// 3. 트랜잭션에 unspectTransactionOut를 찾는다.
		UnspentTransactionOut uTxOut = TransactionUtil.findUTxO(txIn.getTxOutHash(), txIn.getTxOutIndex(), uTxOs)  ;
	    if(uTxOut == null) {
	        throw new RuntimeCryptoException("UnspentTransactionOut is null !!");
	    }
		// 4. uTxOut에서 주소를 추출한다.
		String uTxOutAddress = uTxOut.getAddress();

		// 5. privateKey로부터 Publickey 객체를 가져온다.
		String publicKey = null;
		try {
			PublicKey PUBLICKEY = ECDSAUtil.getPublicKeyFromPrivteKey(privateKey);
			// 6. PUBLICKEY로부터 publicKey를 가져온다
			// 이때 publicKey는 base58로 인코딩해야한다.
			publicKey = Base58.encode(PUBLICKEY.getEncoded());
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchProviderException e) {
			e.printStackTrace();
		}
		
		// 7. uTxOutAddress와 privateKey로부터 추출한 publicKey는 동일해야한다.
		// 동일하지 않으면 invalid
		if(!uTxOutAddress.equals(publicKey)) {
			LOG.info("Invalid privateKey");
			throw new RuntimeCryptoException("key doesn't match!!");
		}
		
		PrivateKey PRIVATKEY = null;
		try {
			PRIVATKEY = ECDSAUtil.getPrivateKeyFromBytes(Base58.decode(privateKey));
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
		}
		
		String signature = null;
		try {
			signature = ECDSAUtil.sign(PRIVATKEY, signatureData);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException | SignatureException
				| UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return signature;
		
	}

	/**
	 * List<UnspentTransactionOut>로부터 특정 조건에 맞는 UnspentTransactionOut을 추출한다.
	 * @param transactionHash
	 * @param index
	 * @param uTxOList
	 * @return UnspentTransactionOut
	 */
	public static UnspentTransactionOut findUTxO(String transactionHash, Integer index, List<UnspentTransactionOut> uTxOs) {
		return uTxOs.stream()
					.filter(uTxO -> uTxO.getTxOutHash().equals(transactionHash) && uTxO.getTxOutIndex() == index)
	    		  	.findAny()
	    		  	.orElse(null);
	}
	
	/**
	 * TransactionOut To UnspentTransactionOut
	 * @param transactionOut
	 * @param transactionHash
	 * @param index
	 * @return UnspentTransactionOut
	 */
	public static UnspentTransactionOut transactionOutToUnspentTransactionOut(TransactionOut transactionOut, 
																			  String transactionHash, 
																			  Integer index) {
			return UnspentTransactionOut.builder()
			  							.txOutHash(transactionHash)
			  							.txOutIndex(index)
			  							.address(transactionOut.getAddress())
			  							.amount(transactionOut.getAmount())
			  							.build();
	}

	/**
	 * TransactionIn To UnspentTransactionOut
	 * @param transactionIn
	 * @return UnspentTransactionOut
	 */
	public static UnspentTransactionOut transactionInToUnspentTransactionOut(TransactionIn transactionIn) {
			return UnspentTransactionOut.builder()
			  							.txOutHash(transactionIn.getTxOutHash())
			  							.txOutIndex(transactionIn.getTxOutIndex())
			  							.address("")
			  							.amount(BigDecimal.valueOf(0))
			  							.build();
	}
	
	/**
	 * 새로 생성된 transactions의 transactionOut으로부터 새로운 utxo 리스트를 생성한다.
	 * @param transactions
	 * @return List<UnspentTransactionOut>
	 */
	public static List<UnspentTransactionOut> createNewUTxOs(List<Transaction> transactions) {
		AtomicInteger index = new AtomicInteger();
		// 1. transactions에서 txoutlist를 조회한다.
		// 2. txoutlist의 txOut정보로 UnspentTransactionOut 리스트를 생성한다.
		// 3. reduce를 통해 생성된 리스트를 concat해서 전체 utxo 리스트를 생성한다.
		return transactions.stream()
						   .map(
								 tx ->   {
									 		return tx.getTxOuts().stream()
									  					.map(txOut ->  
									  							  TransactionUtil.transactionOutToUnspentTransactionOut(txOut, 
									  																					tx.getTxHash(), 
									  																					index.getAndIncrement())
									  										 
									  				     ).collect(Collectors.toList());
								 		 }
						   )
						   .reduce( (previous, next) -> Stream.concat(previous.stream(), next.stream())
								   							  .collect( Collectors.toList()) )
						   .get();
	}
	
	/**
	 * 새로 생성된 transactions의 transactionIn에서 block생성시에 사용된 utxo를 조회한다.
	 * @param transactions
	 * @return List<UnspentTransactionOut>
	 */
	public static List<UnspentTransactionOut> consumeUTxOs(List<Transaction> transactions) {
		// 1. transactions로부터 transactionin list를 찾는다.
		// 2. transactionin list에서 transaction in정보를 통해 consume된 uxto를 찾는다. 이때 주소는 '', amount는 0으로 세팅한다.
		// 3. reduce를 통해 생성된 리스트를 concat해서 전체 consumed된 utxo리스트를 생성한다.
		return transactions.stream()
			   .map(
					 tx ->   {
						 		return tx.getTxIns().stream()
						  					.map(txIn ->  TransactionUtil.transactionInToUnspentTransactionOut(txIn))
						  					.collect(Collectors.toList());
					 		}
			   )
			   .reduce( (previous, next) -> Stream.concat(previous.stream(), next.stream())
					   							  .collect( Collectors.toList()) )
			   .get();
	}
	
	/**
	 * consumeUTxOList가 제거된 uTxO 리스트를 반환한다.
	 * @param totalUTxOs
	 * @param consumeUTxOList
	 * @return List<UnspentTransactionOut>
	 */
	public static List<UnspentTransactionOut> consumeAndResultUtxOs(List<UnspentTransactionOut> totalUTxOs, 
																	   List<UnspentTransactionOut> consumeUTxOs) {
		// 전체 uTxOs에서 consumeUTxOList를 제거한 리스트를 반환한다.
		return totalUTxOs.stream()
					     .filter(uTxO -> TransactionUtil.findUTxO(uTxO.getTxOutHash(), 
					    	  								      uTxO.getTxOutIndex(), 
					    	  								      consumeUTxOs) == null		
					     ).collect(Collectors.toList());  
		
	}
	
	/**
	 * 전체적인 uTxOs의 정보를 업데이트한다.
	 * @param transactions
	 * @param totalUTxOs
	 * @return List<UnspentTransactionOut>
	 */
	public static List<UnspentTransactionOut> updateUTxOList(List<Transaction> transactions, List<UnspentTransactionOut> totalUTxOs) {
		// 1. transactions로부터 createNewUTxOList를 얻는다.
		List<UnspentTransactionOut> newUTxOs = TransactionUtil.createNewUTxOs(transactions);
		
		// 2. consumeUTxOList를 얻는다.
		List<UnspentTransactionOut> consumeUTxOs = TransactionUtil.consumeUTxOs(transactions);
		
		// 3. consumeUTxO를 제거한 uTxO list를 생성한다.
		List<UnspentTransactionOut> consumeAndResultUTxOs = TransactionUtil.consumeAndResultUtxOs(totalUTxOs, consumeUTxOs);
		
		// consumeAndResultUTxOs와 newUTxOs를 concat해서 반환한다.
		return Stream.concat(consumeAndResultUTxOs.stream(), newUTxOs.stream()).collect(Collectors.toList());
	}

	/**
	 * 1. a로부터 b로 코인을 10을 보낸다고 한다면 
	 * uxto에 의하면 사용되지 않은 utxo리스트에서 사용되지 않은 a의 utxo에 기록된 amount의 수량이 10이 될때까지 찾는다.
	 * 만일 그 수량이 11이 되었다고 하면 10은 b에게 보내고 1은 나에게 다시 보낸다.
	 * 10이라면 b에게 10을 보내면 되고 11.4 또는 12라면 그 나머지 금액인 1.4 또는 2를 나에게 다시 보낸다.
	 * @See uxto에 대한 글 -> https://steemit.com/coinkorea/@goldenman/utxo
	 * @param sendAmount
	 * @param selfUTxOs
	 * @return TransactionOutMap
	 */
	public static TransactionOutMap findAmoutFromUTxOs(BigDecimal sendAmount, List<UnspentTransactionOut> selfUTxOs) {
		// 1. uTxOs로부터 sendAmount 수량과 같거나 클 떄가지 uTxO 리스트를 생성한다.
		// 2. 나에게 다시 보낼 sendAmont와 uTxO list의 수량의 차이를 구한다.
		BigDecimal amount = BigDecimal.valueOf(0);
		BigDecimal payBack = BigDecimal.valueOf(0);
		// amount의 조건에 맞는 utxo list를 따로 생성한다.
		List<UnspentTransactionOut> specificUTxOs = new ArrayList<>();
		// for문을 돌면서 amount가 sendAmount와 같거나 클때까지 돈다.
		for(UnspentTransactionOut uTxO: selfUTxOs) {
			// 일단 집어넣는다.
			specificUTxOs.add(uTxO);
			// amount 총합을 계산한다.
			amount = amount.add(uTxO.getAmount());
			// amount가 sendAmount보다 크거나 같다면...
			if(amount.compareTo(sendAmount) == 1 || amount.compareTo(sendAmount) == 0) {
				payBack = amount.subtract(sendAmount);
				break;
			}
		}
		return TransactionOutMap.builder()
								.selfUnspentTransactionOuts(specificUTxOs)
								.payBack(payBack)
								.build();
	}
	
	/**
	 * transaction을 생성할 때 findAmoutFromUTxOs을 통해 TransactionOutMap을 구하게 되면
	 * TransactionOutMap의 mySelfUnspentTransactionOut를 조회하면서 transactionIn을 생성한다.
	 * 이유는 uTxO만큼 transactionIn을 생성해야하기 때문이다.
	 * @param uTxO
	 * @return TransactionIn
	 */
	public static TransactionIn createUnsingedTransationIn(UnspentTransactionOut uTxO) {
		return TransactionIn.builder()
							.txOutHash(uTxO.getTxOutHash())
							.txOutIndex(uTxO.getTxOutIndex())
							.build();
	}
	
	/**
	 * Transaction에 포함될 unsign된 transacinIn list를 생성한다.
	 * 즉 findAmoutFromUTxOs에서 amount를 보내기위해 총 합을 했던 uTxO에 TransactionIn List를 생성하게 된다.
	 * @param selfUTxOs
	 * @return List<TransactionIn>
	 */
	public static List<TransactionIn> unsingedTransationInList(List<UnspentTransactionOut> selfUTxOs) {
		return selfUTxOs.stream()
					   	.map(mapper -> TransactionUtil.createUnsingedTransationIn(mapper))
						.collect(Collectors.toList());
	}
	
	/**
	 * Transaction에 포함될 transactionOut list를 생성한다.
	 * 이 때 두개의 transactionOut를 생성하게 되는데 하나는 받는 주소로 수량을 보내는 transactionOut이고
	 * utxo에 의해 나머지는 자신에게 payback을 해야하나 자신에게 보내는 transactionOut을 생성한다.
	 * @param receivedAddress
	 * @param address
	 * @param amount
	 * @param payBack
	 * @return List<TransactionOut>
	 */
	public static List<TransactionOut> createTransactionOutList(String receivedAddress, 
																String address, 
																BigDecimal amount, 
																BigDecimal payBack) {
		// receivedAddress에 대한 transactionout생성
		List<TransactionOut> transactionOutList = Stream.of(TransactionOut.builder()
																		  .address(receivedAddress)
																		  .amount(amount)
																		  .build())
														 .collect(Collectors.toList());
		// payBack이 0이면 딱 맞기 때문에 payback에 대한 transactionOut를 생성할 필요가 없다.
		// 하지만 0보다 크다면 payback에 대한 transactionOut를 포함해야 한다.
		if(payBack.compareTo(BigDecimal.ZERO) == 1) {
			transactionOutList.add(TransactionOut.builder()
												 .address(address)
												 .amount(payBack)
												 .build());
		}
		return transactionOutList;
	}

	/**
	 * transactionPool과 selfAllUTxOs와 조회해서 소비되지 않은 selfUTxO 리스트를 최종반환한다.
	 * @param selfAllUTxOs
	 * @param transactionPool
	 * @return List<UnspentTransactionOut>
	 */
	public static List<UnspentTransactionOut> filterTransactionPool(List<UnspentTransactionOut> selfAllUTxOs, 
																	List<Transaction> transactionPool) {
		
		// 1. transactionPool에서 trasactionIns를 가져온다.
		List<TransactionIn> txIns = transactionPool.stream()
												   .map(tx -> tx.getTxIns())
												   .reduce((previous, next) -> Stream.concat(previous.stream(), next.stream())
								   							  						 .collect( Collectors.toList()) )
												   .orElse(null);
		if(txIns == null) {
			return selfAllUTxOs;
		}
		
		// 2. transaction pool의 txIns와 uTxO의 특정 조건 값이 맞는 uTxO가 있다면
		// consume된 uTxOs이므로 selfAllUTxOs에서 이 uTxO list를 제거해야 한다.
		List<UnspentTransactionOut> willRemoveUTxOs = new ArrayList<>();
		for(UnspentTransactionOut selfUTxO : selfAllUTxOs) {
			TransactionIn transactionIn = txIns.stream()
											   .filter(txIn -> txIn.getTxOutHash().equals(selfUTxO.getTxOutHash()) && 
													   		   txIn.getTxOutIndex() == selfUTxO.getTxOutIndex())
											   .findAny()
											   .orElse(null);
			if(transactionIn != null) {
				willRemoveUTxOs.add(selfUTxO);
			}
		}
		
		// 2. remove willRemoveUTxOs
		selfAllUTxOs.removeIf(uTxO -> willRemoveUTxOs.contains(uTxO));
		return selfAllUTxOs;
	}
	
	/**
	 * transaction for coinbase
	 * @param address
	 * @param blockIndex
	 * @return Transaction
	 */
	public static Transaction createCoinbaseTransaction(String address, Integer blockIndex) {
		// 1. mining시에는 보상으로 address로 coinbase amount만큼을 out으로 설정하고
		TransactionIn txIn = TransactionIn.builder() 
										  .txOutIndex(blockIndex)
										  .txOutHash("")
										  .signature("")
										  .build();
		
		// 2. in에 대해서는 없기 때문에 다음과 같이 형식으로 txIn과 txOut을 생성해서 반환한다.
		TransactionOut txOut = TransactionOut.builder()
											 .address(address)
											 .amount(COINBASE_AMOUNT)
											 .build();
		
		Transaction coinbaseTransaction = Transaction.builder().txIns(Stream.of(txIn).collect(Collectors.toList()))
															   .txOuts(Stream.of(txOut).collect(Collectors.toList()))
															   .build();
		
		// 3. transactionHash 생성
		String coinbaseTransactionHash = TransactionUtil.createTransactionHash(coinbaseTransaction);
		coinbaseTransaction.setTxHash(coinbaseTransactionHash);
		return coinbaseTransaction;
	}
	
	/**
	 * 전체적인 transaction을 생성한다.
	 * 1. privateKey로부터 publicKey를 생성한다. -> my address
	 * 2. uTxO에서 address에 대한 uTxO리스트를 생성한다.
	 * 3. selfUTxOs, 즉 address에 대한 uTxO리스트에서 transaction pool에 있는 transaction의 모든 transactionIn을 조회한다.
	 *    이 때  transacionIn 리스트에서 selfUTxOs의 uTxO의 txOutHash, txOutIndex가 같은 transactinIn이 있는지 조회한다.
	 *    만일 있다면 이 uTxO는 selfUTxOs에서 지운다.
	 *    이 후의 남은 selfUTxOs의 모든 amount의 합이 나의 발란스가 된다.
	 * 4. 보낼 amount와 나의 uTxO리스트로부터 보낼 amount을 포함하는 uTxO리스트와 payBack정보를 담은  TransactionOutMap을 생성한다.
	 * 5. signature를 생성하지 않은 transactionIn List를 생성한다.
	 * 6. 특정 주소로 amount를 보내는 transactionOut과 payback할 transactionOut를 정보를 담은 list를 생성한다.
	 * 7. 이 두개의 txIns와 txOuts를 통해 transactionHash를 생성한다.
	 * 8. new transaction의 txIns에서 각 txIn에 signature를 생성한다.
	 * 9. signed TxIns를 new transaction에 포함시켜 최종 반환
	 * @param receivedAddress
	 * @param amount
	 * @param privatekey
	 * @param uTxOs
	 * @param transactionPool
	 * @return Transaction
	 */
	public static Transaction createTransaction(String receivedAddress, 
												BigDecimal amount, 
												String privateKey, 
												List<UnspentTransactionOut> uTxOs, 
												List<Transaction> transactionPool) {
		// 1. publicKey 생성
		byte[] publicKeyBtye = null;
		try {
			PublicKey publicKey = ECDSAUtil.getPublicKeyFromPrivteKey(privateKey);
			publicKeyBtye = publicKey.getEncoded();
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchProviderException e) {
			e.printStackTrace();
		}
		// 2. address에 해당하는 uTxO의 리스트를 생성한다.
		String address = Base58.encode(publicKeyBtye);
		List<UnspentTransactionOut> selfAllUTxOs = uTxOs.stream()
													 	.filter(uTxO -> address.equals(uTxO.getAddress()))
													 	.collect(Collectors.toList());
		// 3. transactionPool과 selfUTxOs를 조회해서 해당 address에 대한 총 amount를 가진 uTxOs를 가져야한다.
		List<UnspentTransactionOut> selfUTxOs = TransactionUtil.filterTransactionPool(selfAllUTxOs, transactionPool);
		// 4. TransactionOutMap설정
		TransactionOutMap transactionOutMap = TransactionUtil.findAmoutFromUTxOs(amount, selfUTxOs);
		// 5. transactionOutMap의 selfUnspentTransactionOuts정보로 signature를 생성하지 않은 transactionIn List를 생성한다.
		List<TransactionIn> unsignedTxIns = TransactionUtil.unsingedTransationInList(transactionOutMap.getSelfUnspentTransactionOuts());
		// 6. transactionOut List를 생성한다.
		List<TransactionOut> txOuts = TransactionUtil.createTransactionOutList(receivedAddress, 
																			   address, 
																			   amount, 
																			   transactionOutMap.getPayBack());
		// new transactino 생성
		Transaction newTransaction = Transaction.builder()
												.txIns(unsignedTxIns)
												.txOuts(txOuts)
												.build();
		// 7. 이 정보로 transactionHash 생성
		String transactionHash = TransactionUtil.createTransactionHash(newTransaction);
		newTransaction.setTxHash(transactionHash);
		
		// 8. singed txIns생성
		AtomicInteger index = new AtomicInteger(0);
		List<TransactionIn> signedTxIns = newTransaction.getTxIns().stream()
																   .map(mapper -> {
																	   				String signature = TransactionUtil.createSignature(newTransaction, 
																												   					   index.getAndIncrement(), 
																												   					   privateKey, 
																												   					   selfUTxOs);
																	   				return TransactionIn.builder()
																								 .txOutHash(mapper.getTxOutHash())
																								 .txOutIndex(mapper.getTxOutIndex())
																								 .signature(signature)
																								 .build();
																   				   })
																   .collect(Collectors.toList());
		// 9. signedTxIns를 newTransaction에 새로 반영하고 반환
		newTransaction.setTxIns(signedTxIns);
		return newTransaction;
	}
	
	/**
	 * transactionList와 uTxOslist로 트랜잭션 처리
	 * @param transactionList
	 * @param uTxOs
	 * @param blockIndex
	 * @return List<UnspentTransactionOut>
	 */
	public static List<UnspentTransactionOut> processTransactions(List<Transaction> transactionList, 
																  List<UnspentTransactionOut> uTxOs, 
																  Integer blockIndex) {
		// validateBlockTransactions 체크 유효하지 않으면 빈 배열이 아닌 null을 반환한다.
		if(!TransactionValidator.validateBlockTransactions(transactionList, uTxOs, blockIndex)) {
			LOG.info("invalid transactions in block");
			return null;
		}
		// 위 조건이 만족한다면 updateUTxOList를 반환한다.
		return TransactionUtil.updateUTxOList(transactionList, uTxOs);
	}
	
}
