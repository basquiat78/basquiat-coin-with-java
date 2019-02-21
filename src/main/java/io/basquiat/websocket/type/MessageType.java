package io.basquiat.websocket.type;

/**
 * WebSocket Message Type
 * created by basquiat
 *
 */
public enum MessageType {

	/**
	 * Block List 요청 메세지
	 */
	QUERY_ALL,
	
	/**
	 * 노드의 가장 최신 블록 정보 요청
	 */
	QUERY_LATESTBLOCK,
	
	/**
	 * QUERY_LATESTBLOCK에 대한 response 메세지
	 */
	RESPONSE_LATESTBLOCK,
	
	/**
	 * QUERY_ALL에 대한 response 메세지
	 */
	RESPONSE_BLOCKCHAIN,

	/**
	 * transaction pool 정보 요청
	 */
	QUERY_TRANSACTIONPOOL,
	
	/**
	 * QUERY_TRANSACTIONPOOL에 대한 요청
	 */
	RESPONSE_TRANSACTIONPOOL,

	/**
	 * EMPTY
	 */
	EMPTY
	
}
