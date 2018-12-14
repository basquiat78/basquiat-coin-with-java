package io.basquiat.websocket.client.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.basquiat.util.CommonUtil;
import io.basquiat.websocket.ProcessMessageService;
import io.basquiat.websocket.client.domain.MessageBuffer;
import io.basquiat.websocket.service.MessageService;
import io.basquiat.websocket.service.vo.Message;
import io.basquiat.websocket.type.MessageType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * NettyClientHandler
 * created by basquiat
 *
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<Object> {

	private static final Logger LOG = LoggerFactory.getLogger(NettyClientHandler.class);

	private final ByteBuf requestMessage;

	private ChannelHandlerContext channelHandlerContext;

	private ProcessMessageService processMessageService;
	
	/**
	 * constructor
	 * netty server 접속시 보낼 메세지
	 * 최초로 서버에 접속할 때는 QUERY_LATESTBLOCK 요청을 한다.
	 * @param messageService
	 * @param processMessageService
	 */
	public NettyClientHandler(MessageService messageService, ProcessMessageService processMessageService){
		this.processMessageService = processMessageService;
		Message message = messageService.getRequestMessage(MessageType.QUERY_LATESTBLOCK);
		requestMessage = Unpooled.copiedBuffer(CommonUtil.convertJsonStringFromObject(message), CharsetUtil.UTF_8);
	}

	/**
     * 채널이 끊기면 해당 channelHandlerContext을 맵에서 삭제한다.
     */
	@Override
	public void channelActive(ChannelHandlerContext channelHandlerContext) {
		this.channelHandlerContext = channelHandlerContext;
		channelHandlerContext.writeAndFlush(requestMessage);
	}

	/**
     * 에러가 나면 log남기기
     */
	@Override
	public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) {
		LOG.info("error message : " + cause.getMessage());
	}

	/**
	 * channelRead0가 모두 완료되면 해당 메소드로 넘어온다
	 * channelRead0에서 처리된 메세지를 파악한다.
	 * 1. messageBuffer로부터 전체 메세지를 Message객체로 변환한다.
	 * 2. processMessageService를 통해 관련 메세지에 대한 처리를 한다.
	 */
    @Override
    public void channelReadComplete(ChannelHandlerContext channelHandlerContext) throws Exception {
    	String messageFromBuffer = MessageBuffer.getMessageBuffer(channelHandlerContext.channel().id().toString());
    	LOG.info("channelId : " +  channelHandlerContext.channel().id().toString()  + ", messageFromBuffer : " + messageFromBuffer );
    	if(messageFromBuffer != null) {
	    	Message message = CommonUtil.convertObjectFromJsonString(MessageBuffer.getMessageBuffer(channelHandlerContext.channel().id().toString()), Message.class);
	    	processMessageService.process(channelHandlerContext, message);
	    	// 소모된 메세지는 버퍼에서 지운다
	    	MessageBuffer.clearMessageBuffer(channelHandlerContext.channel().id().toString());
    	}
    }
    
    /**
     * 채널로 들어오는 메세지를 처리한다
     * 메세지가 크면 split되서 넘어오기 때문에 channel id로 맵에 stringbuffer로 저장한다.
     */
	protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
		ByteBuf inBuffer = (ByteBuf) msg;
		MessageBuffer.addMessageBuffer(channelHandlerContext.channel().id().toString(), inBuffer.toString(CharsetUtil.UTF_8));
		LOG.info("channelId : " +  channelHandlerContext.channel().id().toString()  + ", inbound message : " + inBuffer.toString(CharsetUtil.UTF_8) );
	}

	/**
	 * server로 메세지를 보낸다
	 * @param message
	 */
	public void sendMessage(Message message) {
		if(channelHandlerContext != null) {
			String contents = CommonUtil.convertJsonStringFromObject(message);
			ChannelFuture channelFuture = channelHandlerContext.write(Unpooled.copiedBuffer(contents, CharsetUtil.UTF_8));
			channelHandlerContext.flush();
			if(!channelFuture.isSuccess()) {
				LOG.info("Send failed: " + channelFuture.cause());
			}
		} else {
			LOG.info("retry send message");
		}
	}
	
}