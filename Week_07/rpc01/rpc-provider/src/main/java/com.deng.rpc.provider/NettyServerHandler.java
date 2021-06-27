package com.deng.rpc.provider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.deng.rpc.core.domain.RpcfxRequest;
import com.deng.rpc.core.domain.RpcfxResponse;
import com.deng.rpc.core.server.RpcfxInvoker;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Desc:
 * @Auther: dengyanliang
 * @Date: 2021/6/20 08:04
 */
@Component
@ChannelHandler.Sharable
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    @Resource
    RpcfxInvoker invoker;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
       System.out.println("******接收到消息："+msg);
        FullHttpRequest httpRequest = (FullHttpRequest)msg;

        String ret = "";
        try {
            String uri = httpRequest.uri();
            String data = httpRequest.content().toString(CharsetUtil.UTF_8);
            HttpMethod method = httpRequest.method();

            //...
            System.out.println("NettyServerHandler 客户端请求数据内容 ：" + data);

            // 将json转化为对象
            RpcfxRequest rpcfxRequest = JSONObject.parseObject(data).toJavaObject(RpcfxRequest.class);
            System.out.println("rpcfxRequest:"+rpcfxRequest);

            RpcfxResponse response = invoker.invoke(rpcfxRequest);

            String result = JSON.toJSONString(response);

            System.out.println("result:"+result);
            response(result, ctx, HttpResponseStatus.OK);
            //..
        } catch (Exception e) {
            System.out.println("服务器处理失败...");
        } finally {
            httpRequest.release();
        }
    }

    private void response(String data, ChannelHandlerContext ctx, HttpResponseStatus status){
        FullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
                Unpooled.copiedBuffer(data, CharsetUtil.UTF_8));
        resp.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json;charset=UTF-8");
        ctx.writeAndFlush(resp).addListener(ChannelFutureListener.CLOSE);
    }

    private void response(Object data, ChannelHandlerContext ctx, HttpResponseStatus status){
        FullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
                Unpooled.copiedBuffer(data.toString(), CharsetUtil.UTF_8));
//        resp.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=UTF-8");
        resp.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json;charset=utf8");
        ctx.writeAndFlush(resp).addListener(ChannelFutureListener.CLOSE);
    }
}
