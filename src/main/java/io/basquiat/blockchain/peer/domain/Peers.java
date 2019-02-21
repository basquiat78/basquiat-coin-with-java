package io.basquiat.blockchain.peer.domain;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * peer 정보
 * created by basquiat
 *
 */
@Builder
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Peers {

	/**
	 * peerList
	 */
	private List<Peer> peerList;
	
}
