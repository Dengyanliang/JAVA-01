package com.deng.rpc.core.client;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.deng.rpc.core.api.Filter;
import com.deng.rpc.core.api.Router;
import com.deng.rpc.core.domain.RpcfxRequest;
import com.deng.rpc.core.domain.RpcfxResponse;
import com.deng.rpc.core.api.LoadBalancer;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
public final class Rpcfx {

    static {
        // 解决autotype被禁止问题,这个解禁autotype只针对所设置的这个包下的对象
        ParserConfig.getGlobalInstance().addAccept("com.deng");
    }

    public static <T, filters> T createFromRegistry(final Class<T> serviceClass, final String zkUrl,final String uri,
                                                    Router router, LoadBalancer loadBalance, Filter filter) {

        // 加filte之一

        // curator Provider list from zk
        List<String> invokers = new ArrayList<>();
        invokers.addAll(getInvokers(serviceClass,zkUrl));
        // 1. 简单：从zk拿到服务提供的列表
        // 2. 挑战：监听zk的临时节点，根据事件更新这个list（注意，需要做个全局map保持每个服务的提供者List）

        List<String> urls = router.route(invokers);

        String url = loadBalance.select(urls); // router, loadbalance

        String[] addressArr = url.split("_");

        url = "http://" + addressArr[0] + ":" + addressArr[1] + uri;
        System.out.println("url:"+url);

        return (T) create(serviceClass, url, filter);

    }

    private static List<String> getInvokers(final Class serviceClass, final String zkUrl){
        List<String> invokers = new ArrayList<>();
        try {
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
            CuratorFramework client = CuratorFrameworkFactory.builder().connectString(zkUrl).namespace("rpcfx").retryPolicy(retryPolicy).build();
            client.start();

            String url = "/"+serviceClass.getName();
            invokers = client.getChildren().forPath(url);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return invokers;
    }

    public static <T> T create(final Class<T> serviceClass, final String url, Filter... filters) {
        // 0. 替换动态代理 -> AOP
        return (T) Proxy.newProxyInstance(Rpcfx.class.getClassLoader(), new Class[]{serviceClass}, new RpcfxInvocationHandler(serviceClass, url, filters));
    }
}
