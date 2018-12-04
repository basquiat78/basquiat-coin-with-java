package io.basquiat.blockchain.pool.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.basquiat.blockchain.pool.domain.TransactionPoolStore;
import io.basquiat.blockchain.pool.validator.TransactionPoolValidator;
import io.basquiat.blockchain.transaction.domain.Transaction;
import io.basquiat.blockchain.transaction.domain.TransactionIn;
import io.basquiat.blockchain.transaction.domain.UnspentTransactionOut;
import io.basquiat.blockchain.transaction.validator.TransactionValidator;

/**
 * TransactionUtil
 * created by basquiat
 *
 */
@Component
public class TransactionPoolUtil {
	
	private static final Logger LOG = LoggerFactory.getLogger(TransactionPoolUtil.class);

	/**
	 * transaction add to transaction pool
	 * 1. transaction과 deepCopy된 uTxOs와의 유효성 체크
	 * 2. transaction이 transactionpool에 등록되는 대에 있어서 문제가 없는지 유효성 체크
	 * 3. 최종적인 조건에 부합하면 transactionPool에 포함
	 * @param transaction
	 */
	public static void addToTransactionPool(Transaction transaction, List<UnspentTransactionOut> uTxOs) {
		// 1. transaction과 deepCopy된 uTxOs와의 유효성 체크
		if( !TransactionValidator.validateTransaction(transaction, uTxOs) ) {
			throw new RuntimeException("invalid transaction!");
		}
		
		//2. transaction이 transactionpool에 등록되는 대에 있어서 문제가 없는지 유효성 체크
		if( !TransactionPoolValidator.validateTransactionForPool(transaction)) {
			throw new RuntimeException("transactionIn already exist in Transaction Pool : can't add invalid transaction to TransactionPool!");
		}
		LOG.info("transaction add to transaction pool");
		TransactionPoolStore.addTransactionPoolStore(transaction);
	}
	
	/**
	 * transactionpool에 있는 transactionIns 리스트를 반환한다.
	 * 이때 조회하는 transactinopool은 deepcopy가 아닌 concurrentHashMap의 정보에서 조회해야한다.
	 * @return List<TransactionIn>
	 */
	public static List<TransactionIn> getTransactionInFromTransactionPool() {
		List<TransactionIn> txIns = TransactionPoolStore.getTransactionList()
														.stream()
														.map(tx -> tx.getTxIns())
														.reduce((previous, next) -> Stream.concat(previous.stream(), next.stream())
			   							  						 .collect( Collectors.toList()) )
														.orElse(new ArrayList<TransactionIn>());
		
		return txIns;
	}
	
	/**
	 * TransactionPool을 업데이트 한다.
	 * transactionPool과 uTxOs를 조회하며 유효하지 않은 transaction들을 제거하고 새로운 transaction pool로 업데이트한다.
	 * @param uTxOs
	 */
	public static void upadateTransactionPool(List<UnspentTransactionOut> uTxOs) {
		List<Transaction> invalidTransactionList = new ArrayList<>();
		// 1. transactionPool에 있는 transaction을 조회한다.
		List<Transaction> transactinPool = TransactionPoolStore.getTransactionList();
		for(Transaction tx : transactinPool) {
			// 여기서 uTxOs에서   txIn의 정보가 없다면 해당 transaction은 invalid transaction
			for(TransactionIn txIn : tx.getTxIns()) {
				if(!TransactionPoolUtil.hasTransactionIn(txIn, uTxOs)) {
					invalidTransactionList.add(tx);
					break;
				}
			}
		}
		// invalidTransactionList가 존재하면 update 아니면 패쓰
		if(invalidTransactionList.size() > 0) {
			LOG.info("update TransactionPool!");
			transactinPool.removeIf(tx -> invalidTransactionList.contains(tx));
			TransactionPoolStore.updateTransactionPoolStore(transactinPool);
		}
	}
	
	/**
	 * uTxOs list에서 transactionin과 같은 정보가 있는지 체크한다.
	 * 있다면 true를 없다면 false를 반환한다.
	 * @param transactionIn
	 * @param uTxOs
	 * @return boolean
	 */
	public static boolean hasTransactionIn(TransactionIn transactionIn, List<UnspentTransactionOut> uTxOs) {
		boolean isChecked = false;
		UnspentTransactionOut specificUTxO = uTxOs.stream()
							    			.filter(uTxO -> uTxO.getTxOutHash().equals(transactionIn.getTxOutHash()) && 
							    							uTxO.getTxOutIndex() == transactionIn.getTxOutIndex())
							    			.findAny()
							    			.orElse(null);
		if(specificUTxO != null) {
			isChecked = true;
		}
		return isChecked;
	}
	
}
