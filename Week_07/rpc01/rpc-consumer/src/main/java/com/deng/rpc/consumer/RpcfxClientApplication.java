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
		SpringApplication.run(RpcfxClientApplication.class, args);
	}
}



