package com.deng.rpc.core.api;

import com.deng.rpc.core.domain.RpcfxRequest;

public interface Filter {

    boolean filter(RpcfxRequest request);

    // Filter next();

}
