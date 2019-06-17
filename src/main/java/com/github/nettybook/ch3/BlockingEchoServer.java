package com.github.nettybook.ch3;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;

public class BlockingEchoServer {
    public static void main(String[] args) throws Exception {
        EventLoopGroup bossGroup = new OioEventLoopGroup(1);
        EventLoopGroup workerGroup = new OioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(OioServerSocketChannel.class)
                    .childHandler(childHandler());

            ChannelFuture channelFuture = serverBootstrap.bind(8888).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    private static ChannelInitializer<SocketChannel> childHandler() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) {
                ChannelPipeline channelPipeline = ch.pipeline();
                channelPipeline.addLast(new EchoServerHandler());
            }
        };
    }
}
