package io.basquiat.blockchain.transaction.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * TransactionIn
 * 
 * <pre>
 * TransactionIn은 실제 코인을 보낸 발송자의 정보를 담게 된다.
 * 
 * 여기서 signature라는 것은 서명의 역할로 public key와 한쌍으로 존재하게 되는 
 * private key를 가진 유저가 트랜잭션을 만들었음을 증명하는 값이 된다.
 * 따라서 이 값은 private Key로부터 생성된 값이다.
 * 
 * bitcoin은 Transaction Input은 코인 정보를 열람하는 역할 
 *
 * </pre>
 * 
 * created by basquiat
 *
 */
@Builder
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class TransactionIn {

	/**
	 * txOutHash
	 */
	private String txOutHash;
	
	/**
	 * txOutIndex
	 */
	private Integer txOutIndex;
	
	/**
	 * signature
	 */
	private String signature;
	
}
