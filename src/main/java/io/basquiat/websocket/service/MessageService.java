package io.basquiat.websocket.service;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import io.basquiat.blockchain.block.domain.BlockStore;
import io.basquiat.blockchain.block.util.BlockUtil;
import io.basquiat.blockchain.pool.domain.TransactionPoolStore;
import io.basquiat.websocket.service.vo.Message;
import io.basquiat.websocket.type.MessageType;

/**
 * MessageService
 * 
 * 메세지 타입에 따라 Message 객체를 리턴한다.
 * 
 * created by basquiat
 *
 */
@Service("messageService")
public class MessageService {

	/**
	 * broadcast할 Message 객체 반환
	 * @param messageType
	 * @return Message
	 */
	public Message getBroadcastMessage(MessageType messageType) {
		
		switch(messageType) {
		
			case RESPONSE_LATESTBLOCK:
				return Message.builder()
							  .messageType(MessageType.RESPONSE_BLOCKCHAIN)
							  .blockChain(Stream.of(BlockUtil.latestBlockFromBlockStore()).collect(Collectors.toList()))
							  .build();
		
			case RESPONSE_BLOCKCHAIN:
					return Message.builder()
								  .messageType(messageType)
								  .blockChain(BlockStore.getBlockList())
								  .build();
			
			case RESPONSE_TRANSACTIONPOOL:
					return Message.builder()
								  .messageType(messageType)
								  .transactionPool(TransactionPoolStore.getTransactionList())
								  .build();
					
			default: return Message.builder()
								   .messageType(MessageType.EMPTY)
								   .build();
			
		}
		
	}

	/**
	 * 요청 메세지 반환
	 * 서버로 요청하는 message 객체이므로 messageType만 넘긴다.
	 * @param messageType
	 * @return Message
	 */
	public Message getRequestMessage(MessageType messageType) {
		return Message.builder()
				  	  .messageType(messageType)
				  	  .build();
	}

}
