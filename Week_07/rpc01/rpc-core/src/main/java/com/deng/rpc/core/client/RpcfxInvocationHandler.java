package com.deng.rpc.core.client;

import com.alibaba.fastjson.JSON;
import com.deng.rpc.core.api.Filter;
import com.deng.rpc.core.domain.RpcfxRequest;
import com.deng.rpc.core.domain.RpcfxResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class RpcfxInvocationHandler implements InvocationHandler {
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

        RpcfxResponse response = Rpcfx.post(request, url);

        // 加filter地方之三
        // Student.setTeacher("cuijing");

        // 这里判断response.status，处理异常
        // 考虑封装一个全局的RpcfxException

        return JSON.parse(response.getResult().toString());
    }
}
