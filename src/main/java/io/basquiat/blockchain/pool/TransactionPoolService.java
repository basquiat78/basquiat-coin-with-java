package io.basquiat.blockchain.pool;

import org.springframework.stereotype.Service;

import io.basquiat.blockchain.pool.domain.TransactionPoolStore;
import io.basquiat.blockchain.transaction.domain.Transaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Transaction Service
 * created by basquiat
 *
 */
@Service("transactionPoolService")
public class TransactionPoolService {

	/**
	 * get TransactionPool
	 * @return Flux<Transaction>
	 */
	public Flux<Transaction> getTransactionPool() {
	    return Mono.just(TransactionPoolStore.getTransactionList()).flatMapMany(Flux::fromIterable);
	}

}
