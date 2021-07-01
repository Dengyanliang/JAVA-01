package com.deng.rpc.consumer;

import com.deng.rpc.api.User;
import com.deng.rpc.api.UserService;
import com.deng.rpc.core.client.Rpcfx;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RpcfxClientApplication.class)
public class ZkTest {

    @Autowired
    private ZkClient zkClient;

    @Test
    public void test1(){
        String url = zkClient.createFromRegistry(UserService.class, "localhost:2181","/invoke", new TagRouter(), new RandomLoadBalancer(), new CuicuiFilter());
        UserService userService2 = Rpcfx.create(UserService.class,url, new CuicuiFilter());
        User user = userService2.findById(1);
        System.out.println("----- find user2 id=1 from server: " + user.getName());
    }
}
