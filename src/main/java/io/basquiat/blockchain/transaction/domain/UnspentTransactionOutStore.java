package io.basquiat.blockchain.transaction.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * uTxOs list
 * created by basquiat
 *
 */
public class UnspentTransactionOutStore {

	private static final Map<Integer, UnspentTransactionOut> UTXO_STORE = new ConcurrentHashMap<>();
	
	/**
	 * UnspentTransactionOut을 map에 담는다.
	 * @param uTxO
	 */
	public static void addUTxOStore(UnspentTransactionOut uTxO) {
		Integer index = UnspentTransactionOutStore.getUTxOStoreLength();
		UTXO_STORE.put(index, uTxO);
	}
	
	/**
	 * UTXO_STOREE Map을 List객체로 반환한다.
	 * @return List<UnspentTransactionOut>
	 */
	public static List<UnspentTransactionOut> getUTxOs() {
		return new ArrayList<UnspentTransactionOut>(UTXO_STORE.values());
	}
	
	/**
	 * UTXO_STORE에서 Transaction를 가져온다.
	 * @return Transaction
	 */
	public static UnspentTransactionOut getUTxO(Integer index) {
		return UnspentTransactionOutStore.getUTxOs().get(index);
	}
	
	/**
	 * Map to list length를 가져온다.
	 * @return Integer
	 */
	public static Integer getUTxOStoreLength() {
		return UnspentTransactionOutStore.getUTxOs().size();
	}
	
	/**
	 * concurrentHashMap의 값을 변경한다.
	 * @param concurrentHashMap
	 */
	public static void changeUTxOStore(List<UnspentTransactionOut> uTxOs) {
		UTXO_STORE.clear();
		Map<Integer, UnspentTransactionOut> changedMap = IntStream.range(0, uTxOs.size())
														.boxed()
														.collect(Collectors.toMap(index -> index, index -> uTxOs.get(index)));
		UTXO_STORE.putAll(changedMap);
	}
	
	/**
	 * Deep Copy
	 * @return List<UnspentTransactionOut>
	 */
	public static List<UnspentTransactionOut> deepCopyFromUTxOs() {
		List<UnspentTransactionOut> uTxOs = UnspentTransactionOutStore.getUTxOs();
		ObjectMapper objectMapper = new ObjectMapper();
		TypeReference<List<UnspentTransactionOut>> typeReference = new TypeReference<List<UnspentTransactionOut>>() {};
		List<UnspentTransactionOut> deepCopyUTxOs = new ArrayList<>();
		try {
			deepCopyUTxOs = objectMapper.readValue(objectMapper.writeValueAsString(uTxOs), typeReference);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return deepCopyUTxOs;
	}
	
}
