package com.deng.rpc.core.client;

import com.alibaba.fastjson.JSON;
import com.deng.rpc.core.api.Filter;
import com.deng.rpc.core.domain.RpcfxRequest;
import com.deng.rpc.core.domain.RpcfxResponse;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class RpcfxMethodInterceptor implements MethodInterceptor {

    private final Class<?> serviceClass;
    private final String url;
    private final Filter[] filters;

    public <T> RpcfxMethodInterceptor(Class<T> serviceClass, String url, Filter... filters) {
        this.serviceClass = serviceClass;
        this.url = url;
        this.filters = filters;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] params, MethodProxy methodProxy) throws Throwable {
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
