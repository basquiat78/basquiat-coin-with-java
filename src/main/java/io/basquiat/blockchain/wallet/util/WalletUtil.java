package io.basquiat.blockchain.wallet.util;

import java.math.BigDecimal;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.basquiat.blockchain.transaction.domain.UnspentTransactionOut;
import io.basquiat.blockchain.transaction.domain.UnspentTransactionOutStore;
import io.basquiat.blockchain.wallet.domain.CoinbaseStore;
import io.basquiat.blockchain.wallet.domain.Wallet;
import io.basquiat.crypto.ECDSAUtil;
import io.basquiat.util.Base58;
import io.basquiat.util.CommonUtil;
import io.basquiat.util.FileIOUtil;

/**
 * Wallet Util
 * created by basquiat
 *
 */
@Component
public class WalletUtil {
	
	private static final Logger LOG = LoggerFactory.getLogger(WalletUtil.class);

	/**
	 * privateKey를 생성한다.
	 * PrivateKey 객체로부터 생성된 byte[]정보를 base58로 최종 인코딩한 값이다.
	 * @return String
	 */
	public static String generateWalletPrivateKey() {
		String walletPrivateKey = "";
		try {
			KeyPair keyPair = ECDSAUtil.generateKeyPair();
			PrivateKey privateKey = keyPair.getPrivate();
			walletPrivateKey = Base58.encode(privateKey.getEncoded());
		} catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | NoSuchProviderException e) {
			LOG.info("when generate Wallet PrivateKey, error!");
			e.printStackTrace();
		}
		return walletPrivateKey;
	}
	
	/**
	 * private키를 담은 정보를 파일로 저장한다.
	 * 생성하고 CoinBaseStore로부터 coinbase가 세팅되어 있는지 확인하고 없다면
	 * 해당 account를 coinbase로 세팅한다.
	 */
	public static void writeWalletPrivateKey(String account) {
		if(FileIOUtil.hasWalletFile(account)) {
			throw new RuntimeException("already exist account");
		}
		// privateKey를 생성한다.
		String privateKey = WalletUtil.generateWalletPrivateKey();
		long createDttm = CommonUtil.convertUnixTime(new Date());
		FileIOUtil.writeJsonWalletFile(Wallet.builder().account(account).privateKey(privateKey).createDttm(createDttm).build());
		LOG.info("Create Wallet File!");
		// coinbaseStore의 값이 null이면 최초로 생성하는 지갑이다.
		// 해당 노드의 coinbase로 최초로 생성한 지갑 주소를 coinbase로 설정한다.
		if(CoinbaseStore.getCoinbase() == null) {
			CoinbaseStore.setCoinbase(account);
			FileIOUtil.writeCoinbaseFile(account);
		}
	}
	
	/**
	 * 파일의 privateKey로부터 address를 생성한다.
	 * publicKey에서 나온 최종 값을 base58로 인코딩하고 Sha256으로 감싸서 반환한다.
	 * @return String
	 */
	public static String getWalletAddress(String account) {
		String privateKey = WalletUtil.getPrivateKey(account);
		String address = "";
		try {
			PublicKey publicKey = ECDSAUtil.getPublicKeyFromPrivteKey(privateKey);
			address = Base58.encode(publicKey.getEncoded());
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// coinbaseStore의 값이 null이면 최초로 생성하는 지갑이다.
		// 해당 노드의 coinbase로 최초로 생성한 지갑 주소를 coinbase로 설정한다.
		if(CoinbaseStore.getCoinbase() == null) {
			CoinbaseStore.setCoinbase(account);
		}
		return address;
	}
	
	/**
	 * coinbase wallet address을 반환한다.
	 * @return String
	 */
	public static String getCoinbaseWalletAddress() {
		return WalletUtil.getWalletAddress(CoinbaseStore.getCoinbase());
	}

	/**
	 * account 정보로 privateKey 얻기
	 * @param account
	 * @return String
	 */
	public static String getPrivateKey(String account) {
		Wallet wallet = FileIOUtil.readJsonWalletFile(account);
		return wallet.getPrivateKey();
	}
	
	/**
	 * account 정보로 privateKey 얻기
	 * @param account
	 * @return String
	 */
	public static String getCoinbasePrivateKey() {
		return WalletUtil.getPrivateKey(CoinbaseStore.getCoinbase());
	}
	
	/**
	 * 주소에 대한 잔고는 아직 쓰이지 않은 uxtos의 리스트에서 해당 주소의 모든 잔고를 합한 값이 된다.
	 * @param address
	 * @param uTxOs
	 * @return BigDecimal
	 */
	public static BigDecimal getBalanceByAddress(String address) {
		return UnspentTransactionOutStore.deepCopyFromUTxOs().stream()
													     	 .filter(uTxO -> address.equals(uTxO.getAddress()))
													     	 .map(UnspentTransactionOut::getAmount)
													     	 .reduce(BigDecimal.ZERO, BigDecimal::add);
	}
	
	/**
	 * 주소에 대한 잔고는 아직 쓰이지 않은 uxtos의 리스트에서 해당 주소의 모든 잔고를 합한 값이 된다.
	 * account로 wallet file로부터 privateKey를 구하고 address를 추출해 잔고를 계산한다.
	 * @param address
	 * @param uTxOs
	 * @return BigDecimal
	 */
	public static BigDecimal getBalanceByAccount(String account) {
		// account check
		if(!FileIOUtil.hasWalletFile(account)) {
			throw new RuntimeException("doens't exist account!");
		}
		String address =  WalletUtil.getWalletAddress(account);
		return WalletUtil.getBalanceByAddress(address);
	}

}
