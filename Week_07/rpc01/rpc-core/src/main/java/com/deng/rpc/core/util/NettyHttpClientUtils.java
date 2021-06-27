//package com.deng.rpc.core.util;
//
//import com.alibaba.fastjson.JSONObject;
//import com.alibaba.fastjson.serializer.SerializerFeature;
//import com.netty.httpclient.service.nettyclient.NettyClient;
//import com.netty.httpclient.service.nettyclient.pool.NettyClientPool;
//import com.netty.httpclient.service.nettyclient.pool.NettyClientPoolCacheByHostAndPort;
//import com.netty.httpclient.service.nettyclient.vo.HostAndPortConfig;
//import com.netty.httpclient.service.nettyclient.vo.NettyHttpResponse;
//import com.netty.httpclient.utils.ModuleUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.time.StopWatch;
//
///**
// * http工具类，底层使用netty 长连接去发送请求。
// * 拿到要请求的url后，会提取出ip和端口，然后去commons pool查找对应的连接池，如果没有，则会初始化一个连接池
// * 假设连接池配置了最少要8个长连接，则连接池会初始化8个连接出来，（此时会使用netty去连接对应的服务），
// * 并返回1个可用连接。
// * 然后使用此连接，即可发送请求，并接收响应
// */
//@Slf4j
//public class NettyHttpClientUtils {
//
//
//    /**
//     * body无需序列化为json，方法内部会进行序列化
//     * @param url
//     * @param body
//     * @return
//     */
//    public static NettyHttpResponse doPost(String url, Object body) {
//        if (body instanceof String) {
//            String s = (String) body;
//            boolean bMaybeJsonObjStr = s.startsWith("{") && s.endsWith("}");
//            if (bMaybeJsonObjStr) {
//                log.error("we will convert it to json,thanks ");
//            }
//        }
//
//        StopWatch timer = new StopWatch();
//        timer.start();
//        HostAndPortConfig config = new HostAndPortConfig(ModuleUtils.extractIp(url),
//                Integer.valueOf(ModuleUtils.extractPort(url)));
//        NettyClientPool nettyClientPool = NettyClientPoolCacheByHostAndPort.getOrCreateNettyClientPoolIfNecessay(config);
//        NettyClient resource = nettyClientPool.getResource();
//        if (resource == null) {
//            throw new RuntimeException("获取连接失败");
//        }
//        timer.stop();
//        log.info("spent {} to get netty client",timer.getTime());
//
//        try {
//            timer.reset();
//            timer.start();
//            NettyHttpResponse httpResponse = resource.doPost(url, JSONObject.toJSONString(body, SerializerFeature.PrettyFormat));
//            if (httpResponse == null) {
//                throw new RuntimeException("调用超时");
//            }
//            return httpResponse;
//        }finally {
//            timer.stop();
//            log.info("spent {} to invoke rpc",timer.getTime());
//            nettyClientPool.returnResourceObject(resource);
//        }
//    }
//
//
//    public static Object doGet(String url) {
//        log.info("url:{}");
//        HostAndPortConfig config = new HostAndPortConfig(ModuleUtils.extractIp(url),
//                Integer.valueOf(ModuleUtils.extractPort(url)));
//        NettyClientPool nettyClientPool = NettyClientPoolCacheByHostAndPort.getOrCreateNettyClientPoolIfNecessay(config);
//
//        NettyClient resource = nettyClientPool.getResource();
//
//        if (resource == null) {
//            throw new RuntimeException("获取连接失败");
//        }
//
//        try {
//            NettyHttpResponse httpResponse = resource.doGet(url);
//            if (httpResponse == null) {
//                throw new RuntimeException("调用超时");
//            }
//            return httpResponse;
//        }finally {
//            nettyClientPool.returnResourceObject(resource);
//        }
//
//    }
//
//}
