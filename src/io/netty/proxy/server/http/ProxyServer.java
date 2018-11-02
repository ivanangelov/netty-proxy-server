package io.netty.proxy.server.http;

import java.net.InetSocketAddress;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.proxy.server.http.client.ProxyServerClientChannelInitializer;

public class ProxyServer {

	private static final int DEFAULT_PORT = 44444;

	private final int port;

	public ProxyServer() {
		this(DEFAULT_PORT);
	}

	public ProxyServer(int port) {
		this.port = port;
	}

	public static void main(String[] args) throws InterruptedException {
		int port = 4444;
		new ProxyServer(port).start();
	}

	public void start() throws InterruptedException {
		
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(group).channel(NioServerSocketChannel.class).localAddress(new InetSocketAddress(port))
					.childHandler(new ProxyServerClientChannelInitializer());

			ChannelFuture channelFuture = bootstrap.bind().sync();
			
			System.out.println("Proxy server started");
			
			channelFuture.channel().closeFuture().sync();
		} finally {
			group.shutdownGracefully();
		}
	}
}
