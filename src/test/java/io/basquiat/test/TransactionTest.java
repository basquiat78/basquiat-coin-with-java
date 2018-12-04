package io.basquiat.test;

import java.math.BigDecimal;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.basquiat.blockchain.transaction.domain.Transaction;
import io.basquiat.blockchain.transaction.domain.TransactionIn;
import io.basquiat.blockchain.transaction.domain.TransactionOut;
import io.basquiat.blockchain.transaction.domain.UnspentTransactionOut;
import io.basquiat.crypto.ECDSAUtil;
import io.basquiat.util.Base58;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransactionTest {

	//@Test
	public void test() {
		
//		BigDecimal a = BigDecimal.valueOf(1.0);
//		BigDecimal b = BigDecimal.valueOf(1);
//		BigDecimal c = BigDecimal.valueOf(1.1);
//		System.out.println(a.compareTo(b) == 0);
//		System.out.println(b.compareTo(c) != 0);
//		System.out.println(a.compareTo(c) != 0);
//		hasValidTxIns: boolean = transaction.txIns
//		        .map((txIn) => validateTxIn(txIn, transaction, aUnspentTxOuts))
//		        .reduce((a, b) => a && b, true)

		TransactionIn tx0 = TransactionIn.builder().txOutHash("txOutHash0").txOutIndex(0).build();
		TransactionIn tx1 = TransactionIn.builder().txOutHash("txOutHash1").txOutIndex(1).build();
		List<TransactionIn> a = Stream.of(tx0, tx1).collect(Collectors.toList());
		System.out.println(a);
		
		Transaction tx = Transaction.builder().txIns(a).build();
		System.out.println(tx);
		AtomicInteger index = new AtomicInteger();
		a = a.stream().map(mapper -> {
										String signature = "signed_" + index.incrementAndGet();
										return TransactionIn.builder()
													 .txOutHash(mapper.getTxOutHash())
													 .txOutIndex(mapper.getTxOutIndex())
													 .signature(signature)
													 .build();
									}
								 )
				  .collect(Collectors.toList());
		tx.setTxIns(a);
		System.out.println(tx);
	}
	
	//@Test
	public void ECDSAtest() throws Exception {
	        KeyPair keyPair = ECDSAUtil.generateKeyPair();
	        PublicKey publicKey = keyPair.getPublic();
	        PrivateKey privateKey = keyPair.getPrivate();
	        byte[] publicKeyByte = publicKey.getEncoded();
	        byte[] privateKeyByte = privateKey.getEncoded();
	        String pubkey = Base58.encode(publicKeyByte);
	        String privKey = Base58.encode(privateKeyByte);
	        
	        System.out.println(pubkey);
	        System.out.println(pubkey.length());
	        System.out.println(privKey);
	        System.out.println(privKey.length());
//	        String pub = "PZ8Tyr4Nx8MHsRAGMpZmZ6TWY63dXWSCyDAqfoAYPVP72BMyAYBrRRFBkanjZvZJ4XfA7CPhh7AqwLLEgfZASShmJwqFYJKq47bqSKd162DSCM4JqkpQ6t6K";
//	        String pv = "3hupFSQNWwiJuQNc68HiWzPgyNpQA2yy9iiwhytMS7rZyWkTjRAZzkVvhAr1qMSpfwo1CN4XyTrBrfkemhJoEBmroYwbNNMfAA4NVkxmz1dCFkjQSJwKAcBnjwFqwCVA8hyxzjF5R3KEu1by6hmmEMVkdDWrjJ86w3HVfjVCSr3Ljhizf343jcwmynqbRYyXnrN5d";
	        //	        PrivateKey privateKey = ECDSAUtil.getPrivateKeyFromBytes(Base58.decode(pv));
//	        System.out.println(privateKey);
//	        String signatureData = "test1234516";
//	        String signature = "3045022041926ce912c29cbe71ce17855468413d39110ac2b032e5b496c1450893d29277022100f09adea52f33dad85925f9427992be0b0b7cf6be5fc0f398e8c49376946e3abe";
//	        //System.out.println(ECDSAUtil.sign(privateKey, signatureData));
//	        System.out.println(ECDSAUtil.verify(signature, ECDSAUtil.getPublicKeyFromBytes(Base58.decode(pub)), signatureData));
	        
	        //	        String s = "30818d020100301006072a8648ce3d020106052b8104000a047630740201010420f3b1fb449b69e2cdcd6057623662882e386b1203120727af3fe0cd71aece83b2a00706052b8104000aa144034200045c984e3d0d54e3f80c7d3500089d42ff91964162480a94ac3cad6b664e60f25ff9d6bad8881b54579f3cd4356ca6e942629f2a81abf4cbd8cdbb1f12dfbdd743";
//	        String privateKey = "3hupFSQNWwiJuQNc68HiWzPgyNpQA2yy9iiwhytMS7rZygp9eg8zuw1zPbX9QtZGGu7Q3B6utcb1Bx1xoJRTSMKwW11cfYAQSYnVt8gw9LGMzcD63hWZ9js6h2xEtMx9L3et8CgicrewF2UqNhens2MFDtRnjH3dfFmr8HsmwkgWqx5Cnq97C8NXQwbtvWyBmLzfk";
//	        System.out.println(Base58.encode(CommonUtil.hexStringToByes(s)));
//	        
//	        //String s = "test";
////	        String base = Base58.encode(CommonUtil.hexStringToByes(s));
////	        System.out.println(base);
////	        System.out.println(Hex.encodeHexString( Base58.decode(base)));
//	        System.out.println(ECDSAUtil.getPrivateKeyFromBytes(Base58.decode(privateKey)));
        //int len = s.length();
//        byte[] data = new byte[len / 2];
//        for (int i = 0; i < len; i += 2) {
//            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
//                    + Character.digit(s.charAt(i+1), 16));
//        }
//        
//       	System.out.println(ECDSAUtil.getPublicKeyFromBytes(data));
        
//        System.out.println("Public Key: "+ publicKey  + "\n\n");
//
//
//        Signature signature = Signature.getInstance("ECDSA", "BC");
//        signature.initSign(privateKey, new SecureRandom());
//
//        byte[] hashedMessage = sha256Digest.digest("This is the message".getBytes(StandardCharsets.UTF_8));
//
//
//        signature.update(hashedMessage);
//
//        byte[] signatureBytes = signature.sign();
//
//
//        System.out.println("Original Message in hex: "+ DatatypeConverter.printHexBinary("This is the message".getBytes(StandardCharsets.UTF_8)));
//
//        System.out.println("SHA256 Hashed Message in Hex: "+ DatatypeConverter.printHexBinary(hashedMessage)  + "\n\n");
//
//
//        System.out.println("Signature: "+ DatatypeConverter.printHexBinary(signatureBytes) + "\n\n");

		
//        KeyFactory kf = KeyFactory.getInstance("ECDSA");
//        PublicKey pub_recovered = kf.generatePublic(new X509EncodedKeySpec(publicKey.getEncoded()));
//        PrivateKey prv_recovered = kf.generatePrivate(new PKCS8EncodedKeySpec(privateKey.getEncoded()));
        
	}
	
	//@Test
	public void createTransactionHashTest() {
		
		List<TransactionOut> txOuts0 = new ArrayList<>();
		TransactionOut txout0 = TransactionOut.builder().address("test0").amount(BigDecimal.valueOf(1)).build();
		TransactionOut txout1 = TransactionOut.builder().address("test1").amount(BigDecimal.valueOf(2)).build();
		TransactionOut txout2 = TransactionOut.builder().address("test2").amount(BigDecimal.valueOf(3)).build();
		txOuts0.add(txout0);
		txOuts0.add(txout1);
		txOuts0.add(txout2);

		List<TransactionIn> txIns0 = new ArrayList<>();
		TransactionIn txin0 = TransactionIn.builder().txOutIndex(0).txOutHash("testHash0").signature("signature0").build();
		TransactionIn txin1 = TransactionIn.builder().txOutIndex(1).txOutHash("testHash1").signature("signature1").build();
		TransactionIn txin2 = TransactionIn.builder().txOutIndex(2).txOutHash("testHash2").signature("signature2").build();
		txIns0.add(txin0);
		txIns0.add(txin1);
		txIns0.add(txin2);
		
		Transaction tx0 = Transaction.builder().txHash("tx0").txIns(txIns0).txOuts(txOuts0).build();
		
		System.out.println(tx0.getTxOuts().stream().map(txOut ->  txOut.getAmount()).reduce((previous, next)->previous.add(next)).get());
		
		//System.out.println(TransactionUtil.createTransactionHash(tx0));
		
		List<TransactionOut> txOuts1 = new ArrayList<>();
		TransactionOut txout3 = TransactionOut.builder().address("test3").amount(BigDecimal.valueOf(1)).build();
		TransactionOut txout4 = TransactionOut.builder().address("test4").amount(BigDecimal.valueOf(2)).build();
		TransactionOut txout5 = TransactionOut.builder().address("test5").amount(BigDecimal.valueOf(3)).build();
		txOuts1.add(txout3);
		txOuts1.add(txout4);
		txOuts1.add(txout5);

		List<TransactionIn> txIns1 = new ArrayList<>();
		TransactionIn txin3 = TransactionIn.builder().txOutIndex(0).txOutHash("testHash3").signature("signature3").build();
		TransactionIn txin4 = TransactionIn.builder().txOutIndex(1).txOutHash("testHash4").signature("signature4").build();
		TransactionIn txin5 = TransactionIn.builder().txOutIndex(2).txOutHash("testHash5").signature("signature5").build();
		txIns1.add(txin3);
		txIns1.add(txin4);
		txIns1.add(txin5);
		
		Transaction tx1 = Transaction.builder().txHash("tx1").txIns(txIns1).txOuts(txOuts1).build();
		//System.out.println(TransactionUtil.createTransactionHash(tx1));
		
		List<Transaction> transactions = new ArrayList<>();
		transactions.add(tx0);
		transactions.add(tx1);
		//System.out.println(TransactionUtil.consumeUTxOList(transactions));
	}
	
	//@Test
	public void findUTxOTest() {
		
		List<UnspentTransactionOut> uTxOList = new ArrayList<>();
		UnspentTransactionOut uTxO0 = UnspentTransactionOut.builder().txOutHash("txOutHash0").txOutIndex(0).address("test0").amount(BigDecimal.valueOf(1)).build();
		UnspentTransactionOut uTxO1 = UnspentTransactionOut.builder().txOutHash("txOutHash1").txOutIndex(1).address("test1").amount(BigDecimal.valueOf(2)).build();
		UnspentTransactionOut uTxO2 = UnspentTransactionOut.builder().txOutHash("txOutHash2").txOutIndex(2).address("test2").amount(BigDecimal.valueOf(3)).build();
		uTxOList.add(uTxO0);
		uTxOList.add(uTxO1);
		uTxOList.add(uTxO2);
		
		//System.out.println(TransactionUtil.findUTxO("txOutHash1", 1, uTxOList));
		
		
	}
	
	//@Test
	public void conmuseAndResultUTxOListTest() {
		
		List<TransactionOut> txOuts0 = new ArrayList<>();
		TransactionOut txout0 = TransactionOut.builder().address("test0").amount(BigDecimal.valueOf(1)).build();
		TransactionOut txout1 = TransactionOut.builder().address("test1").amount(BigDecimal.valueOf(2)).build();
		TransactionOut txout2 = TransactionOut.builder().address("test2").amount(BigDecimal.valueOf(3)).build();
		txOuts0.add(txout0);
		txOuts0.add(txout1);
		txOuts0.add(txout2);

		List<TransactionIn> txIns0 = new ArrayList<>();
		TransactionIn txin0 = TransactionIn.builder().txOutIndex(0).txOutHash("testHash0").signature("signature0").build();
		TransactionIn txin1 = TransactionIn.builder().txOutIndex(1).txOutHash("testHash1").signature("signature1").build();
		TransactionIn txin2 = TransactionIn.builder().txOutIndex(2).txOutHash("testHash2").signature("signature2").build();
		txIns0.add(txin0);
		txIns0.add(txin1);
		txIns0.add(txin2);
		
		Transaction tx0 = Transaction.builder().txHash("tx0").txIns(txIns0).txOuts(txOuts0).build();
		//System.out.println(TransactionUtil.createTransactionHash(tx0));
		
		List<TransactionOut> txOuts1 = new ArrayList<>();
		TransactionOut txout3 = TransactionOut.builder().address("test3").amount(BigDecimal.valueOf(1)).build();
		TransactionOut txout4 = TransactionOut.builder().address("test4").amount(BigDecimal.valueOf(2)).build();
		TransactionOut txout5 = TransactionOut.builder().address("test5").amount(BigDecimal.valueOf(3)).build();
		txOuts1.add(txout3);
		txOuts1.add(txout4);
		txOuts1.add(txout5);

		List<TransactionIn> txIns1 = new ArrayList<>();
		TransactionIn txin3 = TransactionIn.builder().txOutIndex(0).txOutHash("testHash3").signature("signature3").build();
		TransactionIn txin4 = TransactionIn.builder().txOutIndex(1).txOutHash("testHash4").signature("signature4").build();
		TransactionIn txin5 = TransactionIn.builder().txOutIndex(2).txOutHash("testHash5").signature("signature5").build();
		txIns1.add(txin3);
		txIns1.add(txin4);
		txIns1.add(txin5);
		
		Transaction tx1 = Transaction.builder().txHash("tx1").txIns(txIns1).txOuts(txOuts1).build();
		//System.out.println(TransactionUtil.createTransactionHash(tx1));
		
		List<Transaction> transactions = new ArrayList<>();
		transactions.add(tx0);
		transactions.add(tx1);
		
		List<TransactionIn> txIns = transactions.stream()
				   .map(tx -> tx.getTxIns())
				   .reduce((previous, next) -> Stream.concat(previous.stream(), next.stream())
							  						 .collect( Collectors.toList()) )
				   .get();
		System.out.println(txIns);
		//List<UnspentTransactionOut> newUTxOs = TransactionUtil.createNewUTxOs(transactions);
		//System.out.println(newUTxOs);
//		List<UnspentTransactionOut> consumedUtxOs = TransactionUtil.consumeUTxOList(transactions);
//		System.out.println(consumedUtxOs);
//		List<UnspentTransactionOut> totalUTxOs =  Stream.concat(newUTxOs.stream(), consumedUtxOs.stream())
//					  									.collect( Collectors.toList());
//		System.out.println(totalUTxOs);
//		System.out.println(TransactionUtil.consumeAndResultUtxOList(totalUTxOs, consumedUtxOs));
		
	}
	
	//@Test
	public void findAmoutFromUTxOsTest() {
		//basquiat		PZ8Tyr4Nx8MHsRAGMpZmZ6TWY63dXWSCzMP6Yc8zHdzTbBZjzBvbUwKeAGD3XFhRDuuuvTpnJ6p4NhvPJDbFhQCwmGRNSaDsGJA9v6QwQrAcLcUg6EQAdpbA
		//bsyoon		PZ8Tyr4Nx8MHsRAGMpZmZ6TWY63dXWSCvYq96Xu2eeJVUcWVhkmC7kuMe2rXfeVLL5rAjEPznvvCz6bK4gs9DBavTENa3ArNjet4QZdLyygCTtRqbhf4kkHx
		//john			PZ8Tyr4Nx8MHsRAGMpZmZ6TWY63dXWSCyyEYVM65L6RB6Qgqk1XcPPSdBZZHaZ8fF95yfz3jCNEwecZtEi17cw6en2jBJSttaJ8PYdaWR4U2VKnj9WCDK4cd
//		BigDecimal sendAmount = BigDecimal.valueOf(7);
		String myAddress = "PZ8Tyr4Nx8MHsRAGMpZmZ6TWY63dXWSCzMP6Yc8zHdzTbBZjzBvbUwKeAGD3XFhRDuuuvTpnJ6p4NhvPJDbFhQCwmGRNSaDsGJA9v6QwQrAcLcUg6EQAdpbA0";
		List<UnspentTransactionOut> uTxOList = new ArrayList<>();
		UnspentTransactionOut uTxO0 = UnspentTransactionOut.builder().txOutHash("txOutHash0").txOutIndex(0).address("PZ8Tyr4Nx8MHsRAGMpZmZ6TWY63dXWSCzMP6Yc8zHdzTbBZjzBvbUwKeAGD3XFhRDuuuvTpnJ6p4NhvPJDbFhQCwmGRNSaDsGJA9v6QwQrAcLcUg6EQAdpbA0").amount(BigDecimal.valueOf(3)).build();
		UnspentTransactionOut uTxO1 = UnspentTransactionOut.builder().txOutHash("txOutHash1").txOutIndex(1).address("PZ8Tyr4Nx8MHsRAGMpZmZ6TWY63dXWSCzMP6Yc8zHdzTbBZjzBvbUwKeAGD3XFhRDuuuvTpnJ6p4NhvPJDbFhQCwmGRNSaDsGJA9v6QwQrAcLcUg6EQAdpbA1").amount(BigDecimal.valueOf(2)).build();
		UnspentTransactionOut uTxO2 = UnspentTransactionOut.builder().txOutHash("txOutHash2").txOutIndex(2).address("PZ8Tyr4Nx8MHsRAGMpZmZ6TWY63dXWSCzMP6Yc8zHdzTbBZjzBvbUwKeAGD3XFhRDuuuvTpnJ6p4NhvPJDbFhQCwmGRNSaDsGJA9v6QwQrAcLcUg6EQAdpbA2").amount(BigDecimal.valueOf(1)).build();
		UnspentTransactionOut uTxO3 = UnspentTransactionOut.builder().txOutHash("txOutHash3").txOutIndex(3).address("PZ8Tyr4Nx8MHsRAGMpZmZ6TWY63dXWSCzMP6Yc8zHdzTbBZjzBvbUwKeAGD3XFhRDuuuvTpnJ6p4NhvPJDbFhQCwmGRNSaDsGJA9v6QwQrAcLcUg6EQAdpbA3").amount(BigDecimal.valueOf(3)).build();
		UnspentTransactionOut uTxO4 = UnspentTransactionOut.builder().txOutHash("txOutHash4").txOutIndex(4).address("PZ8Tyr4Nx8MHsRAGMpZmZ6TWY63dXWSCzMP6Yc8zHdzTbBZjzBvbUwKeAGD3XFhRDuuuvTpnJ6p4NhvPJDbFhQCwmGRNSaDsGJA9v6QwQrAcLcUg6EQAdpbA4").amount(BigDecimal.valueOf(3)).build();
		uTxOList.add(uTxO0);
		uTxOList.add(uTxO1);
		uTxOList.add(uTxO2);
		uTxOList.add(uTxO3);
		uTxOList.add(uTxO4);
		//TransactionOutMap map = TransactionUtil.findAmoutFromUTxOs(sendAmount, uTxOList);
		System.out.println(uTxOList.stream().filter(uTxO -> myAddress.equals(uTxO.getAddress())).collect(Collectors.toList()));
		
		//List<TransactionIn> list = TransactionUtil.unsingedTransationInList(map.getMySelfUnspentTransactionOuts());
		//System.out.println(list);
		
	}
	
	@Test
	public void createTxOut() {
		TransactionIn txin1 = TransactionIn.builder().txOutIndex(0).txOutHash("asdfasdf").signature("signature3").build();
		TransactionIn txin2 = TransactionIn.builder().txOutIndex(1).txOutHash("asdfasd").signature("signature4").build();
		TransactionIn txin3 = TransactionIn.builder().txOutIndex(2).txOutHash("asdfasdf").signature("signature5").build();
		TransactionIn txin4 = TransactionIn.builder().txOutIndex(1).txOutHash("asdfasd").signature("signature5").build();
		List<TransactionIn> txIns = Stream.of(txin1,txin2,txin3,txin4).collect(Collectors.toList());
//		//System.out.println(TransactionUtil.createTransactionHash(tx1));
//		
//		TransactionPoolStore.addTransactionPoolStore(tx0);
//		TransactionPoolStore.addTransactionPoolStore(tx1);
//		TransactionPoolStore.addTransactionPoolStore(tx2);
//		System.out.println(TransactionPoolStore.getTransactionList());
//		
//		Transaction tx3 = Transaction.builder().txHash("tx2").build();
//		List<Transaction> tt = Stream.of(tx3).collect(Collectors.toList());
//		//AtomicInteger index = new AtomicInteger();
//		System.out.println(tt);
//		TransactionPoolStore.updateTransactionPoolStore(tt);
//		System.out.println(TransactionPoolStore.getTransactionList());
		Map<String, List<String>> a  = txIns.stream().map(mapper -> mapper.getTxOutHash() + mapper.getTxOutIndex()).collect(Collectors.groupingBy(str -> str));
//		List<String> a = Stream.of("test1", "test3", "test4", "test5", "test6", "test1").collect(Collectors.toList());
//		Map<String, List<String>> r  = a.stream().collect(Collectors.groupingBy(str -> str));
//		System.out.println(r);
//		boolean ab = r.entrySet().stream().filter(items -> items.getValue().size() > 1 )
//											.findAny()
//											.map(item ->  true)
//											.orElse(false);
		System.out.println(a);
	}
	
}
