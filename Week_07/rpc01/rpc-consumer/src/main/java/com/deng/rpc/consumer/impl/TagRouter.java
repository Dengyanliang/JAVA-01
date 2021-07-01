package com.deng.rpc.consumer.impl;

import com.deng.rpc.core.api.Router;

import java.util.List;

public class TagRouter implements Router {
    @Override
    public List<String> route(List<String> urls) {
        return urls;
    }
}
