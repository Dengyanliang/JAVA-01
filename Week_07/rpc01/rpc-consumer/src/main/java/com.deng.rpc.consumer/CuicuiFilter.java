package com.deng.rpc.consumer;

import com.deng.rpc.core.api.Filter;
import com.deng.rpc.core.domain.RpcfxRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CuicuiFilter implements Filter {
    @Override
    public boolean filter(RpcfxRequest request) {
        log.info("filter {} -> {}", this.getClass().getName(), request.toString());
        return true;
    }
}
