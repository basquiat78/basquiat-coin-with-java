package io.basquiat.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.basquiat.blockchain.block.difficulty.BlockDifficulty;
import io.basquiat.blockchain.block.domain.Block;
import io.basquiat.blockchain.block.domain.BlockStore;
import io.basquiat.blockchain.block.util.BlockUtil;
import io.basquiat.util.CommonUtil;
import io.basquiat.util.FileIOUtil;
import io.basquiat.util.Sha256Util;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class blcokTest {

	@Autowired
    private WebTestClient webTestClient;
	
	@Test
	public void test() {
//		Map<Integer, String> blockMap = new ConcurrentHashMap<>();
//		blockMap.put(0, "block0");
//		blockMap.put(4, "block4");
//		blockMap.put(5, "block5");
//		blockMap.put(1, "block1");
//		blockMap.put(3, "block3");
//		blockMap.put(2, "block2");
//		
//		Collection<String> collection = blockMap.values();
//		
//		List<String> list = new ArrayList<>(collection);
//		System.out.println(list.get(list.size()-1));
		
		BlockUtil.initializeBlockStore();
		System.out.println(BlockStore.getBlockList());
	}
	
	//@Test
	public void hexToBinarytest() {
		String hash = "0xb2f8e468d9e51c8b52658dfd19b696759a0597280dfcd1f972b549d4ade9ed6a";
	    System.out.println(BlockDifficulty.hexToBinary(hash));
	    String hexToBinary = BlockDifficulty.hexToBinary(hash);
	    System.out.println(hexToBinary.startsWith(""));
	}
	
	//@Test
	public void createBlockTest() {

		Block genesisBlock = BlockUtil.genesisBlock();
		System.out.println(genesisBlock);
		
		Block secondBlock = BlockUtil.createNextBlock(genesisBlock, "second");
		System.out.println(secondBlock);
		
		Block thirdBlock = BlockUtil.createNextBlock(secondBlock, "third");
		System.out.println(thirdBlock);
		
	}
	
	//@Test
	public void writeBlockFileTest() throws JsonProcessingException {
		Block genesisBlock = BlockUtil.genesisBlock();
		System.out.println(genesisBlock);
		FileIOUtil.writeJsonFile(genesisBlock);
		
		Block secondBlock = BlockUtil.createNextBlock(genesisBlock, "second");
		System.out.println(secondBlock);
		FileIOUtil.writeJsonFile(secondBlock);
		
		Block thirdBlock = BlockUtil.createNextBlock(secondBlock, "third");
		System.out.println(thirdBlock);
		FileIOUtil.writeJsonFile(thirdBlock);
	}

	//@Test
	public void readBlockFileTest() throws JsonProcessingException {
		Block genesisBlock1 = BlockUtil.genesisBlock();
		Block genesisBlock2 = BlockUtil.blockByIndexFromFileRepository(0);
		Block genesisBlock3 = BlockUtil.blockByIndexFromFileRepository(0);
		System.out.println(Sha256Util.SHA256(CommonUtil.convertJsonStringFromObject(genesisBlock1)));
		System.out.println(Sha256Util.SHA256(CommonUtil.convertJsonStringFromObject(genesisBlock2)));
		System.out.println(Sha256Util.SHA256(CommonUtil.convertJsonStringFromObject(genesisBlock3)));
		System.out.println(BlockUtil.blockByIndexFromFileRepository(1));
		System.out.println(BlockUtil.blockByIndexFromFileRepository(2));
		System.out.println(BlockUtil.blockByIndexFromFileRepository(3));
	}
	
	//@Test
	public void webClientTest() throws JsonProcessingException {
		ResponseSpec responseSpec = webTestClient.get()
												 .uri("/blocks/0")
												 .exchange()
												 .expectStatus().isOk();
		System.out.println(responseSpec.returnResult(Block.class));

	}
	
}
