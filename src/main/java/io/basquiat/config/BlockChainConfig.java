package io.basquiat.config;


import java.security.Security;
import java.util.List;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import io.basquiat.blockchain.block.domain.Block;
import io.basquiat.blockchain.block.domain.BlockStore;
import io.basquiat.blockchain.block.util.BlockUtil;
import io.basquiat.blockchain.transaction.domain.UnspentTransactionOut;
import io.basquiat.blockchain.transaction.domain.UnspentTransactionOutStore;
import io.basquiat.blockchain.transaction.util.TransactionUtil;
import io.basquiat.blockchain.wallet.domain.CoinbaseStore;
import io.basquiat.util.FileIOUtil;

/**
 * BlockStore Configuration
 * created By basquiat
 *
 */
@Component
public class BlockChainConfig {

	private static final Logger LOG = LoggerFactory.getLogger(BlockChainConfig.class);
	
	/**
	 * 서버가 뜰때 파일로부터 Block정보를 체크하고 coinbase 파일을 읽어 coinbase를 세팅한다.
	 * 메모리에 올린다.
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void blockStoreInitialize() {
		// 암호화 provider 세팅
		Security.addProvider(new BouncyCastleProvider());
		// 1. coinbase 설정
		String coinbaseAccount = FileIOUtil.getCoinbaseAccount();
		// 만일 null이라면 주소가 생성되지 않았기 때문에 패스
		// 존재한다면 이 account를 초기 coinbase로 정한다.
		if(coinbaseAccount == null) {
			//null이라면 coinbase account를 생성한다.
			LOG.info("you must create coinbase account");
		} else {
			CoinbaseStore.setCoinbase(coinbaseAccount);
		}
		
		// 2. genesisBlock 생성 또는 초기화
		if(FileIOUtil.blockFileLength() == 0) {
			Block genesisBlock = BlockUtil.genesisBlock();
			LOG.info("generate Genesis Block!");
			BlockStore.addBlockStore(genesisBlock);
			FileIOUtil.writeJsonBlockFile(genesisBlock);
		} else {
			BlockUtil.initializeBlockStore();
			LOG.info("BlockStore Initialize!");
		}
		
		// 3. UnspentTransactionOut list 초기화
		if(BlockStore.getBlockList().size() > 0) {
			Integer index = 0;
			for(Block block : BlockStore.getBlockList()) {
				// 최초의 genesis block은 제외한다
				if(index > 0) {
					List<UnspentTransactionOut> changeUTxOs = TransactionUtil.processTransactions(block.getTransactions(), UnspentTransactionOutStore.getUTxOs(), index);
					UnspentTransactionOutStore.changeUTxOStore(changeUTxOs);
				}
				index++;
			}
		}
	}

}
