package com.deng.rpc.provider;

import com.deng.rpc.api.OrderService;
import com.deng.rpc.api.UserService;
import com.deng.rpc.core.api.RpcfxResolver;
import com.deng.rpc.core.domain.RpcfxRequest;
import com.deng.rpc.core.domain.RpcfxResponse;
import com.deng.rpc.core.server.RpcfxInvoker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
//@RestController
public class RpcfxServerApplication implements CommandLineRunner{

	@Autowired
	private NettyServer nettyServer;

	public static void main(String[] args) throws Exception {
		// 进一步的优化，是在spring加载完成后，从里面拿到特定注解的bean，自动注册到zk
		SpringApplication.run(RpcfxServerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		new Thread(nettyServer).start();
	}

	@Bean
	public RpcfxInvoker createInvoker(@Autowired RpcfxResolver resolver){
		return new RpcfxInvoker(resolver);
	}

	@Bean
	public RpcfxResolver createResolver(){
		return new DemoResolver();
	}
	// 能否去掉name
	//

	// annotation


	@Bean(name = "com.deng.rpc.api.UserService")
	public UserService createUserService(){
		return new UserServiceImpl();
	}

	@Bean(name = "com.deng.rpc.api.OrderService")
	public OrderService createOrderService(){
		return new OrderServiceImpl();
	}

}
