package io.basquiat.blockchain.wallet.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Address Domain
 * created by basquiat
 *
 */
@Builder
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Address {

	/**
	 * account
	 */
	private String account;
	
	/**
	 * address
	 */
	private String address;
	
}
