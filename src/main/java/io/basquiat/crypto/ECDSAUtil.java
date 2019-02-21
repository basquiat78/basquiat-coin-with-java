package io.basquiat.crypto;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.basquiat.util.Base58;
import io.basquiat.util.CommonUtil;

/**
 * ECDSA Util
 * created by basquiat
 *
 */
@Component
public class ECDSAUtil {
	
	static String ALGORITHM_NAME;
	
	static String CURVE_NAME;
	
	static String PROVIDER;
	
	static String SIGNATURE;
	
	@Value("${algorithm.name}")
	private void setAlgorithmName(String algorithmName) {
		ALGORITHM_NAME = algorithmName;
    }

	@Value("${algorithm.curve.name}")
	private void setCurveName(String curveName) {
		CURVE_NAME = curveName;
    }
	
	@Value("${algorithm.provider}")
	private void setProvider(String provider) {
		PROVIDER = provider;
    }
	
	@Value("${algorithm.signature}")
	private void setSignature(String signature) {
		SIGNATURE = signature;
    }
	
	/**
	 * generated KeyPair
	 * @return KeyPair
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidAlgorithmParameterException
	 * @throws NoSuchProviderException 
	 */
	public static KeyPair generateKeyPair() throws NoSuchAlgorithmException, 
												   InvalidAlgorithmParameterException, 
												   NoSuchProviderException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM_NAME, PROVIDER);
        ECGenParameterSpec ecGenParameterSpec = new ECGenParameterSpec(CURVE_NAME);
        keyPairGenerator.initialize(ecGenParameterSpec, new SecureRandom());
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        return keyPair;
    }
	
	/**
	 * get PublicKey from bytes
	 * @param publicKeyBytes
	 * @return PublicKey
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
    public static PublicKey getPublicKeyFromBytes(byte[] publicKeyBytes) throws NoSuchAlgorithmException, 
    																			InvalidKeySpecException {
        EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_NAME);
        PublicKey publicKey = keyFactory.generatePublic(encodedKeySpec);
        return publicKey;
    }

    /**
     * get PrivateKey from bytes
     * @param privateKeyBytes
     * @return PrivateKey
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static PrivateKey getPrivateKeyFromBytes(byte[] privateKeyBytes) throws NoSuchAlgorithmException, 
    																			   InvalidKeySpecException {
        EncodedKeySpec encodedKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_NAME);
        PrivateKey privateKey = keyFactory.generatePrivate(encodedKeySpec);
        return privateKey;
    }
	
    /**
     * private key로부터 public key를 유출한다.
     * @param privateKey
     * @return PublicKey
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws NoSuchProviderException
     */
    public static PublicKey getPublicKeyFromPrivteKey(String privateKey) throws NoSuchAlgorithmException, 
    															 				InvalidKeySpecException, 
    															 				NoSuchProviderException {
    	// privateKey는 base58로 encoding했기 때문에 decoding을 해줘야 한다.
    	PrivateKey PRIVATEKEY = ECDSAUtil.getPrivateKeyFromBytes(Base58.decode(privateKey));
    	KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_NAME, PROVIDER);
        ECParameterSpec ecParameterSpec = ECNamedCurveTable.getParameterSpec(CURVE_NAME);
        ECPoint ecPoint = ecParameterSpec.getG().multiply(((ECPrivateKey)PRIVATEKEY).getD());

        ECPublicKeySpec ecPublicKeySpec = new ECPublicKeySpec(ecPoint, ecParameterSpec);
        PublicKey recoverypublicKey = keyFactory.generatePublic(ecPublicKeySpec);
    	return recoverypublicKey;
    }
    
    /**
     * privatekey와 signData로 signature를 생성한다.
     * @param privateKey
     * @param signatureData
     * @return String
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws SignatureException
     * @throws UnsupportedEncodingException
     */
    public static String sign(PrivateKey privateKey, String signatureData) throws InvalidKeyException, 
		    																	  NoSuchAlgorithmException, 
		    																	  NoSuchProviderException, 
		    																	  SignatureException, 
		    																	  UnsupportedEncodingException {
    	Signature ecdsaSign = Signature.getInstance(SIGNATURE, PROVIDER);
    	ecdsaSign.initSign(privateKey);
    	ecdsaSign.update(signatureData.getBytes("UTF-8"));
    	byte[] signature = ecdsaSign.sign();
    	return Hex.toHexString(signature);
    }
    
    /**
     * signature 유효성 체크
     * signature data는 transaction의 hash값이 들어간다.
     * @param signature
     * @param publicKey
     * @param signatureData
     * @return boolean
     * @throws SignatureException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws InvalidKeyException
     * @throws UnsupportedEncodingException
     */
    public static boolean verify(String signature, PublicKey publicKey, String signatureData) throws SignatureException, 
    																								 NoSuchAlgorithmException, 
    																								 NoSuchProviderException, 
    																								 InvalidKeyException, 
    																								 UnsupportedEncodingException {
    	Signature ecdsaVerify = Signature.getInstance(SIGNATURE, PROVIDER);
    	ecdsaVerify.initVerify(publicKey);
    	ecdsaVerify.update(signatureData.getBytes("UTF-8"));
    	boolean result = ecdsaVerify.verify(CommonUtil.hexStringToByes(signature));
    	return result;
    }
    
}
