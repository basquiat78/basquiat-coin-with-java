package io.basquiat.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.basquiat.blockchain.block.domain.Block;
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

		Integer a = 1;
		Integer b = 2;
		System.out.println(!(a == b));

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
