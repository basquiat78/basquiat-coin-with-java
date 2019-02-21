package io.basquiat.websocket.util;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.basquiat.blockchain.block.domain.Block;
import io.basquiat.blockchain.block.domain.BlockStore;
import io.basquiat.blockchain.block.util.BlockUtil;
import io.basquiat.blockchain.block.validator.BlockValidator;
import io.basquiat.blockchain.pool.util.TransactionPoolUtil;
import io.basquiat.blockchain.transaction.domain.Transaction;
import io.basquiat.blockchain.transaction.domain.UnspentTransactionOut;
import io.basquiat.blockchain.transaction.domain.UnspentTransactionOutStore;
import io.basquiat.blockchain.transaction.util.TransactionUtil;
import io.basquiat.util.FileIOUtil;
import io.basquiat.websocket.BroadcastService;
import io.basquiat.websocket.client.domain.ClientStore;
import io.basquiat.websocket.service.MessageService;
import io.basquiat.websocket.type.MessageType;

/**
 * BlockUtil
 * created by basquiat
 *
 */
@Component
public class MessageUtil {

	private static final Logger LOG = LoggerFactory.getLogger(MessageUtil.class);
	
	private static MessageService MESSAGESERVICE;
	
	private static BroadcastService BROADCASTSERVICE;

    @Autowired
    public void setMessageService(MessageService messageService){
    	MESSAGESERVICE = messageService;
    }
	
    @Autowired
    public void setBroadcastService(BroadcastService broadcastService){
    	BROADCASTSERVICE = broadcastService;
    }
    
	/**
	 * peer로부터 blockList를 받으면 현재 노드의 블록체인의 정보를 변경하고 그에 따른 브로드캐스팅을 한다.
	 * @param receivedBlockList
	 */
	public static void processBlockchainResponse(List<Block> receivedBlockList) {
		if(receivedBlockList.size() == 0) {
			LOG.info("received block chain size is 0");
	    }
		
		// 1. 받은 block list에서 latestBlock를 추출한다.
		Block latestFromReceivedBlockList = receivedBlockList.get(receivedBlockList.size() - 1);
	    // 2. 현재 노드의 latestBlock을 가져온다.
		Block latestFromCurrentNode = BlockUtil.latestBlockFromBlockStore();

		// 받은 최신 블록의 index가 현재 노드의 최신 블록의 index보다 크다면
		if(latestFromReceivedBlockList.getIndex() > latestFromCurrentNode.getIndex()) {
	        // 받은 최신 블록의 이전 해쉬가 현재 노드의 최신 블록의 해쉬와 같다면 
			// 현재 노드의 블록 정보가 하나가 빈다는 것을 의미한다.
			// 따라서 현재 노드에 받은 최신 블록을 현재 노드의 블록 리스트에 추가한다.
			if( latestFromCurrentNode.getHash().equals(latestFromReceivedBlockList.getPreviousHash()) ) {
				
				if(BlockValidator.validateNewBlock(latestFromReceivedBlockList, latestFromCurrentNode)) {
					// create changeuTxos for update uTxOs
					List<UnspentTransactionOut> changeUTxOs = TransactionUtil.processTransactions(latestFromReceivedBlockList.getTransactions(), 
																								  UnspentTransactionOutStore.deepCopyFromUTxOs(), 
																								  latestFromReceivedBlockList.getIndex());
					//  changeUTxOs가 null이면 유효성 체크 실패
					if(changeUTxOs == null) {
						LOG.info("block is invalid");
					} else {
						// blockStore latestFromReceivedBlockList 추가
						BlockStore.addBlockStore(latestFromReceivedBlockList);
						FileIOUtil.writeJsonBlockFile(latestFromReceivedBlockList);
						//  uTxOs update
						UnspentTransactionOutStore.changeUTxOStore(changeUTxOs);
						//  transaction pool 
						TransactionPoolUtil.upadateTransactionPool(UnspentTransactionOutStore.getUTxOs());
						// 현재 노드의 latestBlock를 broadcast한다.
						BROADCASTSERVICE.broadcast(MessageType.RESPONSE_LATESTBLOCK);
					}
				} 
			} else if(receivedBlockList.size() == 1) {
					// peer를 통해서  타겟 서버에 접속할때 보내는 메세지가 QUERY_LATESTBLOCK라면 넘겨받는 리스트의 사이즈는 하나이다.
					// 따라서 타겟 서버로부터 타겟 peer의 전체 블록 정보를 요청하는 메세지를 날린다.
		        	LOG.info("request Blockchain to server");
		            // request to server QUERY_ALL
		        	ClientStore.sendToServer(MESSAGESERVICE.getRequestMessage(MessageType.QUERY_ALL));
	        } else {
	        	LOG.info("current node will change blockchain to received blockchain");
	            BlockUtil.changeBlockchain(receivedBlockList);
	            // 현재 노드의 latestBlock를 broadcast한다.
				BROADCASTSERVICE.broadcast(MessageType.RESPONSE_LATESTBLOCK);
	        }
		} else {
	    	LOG.info("need not any action!!");
	    }
	}

	/**
	 * peer로부터 받은 transaction pool을 죄회하며 transaction pool에 넣고 브로드 캐스팅을 한다.
	 * @param transactionPool
	 */
	public static void processTransactionPoolResponse(List<Transaction> transactionPool) {
		transactionPool.stream().forEach(transaction -> {
														try {
															TransactionPoolUtil.addToTransactionPool(transaction, UnspentTransactionOutStore.deepCopyFromUTxOs());
															BROADCASTSERVICE.broadcast(MessageType.RESPONSE_TRANSACTIONPOOL);
														} catch(Exception e) {
															LOG.info(e.getMessage());
														}
		});
	}
	
}
