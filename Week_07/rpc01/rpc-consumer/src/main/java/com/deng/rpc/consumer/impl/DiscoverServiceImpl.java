package com.deng.rpc.consumer.impl;

import com.deng.rpc.consumer.service.DiscoverService;
import com.deng.rpc.core.common.Config;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DiscoverServiceImpl implements DiscoverService {

    private static Map<String,List<String>> invokerMap;

    private CuratorFramework client;

    public DiscoverServiceImpl(){
        System.out.println("------DiscoverServiceImpl------");
        invokerMap = new ConcurrentHashMap();
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder().connectString(Config.ZK_CONNECT_STRING).retryPolicy(retryPolicy).build();
        client.start();
    }

    @Override
    public List<String> getInvokers(Class<?> serviceClass) {
        List<String> invokers = invokerMap.get(serviceClass.getName());
        if(!CollectionUtils.isEmpty(invokers)){
            return invokers;
        }
        try {
            String url = Config.ZK_ROOT_PATH + Config.OBLIQUE_LINE +serviceClass.getName();
            invokers = client.getChildren().forPath(url);

            invokerMap.put(serviceClass.getName(),invokers);
            // 注册监听器
            registerWatcher(client,url,serviceClass);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return invokers;
    }

    private void registerWatcher(CuratorFramework curatorFramework, String path, Class serviceClass){
        CuratorCache curatorCache = CuratorCache.build(curatorFramework,path);
        // 当前节点
        CuratorCacheListener currentNodeListener = CuratorCacheListener.builder().forNodeCache(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                List<String> invokes = curatorFramework.getChildren().forPath(path);
                invokerMap.put(serviceClass.getName(), invokes);
                System.out.println("currentNodeListener invokes:"+invokes);

                System.out.println("---------currentNodeListener------------");
                Optional<ChildData> childData = curatorCache.get(path);
                if(childData.isPresent()){
                    String path1 = childData.get().getPath();
                    String data = new String(childData.get().getData());

                    Class<? extends ChildData> aClass = childData.get().getClass();

                    System.out.println("currentNodeListener data:" + data + ",path:" + path1 + ":" + aClass.getName());
                }
            }
        }).build();
        curatorCache.listenable().addListener(currentNodeListener);

        // 监听子节点，不监听当前节点
        CuratorCacheListener childNodeListener = CuratorCacheListener.builder().forPathChildrenCache(path, curatorFramework, new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent event) throws Exception {
                System.out.println("---------childNodeListener------------");
                String type = event.getType().name();
                System.out.println("childNodeListener type:" + type);
                ChildData data = event.getData();
                if(Objects.nonNull(data)){
                    String path1 = data.getPath();
                    String nodeData = new String(data.getData());
                    System.out.println("childNodeListener: " + path1 + "," + nodeData);
                }
            }
        }).build();
        curatorCache.listenable().addListener(childNodeListener);

        //
        CuratorCacheListener treeCacheListener = CuratorCacheListener.builder().forTreeCache(curatorFramework, new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent event) throws Exception {
                System.out.println("---------treeCacheListener------------");
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
