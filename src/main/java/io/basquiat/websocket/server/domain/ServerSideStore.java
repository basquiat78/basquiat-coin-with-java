package io.basquiat.websocket.server.domain;

import io.basquiat.util.CommonUtil;
import io.basquiat.websocket.service.vo.Message;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * ServerSideStore
 * created by basquiat
 *
 */
public class ServerSideStore {

	private static final ChannelGroup CHANNELS = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	
	/**
	 * ChannelGroup에 채널 정보를 담는다.
	 * @param channelHandlerContext
	 */
	public static void addChannel(ChannelHandlerContext channelHandlerContext) {
		CHANNELS.add(channelHandlerContext.channel());
	}
	
	/**
	 * remove channel
	 * @param channelHandlerContext
	 */
	public static void remove(ChannelHandlerContext channelHandlerContext) {
		CHANNELS.remove(channelHandlerContext.channel());
	}
	
	/**
	 * send message To Server
	 * @param message
	 */
	public static void sendToServer(Message message) {
		for(Channel channel : CHANNELS) {
			channel.writeAndFlush(CommonUtil.convertJsonStringFromObject(message));
		}
	}

}
