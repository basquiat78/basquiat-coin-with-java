package io.basquiat.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.basquiat.websocket.client.domain.ClientStore;
import io.basquiat.websocket.server.domain.ServerSideStore;
import io.basquiat.websocket.service.MessageService;
import io.basquiat.websocket.type.MessageType;

/**
 * BroadcastService
 * created by basquiat
 *
 */
@Service("broadcastService")
public class BroadcastService {

	@Autowired
	private MessageService messageService;
	
	/**
	 * broadcast client/server
	 * @param messageType
	 */
	public void broadcast(MessageType messageType) {
		ServerSideStore.sendToServer(messageService.getBroadcastMessage(messageType));
		ClientStore.sendToServer(messageService.getBroadcastMessage(messageType));
	}
	
}
