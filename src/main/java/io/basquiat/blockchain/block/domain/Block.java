package io.basquiat.blockchain.block.domain;

import java.util.List;

import io.basquiat.blockchain.transaction.domain.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Block Domain
 * created by basquiat
 *
 */
@Builder
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Block {

	/**
	 * block number
	 */
	private Integer index;
	
	/**
	 * block hash
	 */
	private String hash;
	
	/**
	 * previous hash
	 */
	private String previousHash;

	/**
	 * block Transaction
	 */
	private List<Transaction> transactions;
	
	/**
	 * difficulty
	 */
	private Integer difficulty;
	
	/**
	 * nonce
	 */
	private Integer nonce;
	
	/**
	 * time stamp
	 */
	private long timestamp;
	
}
