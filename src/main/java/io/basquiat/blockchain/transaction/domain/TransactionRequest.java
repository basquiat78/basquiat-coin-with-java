package io.basquiat.blockchain.transaction.domain;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * TransactionRequest
 * 
 * <pre>
 * sendTransaction 요청 정보
 * </pre>
 * created by basquiat
 *
 */
@Builder
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class TransactionRequest {

	/**
	 * receive address
	 */
	private String receivedAddress;
	
	/**
	 * amount
	 */
	private BigDecimal amount;
	
}
