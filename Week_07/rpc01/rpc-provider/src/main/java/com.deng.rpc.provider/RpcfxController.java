package com.deng.rpc.provider;

import com.deng.rpc.core.domain.RpcfxRequest;
import com.deng.rpc.core.domain.RpcfxResponse;
import com.deng.rpc.core.server.RpcfxInvoker;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Desc:
 * @Auther: dengyanliang
 * @Date: 2021/6/27 17:25
 */
@RestController
public class RpcfxController {

    @Resource
    RpcfxInvoker invoker;

    @PostMapping("/invoke")
    public RpcfxResponse invoke(@RequestBody RpcfxRequest request) {
        System.out.println("RpcfxController 接收到消息："+request);
        return invoker.invoke(request);
    }
}
