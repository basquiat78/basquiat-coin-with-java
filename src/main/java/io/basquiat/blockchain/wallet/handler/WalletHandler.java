package io.basquiat.blockchain.wallet.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import io.basquiat.blockchain.wallet.WalletService;
import io.basquiat.blockchain.wallet.domain.Address;
import io.basquiat.blockchain.wallet.domain.CoinbaseStore;
import reactor.core.publisher.Mono;

/**
 * 
 * webflux use handler instead controller
 * 
 * created by basquiat
 *
 */
@Component
public class WalletHandler {

	@Autowired
	private WalletService walletService;
	
	/**
	 * get address by account
	 * @param request
	 * @return Mono<ServerResponse>
	 */
	public Mono<ServerResponse> getAddress(ServerRequest request) {
		String account = request.pathVariable("account");
		Mono<Address> mono = walletService.getWalletAddress(account);
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(mono, Address.class);
	}

	/**
	 * get coinbase address
	 * @param request
	 * @return Mono<ServerResponse>
	 */
	public Mono<ServerResponse> getCoinbase(ServerRequest request) {
		if(CoinbaseStore.getCoinbaseStore() == null) {
			throw new RuntimeException("not yet coinbase, create coinbase");
		}
		Mono<Address> mono = walletService.getWalletAddress(CoinbaseStore.getCoinbaseStore());
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(mono, Address.class);
	}
	
	/**
	 * change coingbase by account
	 * @param request
	 * @return Mono<ServerResponse>
	 */
	public Mono<ServerResponse> changeCoingbase(ServerRequest request) {
		String account = request.pathVariable("account");
		Mono<Address> mono = walletService.changeCoinbase(account);
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(mono, Address.class);
	}

	/**
	 * create address
	 * @param request
	 * @return Mono<ServerResponse>
	 */
	public Mono<ServerResponse> createAddress(ServerRequest request) {
		Mono<Address> mono = request.bodyToMono(Address.class).flatMap(address -> walletService.createAddress(address.getAccount()));
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(mono, Address.class);
	}
	
}
