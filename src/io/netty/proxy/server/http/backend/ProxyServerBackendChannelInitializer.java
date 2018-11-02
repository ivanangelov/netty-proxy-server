package io.netty.proxy.server.http.backend;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;

public class ProxyServerBackendChannelInitializer extends ChannelInitializer<SocketChannel> {
	
	private final Channel clientChannel;

	public ProxyServerBackendChannelInitializer(Channel clientChannel) {
		this.clientChannel = clientChannel;
	}

	@Override
	public void initChannel(SocketChannel channel) {
		ChannelPipeline pipeline = channel.pipeline();

		// inbound
		pipeline.addLast(new HttpResponseDecoder());
		
		// maxContentLenght is 64 MB
		channel.pipeline().addLast(new HttpObjectAggregator(64 * 1024 * 1024));
		channel.pipeline().addLast(new HttpResponseInboundHandler(clientChannel));

		// outbound
		pipeline.addLast(new HttpRequestEncoder());		
	}
}
