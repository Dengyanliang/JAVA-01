package com.deng.rpc.core.common;

import java.io.Serializable;

public interface BizEnum extends Serializable {
    int getCode();

    String getName();

    String getDesc();
}
