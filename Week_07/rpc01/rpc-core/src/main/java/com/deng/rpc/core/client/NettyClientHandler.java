package com.deng.rpc.core.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.deng.rpc.core.domain.RpcfxResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;
import java.net.URISyntaxException;

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
        promise.setSuccess();
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive....");
        this.context = ctx;
    }

    public void sendMessage(Object msg){
        System.out.println("context:"+context);
        promise = context.newPromise();

        URI uri = null;
        try {
            uri = new URI("/invoke");

            DefaultFullHttpRequest req = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST,
                    uri.toASCIIString(), Unpooled.wrappedBuffer(msg.toString().getBytes("UTF-8")));

            req.headers().set(HttpHeaderNames.CONTENT_TYPE,"application/json;charset=utf8");
            req.headers().set(HttpHeaderNames.HOST, "127.0.0.1");
            req.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            req.headers().set(HttpHeaderNames.CONTENT_LENGTH, req.content().readableBytes());

            context.writeAndFlush(req);
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
