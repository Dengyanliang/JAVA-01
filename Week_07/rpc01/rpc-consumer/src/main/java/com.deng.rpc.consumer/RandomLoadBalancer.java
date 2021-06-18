package com.deng.rpc.consumer;

import com.deng.rpc.core.api.LoadBalancer;

import java.util.List;

public class RandomLoadBalancer implements LoadBalancer {
    @Override
    public String select(List<String> urls) {
        return urls.get(0);
    }
}
