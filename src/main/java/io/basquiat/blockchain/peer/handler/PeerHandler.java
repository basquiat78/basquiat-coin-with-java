package io.basquiat.blockchain.peer.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import io.basquiat.blockchain.peer.PeerService;
import io.basquiat.blockchain.peer.domain.Peer;
import io.basquiat.blockchain.peer.domain.Peers;
import reactor.core.publisher.Mono;

/**
 * 
 * webflux use handler instead controller
 * 
 * created by basquiat
 *
 */
@Component
public class PeerHandler {

	@Autowired
	private PeerService peerService;
	
	/**
	 * get block info by block index
	 * @param request
	 * @return Mono<ServerResponse>
	 */
	public Mono<ServerResponse> getPeers(ServerRequest request) {
		Mono<Peers> mono = peerService.getPeers();
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(mono, Peers.class);
	}

	/**
	 * get latest block info
	 * @param request
	 * @return Mono<ServerResponse>
	 */
	public Mono<ServerResponse> addPeer(ServerRequest request) {
		Mono<Peer> mono = request.bodyToMono(Peer.class).flatMap(peer -> peerService.addPeer(peer));
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(mono, Peer.class);
	}
	
}
