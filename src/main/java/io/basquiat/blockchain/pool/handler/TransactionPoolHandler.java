package io.basquiat.blockchain.pool.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import io.basquiat.blockchain.pool.TransactionPoolService;
import io.basquiat.blockchain.transaction.domain.Transaction;
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
public class TransactionPoolHandler {

	@Autowired
	private TransactionPoolService transactionPoolService;
	
	/**
	 * get TransactionPool
	 * @param request
	 * @return Mono<ServerResponse>
	 */
	public Mono<ServerResponse> getTransactionPool(ServerRequest request) {
		Flux<Transaction> flux = transactionPoolService.getTransactionPool();
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(flux, Transaction.class);
	}
	
}
