package io.basquiat.blockchain.wallet;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import org.springframework.stereotype.Service;

import io.basquiat.blockchain.wallet.domain.Address;
import io.basquiat.blockchain.wallet.domain.CoinbaseStore;
import io.basquiat.blockchain.wallet.domain.Wallet;
import io.basquiat.blockchain.wallet.util.WalletUtil;
import io.basquiat.crypto.ECDSAUtil;
import io.basquiat.util.Base58;
import io.basquiat.util.FileIOUtil;
import reactor.core.publisher.Mono;

/**
 * Wallet Service
 * created by basquiat
 *
 */
@Service("walletService")
public class WalletService {

	/**
	 * changeCoinbase
	 * @return Mono<Address>
	 */
	public Mono<Address> changeCoinbase(String account) {
		// accout로 생성된 wallet이 노드에 있는 지 확인
		// 없다면 throws를 던진다.
		if(!FileIOUtil.hasWalletFile(account)) {
			throw new RuntimeException("Not Found Account!");
		}
		// 1. 이전에 있던 file를 삭제한다.
		FileIOUtil.deleteCoinbaseFile(CoinbaseStore.getCoinbaseStore());
		// 2. 새로운 account의 파일명의 빈 파일을 생성한다.
		FileIOUtil.writeCoinbaseFile(account);
		
		Wallet wallet = FileIOUtil.readJsonWalletFile(account);
		String address = "";
		try {
			PublicKey publicKey = ECDSAUtil.getPublicKeyFromPrivteKey(wallet.getPrivateKey());
			address = Base58.encode(publicKey.getEncoded());
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 주소까지 생성되면 새로운 account를 coinbase store에 저장한다.
		CoinbaseStore.setCoinbaseStore(account);
		return  Mono.just(Address.builder().account(account).address(address).build());
	}

	/**
	 * account에 대한 주소 정보 가져오기
	 * @param account
	 * @return Mono<Address>
	 */
	public Mono<Address> getWalletAddress(String account) {
		// accout로 생성된 wallet이 노드에 있는 지 확인
		// 없다면 throws를 던진다.
		if(!FileIOUtil.hasWalletFile(account)) {
			throw new RuntimeException("Not Found Account!");
		}
		String address = WalletUtil.getWalletAddress(account);
		return  Mono.just(Address.builder().account(account).address(address).build());
	}

	/**
	 * private key file을 생성하고 생성한 파일로부터 public key를 생성한다.
	 * @param account
	 * @return Mono<Address>
	 */
	public Mono<Address> createAddress(String account) {
		// account로 private key를 파일로 저장한다.
		// 이미 존재한다면 에러
		WalletUtil.writeWalletPrivateKey(account);
		String address = WalletUtil.getWalletAddress(account);
		return  Mono.just(Address.builder().account(account).address(address).build());
	}
	
}
