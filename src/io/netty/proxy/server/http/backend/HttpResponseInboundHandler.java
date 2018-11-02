package io.netty.proxy.server.http.backend;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.ReferenceCountUtil;

public class HttpResponseInboundHandler extends SimpleChannelInboundHandler<HttpObject> {
	
	private final Channel clientChannel;

	public HttpResponseInboundHandler(Channel clientChannel) {
		this.clientChannel = clientChannel;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpObject httpObject) throws Exception {
		
		ReferenceCountUtil.retain(httpObject);
		clientChannel.writeAndFlush(httpObject);
		
		if (httpObject instanceof LastHttpContent) {
			clientChannel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
			ctx.channel().writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		System.err.println(cause.getMessage());
	}
}