package com.github.nettybook.ch3;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class EchoServer {
    public static void main(String[] args) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        // NioEventLoopGroup 생성시 스레드 개수를 지정하지 않으면 코어 수를 기준으로 개수가 정해진다.
        // 스레드 수는 하드웨어가 갖고있는 CPU 코어 수의 2배를 사용한다.
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap
                    // bossGroup: 클라이언트의 연결을 수락하는 부모 스레드 그룹
                    // workerGroup: 연결된 클라이언트 소켓으로부터 데이터 입출력 및 이벤트 처리를 담당하는 자식 스레드 그룹
                    .group(bossGroup, workerGroup)
                    // 서버 소켓(부모 스레드)이 사용할 네트워크 입출력 모드를 설정한다.
                    .channel(NioServerSocketChannel.class)
                    // 자식 채널의 초기화 방법을 설정한다.
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
                // 채널 파이프라인 객체 생성
                ChannelPipeline p = ch.pipeline();
                p.addLast(new EchoServerHandler());
            }
        };
    }
}
