package io.basquiat.blockchain.block;

import org.springframework.stereotype.Service;

import io.basquiat.blockchain.block.domain.Block;
import io.basquiat.blockchain.block.domain.BlockStore;
import io.basquiat.blockchain.block.util.BlockUtil;
import io.basquiat.blockchain.block.validator.BlockValidator;
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
			mono = Mono.just(BlockUtil.blockByIndexFromBlockStore(Integer.parseInt(blockIndex)));
		}
		return  mono;
	}
	
	/**
	 * find latest block
	 * @return Mono<Block>
	 */
	public Mono<Block> findLatestBlock() {
		return  Mono.just(BlockUtil.latestBlockFromBlockStore());
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
		Block previousBlock = BlockUtil.latestBlockFromBlockStore();
		Block newBlock = BlockUtil.createNextBlock(previousBlock, data);
		if(BlockValidator.validatNewBlock(newBlock, previousBlock)) {
			BlockStore.addBlockStore(newBlock);
			FileIOUtil.writeJsonBlockFile(newBlock);
			//TODO
			// websocket을 통해 새로운 블록을 전파해야한다.
		}
		return newBlock;
	}
	
}
