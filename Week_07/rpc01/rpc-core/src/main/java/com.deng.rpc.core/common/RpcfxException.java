package com.deng.rpc.core.common;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RpcfxException extends RuntimeException{
    private BizEnum errorCode;
    private String errorMessage;


    public RpcfxException(){

    }

    public RpcfxException(String message, BizEnum errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
