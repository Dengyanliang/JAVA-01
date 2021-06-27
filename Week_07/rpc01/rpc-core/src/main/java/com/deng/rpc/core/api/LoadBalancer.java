package com.deng.rpc.core.api;

import java.util.List;

public interface LoadBalancer {

    String select(List<String> urls);

}
