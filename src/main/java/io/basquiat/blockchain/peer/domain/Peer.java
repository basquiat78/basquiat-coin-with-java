package io.basquiat.blockchain.peer.domain;

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
public class Peer {

	/**
	 * url
	 */
	private String url;
	
	/**
	 * port
	 */
	private String port;
	
}
