package io.netty.proxy.server.http.client;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.proxy.server.http.backend.ProxyServerBackendChannelInitializer;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class HttpRequestInboundHandler extends SimpleChannelInboundHandler<HttpObject> {
	private static final int BACKEND_PORT = 8080;
	private static final String BACKEND_HOST = "localhost";

	private Channel backendChannel;

	@Override
	public void channelRead0(ChannelHandlerContext ctx, HttpObject httpObject) throws Exception {

		if (httpObject instanceof HttpRequest) {
			createBackendConnection(ctx.channel());
			transformToRelative((HttpRequest) httpObject);
		}

		ReferenceCountUtil.retain(httpObject);
		backendChannel.writeAndFlush(httpObject);
	}

	private void createBackendConnection(Channel clientChannel) throws InterruptedException {
		final EventLoopGroup group = new NioEventLoopGroup();
		InetSocketAddress address = new InetSocketAddress(BACKEND_HOST, BACKEND_PORT);
		Bootstrap bootstrap = new Bootstrap();
		
		// Solution 1: if the below line is uncommented then the issue disappears
		// bootstrap.option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT);
		
		bootstrap.group(group).channel(NioSocketChannel.class).handler(new ProxyServerBackendChannelInitializer(clientChannel));
		backendChannel = bootstrap.connect(address).sync().channel();
		
		// Solution 2: if the below lines are uncommented the issue disappears
//		clientChannel.closeFuture().addListener(new GenericFutureListener<Future<? super Void>>() {
//			
//			@Override
//			public void operationComplete(Future<? super Void> future) throws Exception {
//				group.shutdownGracefully();
//			}
//		});
	}
	
	private void transformToRelative(HttpRequest request) throws URISyntaxException {
		URI requestURI = new URI(request.uri());
		request.setUri(requestURI.getRawPath());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		System.err.println(cause.getMessage());
	}
}
