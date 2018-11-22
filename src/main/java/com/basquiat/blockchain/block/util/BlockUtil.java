package com.basquiat.blockchain.block.util;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.basquiat.blockchain.block.domain.Block;
import com.basquiat.util.CommonUtil;
import com.basquiat.util.FileIOUtil;
import com.basquiat.util.Sha256Util;

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

	@Value("${genesis.hash.value}")
	private void setGenesisSource(String genesisHashValue) {
		GENESIS_SOURCE = genesisHashValue;
    }

	@Value("${genesis.timestamp}")
	private void setGenesisTimestamp(long genesisTimestamp) {
		GENESIS_TIMESTAMP = genesisTimestamp;
    }
	
	/**
	 * create block hash from block by sha256
	 * @param index
	 * @param previousHash
	 * @param timestamp
	 * @param data
	 * @return String
	 */
	public static String createHash(Integer index, String previousHash, long timestamp, String data) {
		return Sha256Util.SHA256(index + previousHash + timestamp + data);
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
				    .build();
	}
	
	/**
	 * create next block
	 * @param previousBlock
	 * @param data
	 * @return Block
	 */
	public static Block createNextBlock(Block previousBlock, String data) {
		Integer nextIndex = previousBlock.getIndex()+1;
		String previousHash = previousBlock.getHash();
		// unix time
		long timestamp = CommonUtil.convertUnixTime(new Date());
		return Block.builder()
			    	.index(nextIndex)
				    .previousHash(previousHash)
				    .hash(BlockUtil.createHash(nextIndex, previousHash, timestamp, data))
				    .timestamp(timestamp)
				    .data(data)
				    .build();
	}
	
	/**
	 * get genesis block from file repository
	 * @return Block
	 */
	public static Block genesisBlockFromFileRepository() {
		return FileIOUtil.readJsonFile(0, Block.class);
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
	 * get block by index from file repository
	 * @param index
	 * @return Block
	 */
	public static Block blockByIndexFromFileRepository(Integer index) {
		return FileIOUtil.readJsonFile(index, Block.class);
	}
	
}
