package io.basquiat.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.bouncycastle.util.encoders.Hex;
import org.springframework.stereotype.Component;

/**
 * simple Sha256 Decrption
 * created by basquiat
 *
 */
@Component
public class Sha256Util {

	/**
	 * encryt sha256
	 * @param target
	 * @return String
	 */
	public static String SHA256(String target) {
		String SHA256Hex = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(target.getBytes(StandardCharsets.UTF_8));
			SHA256Hex = new String(Hex.encode(hash));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
        return "0x" + SHA256Hex;
	}
	
}
