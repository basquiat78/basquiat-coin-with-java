package io.basquiat.blockchain.block;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import io.basquiat.blockchain.block.domain.Block;
import io.basquiat.blockchain.block.domain.BlockStore;
import io.basquiat.blockchain.block.util.BlockUtil;
import io.basquiat.blockchain.block.validator.BlockValidator;
import io.basquiat.blockchain.pool.domain.TransactionPoolStore;
import io.basquiat.blockchain.pool.util.TransactionPoolUtil;
import io.basquiat.blockchain.transaction.domain.Transaction;
import io.basquiat.blockchain.transaction.domain.TransactionRequest;
import io.basquiat.blockchain.transaction.domain.UnspentTransactionOut;
import io.basquiat.blockchain.transaction.domain.UnspentTransactionOutStore;
import io.basquiat.blockchain.transaction.util.TransactionUtil;
import io.basquiat.blockchain.wallet.util.WalletUtil;
import io.basquiat.blockchain.wallet.validator.WalletValidator;
import io.basquiat.util.CommonUtil;
import io.basquiat.util.FileIOUtil;
import reactor.core.publisher.Mono;

/**
 * Blockchain Service
 * created by basquiat
 *
 */
@Service("blockchainService")
public class BlockService {

	/**
	 * find Block by blockIndex
	 * @param blockIndex
	 * @return Mono<Block>
	 */
	public Mono<Block> findBlockByIndex(String blockIndex) {
		Mono<Block> mono = null;
		if(CommonUtil.validNumber(blockIndex)) {
			mono = Mono.just(BlockUtil.blockByIndexFromBlockStore(Integer.parseInt(blockIndex)));
		}
		return  mono;  
	}
	
	/**
	 * find latest block
	 * @return Mono<Block>
	 */
	public Mono<Block> findLatestBlock() {
		return  Mono.just(BlockUtil.latestBlockFromBlockStore());
	}
	
	/**
	 * mine block
	 * @return Mono<Block>
	 */
	public Mono<Block> mineBlock() {
		return  Mono.just(this.miningBlock());
	}

	/**
	 * mine raw block
	 * with transaction info
	 * @return Mono<Block>
	 */
	public Mono<Block> mineRawBlock(List<Transaction> txList) {
		return  Mono.just(this.miningRawBlock(txList));
	}
	
	/**
	 * generate new Block with transaction
	 * 1. transaction 정보를 생성하고 해당 transaction으로 block을 생성한다.
	 *    이 때는 생성한 transaction을 pool에 등록하지 않는다. 
	 * @return Block
	 */
	public Mono<Block> miningBlockWithTransaction(TransactionRequest transactionRequest) {
		String receivedAddress = transactionRequest.getReceivedAddress();
		// 1. address validate 체크
		if(!WalletValidator.validateAddress(receivedAddress)) {
			throw new RuntimeException("invalid address");
		}
		
		Block latestBlock = BlockUtil.latestBlockFromBlockStore();
		// 2. coinbase transaction 생성
		Transaction coinbaseTransaction = TransactionUtil.createCoinbaseTransaction(WalletUtil.getCoinbaseWalletAddress(), latestBlock.getIndex() + 1);
		// 3. 정보로부터 transaction 생성
		Transaction transaction = TransactionUtil.createTransaction(receivedAddress, 
																	transactionRequest.getAmount(), 
																	WalletUtil.getCoinbasePrivateKey(), 
																	UnspentTransactionOutStore.deepCopyFromUTxOs(), 
																	TransactionPoolStore.deepCopyFromTransactionPool());
		
		// 2. coinbase transaction과 transactionPool을 concat해서 transactionList 생성
		List<Transaction> txList = Stream.concat(Stream.of(coinbaseTransaction), Stream.of(transaction)).collect( Collectors.toList());
		return Mono.just(this.miningRawBlock(txList));
	}
	
	/**
	 * generate new Block
	 * 1. coinbaseTransaction을 생성한다.
	 * 2. 블록을 마이닝할때 생성한 coinbase transaction transactin pool에 있는 transactionList를 합해서 블록에 올린다.
	 * @return Block
	 */
	private Block miningBlock() {
		// 1. coinbaseTransaction 생성
		Block latestBlock = BlockUtil.latestBlockFromBlockStore();
		Transaction coinbaseTransaction = TransactionUtil.createCoinbaseTransaction(WalletUtil.getCoinbaseWalletAddress(), latestBlock.getIndex() + 1);
		// 2. coinbase transaction과 transactionPool을 concat해서 transactionList 생성
		List<Transaction> txList = Stream.concat(Stream.of(coinbaseTransaction), TransactionPoolStore.deepCopyFromTransactionPool().stream()).collect( Collectors.toList());
		return this.miningRawBlock(txList);
	}
	
	/**
	 * generate new raw Block
	 * 1. 넘겨받은 txlist로 block생성
	 * 2. utxos를 새로 갱신해야한다.
	 * 3. transactionpool를 새로 갱신해야한다.
	 * @return Block
	 */
	private Block miningRawBlock(List<Transaction> txList) {
		Block newBlock = BlockUtil.createRawNextBlock(txList);
		if(BlockValidator.validateNewBlock(newBlock, BlockUtil.latestBlockFromBlockStore())) {
			// 1. create changeuTxos for update uTxOs
			List<UnspentTransactionOut> changeUTxOs = TransactionUtil.processTransactions(newBlock.getTransactions(), 
																						  UnspentTransactionOutStore.deepCopyFromUTxOs(), 
																						  newBlock.getIndex());
			// 2. changeUTxOs가 null이면 유효성 체크 실패
			if(changeUTxOs == null) {
				return null;
			} else {
				// 3. blockStore newBlock 추가
				BlockStore.addBlockStore(newBlock);
				FileIOUtil.writeJsonBlockFile(newBlock);
				// 4. uTxOs update
				UnspentTransactionOutStore.changeUTxOStore(changeUTxOs);
				// 5. transaction pool 
				TransactionPoolUtil.upadateTransactionPool(UnspentTransactionOutStore.getUTxOs());
				//TODO
				// websocket을 통해 새로운 블록을 전파해야한다.}
			}
		} else {
			return null;
		}
		return newBlock;
	}
	
}
