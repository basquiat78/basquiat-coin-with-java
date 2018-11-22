package io.basquiat.blockchain.block.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.basquiat.blockchain.block.domain.Block;
import io.basquiat.blockchain.block.util.BlockUtil;

/**
 * Block Validator
 * created by basquiat
 *
 */
@Component
public class BlockValidator {
	
	private static final Logger LOG = LoggerFactory.getLogger(BlockValidator.class);
	
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
		boolean isValid = true;
		// 1. index 체크
		// 새로운 블록의 인데스에서 1을 뺀 값이 이전 블록의 index와 같아야 하며 같지 않다면 이 블록은 유효하지 않다.
		if( previousBlock.getIndex() != newBlock.getIndex()-1 ) {
			LOG.info("invalid New Block Index");
			return false;
		}
		// 2. hash 체크
		// 새로운 블록의 이전 블록 해쉬값은 이전 블록의 해쉬값과 동일해야한다.
		if( !previousBlock.getHash().equals(newBlock.getPreviousHash()) ) {
			LOG.info("invalid New Block Previous Hash");
			return false;
		}
		// 3. 새로운 블록의 hash값을 다시 체크한다.
		String regenerateNewBlockHash = BlockUtil.createHash(newBlock.getIndex(), newBlock.getPreviousHash(), newBlock.getTimestamp(), newBlock.getData());
		if( !regenerateNewBlockHash.equals(newBlock.getHash()) ) {
			LOG.info("invalid New Block Hash");
			return false;
		}
		// 위의 모든 조건이 만족한다면 true 반환
		return isValid;
	}
	
	/**
	 * peer를 통해서 block를 끌어올 때
	 * Genesis block정보를 체크해야한다.
	 * @param receivedGenesisBlock
	 * @return boolean
	 */
	public static boolean validatGenesisBlock(Block receivedGenesisBlock) {
		boolean isValid = true;
		// 1. 받은 genesis block의 index와 현재 노드의 genesis block의 index 체크
		Block genesisBlock = BlockUtil.genesisBlockFromFileRepository();
		if( genesisBlock.getIndex() != receivedGenesisBlock.getIndex() ) {
			LOG.info("invalid Receidved Genesis Block Index");
			return false;
		}
		// 2. 받은 genesis block의 hash와 현재 노드의 genesis block의 hash 체크
		if( !genesisBlock.getHash().equals(receivedGenesisBlock.getHash()) ) {
			LOG.info("invalid Receidved Genesis Block Hash");
			return false;
		}
		// 3. 받은 genesis block의 timestamp와 현재 노드의 genesis block의 timestamp 체크
		if( genesisBlock.getTimestamp() != receivedGenesisBlock.getTimestamp() ) {
			LOG.info("invalid Receidved Genesis Block Timestamp");
			return false;
		}
		// 4. 받은 genesis block의 data와 현재 노드의 genesis block의 data 체크
		if( !genesisBlock.getData().equals(receivedGenesisBlock.getData()) ) {
			LOG.info("invalid Receidved Genesis Block Data");
			return false;
		}
		// 5. 최종적으로 받은 genesis block의 정보로 생성한 hash가 현재 노드의 genesis block의 hash와 같은지 체크
		String regenerateGenesisBlockHash = BlockUtil.createHash(receivedGenesisBlock.getIndex(), 
																 receivedGenesisBlock.getPreviousHash(), 
																 receivedGenesisBlock.getTimestamp(), 
																 receivedGenesisBlock.getData());
		if( !regenerateGenesisBlockHash.equals(genesisBlock.getHash()) ) {
			LOG.info("invalid Receidved Genesis Block Hash");
			return false;
		}
		return isValid;
	}
	
}
