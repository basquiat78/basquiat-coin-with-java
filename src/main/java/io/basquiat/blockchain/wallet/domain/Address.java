package io.basquiat.blockchain.wallet.domain;

import java.math.BigDecimal;

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
	
	/**
	 * amount
	 */
	private BigDecimal amount;
	
}
