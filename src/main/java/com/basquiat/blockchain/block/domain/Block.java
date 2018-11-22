package com.basquiat.blockchain.block.domain;

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
	 * block data
	 */
	private String data;
	
	/**
	 * time stamp
	 */
	private long timestamp;
	
}
