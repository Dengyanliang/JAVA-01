package com.deng.rpc.consumer;


import com.deng.rpc.core.api.Filter;
import com.deng.rpc.core.api.LoadBalancer;
import com.deng.rpc.core.api.Router;
import com.deng.rpc.core.common.ZkConfig;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class ZkClient {

    private CuratorFramework client;

    public ZkClient(){
        System.out.println("------ZkClient------");
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder().connectString(ZkConfig.CONNECT_STRING).retryPolicy(retryPolicy).build();
        client.start();

        String url = "/rpcfx/com.deng.rpc.api.UserService";
        registerWatcher(client,url);
    }

    public String createFromRegistry(final Class<?> serviceClass, final String zkUrl, final String uri,
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

        return url;

    }


    private List<String> getInvokers(final Class serviceClass, final String zkUrl){
        List<String> invokers = new ArrayList<>();
        try {
            String url = "/rpcfx/"+serviceClass.getName();
            invokers = client.getChildren().forPath(url);

            // 注册监听器
            registerWatcher(client,url);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return invokers;
    }

    private static void registerWatcher(CuratorFramework curatorFramework, String path){
        CuratorCache curatorCache = CuratorCache.build(curatorFramework,path);
        // 当前节点
        CuratorCacheListener listener = CuratorCacheListener.builder().forNodeCache(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                System.out.println("---------forNodeCache------------");
                Optional<ChildData> childData = curatorCache.get(path);
                if(childData.isPresent()){
                    String path1 = childData.get().getPath();
                    String data = new String(childData.get().getData());
                    System.out.println("forNodeCache data:" + data + ",path:" + path1);
                }
            }
        }).build();
        curatorCache.listenable().addListener(listener);

        // 监听子节点，不监听当前节点
        CuratorCacheListener pathCacheListener = CuratorCacheListener.builder().forPathChildrenCache(path, curatorFramework, new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent event) throws Exception {
                System.out.println("---------forPathChildrenCache------------");
                String type = event.getType().name();
                System.out.println("pathCacheListener type:" + type);
                ChildData data = event.getData();
                if(Objects.nonNull(data)){
                    String path1 = data.getPath();
                    String nodeData = new String(data.getData());
                    System.out.println("pathCacheListener: " + path1 + "," + nodeData);
                }
            }
        }).build();
        curatorCache.listenable().addListener(pathCacheListener);

        //
        CuratorCacheListener treeCacheListener = CuratorCacheListener.builder().forTreeCache(curatorFramework, new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent event) throws Exception {
                System.out.println("---------forTreeCache------------");
                String type = event.getType().name();
                System.out.println("treeCacheListener type:" + type);
                ChildData data = event.getData();
                if(Objects.nonNull(data)){
                    String path1 = data.getPath();
                    String nodeData = new String(data.getData());
                    System.out.println("treeCacheListener: " + path1 + "," + nodeData);
                }
            }
        }).build();
        curatorCache.listenable().addListener(treeCacheListener);

        // 一定不要忘记了这句，很重要
        curatorCache.start();
    }
}
