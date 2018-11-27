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
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bouncycastle.crypto.RuntimeCryptoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.basquiat.blockchain.transaction.domain.Transaction;
import io.basquiat.blockchain.transaction.domain.TransactionIn;
import io.basquiat.blockchain.transaction.domain.TransactionOut;
import io.basquiat.blockchain.transaction.domain.UnspentTransactionOut;
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
				.reduce((previous, next) -> previous+next).get();

		// txOut --> forEach (address + coin amout)
		String txOutValueConcat = transaction.getTxOuts()
								  .stream()
								  .map(txOut -> txOut.getAddress() + txOut.getAmount().toString())
								  .reduce((previous, next) -> previous+next).get();
		return Sha256Util.SHA256(txInValueConcat + txOutValueConcat);
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
	public static String createSignature(Transaction transaction, Integer txInIndex, String privateKey, List<UnspentTransactionOut> uTxOutList) {
		// 1. transaction에서 txInIndex에 해당하는 TransactionIn 객체를 추출한다.
		TransactionIn txIn = transaction.getTxIns().get(txInIndex);
		// 2. sigantrue에 사용될 transaction hash값을 추출한다.
		String signatureData = transaction.getTxHash();
		// 3. 트랜잭션에 unspectTransactionOut를 찾는다.
		UnspentTransactionOut uTxOut = TransactionUtil.findUTxO(txIn.getTxOutHash(), txIn.getTxOutIndex(), uTxOutList)  ;
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
			// TODO Auto-generated catch block
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
	public static UnspentTransactionOut findUTxO(String transactionHash, Integer index, List<UnspentTransactionOut> uTxOList) {
		return uTxOList.stream()
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
	public static UnspentTransactionOut TransactionOutToUnspentTransactionOut(TransactionOut transactionOut, String transactionHash, Integer index) {
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
	public static UnspentTransactionOut TransactionInToUnspentTransactionOut(TransactionIn transactionIn) {
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
	public static List<UnspentTransactionOut> createNewUTxOList(List<Transaction> transactions) {
		AtomicInteger index = new AtomicInteger();
		// 1. transactions에서 txoutlist를 조회한다.
		// 2. txoutlist의 txOut정보로 UnspentTransactionOut 리스트를 생성한다.
		// 3. reduce를 통해 생성된 리스트를 concat해서 전체 utxo 리스트를 생성한다.
		return transactions.stream()
						   .map(
								 tx ->   {
									 		return tx.getTxOuts().stream()
									  					.map(txOut ->  
									  							  TransactionUtil.TransactionOutToUnspentTransactionOut(txOut, 
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
	public static List<UnspentTransactionOut> consumeUTxOList(List<Transaction> transactions) {
		// 1. transactions로부터 transactionin list를 찾는다.
		// 2. transactionin list에서 transaction in정보를 통해 consume된 uxto를 찾는다. 이때 주소는 '', amount는 0으로 세팅한다.
		// 3. reduce를 통해 생성된 리스트를 concat해서 전체 consumed된 utxo리스트를 생성한다.
		return transactions.stream()
			   .map(
					 tx ->   {
						 		return tx.getTxIns().stream()
						  					.map(txIn ->  TransactionUtil.TransactionInToUnspentTransactionOut(txIn))
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
	public static List<UnspentTransactionOut> consumeAndResultUtxOList(List<UnspentTransactionOut> totalUTxOs, 
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
		List<UnspentTransactionOut> newUTxOs = TransactionUtil.createNewUTxOList(transactions);
		
		// 2. consumeUTxOList를 얻는다.
		List<UnspentTransactionOut> consumeUTxOs = TransactionUtil.consumeUTxOList(transactions);
		
		// 3. consumeUTxO를 제거한 uTxO list를 생성한다.
		List<UnspentTransactionOut> consumeAndResultUTxOs = TransactionUtil.consumeAndResultUtxOList(totalUTxOs, consumeUTxOs);
		
		// consumeAndResultUTxOs와 newUTxOs를 concat해서 반환한다.
		return Stream.concat(consumeAndResultUTxOs.stream(), newUTxOs.stream()).collect( Collectors.toList());
	}

}
