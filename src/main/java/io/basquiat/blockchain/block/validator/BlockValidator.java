package io.basquiat.blockchain.block.validator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.basquiat.blockchain.block.difficulty.BlockDifficulty;
import io.basquiat.blockchain.block.domain.Block;
import io.basquiat.blockchain.block.util.BlockUtil;
import io.basquiat.blockchain.transaction.domain.UnspentTransactionOut;
import io.basquiat.blockchain.transaction.util.TransactionUtil;
import io.basquiat.util.Sha256Util;

/**
 * Block Validator
 * created by basquiat
 *
 */
@Component
public class BlockValidator {
	
	static String GENESIS_SOURCE;

	@Value("${genesis.hash.value}")
	private void setGenesisSource(String genesisHashValue) {
		GENESIS_SOURCE = genesisHashValue;
    }
	
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
	public static boolean validateNewBlock(Block newBlock, Block previousBlock) {
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
		String regenerateNewBlockHash = BlockUtil.createHash(newBlock.getIndex(), 
															 newBlock.getPreviousHash(), 
															 newBlock.getTimestamp(), 
															 newBlock.getTransactions().toString(),
															 newBlock.getDifficulty(),
															 newBlock.getNonce());
		if( !regenerateNewBlockHash.equals(newBlock.getHash()) ) {
			LOG.info("invalid New Block Hash");
			return false;
		}
		// 위의 모든 조건이 만족한다면 true 반환
		return isValid;
	}
	
	/**
	 * <pre>
	 * timestamp를 통해서 정상적인 난이도를 거쳤는지 체크한다.
	 * Role Of Timestamp
	 * 밑이 @see의 링크에 관련 정보가 있다.
	 * timestamp가 difficulty에 어떻게 사용되는지 해당 질문에 대한 답변이 있다.
	 * <pre>
	 * @See bitcoin timestamp valid -> https://bitcoin.stackexchange.com/questions/68653/the-role-of-timestamp
	 * @param newBlock
	 * @param previousBlock
	 * @return boolean
	 */
	public static boolean validateTimestamp(Block newBlock, Block previousBlock) {
		// 이전 블록의 timestamp에서 60 (unixTime 1분)을 뺀 값이 새로운 블록의 timestamp보다 작아야 한다. 당연한가???
		// 근데 1분의 근거는??
		// 새로운 블록의 timestamp에서 60을 뺀 값은 현재 시간보다 작아야 한다. 당연한건데?
		// 근데 여기서 1분의 근거는 ??
		return (previousBlock.getTimestamp() - 60 < newBlock.getTimestamp() && newBlock.getTimestamp() - 60 < new Date().getTime()/1000);
	}
	
	/**
	 * peer를 통해서 block를 끌어올 때
	 * Genesis block정보를 체크해야한다.
	 * @param receivedGenesisBlock
	 * @return boolean
	 */
	public static boolean validateGenesisBlock(Block receivedGenesisBlock) {
		boolean isValid = true;
		// 1. 받은 genesis block의 index와 현재 노드의 genesis block의 index 체크
		Block genesisBlock = BlockUtil.genesisBlockFromBlockStore();
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
		if( !genesisBlock.getTransactions().toString().equals(receivedGenesisBlock.getTransactions().toString()) ) {
			LOG.info("invalid Receidved Genesis Block Data");
			return false;
		}
		// 5. 현재 노드의 genesis_source로 만든 hash가 넘어온 genesis block의 hash와 같아야 한다.
		String regenerateGenesisBlockHash = Sha256Util.SHA256(GENESIS_SOURCE);
		
		if( !regenerateGenesisBlockHash.equals(genesisBlock.getHash()) ) {
			LOG.info("invalid Receidved Genesis Block Hash");
			return false;
		}
		return isValid;
	}
	
	/**
	 * 1. Block의 정보로 생성한 hash값이 Block에 존재하는 hash랑 값이 같은지 체크한다.
	 * 2. difficulty가 제대로 적용된 Block인지 체크한다.
	 * @param block
	 * @return boolean
	 */
	public static boolean isValidHashBlockSelf(Block block) {
		boolean isValid = true;
		String hash = block.getHash();
		String regenereateHash = BlockUtil.createHash(block.getIndex(), 
													  block.getPreviousHash(), 
													  block.getTimestamp(),
													  block.getTransactions().toString(), 
													  block.getDifficulty(), 
													  block.getNonce());
		// 생성한 hash와 block정보에 있는 hash랑 다르면 false리턴
		if(!hash.equals(regenereateHash)) {
			return false;
		}
		
		// difficulty체크가 맞지 않으면 false리턴
		if(!BlockDifficulty.matchesDifficulty(block.getHash(), block.getDifficulty())) {
			return false;
		}
		return isValid;
	}

	/**
	 * 블록 리스트, 즉 블록체인이 유효한지 체크한다.
	 * peer에서 넘겨받은 블록체인을 변경할때 유효성체크하기 위해 사용
	 * 
	 * 1. genesis block에 대한 유효성 체크
	 * 2. uTxOs를 생성한다.
	 * 3. peer로부터 받은 blocklist에서 블록과 이전 블록의 유효성을 일일이 체크한다.
	 * 4. block으로부터 uTxO를 생성하고 전체 블록을 조회하며 uTxOs를 생성한다.
	 * 5. 유효성 검사에서 패스하면 전체 uTxOs를 새로 반환한다.
	 * 
	 * @param blockList
	 * @return List<UnspentTransactionOut>
	 */
	public static List<UnspentTransactionOut> validBlockchain(List<Block> blockList) {
		// 1. genesis block유효성 체크
		Block receivedGenesisBlock = blockList.get(0);
		if(!BlockValidator.validateGenesisBlock(receivedGenesisBlock)) {
			// 유효하지 않다면 null을 반환한다.
			return null;
		}
		
		List<UnspentTransactionOut> resultUTxOs = new ArrayList<>();
		for(int i = 0; i < blockList.size(); i++) {
			if( i != 0 && !BlockValidator.validateNewBlock(blockList.get(i), blockList.get(i-1))) {
				// 유효한 정보가 아니라면 null 반환
				return null;
			}
			// 블록으로부터 uTxOs를 생성한다.
			resultUTxOs = TransactionUtil.processTransactions(blockList.get(i).getTransactions(), resultUTxOs, blockList.get(i).getIndex());
			if(resultUTxOs == null) {
				LOG.info("exist invalid transactions in blockchain");
			}
		}
		return resultUTxOs;
	}
	
}
