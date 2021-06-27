package com.deng.rpc.core.client;

import com.deng.rpc.core.domain.RpcfxRequest;
import com.deng.rpc.core.domain.RpcfxResponse;

/**
 * @Desc:
 * @Auther: dengyanliang
 * @Date: 2021/6/18 22:33
 */
public class NettyClientMain {

    public static void main(String[] args) throws InterruptedException {
        NettyClient client = new NettyClient("127.0.0.1",8082);

        NettyClientHandler clientHandler = client.getClientHandler();

//        CountDownLatch countDownLatch = new CountDownLatch(1);
//        clientHandler.setCountDownLatch(countDownLatch);

        RpcfxRequest requestModel = new RpcfxRequest();
        requestModel.setMethod("123");

        clientHandler.sendMessage(requestModel);

//        countDownLatch.await();

        String result = clientHandler.getResult();
//        RpcfxResponse response = result;
        System.out.println("response:"+result);
    }
}
