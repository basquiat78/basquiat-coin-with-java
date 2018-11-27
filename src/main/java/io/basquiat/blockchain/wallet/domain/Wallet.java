package io.basquiat.blockchain.wallet.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Wallet Domain
 * created by basquiat
 *
 */
@Builder
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Wallet {

	/**
	 * privateKey
	 */
	private String account;
	
	/**
	 * privateKey
	 */
	private String privateKey;
	
	/**
	 * unixTime
	 */
	private long createDttm;
	
}
