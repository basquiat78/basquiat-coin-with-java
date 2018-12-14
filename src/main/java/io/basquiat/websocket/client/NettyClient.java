package io.basquiat.websocket.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.basquiat.websocket.ProcessMessageService;
import io.basquiat.websocket.client.handler.NettyClientHandler;
import io.basquiat.websocket.service.MessageService;
import io.basquiat.websocket.service.vo.Message;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpServerKeepAliveHandler;

/**
 * NettyClient
 * created by basquiat
 *
 */
public class NettyClient implements Runnable {

	private String host;

	private int port;

	private boolean isRunning = false;

	private ExecutorService executor = null;

	private final NettyClientHandler nettyClientHandler;

	/**
	 * constructor
	 * @param host
	 * @param port
	 * @param messageService
	 * @param processMessageService
	 */
	public NettyClient(String host, int port, MessageService messageService, ProcessMessageService processMessageService) {
		this.host = host;
		this.port = port;
		nettyClientHandler = new NettyClientHandler(messageService, processMessageService);
	}
	
	/**
	 * start client
	 */
	public synchronized void startClient() {
		if(!isRunning) {
			executor = Executors.newFixedThreadPool(1);
			executor.execute(this);
			isRunning = true;
		}
    }
	
	@Override
	public void run() {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(group)
					 .channel(NioSocketChannel.class)
					 .option(ChannelOption.TCP_NODELAY, true)
					 .handler(new ChannelInitializer<SocketChannel>() {
																		@Override
																		protected void initChannel(SocketChannel sc) throws Exception {
																			ChannelPipeline cp = sc.pipeline();
																			cp.addLast(nettyClientHandler);
																			cp.addLast("httpKeepAlive", new HttpServerKeepAliveHandler());
																		}
					});
			ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
			channelFuture.channel().closeFuture().sync();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}
	}
	
	/**
	 * client -> server send message
	 * @param message
	 */
	public void writeMessage(Message message) {
		nettyClientHandler.sendMessage(message);
	}

}
