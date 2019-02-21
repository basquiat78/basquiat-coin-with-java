package io.basquiat.blockchain.peer;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.basquiat.blockchain.peer.domain.Peer;
import io.basquiat.blockchain.peer.domain.Peers;
import io.basquiat.blockchain.peer.util.PeerUtil;
import io.basquiat.websocket.ProcessMessageService;
import io.basquiat.websocket.client.NettyClient;
import io.basquiat.websocket.client.domain.ClientStore;
import io.basquiat.websocket.service.MessageService;
import reactor.core.publisher.Mono;

/**
 * Peer Service
 * created by basquiat
 *
 */
@Service("peerService")
public class PeerService {

	@Autowired
	private MessageService messageService;
	
	@Autowired
	private ProcessMessageService processMessageService;
	/**
	 * Peer list 정보를 가져온다.
	 * @return Mono<Peers>
	 */
	public Mono<Peers> getPeers() {
		return Mono.just(PeerUtil.getPeers());
	}
	
	/**
	 * peer 정보를 추가한다.
	 * @param peer
	 * @return Mono<Peer>
	 */
	public Mono<Peer> addPeer(Peer peer) {
		// 1. peer list를 얻어온다.
		Peers peers = PeerUtil.getPeers();
		if(peers.getPeerList() == null) {
			peers.setPeerList(new ArrayList<Peer>());
		} else {
			Peer existPeer = peers.getPeerList().stream()
											    .filter(value -> value.getUrl().equals(peer.getUrl()) && value.getPort().equals(peer.getPort()))
											    .findAny()
											    .orElse(null);
			if(existPeer != null) {
				throw new RuntimeException("peers already exist!");
			}
		}
		
		peers.getPeerList().add(peer);
		// 2. 갱신된 peerlist를 파일에 overwrite한다.
		PeerUtil.updatePeerList(peers);
		// 3. netty client를 통해 peer의 netty server에 접속하고 실행
		NettyClient client = new NettyClient(peer.getUrl(), Integer.parseInt(peer.getPort()), messageService, processMessageService);
        client.startClient();
        // 4. 해당 클라이언트 맵에 저장한다.
        ClientStore.addChannelHandlerContextStore(client);
		return Mono.just(peer);
	}

}
