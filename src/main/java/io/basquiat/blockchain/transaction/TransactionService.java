package io.basquiat.blockchain.transaction;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.basquiat.blockchain.block.domain.BlockStore;
import io.basquiat.blockchain.pool.domain.TransactionPoolStore;
import io.basquiat.blockchain.pool.util.TransactionPoolUtil;
import io.basquiat.blockchain.transaction.domain.Transaction;
import io.basquiat.blockchain.transaction.domain.TransactionRequest;
import io.basquiat.blockchain.transaction.domain.UnspentTransactionOut;
import io.basquiat.blockchain.transaction.domain.UnspentTransactionOutStore;
import io.basquiat.blockchain.transaction.util.TransactionUtil;
import io.basquiat.blockchain.wallet.domain.CoinbaseStore;
import io.basquiat.blockchain.wallet.util.WalletUtil;
import io.basquiat.blockchain.wallet.validator.WalletValidator;
import io.basquiat.websocket.BroadcastService;
import io.basquiat.websocket.type.MessageType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Transaction Service
 * created by basquiat
 *
 */
@Service("transactionService")
public class TransactionService {

	@Autowired
	private BroadcastService broadcastService;
	
	/**
	 * send transaction
	 * 1. transactionRequest의 received address와 amount, account를 체크한다.
	 * 2. account의 경우에는 해당 node에 주소가 있는지 확인을 먼저 해야한다.
	 * 
	 * 4. transactionPool, uTxOs와 나머지 정보를 통해 new transaction을 생성한다.
	 * 5. 최종적으로 모든 조건이 맞으면 transaction pool에 추가한다.
	 * 6. 연결된 socket client/server로 broadcasting을 한다.
	 * @param transactionRequest
	 * @return Mono<Transaction>
	 */
	public Mono<Transaction> sendTransaction(TransactionRequest transactionRequest) {
		// 1. received address 유효성 체크
		String receivedAddress = transactionRequest.getReceivedAddress();
		if(!WalletValidator.validateAddress(receivedAddress)) {
			throw new RuntimeException("receivedAddress is " + receivedAddress + ", this address is invalid key length");
		}
		// account에 해당하는 balance를 체크해야한다. 이 잔액이 amount보다 크거나 같아야 한다.
		// 만일 작다면 메세지를 보낸다.
		if(!WalletValidator.checkAmount(CoinbaseStore.getCoinbase(), transactionRequest.getAmount())) {
			throw new RuntimeException("not enough account balance!");
		}
		
		List<UnspentTransactionOut> deepCopyUTxOs = UnspentTransactionOutStore.deepCopyFromUTxOs();
		
		// 4. create new transaction
		// deep copy uTxOs, deep copy transactionpool
		// 트랜잭션이 생성될 당시의 정보를 deepcopy하므로서 본래의 정보와의 단절시켜야하기 때문이다.
		Transaction transaction = TransactionUtil.createTransaction(receivedAddress, 
																	transactionRequest.getAmount(), 
																	WalletUtil.getCoinbasePrivateKey(), 
																	deepCopyUTxOs, 
																	TransactionPoolStore.deepCopyFromTransactionPool()
																	);
		//5. transaction pool에 추가한다.	
		TransactionPoolUtil.addToTransactionPool(transaction, deepCopyUTxOs);
		//6. transactionPool 정보를 브로드캐스팅한다.
		broadcastService.broadcast(MessageType.RESPONSE_TRANSACTIONPOOL);
	    return Mono.just(transaction);
	}

	/**
	 * get transaction by transactionHash
	 * @param transactionHash
	 * @return Mono<Transaction>
	 */
	public Mono<Transaction> getTransaction(String transactionHash) {
		
		Transaction transaction = BlockStore.getBlockList().stream().map(block -> block.getTransactions())
												   		   .flatMap(txList -> {
												   			   					return txList.stream()
												   			   							 	 .filter(tx -> transactionHash.equals(tx.getTxHash())); 
												   			   				  })
												   		   .findFirst().get();
		return Mono.just(transaction);
	}
	
	/**
	 * get total uTxOs
	 * @return Flux<UnspentTransactionOut>
	 */
	public Flux<UnspentTransactionOut> getUTxOs() {
		return Mono.just(UnspentTransactionOutStore.deepCopyFromUTxOs()).flatMapMany(Flux::fromIterable);

	}
	
	/**
	 * get coinbase uTxOs
	 * @return Flux<UnspentTransactionOut>
	 */
	public Flux<UnspentTransactionOut> getCoinbaseUTxOs() {
		
		String coinbaseAddress = WalletUtil.getCoinbaseWalletAddress();
		List<UnspentTransactionOut> uTxOs = UnspentTransactionOutStore.deepCopyFromUTxOs().stream()
																						  .filter(uTxO -> coinbaseAddress.equals(uTxO.getAddress()))
																						  .collect(Collectors.toList());
		return Mono.just(uTxOs).flatMapMany(Flux::fromIterable);
	}
	
}
