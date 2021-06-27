package com.deng.rpc.provider;

import com.deng.rpc.core.domain.ServiceProviderDesc;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

@Component
public class ZkServer implements InitializingBean {

    @Value("${server.port}")
    private int port;

    @Override
    public void afterPropertiesSet() throws Exception {
        initRegisterService();
    }

    public ZkServer(){

    }

    private void initRegisterService(){
        // start zk client
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.builder().connectString("localhost:2181").namespace("rpcfx").retryPolicy(retryPolicy).build();
        client.start();

        // register service
        // xxx "io.kimmking.rpcfx.demo.api.UserService"

        String userService = "com.deng.rpc.api.UserService";
        registerService(client, userService);
        String orderService = "com.deng.rpc.api.OrderService";
        registerService(client, orderService);

        // 进一步的优化，是在spring加载完成后，从里面拿到特定注解的bean，自动注册到zk
    }

    private void registerService(CuratorFramework client, String service){
        try {
            ServiceProviderDesc userServiceSesc = ServiceProviderDesc.builder()
                    .host(InetAddress.getLocalHost().getHostAddress())
                    .port(port).serviceClass(service).build();
            // String userServiceSescJson = JSON.toJSONString(userServiceSesc);

            if ( null == client.checkExists().forPath("/" + service)) {
                client.create().withMode(CreateMode.PERSISTENT).forPath("/" + service, "service".getBytes());
            }

            client.create().withMode(CreateMode.EPHEMERAL).
                    forPath( "/" + service + "/" + userServiceSesc.getHost() + "_" + userServiceSesc.getPort(), "provider".getBytes());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
