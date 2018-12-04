package io.basquiat.blockchain.pool.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.basquiat.blockchain.transaction.domain.Transaction;

/**
 * Transaction Pool Store
 * created by basquiat
 *
 */
public class TransactionPoolStore {

	private static final Map<Integer, Transaction> TRANSACTIONPOOL_STORE = new ConcurrentHashMap<>();
	
	/**
	 * Transaction을 map에 담는다.
	 * @param transaction
	 */
	public static void addTransactionPoolStore(Transaction transaction) {
		Integer index = TransactionPoolStore.getTransactionPoolLength();
		TRANSACTIONPOOL_STORE.put(index, transaction);
	}
	
	/**
	 * TRANSACTIONPOOL_STORE Map을 List객체로 반환한다.
	 * @return List<Transaction>
	 */
	public static List<Transaction> getTransactionList() {
		return new ArrayList<Transaction>(TRANSACTIONPOOL_STORE.values());
	}
	
	/**
	 * TRANSACTIONPOOL_STORE에서 Transaction를 가져온다.
	 * @return Transaction
	 */
	public static Transaction getTransaction(Integer index) {
		return TransactionPoolStore.getTransactionList().get(index);
	}
	
	/**
	 * Map to list length를 가져온다.
	 * @return Integer
	 */
	public static Integer getTransactionPoolLength() {
		return TransactionPoolStore.getTransactionList().size();
	}
	
	/**
	 * concurrentHashMap의 값을 변경한다.
	 * @param concurrentHashMap
	 */
	public static void updateTransactionPoolStore(List<Transaction> transactionList) {
		TRANSACTIONPOOL_STORE.clear();
		Map<Integer, Transaction> changedMap = IntStream.range(0, transactionList.size())
														.boxed()
														.collect(Collectors.toMap(index -> index, index -> transactionList.get(index)));
		TRANSACTIONPOOL_STORE.putAll(changedMap);
	}
	
	/**
	 * Deep Copy
	 * @return List<Transaction>
	 */
	public static List<Transaction> deepCopyFromTransactionPool() {
		List<Transaction> transactionList = TransactionPoolStore.getTransactionList();
		ObjectMapper objectMapper = new ObjectMapper();
		TypeReference<List<Transaction>> typeReference = new TypeReference<List<Transaction>>() {};
		List<Transaction> deepCopyTransactionPool = new ArrayList<>();
		try {
			deepCopyTransactionPool = objectMapper.readValue(objectMapper.writeValueAsString(transactionList), typeReference);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return deepCopyTransactionPool;
	}

}
