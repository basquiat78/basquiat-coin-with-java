package io.basquiat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import io.basquiat.websocket.server.NettyServer;

@SpringBootApplication
@EnableScheduling
public class BasquiatCoinWithJavaApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(BasquiatCoinWithJavaApplication.class, args);
		/**
		 * p2p를 위한 NettyServer 띄우기
		 */
		NettyServer nettyServer = context.getBean(NettyServer.class);
        nettyServer.start(context);
	}
}
