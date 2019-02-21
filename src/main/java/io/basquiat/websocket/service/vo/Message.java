package io.basquiat.websocket.service.vo;

import java.util.List;

import io.basquiat.blockchain.block.domain.Block;
import io.basquiat.blockchain.transaction.domain.Transaction;
import io.basquiat.websocket.type.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Message
 * created by basquiat
 *
 */
@Builder
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Message {

	/**
	 * message type
	 * reference MessageType.class
	 */
	private MessageType messageType;
	
	/**
	 * blockChain
	 */
	private List<Block> blockChain;
	
	/**
	 * transactionPool
	 */
	private List<Transaction> transactionPool;
	
}
