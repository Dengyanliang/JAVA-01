package com.deng.rpc.core.client;

import com.alibaba.fastjson.JSON;
import com.deng.rpc.core.api.Filter;
import com.deng.rpc.core.domain.RpcfxRequest;
import com.deng.rpc.core.domain.RpcfxResponse;
import okhttp3.MediaType;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class RpcfxInvocationHandler implements InvocationHandler {
    private final static String HOST = "host";
    private final static String PORT = "port";
    private final static String URI = "uri";
    public static final MediaType JSONTYPE = MediaType.get("application/json; charset=utf-8");

    private final Class<?> serviceClass;
    private final String url;
    private final Filter[] filters;

    public <T> RpcfxInvocationHandler(Class<T> serviceClass, String url, Filter... filters) {
        this.serviceClass = serviceClass;
        this.url = url;
        this.filters = filters;
    }

    // 可以尝试，自己去写对象序列化，二进制还是文本的，，，rpcfx是xml自定义序列化、反序列化，json: code.google.com/p/rpcfx
    // int byte char float double long bool
    // [], data class

    @Override
    public Object invoke(Object proxy, Method method, Object[] params) throws Throwable {

        // 加filter地方之二
        // mock == true, new Student("hubao");

        RpcfxRequest request = new RpcfxRequest();
        request.setServiceClass(this.serviceClass.getName());
        request.setMethod(method.getName());
        request.setParams(params);

        if (null!=filters) {
            for (Filter filter : filters) {
                if (!filter.filter(request)) {
                    return null;
                }
            }
        }

        RpcfxResponse response = post(request, url);

        // 加filter地方之三
        // Student.setTeacher("cuijing");

        // 这里判断response.status，处理异常
        // 考虑封装一个全局的RpcfxException

        return JSON.parse(response.getResult().toString());
    }

    private RpcfxResponse post(RpcfxRequest request, String url) throws IOException {
        String reqJson = JSON.toJSONString(request);
        System.out.println("req json: "+reqJson);

        // 1.可以复用client
        // 2.尝试使用httpclient或者netty client

        // nettyclient


        // 1.OKHttpClient
//            OkHttpClient client = new OkHttpClient();
//            final Request request = new Request.Builder()
//                    .url(url)
//                    .post(RequestBody.create(JSONTYPE, reqJson))
//                    .build();
//            String respJson = client.newCall(request).execute().body().string();

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
        Map urlInfo = getUrlInfo(url);
        NettyClient nettyClient = new NettyClient(urlInfo.get(HOST).toString(),Integer.parseInt(urlInfo.get(PORT).toString()));
        NettyClientHandler clientHandler = nettyClient.getClientHandler();
        clientHandler.sendMessage(reqJson,urlInfo.get(HOST).toString(),urlInfo.get(URI).toString());
        String respJson = clientHandler.getResult();
        System.out.println("resp json 1: "+respJson);

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
        map.put(HOST,addArr[0]);
        map.put(PORT,addArr[1]);
        map.put(URI,uri);

        return map;
    }
}
