package io.basquiat.blockchain.transaction.validator;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.basquiat.blockchain.transaction.domain.Transaction;
import io.basquiat.blockchain.transaction.domain.TransactionIn;
import io.basquiat.blockchain.transaction.domain.UnspentTransactionOut;
import io.basquiat.blockchain.transaction.util.TransactionUtil;
import io.basquiat.crypto.ECDSAUtil;
import io.basquiat.util.Base58;

/**
 * Block Validator
 * created by basquiat
 *
 */
@Component
public class TransactionValidator {
	
	private static final Logger LOG = LoggerFactory.getLogger(TransactionValidator.class);
	
	static BigDecimal COINBASE_AMOUNT;
	
	@Value("${coinbase.amount}")
	private void setCoinbaseAmount(BigDecimal coinbaseAmount) {
		COINBASE_AMOUNT = coinbaseAmount;
    }
	
	/**
	 * <pre>
	 * transaction이 유효한 tx인지 체크를 해야한다.
	 * 1. transaction의 hash가 실제 tx의 정보로 만들어진 hash와 같은지 확인해야한다.
	 * 2. transactionIn의 유효성을 체크한다.
	 * 3. transactionIn과 transactionOut의 amount는 같아야 한다. 틀리다면 조작된 uTxO
	 * 
	 * </pre>   
	 * @param newBlock
	 * @param previousBlock
	 * @return boolean
	 */
	public static boolean validatTransaction(Transaction transaction, List<UnspentTransactionOut> totalUTxOs) {
		boolean isValid = true;
		
		// 1. transaction hash 체크
		if( !transaction.getTxHash().equals(TransactionUtil.createTransactionHash(transaction)) ) {
			LOG.info("invalid Transaction Hash!");
			return false;
		}
		
		// 2. transaction의 txIn List를 돌면서 유효성을 체크한다.
		// 그중에 하나라도 false가 뜨면 validateTxIn은 false를 반환한다. 
		boolean validateTxIn = transaction.getTxIns().stream()
													 .map(txIn -> TransactionValidator.validatTransactionIn(transaction, txIn, totalUTxOs))
													 .reduce((previous, next) -> previous && next).get();

		// false라면 invalid
		if(!validateTxIn) {
			LOG.info("Exist Invalid TransactionIn!");
	        return false;
		}
		
		// 3. transactionOut의 모든 amount의 합
		BigDecimal totalTransactionOutAmounts = transaction.getTxOuts().stream()
																	   .map(txOut ->  txOut.getAmount())
																	   .reduce((previous, next)->previous.add(next))
																	   .get();
		
		// 4. uTxO와 transactionIn의 해쉬를 통해서 inbound에 대한 모든 amount의 합을 구한다.
		BigDecimal totalTransactionInAmounts = transaction.getTxIns().stream()
																	 .map(txIn -> TransactionValidator.calculateTransactionInAmount(txIn, totalUTxOs))
																	 .reduce((previous, next)->previous.add(next))
																	 .get();
		
		// 5. totalTransactionOutAmounts과 totalTransactionInAmounts다르다면 invalid
		if(totalTransactionOutAmounts.compareTo(totalTransactionInAmounts) != 0) {
			LOG.info("totalTransactionOutAmounts doesn't Match totalTransactionInAmounts!");
			return false;
		}
		
		// 위의 모든 조건이 만족한다면 true 반환
		return isValid;
	}

	/**
	 * 개별적인 uTxO로부터 amount를 구한다.
	 * @param transactionIn
	 * @param totalUTxOs
	 * @return BigDecimal
	 */
	public static BigDecimal calculateTransactionInAmount(TransactionIn transactionIn, List<UnspentTransactionOut> totalUTxOs) {
		UnspentTransactionOut uTxO = TransactionUtil.findUTxO(transactionIn.getTxOutHash(), transactionIn.getTxOutIndex(), totalUTxOs);
	    return uTxO.getAmount();
	}
	
	/**
	 * txin 유효성 체크
	 * @param transaction
	 * @param transactionIn
	 * @param totalUTxOs
	 * @return boolean
	 */
	public static boolean validatTransactionIn(Transaction transaction, TransactionIn transactionIn, List<UnspentTransactionOut> totalUTxOs) {
		Boolean isValid = true;
		// 1. totalUTxOs리스트에서 transactionIn의 정보를 대조해 본다.
		// utxo의 outHash, index와 transactionIn의 hash, index가 같은 utxo을 조회한다. 없으면 유효하지 않다.
		UnspentTransactionOut checkUTxO = totalUTxOs.stream()
												    .filter(uTxO -> uTxO.getTxOutHash().equals(transactionIn.getTxOutHash()) && 
												    		        uTxO.getTxOutIndex() == transactionIn.getTxOutIndex())
											  	    .findAny()
											  	    .orElse(null);
		if(checkUTxO == null) {
			LOG.info("Not Found uTxO");
			return false;
		}
		
		// 2. 주소로부터 Publickey를 추출하고 특정 값에 대해서 verify의 결과를 반환한다. 
		String address = checkUTxO.getAddress();
		PublicKey publicKey = null;
		try {
			publicKey = ECDSAUtil.getPublicKeyFromBytes(Base58.decode(address));
			isValid =  ECDSAUtil.verify(transactionIn.getSignature(), publicKey, transaction.getTxHash());
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | 
				 InvalidKeyException | SignatureException | NoSuchProviderException | 
				 UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return isValid;
	}
	
	/**
	 * transactionIn은 uTxO의 out과 연관이 되어 있다. 즉 참조한 transactionIn에 대한 정보가 없다.
	 * 따라서 coinbase, 최초의 transaction에 대한 처리가 따로 필요하다.
	 * coinbase는 transactionOut만 존재한다. 여기서 아웃풋의 초기 양을 100으로 잡으며
	 * 블록을 채굴할때마다 이 coinbase에 정의된 100코인이 보상으로 주어지게 된다.
	 * @param transaction
	 * @param blockIndex
	 * @return boolean
	 */
	public static boolean validateCoinbaseTransaction(Transaction transaction, Integer blockIndex) {
		boolean isValid = true;
		
		// 최초의 transaction은 genesis transaction, 즉 coinbase transaction을 포함해야한다.
		if(transaction == null) {
			return false;
	    }
		// 1. txHash 체크
	    if( transaction.getTxHash().equals(TransactionUtil.createTransactionHash(transaction)) ) {
	    	return false;
	    }
	    
	    // 2. genesis block에 포함될 genesis transaction은 특정한 값으로 세팅하게 될것이다.
	    // 따라서 coinbase의 txIns은 하나를 무조건 가지게 된다.
	    // txIn list의 사이즈가 1이 아니면 false를 반환한다.
	    if(transaction.getTxIns().size() != 1) {
	        return false;
	    }
	    
	    // 3. coinbase의 경우에는 txIn의 txOutIndex와 blockIndex는 동일해야 한다.
	    if(transaction.getTxIns().get(0).getTxOutIndex() != blockIndex) {
	        return false;
	    }
	    
	    // 4. 특별하게 정의된 txOut가 존재하기 때문에 txOuts의 사이즈는 1이다.
	    if(transaction.getTxOuts().size() != 1) {
	        return false;
	    }
	    
	    // 5. txOuts의 첫번째 txOut의 수량과 정의한 coinbase_amout와 동일해야 한다.
	    if(transaction.getTxOuts().get(0).getAmount().compareTo(COINBASE_AMOUNT) != 0) {
	        return false;
	    }
	    
	    // 모든 조건에 부합하면 true 반환
	    return isValid;
	}
	
}
