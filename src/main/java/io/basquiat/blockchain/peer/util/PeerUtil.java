package io.basquiat.blockchain.peer.util;

import org.springframework.stereotype.Component;

import io.basquiat.blockchain.peer.domain.Peers;
import io.basquiat.util.FileIOUtil;

/**
 * PeerUtil
 * created by basquiat
 *
 */
@Component
public class PeerUtil {

	/**
	 * get peer list
	 * @return Peers
	 */
	public static Peers getPeers() {
		return FileIOUtil.readJsonPeerFile();
	}

	/**
	 * update peer list
	 * @param peers
	 */
	public static void updatePeerList(Peers peers) {
		FileIOUtil.writeJsonPeerFile(peers);
	}

}
