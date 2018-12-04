package io.basquiat.blockchain.transaction.domain;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * TransactionOutMap
 * 
 * <pre>
 * uTxO에 의해 a -> b로 코인을 보낼때
 * uTxO로부터 a의 사용되지 않은 uTxO의 리스트를 돌면서 uTxO의 수량을 합한다. 이때 보낼 수량보다 같거나 클때가지 리스트로 가지게 된다.
 * 이때 전체 수량이 보낼 수량보다 크다면 나머지는 자신에게 payBack을 해야한다.
 * 이 Domain은 해당 정보를 담는 Map역할을 한다.
 * </pre>
 * created by basquiat
 *
 */
@Builder
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class TransactionOutMap {

	/**
	 * mySelfUnspentTransactionOut
	 */
	private List<UnspentTransactionOut> selfUnspentTransactionOuts;
	
	/**
	 * payBack
	 */
	private BigDecimal payBack;
}
