package io.basquiat.blockchain.block.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Block Store
 * created by basquiat
 *
 */
public class BlockStore {

	private static final Map<Integer, Block> BLOCK_STORE = new ConcurrentHashMap<>();
	
	/**
	 * block을 map에 담는다.
	 * @param block
	 */
	public static void addBlockStore(Block block) {
		BLOCK_STORE.put(block.getIndex(), block);
	}
	
	/**
	 * Block Store Map을 List객체로 반환한다.
	 * @return List<Block>
	 */
	public static List<Block> getBlockList() {
		return new ArrayList<Block>(BLOCK_STORE.values());
	}
	
	/**
	 * Block Store에서 block를 가져온다.
	 * @param index
	 * @return Block
	 */
	public static Block getBlock(Integer index) {
		return BlockStore.getBlockList().get(index);
	}
	
	/**
	 * change Block Store
	 * @param transactionList
	 */
	public static void changeBlockStore(List<Block> blockList) {
		BLOCK_STORE.clear();
		Map<Integer, Block> changedMap = IntStream.range(0, blockList.size())
												  .boxed()
												  .collect(Collectors.toMap(index -> index, index -> blockList.get(index)));
		BLOCK_STORE.putAll(changedMap);
	}
	
}
