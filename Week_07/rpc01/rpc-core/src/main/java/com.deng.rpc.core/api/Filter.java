package com.deng.rpc.core.api;

public interface Filter {

    boolean filter(RpcfxRequest request);

    // Filter next();

}
