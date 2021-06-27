package com.deng.rpc.provider;

import com.deng.rpc.core.domain.NettyRpcDecoder;
import com.deng.rpc.core.domain.NettyRpcEncoder;
import com.deng.rpc.core.domain.RpcfxRequest;
import com.deng.rpc.core.domain.RpcfxResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * @Desc:
 * @Auther: dengyanliang
 * @Date: 2021/6/20 06:37
 */
@Component
public class NettyServer implements Runnable {

    @Value("${server.port}")
    private int port;

    @Autowired
    private NettyServerHandler nettyServerHandler;

    @Override
    public void run() {
        start(port);
    }

    //    public static void main(String[] args) {
//        new NettyServer().start(8082);
//    }


//    @Override
//    public void afterPropertiesSet() throws Exception {
//        System.out.println("NettyServer start....");
//        start(port);
//    }

    public void start(int port){
        try {
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            ServerBootstrap bs = new ServerBootstrap();

            bs.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,128)
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
//                            socketChannel.pipeline()
//                                    .addLast(new NettyRpcDecoder(RpcfxRequest.class))
//                                    .addLast(new NettyRpcEncoder(RpcfxResponse.class))
//                                    .addLast(new NettyServerHandler());

                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new HttpServerCodec());
                            pipeline.addLast(new HttpObjectAggregator(1024));
                            pipeline.addLast(new HttpContentCompressor());
                            pipeline.addLast(nettyServerHandler);
                        }
                    });

            ChannelFuture future = bs.bind(8083).sync();
            if(future.isSuccess()){
                System.out.println("服务端启动成功");
            }else {
                System.out.println("服务端启动失败");
            }

            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
