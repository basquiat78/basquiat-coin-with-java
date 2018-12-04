package io.basquiat.blockchain.wallet.validator;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.basquiat.blockchain.wallet.util.WalletUtil;
import io.basquiat.util.FileIOUtil;

/**
 * Block Validator
 * created by basquiat
 *
 */
@Component
public class WalletValidator {
	
	private static final Logger LOG = LoggerFactory.getLogger(WalletValidator.class);
	
	/**
	 * 주소 체계가 확실히 잡힐 때 확장을 해야하지만 여기서는 간략하게 길이만 체크한다.
	 * @param address
	 * @return boolean
	 */
	public static boolean validateAddress(String address) {
		boolean isValid = true;
		if(address.length() != 120) {
	        LOG.info("address is " + address + ", this address is invalid key length");
	        return false;
	    }
	    return isValid;
	}
	
	/**
	 * account가 노드에 존재하는지 확인한다.
	 * @param account
	 * @return boolean
	 */
	public static boolean validateAccount(String account) {
	    return FileIOUtil.hasWalletFile(account);
	}

	/**
	 * account의 발란스가 보내는 수량보다 적어도 같거나 많아야 한다.
	 * @param account
	 * @param amount
	 * @return boolean
	 */
	public static boolean checkAmount(String account, BigDecimal amount) {
		BigDecimal accountBalance = WalletUtil.getBalanceByAccount(account);
		return accountBalance.compareTo(amount) == 0 || accountBalance.compareTo(amount) == 1;
	}

}
