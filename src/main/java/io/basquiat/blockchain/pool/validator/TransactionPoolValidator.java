package io.basquiat.blockchain.pool.validator;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.basquiat.blockchain.pool.util.TransactionPoolUtil;
import io.basquiat.blockchain.transaction.domain.Transaction;
import io.basquiat.blockchain.transaction.domain.TransactionIn;

/**
 * Block Validator
 * created by basquiat
 *
 */
@Component
public class TransactionPoolValidator {
	
	private static final Logger LOG = LoggerFactory.getLogger(TransactionPoolValidator.class);
	
	/**
	 * transaction이 transactionpool에 올리는데 있어서 문제가 없는지 유효성 체크를 해야한다.
	 * @param transaction
	 * @return boolean
	 */
	public static boolean validateTransactionForPool(Transaction transaction) {
		boolean isValid = true;
		// 1. transactionPool에서 txIn list를 가져온다.
		List<TransactionIn> txIns = TransactionPoolUtil.getTransactionInFromTransactionPool();
		// 2. txIns에서 transaction에 있는 txIns와 비교하면서 같은 정보를 가진 txIn이 있는지 조회를 한다.
		// 하나라도 같은게 있다면 해당 transaction을 transactinopool에 등록할 수 없다. 
		for(TransactionIn txInFromPool : txIns) {
			TransactionIn specificTxIn = transaction.getTxIns()
									  .stream()
									  .filter(txIn -> txIn.getTxOutHash().equals(txInFromPool.getTxOutHash()) && 
													  txIn.getTxOutIndex() == txInFromPool.getTxOutIndex()
											  )
									  .findAny()
									  .orElse(null);
			if(specificTxIn != null) {
				isValid = false;
				LOG.info("transactionIn already exist in Transaction Pool");
				break;
			}
		}
		return isValid;
	}

}
