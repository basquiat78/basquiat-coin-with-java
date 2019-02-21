package io.basquiat.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.basquiat.websocket.client.domain.ClientStore;
import io.basquiat.websocket.service.MessageService;
import io.basquiat.websocket.type.MessageType;

/**
 * RequestService
 * 
 * netty server로 메세지를 요청한다.
 * created by basquiat
 *
 */
@Service("requestService")
public class RequestService {

	@Autowired
	private MessageService messageService;
	
	/**
	 * server로 request message를 날린다.
	 * @param messageType
	 */
	public void request(MessageType messageType) {
		ClientStore.sendToServer(messageService.getRequestMessage(messageType));
	}
	
}
