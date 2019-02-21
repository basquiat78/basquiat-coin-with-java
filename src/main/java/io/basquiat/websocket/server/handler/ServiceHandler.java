package io.basquiat.websocket.server.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.basquiat.util.CommonUtil;
import io.basquiat.websocket.ProcessMessageService;
import io.basquiat.websocket.client.domain.MessageBuffer;
import io.basquiat.websocket.server.domain.ServerSideStore;
import io.basquiat.websocket.service.vo.Message;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * ServiceHandler
 * created by basquiat
 */
@ChannelHandler.Sharable
public class ServiceHandler extends SimpleChannelInboundHandler<String> {

	private static final Logger LOG = LoggerFactory.getLogger(ServiceHandler.class);

	private ProcessMessageService processMessageService;

	public ServiceHandler(ProcessMessageService processMessageService) {
		this.processMessageService = processMessageService;
    }

	/**
     * client로부터 연결이 되면 해당 channelHandlerContext을 맵에 저장한다.
     */
    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
    	ServerSideStore.addChannel(channelHandlerContext);
    }

    /**
     * 채널이 끊기면 해당 channelHandlerContext을 맵에서 삭제한다.
     */
    @Override
    public void channelInactive(ChannelHandlerContext channelHandlerContext) throws Exception {
    	ServerSideStore.remove(channelHandlerContext);
    }

	/**
	 * channelRead0가 모두 완료되면 해당 메소드로 넘어온다
	 * channelRead0에서 처리된 메세지를 파악한다.
	 * 1. messageBuffer로부터 전체 메세지를 Message객체로 변환한다.
	 * 2. processMessageService를 통해 관련 메세지에 대한 처리를 한다.
	 */
    @Override
    public void channelReadComplete(ChannelHandlerContext channelHandlerContext) throws Exception {
		Message message = CommonUtil.convertObjectFromJsonString(MessageBuffer.getMessageBuffer(channelHandlerContext.channel().id().toString()), Message.class);
		processMessageService.process(channelHandlerContext, message);
		MessageBuffer.clearMessageBuffer(channelHandlerContext.channel().id().toString());
    }

    /**
     * 에러가 나면 log남기기
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) throws Exception {
    	LOG.info("error message : " + cause.getMessage());
    }

    /**
     * 채널로 들어오는 메세지를 처리한다
     * 메세지가 크면 split되서 넘어오기 때문에 channel id로 맵에 stringbuffer로 저장한다.
     * 한번에 받을 수 있는 방법이 없을까?????
     */
    @Override
	protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
		MessageBuffer.addMessageBuffer(channelHandlerContext.channel().id().toString(), msg);
		LOG.info("inbound message : " + msg);
	}
    
}