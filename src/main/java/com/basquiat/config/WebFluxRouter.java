package com.basquiat.config;


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

import com.basquiat.blockchain.block.handler.BlockHandler;

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
	
	@Bean
    public RouterFunction<ServerResponse> jazzAlbumRouter() {
        return route(GET("/blocks/{blockIndex}").and(accept(APPLICATION_JSON)), blockHandler::findBlockByIndex)
        	   .andRoute(GET("/blocks/block/latest").and(accept(APPLICATION_JSON)), blockHandler::findLatestBlock)
        	   .andRoute(POST("/mining").and(accept(APPLICATION_JSON)), blockHandler::mining);
    }

}
