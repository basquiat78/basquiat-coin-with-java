package io.basquiat.blockchain.block.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import io.basquiat.blockchain.block.BlockService;
import io.basquiat.blockchain.block.domain.Block;
import io.basquiat.blockchain.block.domain.RequestMap;
import reactor.core.publisher.Mono;

/**
 * 
 * webflux use handler instead controller
 * 
 * created by basquiat
 *
 */
@Component
public class BlockHandler {

	@Autowired
	private BlockService blockService;
	
	/**
	 * get block info by block index
	 * @param request
	 * @return Mono<ServerResponse>
	 */
	public Mono<ServerResponse> findBlockByIndex(ServerRequest request) {
		String blockIndex = request.pathVariable("blockIndex");
		Mono<Block> mono = blockService.findBlockByIndex(blockIndex);
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(mono, Block.class);
	}

	/**
	 * get latest block info
	 * @param request
	 * @return Mono<ServerResponse>
	 */
	public Mono<ServerResponse> findLatestBlock(ServerRequest request) {
		Mono<Block> mono = blockService.findLatestBlock();
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(mono, Block.class);
	}

	/**
	 * mining block
	 * @param request
	 * @return Mono<ServerResponse>
	 */
	public Mono<ServerResponse> miningBlock(ServerRequest request) {
		Mono<Block> mono = blockService.mineBlock();
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(mono, Block.class);
	}
	
	/**
	 * mining raw block with transaction info
	 * @param request
	 * @return Mono<ServerResponse>
	 */
	public Mono<ServerResponse> miningRawBlock(ServerRequest request) {
		Mono<Block> mono = request.bodyToMono(RequestMap.class).flatMap(requestMap -> blockService.mineRawBlock(requestMap.getTransactions()));
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(mono, Block.class);
	}
	
}
