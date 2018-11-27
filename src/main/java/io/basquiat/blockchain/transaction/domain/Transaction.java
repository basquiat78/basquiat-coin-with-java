package io.basquiat.blockchain.transaction.domain;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Transaction
 * 
 * <pre>
 * TransactionIn, TransactionOut 정보를 담는 객체
 * </pre>
 * 
 * created by basquiat
 *
 */
@Builder
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Transaction {

	/**
	 * txHash
	 */
	private String txHash;
	
	/**
	 * txIns
	 */
	private List<TransactionIn> txIns;
	
	/**
	 * txOuts
	 */
	private List<TransactionOut> txOuts;
	
}
