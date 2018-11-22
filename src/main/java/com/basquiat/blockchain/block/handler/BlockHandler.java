package com.basquiat.blockchain.block.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.basquiat.blockchain.block.BlockService;
import com.basquiat.blockchain.block.domain.Block;
import com.basquiat.blockchain.block.domain.DataVO;

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
	 * mining
	 * @param request
	 * @return Mono<ServerResponse>
	 */
	public Mono<ServerResponse> mining(ServerRequest request) {
		Mono<Block> mono = request.bodyToMono(DataVO.class).flatMap(dataVO -> blockService.mining(dataVO.getData()));
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(mono, Block.class);
	}
	
}
