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
	
	@Bean
    public RouterFunction<ServerResponse> jazzAlbumRouter() {
        return route(GET("/blocks/{blockIndex}").and(accept(APPLICATION_JSON)), blockHandler::findBlockByIndex)
        	   .andRoute(GET("/blocks/block/latest").and(accept(APPLICATION_JSON)), blockHandler::findLatestBlock)
        	   .andRoute(POST("/mining").and(accept(APPLICATION_JSON)), blockHandler::mining)
        	   .andRoute(GET("/addresses/address/{account}").and(accept(APPLICATION_JSON)), walletHandler::getAddress)
        	   .andRoute(GET("/addresses/coinbase").and(accept(APPLICATION_JSON)), walletHandler::getCoinbase)
        	   .andRoute(GET("/addresses/coinbase/{account}").and(accept(APPLICATION_JSON)), walletHandler::changeCoingbase)
        	   .andRoute(POST("/addresses").and(accept(APPLICATION_JSON)), walletHandler::createAddress);
    }

}
