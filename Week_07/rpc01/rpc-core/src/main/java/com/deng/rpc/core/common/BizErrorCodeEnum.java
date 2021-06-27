package com.deng.rpc.core.common;

public enum BizErrorCodeEnum implements BizEnum{

    SUCCESS(10000, "SUCCESS", "操作成功"),
    OPERATION_FAILED(10024, "OPERATION_FAILED", "操作失败"),
    SYSTEM_ERROR(10025, "SYSTEM_ERROR", "系统异常"),


    ;


    private int code;
    private String name;
    private String desc;

    BizErrorCodeEnum(int code, String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
    }

    @Override
    public int getCode() {
        return 0;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getDesc() {
        return null;
    }
}
