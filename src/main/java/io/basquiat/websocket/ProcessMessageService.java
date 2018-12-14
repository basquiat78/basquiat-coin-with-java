package io.basquiat.websocket;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.basquiat.blockchain.block.domain.BlockStore;
import io.basquiat.blockchain.block.util.BlockUtil;
import io.basquiat.blockchain.pool.domain.TransactionPoolStore;
import io.basquiat.util.CommonUtil;
import io.basquiat.websocket.service.vo.Message;
import io.basquiat.websocket.type.MessageType;
import io.basquiat.websocket.util.MessageUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;

/**
 * ProcessMessageService
 * client/server로 들어온 메세지를 처리하는 서비스
 * created by basquiat
 *
 */
@Service("processMessageService")
public class ProcessMessageService {

	private static final Logger LOG = LoggerFactory.getLogger(ProcessMessageService.class);
	
	/**
	 * 1. QUERY_ALL -> 블록체인을 요청하는 메세지를 받으면 노드의 blockList를 넘겨준다.
	 * 2. QUERY_LATESTBLOCK -> 최신 블록을 요청하면 최신 블록을 배열로 넘겨준다.
	 * 3. RESPONSE_BLOCKCHAIN -> blockList요청시 받은 peer의 blocklist를 받아서 처리한다.
	 * 4. QUERY_TRANSACTIONPOOL -> transaction pool 리스트를 요청한다.
	 * 5. RESPONSE_TRANSACTIONPOOL -> transaction pool 요청시 받은 transactionpool 받아서 정보를 처리한다.
	 * @param channelHandlerContext
	 * @param message
	 */
	public void process(ChannelHandlerContext channelHandlerContext, Message message) {
		switch(message.getMessageType()) {
			case QUERY_ALL:
					Message responseQUERY_ALLMessage =  Message.builder()
															   .messageType(MessageType.RESPONSE_BLOCKCHAIN)
															   .blockChain(BlockStore.getBlockList())
															   .build();
					channelHandlerContext.writeAndFlush(Unpooled.copiedBuffer(CommonUtil.convertJsonStringFromObject(responseQUERY_ALLMessage), CharsetUtil.UTF_8));
					break;
			case QUERY_LATESTBLOCK:
					Message responseQUERY_LATESTBLOCKMessage =  Message.builder()
																	   .messageType(MessageType.RESPONSE_BLOCKCHAIN)
																	   .blockChain(Stream.of(BlockUtil.latestBlockFromBlockStore()).collect(Collectors.toList()))
																	   .build();
					channelHandlerContext.writeAndFlush(Unpooled.copiedBuffer(CommonUtil.convertJsonStringFromObject(responseQUERY_LATESTBLOCKMessage), CharsetUtil.UTF_8));
					break;
			case RESPONSE_BLOCKCHAIN:
					if(message.getBlockChain().size() == 0) {
						LOG.info("Received block size is 0");
					} else {
						MessageUtil.processBlockchainResponse(message.getBlockChain());
					}
					break;
			case QUERY_TRANSACTIONPOOL:
					Message responseQUERY_TRANSACTIONPOOLKMessage =  Message.builder()
																		    .messageType(MessageType.RESPONSE_TRANSACTIONPOOL)
																		    .transactionPool(TransactionPoolStore.deepCopyFromTransactionPool())
																		    .build();
					channelHandlerContext.writeAndFlush(Unpooled.copiedBuffer(CommonUtil.convertJsonStringFromObject(responseQUERY_TRANSACTIONPOOLKMessage), CharsetUtil.UTF_8));
					break;
			case RESPONSE_TRANSACTIONPOOL:
					if(message.getTransactionPool().size() == 0) {
						LOG.info("Received transaction pool size is 0");
					} else {
						MessageUtil.processTransactionPoolResponse(message.getTransactionPool());
					}
					break;
			default: break;
			
		}
	}
	
}
