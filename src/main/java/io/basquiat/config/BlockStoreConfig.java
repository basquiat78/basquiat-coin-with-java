package io.basquiat.config;


import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.config.DelegatingWebFluxConfiguration;

import io.basquiat.blockchain.block.domain.Block;
import io.basquiat.blockchain.block.domain.BlockStore;
import io.basquiat.blockchain.block.util.BlockUtil;
import io.basquiat.util.FileIOUtil;

/**
 * BlockStore Configuration
 * created By basquiat
 *
 */
@Component
public class BlockStoreConfig extends DelegatingWebFluxConfiguration {

	/**
	 * 서버가 뜰때 파일로부터 Block정보를 체크하고
	 * 메모리에 올린다.
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void blockStoreInitialize() {
		// 1. 최초 서버가 뜰때 특정 폴더의 파일을 본다.
		// 2. 파일이 없다면 genesis block을 생성한다.
		if(FileIOUtil.fileLength() == 0) {
			Block genesisBlock = BlockUtil.genesisBlock();
			BlockStore.addBlockStore(genesisBlock);
			FileIOUtil.writeJsonFile(genesisBlock);
		} else {
			BlockUtil.initializeBlockStore();
		}
	}

}
