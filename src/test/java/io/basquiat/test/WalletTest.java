package io.basquiat.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.basquiat.util.FileIOUtil;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WalletTest {

	//@Autowired
	//private WebTestClient webTestClient;
	
	@Test
	public void test() {
		System.out.println(FileIOUtil.getCoinbaseAccount());
	}
	
}
