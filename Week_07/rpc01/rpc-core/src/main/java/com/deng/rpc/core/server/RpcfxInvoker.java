package com.deng.rpc.core.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.deng.rpc.core.api.RpcfxResolver;
import com.deng.rpc.core.common.BizErrorCodeEnum;
import com.deng.rpc.core.common.RpcfxException;
import com.deng.rpc.core.domain.RpcfxRequest;
import com.deng.rpc.core.domain.RpcfxResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class RpcfxInvoker {

    private RpcfxResolver resolver;

    public RpcfxInvoker(){

    }

    public RpcfxInvoker(RpcfxResolver resolver){
        this.resolver = resolver;
    }

    public RpcfxResponse invoke(RpcfxRequest request) {
        RpcfxResponse response = new RpcfxResponse();
        String serviceClass = request.getServiceClass();

        // 作业1：改成泛型和反射 //this.applicationContext.getBean(serviceClass);
        Object service = resolver.resolve(serviceClass);

        Object[] params = request.getParams();
        Class<?>[] parameterTypes = new Class<?>[params.length];
        for (int i = 0; i < params.length; i++) {
            parameterTypes[i] = params[i].getClass().getComponentType();
        }

        try {
            Method method = resolveMethodFromClass(service.getClass(), request.getMethod());
//            Method method = service.getClass().getMethod(request.getMethod(),parameterTypes);
//            Object resutl2 = method1.invoke(service, request.getParams());
//            System.out.println("-------------------:" + resutl2);

            Object result = method.invoke(service, request.getParams()); // dubbo, fastjson,
            // 两次json序列化能否合并成一个
            response.setResult(JSON.toJSONString(result, SerializerFeature.WriteClassName));
            response.setStatus(true);
            return response;
        } catch (IllegalAccessException | InvocationTargetException e) {

            // 3.Xstream

            // 2.封装一个统一的RpcfxException
            // 客户端也需要判断异常
//            e.printStackTrace();

            RpcfxException rpcfxException = new RpcfxException();
            rpcfxException.setErrorMessage(e.getMessage());
            rpcfxException.setErrorCode(BizErrorCodeEnum.SYSTEM_ERROR);

            response.setException(rpcfxException);
            response.setStatus(false);
            return response;
        }
    }

    private Method resolveMethodFromClass(Class<?> klass, String methodName) {
        return Arrays.stream(klass.getMethods()).filter(m -> methodName.equals(m.getName())).findFirst().get();
    }

}
