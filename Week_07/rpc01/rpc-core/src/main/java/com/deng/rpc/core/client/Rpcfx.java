package com.deng.rpc.core.client;


import com.alibaba.fastjson.parser.ParserConfig;
import com.deng.rpc.core.api.Filter;
import com.deng.rpc.core.api.LoadBalancer;
import com.deng.rpc.core.api.Router;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public final class Rpcfx {


    private static Map<String,List<String>> invokeMap = new ConcurrentHashMap();

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
        List<String> urlList = invokeMap.get(serviceClass.getName());
        if(!CollectionUtils.isEmpty(urlList)){
            return urlList;
        }

        List<String> invokers = new ArrayList<>();
        try {
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
            CuratorFramework client = CuratorFrameworkFactory.builder().connectString(zkUrl).retryPolicy(retryPolicy).build();
            client.start();

            String url = "/rpcfx/"+serviceClass.getName();
            invokers = client.getChildren().forPath(url);

            // 注册监听器
            registerWatcher(client,url);

            invokeMap.put(serviceClass.getName(),invokers);
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
                    String data = new String(childData.get().getData());
                    System.out.println("data:" + data);
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
                    System.out.println("pathCacheListener: " + path1 + ":" + nodeData);
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
                    System.out.println("treeCacheListener: " + path1 + ":" + nodeData);
                }
            }
        }).build();

        curatorCache.listenable().addListener(treeCacheListener);
    }




    public static <T> T create(final Class<T> serviceClass, final String url, Filter... filters) {
        // 0. 替换动态代理 -> AOP
        return (T) Proxy.newProxyInstance(Rpcfx.class.getClassLoader(), new Class[]{serviceClass}, new RpcfxInvocationHandler(serviceClass, url, filters));
    }
}
