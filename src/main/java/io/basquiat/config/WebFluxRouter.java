package io.basquiat.config;


import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.DelegatingWebFluxConfiguration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import io.basquiat.blockchain.block.handler.BlockHandler;
import io.basquiat.blockchain.pool.handler.TransactionPoolHandler;
import io.basquiat.blockchain.transaction.handler.TransactionHandler;
import io.basquiat.blockchain.wallet.handler.WalletHandler;

/**
 * RxJava WebFluxRouter Configuration
 * created By basquiat
 *
 */
@EnableWebFlux
@Configuration
public class WebFluxRouter extends DelegatingWebFluxConfiguration {

	@Autowired
	private BlockHandler blockHandler;
	
	@Autowired
	private WalletHandler walletHandler;
	
	@Autowired
	private TransactionHandler transactionHandler;
	
	@Autowired
	private TransactionPoolHandler transactionPoolHandler;
	
	@Bean
    public RouterFunction<ServerResponse> BlockChainRouter() {
        return route(GET("/blocks/{blockIndex}").and(accept(APPLICATION_JSON)), blockHandler::findBlockByIndex)
        	   .andRoute(GET("/blocks/block/latest").and(accept(APPLICATION_JSON)), blockHandler::findLatestBlock)
        	   .andRoute(POST("/mining/block").and(accept(APPLICATION_JSON)), blockHandler::miningBlock)
        	   .andRoute(POST("/mining/rawBlock").and(accept(APPLICATION_JSON)), blockHandler::miningRawBlock)
        	   .andRoute(GET("/addresses/address/{account}").and(accept(APPLICATION_JSON)), walletHandler::getAddress)
        	   .andRoute(GET("/addresses/coinbase").and(accept(APPLICATION_JSON)), walletHandler::getCoinbase)
        	   .andRoute(GET("/addresses/coinbase/{account}").and(accept(APPLICATION_JSON)), walletHandler::changeCoingbase)
        	   .andRoute(POST("/addresses").and(accept(APPLICATION_JSON)), walletHandler::createAddress)
        	   .andRoute(GET("/balance/address/{address}").and(accept(APPLICATION_JSON)), walletHandler::getBalanceByAddress)
        	   .andRoute(GET("/balance/account/{account}").and(accept(APPLICATION_JSON)), walletHandler::getBalanceByAccount)
        	   .andRoute(GET("/transactions/transaction/{transactionHash}").and(accept(APPLICATION_JSON)), transactionHandler::getTransaction)
        	   .andRoute(POST("/transactions/sendTransaction").and(accept(APPLICATION_JSON)), transactionHandler::sendTransaction)
        	   .andRoute(POST("/transactions/mineTransaction").and(accept(APPLICATION_JSON)), transactionHandler::mindTransaction)
        	   .andRoute(GET("/transactions/transactionPool").and(accept(APPLICATION_JSON)), transactionPoolHandler::getTransactionPool)
        	   .andRoute(GET("/utxos").and(accept(APPLICATION_JSON)), transactionHandler::getUTxOs)
        	   .andRoute(GET("/utxos/coinbase").and(accept(APPLICATION_JSON)), transactionHandler::getCoinbaseUTxOs);
    }

}
