package com.basquiat.blockchain.block.validator;

import org.springframework.stereotype.Component;

import com.basquiat.blockchain.block.domain.Block;

/**
 * Block Validator
 * created by basquiat
 *
 */
@Component
public class BlockValidator {

	/**
	 * <pre>
	 * 새로운 블록과 이전 블록을 검사한다.
	 * 1. 새로운 블록과 이전 블록의 index는 당연히 같을 수 없다.
	 * 2. 새로운 블록의 previousHash정보는 이전 블록의 block hash와 당연히 같아야 한다.
	 * 3. 마지막으로 새로운 블록의 block hash값은 새로운 블록 정보인 index, previousHash, timestamp, data 정보를 가지고
	 *    BlockUtil.createHash(index, previousHash, timestamp, data)를 실행해서 나온 hash값과 당연히 같아야 한다.
	 *    만일 같지 않다면 이것은 유효한 블록 정보가 아니다.
	 * </pre>   
	 * @param newBlock
	 * @param previousBlock
	 * @return boolean
	 */
	public static boolean validatNewBlock(Block newBlock, Block previousBlock) {
		boolean isValid = false;
		return isValid;
	}

	
}
