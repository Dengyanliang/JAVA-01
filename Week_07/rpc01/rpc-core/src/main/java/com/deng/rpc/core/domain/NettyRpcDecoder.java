package com.deng.rpc.core.domain;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @Desc:解码器
 * @Auther: dengyanliang
 * @Date: 2021/6/18 21:27
 */
public class NettyRpcDecoder extends ByteToMessageDecoder {

    // 目标对象类型解码
    private Class<?> target;

    public NettyRpcDecoder(Class<?> target) {
        this.target = target;
    }

    @Override
    protected void decode(ChannelHandlerContext context, ByteBuf in, List<Object> out) throws Exception {
        if(in.readableBytes() < 4){ // 不够长度则丢弃
            return;
        }
        in.markReaderIndex();  // 标记一下当前的readIndex的位置
        int dataLength = in.readInt(); // 读取传送过来的消息长度。该方法会让readIndex增加4
        // https://blog.csdn.net/qq_22200097/article/details/83042424
        if(in.readableBytes() < dataLength){
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);

        Object object = JSON.parseObject(data, target);
        out.add(object);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext channelHandlerContext) throws Exception {

    }
}
