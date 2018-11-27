package io.basquiat.blockchain.transaction.domain;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * UnspentTransactionOut
 * 
 * <pre>
 * @See uTxO -> https://steemit.com/coinkorea/@goldenman/utxo
 * </pre>
 * created by basquiat
 *
 */
@Builder
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class UnspentTransactionOut {

	/**
	 * txOutHash
	 */
	private String txOutHash;

	/**
	 * txOutIndex
	 */
	private Integer txOutIndex;

	/**
	 * address
	 */
	private String address;

	/**
	 * amount
	 */
	private BigDecimal amount;
	
}
