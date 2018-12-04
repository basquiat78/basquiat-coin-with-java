package io.basquiat.test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WalletTest {

	@Test
	public void test() {
		//System.out.println(BigDecimal.ZERO.compareTo(BigDecimal.valueOf(1)) == 0 || BigDecimal.ZERO.compareTo(BigDecimal.valueOf(1)) == 1);
		List<String> a = Stream.of("a").collect(Collectors.toList());
		List<String> b = Stream.of("b", "c", "d").collect(Collectors.toList());
		System.out.println( Stream.concat(a.stream(), b.stream()).collect(Collectors.toList()) );
		
	}
	
	//@Test
	public void getBalanceTest() {
		List<String> sList = Stream.of("a", "b", "c").collect(Collectors.toList());
		
		List<String> tList = Stream.of("b", "d", "e").collect(Collectors.toList());
		String tt = null;
		for(String s : sList) {
			String ss = tList.stream()
							 .filter(value -> s.equals(value))
							 .findAny()
							 .orElse(null);
			if(ss != null) {
				tt = ss;
				break;
			}
			System.out.println(ss);
		}
		
		System.out.println(tt);
		
//basquiat		PZ8Tyr4Nx8MHsRAGMpZmZ6TWY63dXWSCzMP6Yc8zHdzTbBZjzBvbUwKeAGD3XFhRDuuuvTpnJ6p4NhvPJDbFhQCwmGRNSaDsGJA9v6QwQrAcLcUg6EQAdpbA
//bsyoon		PZ8Tyr4Nx8MHsRAGMpZmZ6TWY63dXWSCvYq96Xu2eeJVUcWVhkmC7kuMe2rXfeVLL5rAjEPznvvCz6bK4gs9DBavTENa3ArNjet4QZdLyygCTtRqbhf4kkHx
//john			PZ8Tyr4Nx8MHsRAGMpZmZ6TWY63dXWSCyyEYVM65L6RB6Qgqk1XcPPSdBZZHaZ8fF95yfz3jCNEwecZtEi17cw6en2jBJSttaJ8PYdaWR4U2VKnj9WCDK4cd
		System.out.println("PZ8Tyr4Nx8MHsRAGMpZmZ6TWY63dXWSCzMP6Yc8zHdzTbBZjzBvbUwKeAGD3XFhRDuuuvTpnJ6p4NhvPJDbFhQCwmGRNSaDsGJA9v6QwQrAcLcUg6EQAdpbA".length());
//		List<UnspentTransactionOut> uTxOList = new ArrayList<>();
//		UnspentTransactionOut uTxO0 = UnspentTransactionOut.builder().txOutHash("txOutHash0").txOutIndex(0).address("PZ8Tyr4Nx8MHsRAGMpZmZ6TWY63dXWSCyyEYVM65L6RB6Qgqk1XcPPSdBZZHaZ8fF95yfz3jCNEwecZtEi17cw6en2jBJSttaJ8PYdaWR4U2VKnj9WCDK4cd").amount(BigDecimal.valueOf(3)).build();
//		UnspentTransactionOut uTxO1 = UnspentTransactionOut.builder().txOutHash("txOutHash1").txOutIndex(1).address("PZ8Tyr4Nx8MHsRAGMpZmZ6TWY63dXWSCyyEYVM65L6RB6Qgqk1XcPPSdBZZHaZ8fF95yfz3jCNEwecZtEi17cw6en2jBJSttaJ8PYdaWR4U2VKnj9WCDK4cd").amount(BigDecimal.valueOf(2)).build();
//		UnspentTransactionOut uTxO2 = UnspentTransactionOut.builder().txOutHash("txOutHash2").txOutIndex(2).address("PZ8Tyr4Nx8MHsRAGMpZmZ6TWY63dXWSCvYq96Xu2eeJVUcWVhkmC7kuMe2rXfeVLL5rAjEPznvvCz6bK4gs9DBavTENa3ArNjet4QZdLyygCTtRqbhf4kkHx").amount(BigDecimal.valueOf(31)).build();
//		UnspentTransactionOut uTxO3 = UnspentTransactionOut.builder().txOutHash("txOutHash3").txOutIndex(2).address("PZ8Tyr4Nx8MHsRAGMpZmZ6TWY63dXWSCzMP6Yc8zHdzTbBZjzBvbUwKeAGD3XFhRDuuuvTpnJ6p4NhvPJDbFhQCwmGRNSaDsGJA9v6QwQrAcLcUg6EQAdpbA").amount(BigDecimal.valueOf(3)).build();
//		UnspentTransactionOut uTxO4 = UnspentTransactionOut.builder().txOutHash("txOutHash4").txOutIndex(2).address("PZ8Tyr4Nx8MHsRAGMpZmZ6TWY63dXWSCzMP6Yc8zHdzTbBZjzBvbUwKeAGD3XFhRDuuuvTpnJ6p4NhvPJDbFhQCwmGRNSaDsGJA9v6QwQrAcLcUg6EQAdpbA").amount(BigDecimal.valueOf(3)).build();
//		uTxOList.add(uTxO0);
//		uTxOList.add(uTxO1);
//		uTxOList.add(uTxO2);
//		uTxOList.add(uTxO3);
//		uTxOList.add(uTxO4);
//		String address = "PZ8Tyr4Nx8MHsRAGMp2ZmZ6TWY63dXWSCvYq96Xu2eeJVUcWVhkmC7kuMe2rXfeVLL5rAjEPznvvCz6bK4gs9DBavTENa3ArNjet4QZdLyygCTtRqbhf4kkHx";
//		String account = "bsyoo2n";
//		System.out.println(WalletUtil.getBalanceByAddress(address, uTxOList));
//		System.out.println(WalletUtil.getBalanceByAccount(account, uTxOList));
	}

}
