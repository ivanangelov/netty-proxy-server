package io.netty.proxy.server.http.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class ProxyServerClientChannelInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	public void initChannel(SocketChannel channel) {
		ChannelPipeline pipeline = channel.pipeline();

		// outbound
		pipeline.addLast(new HttpResponseEncoder());
		
		// inbound
		pipeline.addLast(new HttpRequestDecoder());
		
		// maxContentLengthis 64 KB
		pipeline.addLast(new HttpObjectAggregator(64*1024));
		pipeline.addLast(new HttpRequestInboundHandler());
	}
}
