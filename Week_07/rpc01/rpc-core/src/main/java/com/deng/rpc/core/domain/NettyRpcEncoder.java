package com.deng.rpc.core.domain;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @Desc:编码
 * @Auther: dengyanliang
 * @Date: 2021/6/18 21:34
 */
public class NettyRpcEncoder extends MessageToByteEncoder {

    private Class<?> target;

    public NettyRpcEncoder(Class<?> target) {
        this.target = target;
    }

    @Override
    protected void encode(ChannelHandlerContext context, Object msg, ByteBuf out) throws Exception {
        if(target.isInstance(msg)){
            byte[] data = JSON.toJSONBytes(msg);
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }
}
