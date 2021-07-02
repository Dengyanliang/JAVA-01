package com.deng.rpc.core.common;

import okhttp3.MediaType;

public class Config {
    public static final String ZK_CONNECT_STRING = "127.0.0.1:2181";
    public static final String ZK_NAMESPACE = "rpcfx";
    public static final String ZK_ROOT_PATH = "/rpcfx";
    public static final String OBLIQUE_LINE = "/";
    public static final String UNDERLINE = "_";
    public static final String PROVIDER = "provider";
    public static final String SERVICE = "service";
    public static final String HOST = "host";
    public static final String PORT = "port";
    public static final String URI = "uri";
    public static final MediaType JSONTYPE = MediaType.get("application/json; charset=utf-8");

}
