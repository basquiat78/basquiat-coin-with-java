package io.basquiat.blockchain.block.util;

import java.io.File;
import java.util.Date;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.basquiat.blockchain.block.difficulty.BlockDifficulty;
import io.basquiat.blockchain.block.domain.Block;
import io.basquiat.blockchain.block.domain.BlockStore;
import io.basquiat.util.CommonUtil;
import io.basquiat.util.FileIOUtil;
import io.basquiat.util.Sha256Util;

/**
 * BlockUtil
 * created by basquiat
 *
 */
@Component
public class BlockUtil {

	private static final Logger LOG = LoggerFactory.getLogger(BlockUtil.class);
	
	static String GENESIS_SOURCE;
	
	static long GENESIS_TIMESTAMP;
	
	static Integer GENESIS_DIFFICULTY;
	
	static Integer GENESIS_NONCE;

	@Value("${genesis.hash.value}")
	private void setGenesisSource(String genesisHashValue) {
		GENESIS_SOURCE = genesisHashValue;
    }

	@Value("${genesis.timestamp}")
	private void setGenesisTimestamp(long genesisTimestamp) {
		GENESIS_TIMESTAMP = genesisTimestamp;
    }
	
	@Value("${genesis.difficulty}")
	private void setGenesisDifficulty(Integer genesisDifficulty) {
		GENESIS_DIFFICULTY = genesisDifficulty;
    }
	
	@Value("${genesis.nonce}")
	private void setGenesisNonce(Integer genesisNonce) {
		GENESIS_NONCE = genesisNonce;
    }
	
	/**
	 * create block hash from block by sha256
	 * @param index
	 * @param previousHash
	 * @param timestamp
	 * @param data
	 * @return String
	 */
	public static String createHash(Integer index, String previousHash, 
									long timestamp, String data, 
									Integer difficulty, Integer nonce) {
		return Sha256Util.SHA256(index + previousHash + timestamp + data + difficulty + nonce);
	}
	
	/**
	 * initialize genesis block
	 * @return Block
	 */
	public static Block genesisBlock() {
		// 1. index is 0
		// 2. previousHash is null because genesis block don't have previousHash
		// 3. hash infomation create from GENESIS_SOURCE
		// 4. timestamp (unix time)
		// 5. data is from GENESIS_SOURCE
		return Block.builder()
				    .index(0)
				    .previousHash(null)
				    .hash(Sha256Util.SHA256(GENESIS_SOURCE))
				    .timestamp(GENESIS_TIMESTAMP/1000)
				    .data(GENESIS_SOURCE)
				    .difficulty(GENESIS_DIFFICULTY)
				    .nonce(GENESIS_NONCE)
				    .build();
	}
	
	/**
	 * create next block
	 * 
	 * 1. difficulty를 찾는다.
	 * 2. difficulty를 통해서 nonce를 구한다.
	 * 3. 블록을 찾기 위한 답을 얻었다면 difficulty와 nonce를 통해서 block을 만들어야한다.
	 * 
	 * @param previousBlock
	 * @param data
	 * @return Block
	 */
	public static Block createNextBlock(Block previousBlock, String data) {
		Integer nextIndex = previousBlock.getIndex()+1;
		String previousHash = previousBlock.getHash();
		//1. difficulty
		Integer difficulty = BlockDifficulty.calculateDifficulty();
		LOG.info("Current This Node Difficulty is [" + difficulty + "]");
		//2. difficulty를 가지고 block을 찾는다.
		long timestamp = CommonUtil.convertUnixTime(new Date());
		//return block
		// unix time
		Block newBlock = BlockDifficulty.findBlock(nextIndex, previousHash, timestamp, data, difficulty);
		return newBlock;
	}
	
	/**
	 * get genesis block from file repository
	 * @return Block
	 */
	public static Block genesisBlockFromFileRepository() {
		return FileIOUtil.readJsonFile(0, Block.class);
	}
	
	/**
	 * get genesis block from file BlockStore
	 * @return Block
	 */
	public static Block genesisBlockFromBlockStore() {
		return BlockStore.getBlock(0);
	}
	
	/**
	 * get latest block from file repository
	 * @return Block
	 */
	public static Block latestBlockFromFileRepository() {

		Block block = null;
		
		Integer index = FileIOUtil.fileLength();
		if(index == 0) {
			LOG.info("File doesn't exists");
		} else if(index > 0) {
			block =  FileIOUtil.readJsonFile(index-1, Block.class);
		}
		
		return block;
		
	}
	
	/**
	 * get latest block from BlockStore
	 * @return Block
	 */
	public static Block latestBlockFromBlockStore() {
		return BlockStore.getBlock(BlockStore.getBlockList().size()-1);
	}
	
	/**
	 * get block by index from file repository
	 * @param index
	 * @return Block
	 */
	public static Block blockByIndexFromFileRepository(Integer index) {
		return FileIOUtil.readJsonFile(index, Block.class);
	}

	/**
	 * get block by index from BlockStore
	 * @param index
	 * @return Block
	 */
	public static Block blockByIndexFromBlockStore(Integer index) {
		return BlockStore.getBlock(index);
	}
	
	/**
	 * initialize BlockStore
	 */
	public static void initializeBlockStore() {
		File[] files = FileIOUtil.fileList();
		System.out.println(FileIOUtil.fileLength());
		Stream.of(files).forEach(file -> BlockStore.addBlockStore(FileIOUtil.readFile(file, Block.class)));
	}
	
}
