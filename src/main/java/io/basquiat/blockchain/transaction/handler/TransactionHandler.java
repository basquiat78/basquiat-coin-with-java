package io.basquiat.blockchain.transaction.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import io.basquiat.blockchain.block.BlockService;
import io.basquiat.blockchain.block.domain.Block;
import io.basquiat.blockchain.transaction.TransactionService;
import io.basquiat.blockchain.transaction.domain.Transaction;
import io.basquiat.blockchain.transaction.domain.TransactionRequest;
import io.basquiat.blockchain.transaction.domain.UnspentTransactionOut;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 
 * webflux use handler instead controller
 * 
 * created by basquiat
 *
 */
@Component
public class TransactionHandler {

	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private BlockService blockService;
	
	/**
	 * send Transaction
	 * transaction pool에 새로운 트랜잭션 정보를 담는다.
	 * @param request
	 * @return Mono<ServerResponse>
	 */
	public Mono<ServerResponse> sendTransaction(ServerRequest request) {
		Mono<Transaction> mono = request.bodyToMono(TransactionRequest.class).flatMap(transactionRequest -> transactionService.sendTransaction(transactionRequest));
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(mono, Transaction.class);
	}
	
	/**
	 * mineTransaction
	 * transaction 정보와 함께 block을 mining한다.
	 * @param request
	 * @return Mono<ServerResponse>
	 */
	public Mono<ServerResponse> mindTransaction(ServerRequest request) {
		Mono<Block> mono = request.bodyToMono(TransactionRequest.class).flatMap(transactionRequest -> blockService.miningBlockWithTransaction(transactionRequest));
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(mono, Block.class);
	}
	
	/**
	 * get transaction by transactionHash
	 * @param request
	 * @return Mono<ServerResponse>
	 */
	public Mono<ServerResponse> getTransaction(ServerRequest request) {
		String transactionHash = request.pathVariable("transactionHash");
		Mono<Transaction> mono = transactionService.getTransaction(transactionHash);
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(mono, Transaction.class);
	}
	
	/**
	 * get uTxOs
	 * @param request
	 * @return Mono<ServerResponse>
	 */
	public Mono<ServerResponse> getUTxOs(ServerRequest request) {
		Flux<UnspentTransactionOut> flux = transactionService.getUTxOs();
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(flux, UnspentTransactionOut.class);
	}
	
	/**
	 * get coinbase uTxOs
	 * @param request
	 * @return Mono<ServerResponse>
	 */
	public Mono<ServerResponse> getCoinbaseUTxOs(ServerRequest request) {
		Flux<UnspentTransactionOut> flux = transactionService.getCoinbaseUTxOs();
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(flux, UnspentTransactionOut.class);
	}
	
}
