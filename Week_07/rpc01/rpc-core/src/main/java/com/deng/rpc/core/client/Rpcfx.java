package com.deng.rpc.core.client;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.deng.rpc.core.api.Filter;
import com.deng.rpc.core.common.Config;
import com.deng.rpc.core.domain.RpcfxRequest;
import com.deng.rpc.core.domain.RpcfxResponse;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public final class Rpcfx {

    static {
        // 解决autotype被禁止问题,这个解禁autotype只针对所设置的这个包下的对象
        ParserConfig.getGlobalInstance().addAccept("com.deng");
    }

    public static <T> T create(final Class<T> serviceClass, final String url, Filter... filters) {
        // 0. 替换动态代理 -> AOP
//        InvocationHandler invocationHandler = new RpcfxInvocationHandler(serviceClass,url,filters);
//        return (T) Proxy.newProxyInstance(Rpcfx.class.getClassLoader(), new Class[]{serviceClass}, invocationHandler);

        // 1. Cglib
        MethodInterceptor methodInterceptor = new RpcfxMethodInterceptor(serviceClass,url,filters);

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(serviceClass);
        enhancer.setCallback(methodInterceptor);

        return (T) enhancer.create();
    }

    public static RpcfxResponse post(RpcfxRequest rpcfxRequest, String url) throws IOException {
        String reqJson = JSON.toJSONString(rpcfxRequest);
        System.out.println("req json: "+reqJson);

//        XStream xStream = new XStream();
//        String reqJson = xStream.toXML(rpcfxRequest);
//        System.out.println("req xml: " + reqJson);

//        ObjectMapper mapper = new ObjectMapper();
//        String reqJson = mapper.writeValueAsString(xmlStr);

        // 1.可以复用client
        // 2.尝试使用httpclient或者netty client

        // 1.OKHttpClient
            OkHttpClient client = new OkHttpClient();
            final Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(Config.JSONTYPE, reqJson))
                    .build();
            String respJson = client.newCall(request).execute().body().string();

        // 2.Httpclient
//            HttpPost httpPost = new HttpPost(url);
//            httpPost.setEntity(new StringEntity(reqJson));
//            httpPost.setHeader("Content-Type","application/json;charset=utf8");
//
//            HttpClient httpClient = HttpClientBuilder.create().build();
//            HttpResponse response = httpClient.execute(httpPost);
//            HttpEntity responseEntity = response.getEntity();
//            String respJson = EntityUtils.toString(responseEntity);

        // 3.nettyClient
//        Map urlInfo = getUrlInfo(url);
//        NettyClient nettyClient = new NettyClient(urlInfo.get(Config.HOST).toString(),Integer.parseInt(urlInfo.get(Config.PORT).toString()));
//        NettyClientHandler clientHandler = nettyClient.getClientHandler();
//        clientHandler.sendMessage(reqJson,urlInfo.get(Config.HOST).toString(),urlInfo.get(Config.URI).toString());
//        String respJson = clientHandler.getResult();
        System.out.println("resp json : "+respJson);

        return JSON.parseObject(respJson, RpcfxResponse.class);
    }

    private static Map getUrlInfo(String url){
        int start = url.indexOf("//");
        url = url.substring(start+2);

        int end = url.indexOf("/");
        if(end == -1){
            end = url.length();
        }
        String uri = url.substring(end);
        String address = url.substring(0,end);
        String[] addArr = address.split(":");
        Map map = new HashMap();
        map.put(Config.HOST,addArr[0]);
        map.put(Config.PORT,addArr[1]);
        map.put(Config.URI,uri);

        return map;
    }
}
