package com.deng.rpc.consumer;

import com.deng.rpc.core.api.Router;

import java.util.List;

public class TagRouter implements Router {
    @Override
    public List<String> route(List<String> urls) {
        return urls;
    }
}
