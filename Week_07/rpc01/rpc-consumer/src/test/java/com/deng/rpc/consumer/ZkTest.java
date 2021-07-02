package com.deng.rpc.consumer;

import com.deng.rpc.api.Order;
import com.deng.rpc.api.OrderService;
import com.deng.rpc.api.User;
import com.deng.rpc.api.UserService;
import com.deng.rpc.consumer.impl.*;
import com.deng.rpc.consumer.service.QueryService;
import com.deng.rpc.core.client.Rpcfx;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 一定要使用org.junit.Test
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RpcfxClientApplication.class)
public class ZkTest {

    @Autowired
    private ZkClient zkClient;

    @Autowired
    private QueryService queryService;


    @Test
    public void test1(){
        // 手动获取被代理对象
        UserService userService = Rpcfx.create(UserService.class, "http://localhost:8082/invoke");
		User user = userService.findById(1);
		System.out.println("find user id=1 from server: " + user.getName());

		// 手动获取被代理对象
		OrderService orderService = Rpcfx.create(OrderService.class, "http://localhost:8082/invoke");
		Order order = orderService.findOrderById(1992129);
		System.out.println(String.format("find order name=%s, amount=%f",order.getName(),order.getAmount()));

//		UserService userService2 = Rpcfx.createFromRegistry(UserService.class, "localhost:2181","/invoke", new TagRouter(), new RandomLoadBalancer(), new CuicuiFilter());
//		user = userService2.findById(1);
//		System.out.println("----- find user2 id=1 from server: " + user.getName());

        String url = zkClient.createFromRegistry(UserService.class, "localhost:2181","/invoke", new TagRouter(), new RandomLoadBalancer(), new CuicuiFilter());
        UserService userService2 = Rpcfx.create(UserService.class,url, new CuicuiFilter());
        user = userService2.findById(1);
        System.out.println("----- find user2 id=1 from server: " + user.getName());
    }

    @Test
    public void test2(){
        User user = queryService.getUserById("http://localhost:8082",1);
        System.out.println("find user id=1 from server: " + user.getName());

        Order order = queryService.getOrderById("http://localhost:8082",1992129);
        System.out.println(String.format("find order name=%s, amount=%f",order.getName(),order.getAmount()));
    }
}
