package com.deng.rpc.consumer;


import com.deng.rpc.consumer.service.DiscoverService;
import com.deng.rpc.core.api.Filter;
import com.deng.rpc.core.api.LoadBalancer;
import com.deng.rpc.core.api.Router;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ZkClient {

    @Autowired
    private DiscoverService discoverService;

    public String createFromRegistry(final Class<?> serviceClass, final String zkUrl, final String uri,
                                                    Router router, LoadBalancer loadBalance, Filter filter) {

        // 加filte之一

        // curator Provider list from zk
        List<String> invokers = new ArrayList<>();
        invokers.addAll(getInvokers(serviceClass));
        // 1. 简单：从zk拿到服务提供的列表
        // 2. 挑战：监听zk的临时节点，根据事件更新这个list（注意，需要做个全局map保持每个服务的提供者List）

        List<String> urls = router.route(invokers);

        String url = loadBalance.select(urls); // router, loadbalance

        if(StringUtils.isBlank(url)){
            throw new RuntimeException("not found avaiable url");
        }

        String[] addressArr = url.split("_");

        url = "http://" + addressArr[0] + ":" + addressArr[1] + uri;
        System.out.println("url:"+url);

        return url;
    }

    private List<String> getInvokers(Class<?> serviceClass){
        return discoverService.getInvokers(serviceClass);
    }
}
