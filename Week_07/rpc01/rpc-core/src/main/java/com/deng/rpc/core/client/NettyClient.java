package com.deng.rpc.core.client;

import com.deng.rpc.core.domain.NettyRpcDecoder;
import com.deng.rpc.core.domain.NettyRpcEncoder;
import com.deng.rpc.core.domain.RpcfxRequest;
import com.deng.rpc.core.domain.RpcfxResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import lombok.Getter;

/**
 * @Desc:
 * @Auther: dengyanliang
 * @Date: 2021/6/19 21:29
 */
@Getter
public class NettyClient {

    private String host;
    private int port;
    private Channel channel;
    private NettyClientHandler clientHandler;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
        connect();
    }

    public void connect(){
        Bootstrap bs = null;
        try {
            EventLoopGroup group = new NioEventLoopGroup();
            bs = new Bootstrap();
            bs.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            clientHandler = new NettyClientHandler();
//                            socketChannel.pipeline()
//                                    .addLast(new NettyRpcEncoder(RpcfxRequest.class))
//                                    .addLast(new NettyRpcDecoder(RpcfxResponse.class))
//                                    .addLast(clientHandler);

                            ch.pipeline().addLast(new HttpClientCodec());
                            ch.pipeline().addLast(new HttpObjectAggregator(2014));
                            ch.pipeline().addLast(new HttpContentDecompressor());
                            ch.pipeline().addLast(clientHandler);
                        }
                    });

            ChannelFuture future = bs.connect(host,port).sync();
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if(future.isSuccess()){
                        System.out.println("服务器连接成功");
                    }else{
                        System.out.println("服务器连接失败");
                        future.cause().printStackTrace();
                        group.shutdownGracefully();
                    }
                }
            });
            channel = future.channel();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
