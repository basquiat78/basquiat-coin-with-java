package io.basquiat.blockchain.wallet.domain;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Block Store
 * created by basquiat
 *
 */
public class CoinbaseStore {

	private static final Map<String, String> COINBASE_STORE = new ConcurrentHashMap<>();
	
	/**
	 * block을 map에 담는다.
	 * @param block
	 */
	public static void setCoinbaseStore(String account) {
		COINBASE_STORE.put("COINBASE", account);
	}
	
	/**
	 * Block Store에서 block를 가져온다.
	 * @return Block
	 */
	public static String getCoinbaseStore() {
		return COINBASE_STORE.get("COINBASE");
	}
	
}
