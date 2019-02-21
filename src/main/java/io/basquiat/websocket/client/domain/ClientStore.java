package io.basquiat.websocket.client.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.basquiat.websocket.client.NettyClient;
import io.basquiat.websocket.service.vo.Message;

/**
 * Block Store
 * created by basquiat
 *
 */
public class ClientStore {

	private static final Map<Integer, NettyClient> NETTYCLIENT_STORE = new ConcurrentHashMap<>();
	
	/**
	 * nettyClient을 map에 담는다.
	 * @param nettyClient
	 */
	public static void addChannelHandlerContextStore(NettyClient nettyClient) {
		NETTYCLIENT_STORE.put(ClientStore.getNettyClients().size(), nettyClient);
	}
	
	/**
	 * NETTYCLIENT_STORE Map을 List객체로 반환한다.
	 * @return List<NettyClient>
	 */
	public static List<NettyClient> getNettyClients() {
		return new ArrayList<NettyClient>(NETTYCLIENT_STORE.values());
	}
	
	/**
	 * client에 설정된 서버로 메세지를 보낸다
	 * @param message
	 */
	public static void sendToServer(Message message) {
		for(NettyClient client : ClientStore.getNettyClients()) {
			client.writeMessage(message);
		}
	}
	
}
