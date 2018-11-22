package io.basquiat.blockchain.block;

import org.springframework.stereotype.Service;

import io.basquiat.blockchain.block.domain.Block;
import io.basquiat.blockchain.block.util.BlockUtil;
import io.basquiat.util.CommonUtil;
import io.basquiat.util.FileIOUtil;
import reactor.core.publisher.Mono;

/**
 * Blockchain Service
 * created by basquiat
 *
 */
@Service("blockchainService")
public class BlockService {

	/**
	 * find Block by blockIndex
	 * @param blockIndex
	 * @return Mono<Block>
	 */
	public Mono<Block> findBlockByIndex(String blockIndex) {
		Mono<Block> mono = null;
		
		if(CommonUtil.validNumber(blockIndex)) {
			mono = Mono.just(BlockUtil.blockByIndexFromFileRepository(Integer.parseInt(blockIndex)));
		}
		return  mono;  
	}
	
	/**
	 * find latest block
	 * @return Mono<Block>
	 */
	public Mono<Block> findLatestBlock() {
		return  Mono.just(BlockUtil.latestBlockFromFileRepository());
	}
	
	/**
	 * mine block
	 * @return Mono<Block>
	 */
	public Mono<Block> mining(String data) {
		return  Mono.just(this.miningBlock(data));
	}

	/**
	 * generate new Block
	 * @return Block
	 */
	private Block miningBlock(String data) {
		/*
		 * previous block을 가져와서 새로운 블록을 생성하고 file로 저장한다. 
		 */
		Block previousBlock = BlockUtil.latestBlockFromFileRepository();
		Block newBlock = BlockUtil.createNextBlock(previousBlock, data);
		FileIOUtil.writeJsonFile(newBlock);
		return newBlock;
	}
	
}
