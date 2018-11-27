package io.basquiat.blockchain.transaction.domain;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * TransactionOut
 * 
 * <pre>
 * TransactionOut은 어떤 정보를 보낼지에 대한 정보를 담는 객체이다.
 * 일반적으로 Bitcoin을 예로 들면 코인을 어떤 주소로 보낼지에 대한 정보를 담는 객체이다.
 * 주소는 ECDSA에 입각한 정보로 흔히 public key라고 볼 수 있다.
 * 
 * bitcoin은 Transaction Input은 코인 정보를 lock 역할 
 * 
 * </pre>
 * created by basquiat
 *
 */
@Builder
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class TransactionOut {

	/**
	 * address
	 */
	private String address;
	
	/**
	 * amount
	 */
	private BigDecimal amount;
}
