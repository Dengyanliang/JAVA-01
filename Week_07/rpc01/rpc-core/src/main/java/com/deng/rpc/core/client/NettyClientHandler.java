package com.deng.rpc.core.client;

import com.deng.rpc.core.common.Config;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;

/**
 * @Desc:
 * @Auther: dengyanliang
 * @Date: 2021/6/19 23:09
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private ChannelHandlerContext context;
    private ChannelPromise promise;
    private String result;

    public NettyClientHandler() {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("-----接收到消息-----");
        FullHttpResponse response = (FullHttpResponse)msg;
        //
        String string = response.content().toString(CharsetUtil.UTF_8);
        System.out.println("channelRead:" + string);

        result = string;
        promise.setSuccess(); // 接收成功之后设置，此时会触发promise.await，从而解锁
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.context = ctx;
        System.out.println("channelActive....");
    }

    public void sendMessage(Object msg,String host,String uriStr){
        System.out.println("context:"+context);
        promise = context.newPromise();

        try {
            URI uri = new URI(uriStr);

            DefaultFullHttpRequest req = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST,
                    uri.toASCIIString(), Unpooled.wrappedBuffer(msg.toString().getBytes("UTF-8")));

            req.headers().set(HttpHeaderNames.CONTENT_TYPE, Config.JSONTYPE);
            req.headers().set(HttpHeaderNames.HOST, host);
            req.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            req.headers().set(HttpHeaderNames.CONTENT_LENGTH, req.content().readableBytes());

            context.writeAndFlush(req);
            System.out.println("发送完毕...");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public String getResult() {
        try {
            promise.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }
}
