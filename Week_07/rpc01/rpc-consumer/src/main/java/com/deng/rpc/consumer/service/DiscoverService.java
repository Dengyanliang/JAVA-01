package com.deng.rpc.consumer.service;

import java.util.List;

public interface DiscoverService {
    List<String> getInvokers(Class<?> serviceClass);
}
