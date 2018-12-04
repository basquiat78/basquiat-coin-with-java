package io.basquiat.blockchain.block.difficulty;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.basquiat.blockchain.block.domain.Block;
import io.basquiat.blockchain.block.domain.BlockStore;
import io.basquiat.blockchain.block.util.BlockUtil;
import io.basquiat.blockchain.transaction.domain.Transaction;
import io.basquiat.util.CommonUtil;

/**
 * Block Validator
 * created by basquiat
 *
 */
@Component
public class BlockDifficulty {
	
	static Integer BLOCK_GENERATION_INTERVAL;
	
	static Integer BLOCK_DIFFICULTY_ADJUSTMENT_INTERVAL;
	
	@Value("${block.generation.interval}")
	private void setBlockGenerationInterval(Integer blockGenerationInterval) {
		BLOCK_GENERATION_INTERVAL = blockGenerationInterval;
    }

	@Value("${block.difficulty.adjustment.interval}")
	private void setBlockDifficultyAdjustmentInterval(Integer blockDifficultyAdjustmentInterval) {
		BLOCK_DIFFICULTY_ADJUSTMENT_INTERVAL = blockDifficultyAdjustmentInterval;
    }
	
	/**
	 * hexToBinary Lookup Table
	 */
	private static final Map<String, String> LOOKUP_TABLE = Stream.of(
															 new AbstractMap.SimpleImmutableEntry<>("0", "0000"),
															 new AbstractMap.SimpleImmutableEntry<>("1", "0001"),
															 new AbstractMap.SimpleImmutableEntry<>("2", "0010"),
															 new AbstractMap.SimpleImmutableEntry<>("3", "0011"),
															 new AbstractMap.SimpleImmutableEntry<>("4", "0100"),
															 new AbstractMap.SimpleImmutableEntry<>("5", "0101"),
															 new AbstractMap.SimpleImmutableEntry<>("6", "0110"),
															 new AbstractMap.SimpleImmutableEntry<>("7", "0111"),
															 new AbstractMap.SimpleImmutableEntry<>("8", "1000"),
															 new AbstractMap.SimpleImmutableEntry<>("9", "1001"),
															 new AbstractMap.SimpleImmutableEntry<>("a", "1010"),
															 new AbstractMap.SimpleImmutableEntry<>("b", "1011"),
															 new AbstractMap.SimpleImmutableEntry<>("c", "1100"),
															 new AbstractMap.SimpleImmutableEntry<>("d", "1101"),
															 new AbstractMap.SimpleImmutableEntry<>("e", "1110"),
															 new AbstractMap.SimpleImmutableEntry<>("f", "1111")
															).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	
	/**
	 * <pre>
	 * Difficulty를 계산한다.
	 * difficulty는 설정한 15블록 간격으로 예상 시간을 체크한다.
	 * @See Bitcoin difficulty 난이도 구하는 공식 -> https://steemit.com/kr/@yahweh87/5smxh6
	 * </pre>
	 * @return Integer
	 */
	public static Integer calculateDifficulty() {
		List<Block> totalList = BlockStore.getBlockList();
		Block latestBlock = totalList.get(totalList.size()-1);
		// 1. 최신 블록의 인덱스를 difficulty 적용 블록 갯수로 나눴을때 나머지가 0이고 최신 블록의 인덱스가 0이 아니라면
		// difficulty을 조종해야한다.
		// BLOCK_DIFFICULTY_ADJUSTMENT_INTERVAL가 15이므로
		// 15번째 블록부터는 difficulty가 조정되어야 한다.
		if( latestBlock.getIndex() % BLOCK_DIFFICULTY_ADJUSTMENT_INTERVAL == 0 &&
		    latestBlock.getIndex() != 0	
		  ) {
			return BlockDifficulty.adjustDifficulty(latestBlock, totalList);
		}
		
		return latestBlock.getDifficulty();
	}

	/**
	 * <pre>
	 * difficulty를 조정한다.
	 * like bitcoin
	 * </pre>
	 * @param latestBlock
	 * @param totalList
	 * @return Integer
	 */
	public static Integer adjustDifficulty(Block latestBlock, List<Block> totalList) {
		// 1. 이전 difficulty가 가장 마지막으로 적용된 block을 구한다.
		Block previousAdjustDifficultyBlock = totalList.get(totalList.size() - BLOCK_DIFFICULTY_ADJUSTMENT_INTERVAL);
		// mining 예상 시간
		long expectedTime = Long.parseLong(String.valueOf(BLOCK_GENERATION_INTERVAL * BLOCK_DIFFICULTY_ADJUSTMENT_INTERVAL));
		// 최신 블록의 timestamp와 previousAdjustDifficultyBlock의 timestamp의 차이
		long takenTime = latestBlock.getTimestamp() - previousAdjustDifficultyBlock.getTimestamp();
		
		// takenTime이 예상 시간을 2로 나눈것보다 작다면 난이도가 낮으므로 이전 difficulty에서 증가시킨다.
		if(takenTime < expectedTime/2 ) {
			return previousAdjustDifficultyBlock.getDifficulty() + 1; 
		}
		
		// 예상 시간의 두배보다 takenTime의 시간이 크다면 난이도가 높으니 낮춘다.
		if(takenTime > expectedTime*2 ) {
			return previousAdjustDifficultyBlock.getDifficulty() - 1; 
		}
		// 아무것도 해당되지 않는다면 이전 난이도를 그냥 반환하자
		return previousAdjustDifficultyBlock.getDifficulty();
	}
	
	/**
	 * <pre>
	 * block의 hash값이 난이도와 매칭이 되는지 조사해야한다.
	 * like bitcoin처럼
	 * </pre>   
	 * @param hash
	 * @param difficulty
	 * @return boolean
	 */
	public static boolean matchesDifficulty(String hash, Integer difficulty) {
		String hexToBinaryString = BlockDifficulty.hexToBinary(hash);
		String matchesDifficultyValue = CommonUtil.repeat("0", difficulty);
		return hexToBinaryString.startsWith(matchesDifficultyValue);
	}
	
	/**
	 * convert hash to binary
	 * @param hash
	 * @return String
	 */
	public static String hexToBinary(String hash) {
		hash = hash.substring(2);
		char[] hex= hash.toCharArray();
	    String binaryString = "";
	    for(char h : hex) {
	        binaryString=binaryString+ LOOKUP_TABLE.get(String.valueOf(h));
	    }
	    return binaryString;
	}
	
	/**
	 * find block with difficulty and nonce
	 * @param nextIndex
	 * @param previousHash
	 * @param timestamp
	 * @param data
	 * @param difficulty
	 * @return Block
	 */
	public static Block findBlock(Integer nextIndex, String previousHash, long timestamp, List<Transaction> tx, Integer difficulty) {
		Integer nonce = 0;
		while(true) {
			String hash = BlockUtil.createHash(nextIndex, previousHash, timestamp, tx.toString(), difficulty, nonce);
			System.out.println("findBlock hash : " + hash);
		    if(BlockDifficulty.matchesDifficulty(hash, difficulty)) {
		        return Block.builder()
		        		    .index(nextIndex)
		        		    .hash(hash)
		        		    .previousHash(previousHash)
		        		    .timestamp(timestamp)
		        		    .transactions(tx)
		        		    .difficulty(difficulty)
		        		    .nonce(nonce)
		        		    .build();
		    }
		    nonce++;
		}
	}
	
}
