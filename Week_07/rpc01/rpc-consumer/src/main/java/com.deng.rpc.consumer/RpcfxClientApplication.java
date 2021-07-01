package com.deng.rpc.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RpcfxClientApplication {

	// 二方库
	// 三方库 lib
	// nexus, userserivce -> userdao -> user
	//
	public static void main(String[] args) {

//		UserService userService = Rpcfx.create(UserService.class, "http://localhost:8082/invoke");
//		User user = userService.findById(1);
//		System.out.println("find user id=1 from server: " + user.getName());
//
//		OrderService orderService = Rpcfx.create(OrderService.class, "http://localhost:8082/invoke");
//		Order order = orderService.findOrderById(1992129);
//		System.out.println(String.format("find order name=%s, amount=%f",order.getName(),order.getAmount()));
//
//		UserService userService2 = Rpcfx.createFromRegistry(UserService.class, "localhost:2181","/invoke", new TagRouter(), new RandomLoadBalancer(), new CuicuiFilter());
//		user = userService2.findById(1);
//		System.out.println("----- find user2 id=1 from server: " + user.getName());

		SpringApplication.run(RpcfxClientApplication.class, args);
	}
}



