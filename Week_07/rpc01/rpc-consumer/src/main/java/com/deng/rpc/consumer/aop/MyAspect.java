package com.deng.rpc.consumer.aop;


import com.deng.rpc.core.client.Rpcfx;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

@Aspect
@Component
public class MyAspect {

    @Pointcut("execution(* com.deng.rpc.consumer.impl.*.*(..))")
    public void rpcInvoke(){

    }

    @Before("rpcInvoke()")
    public void around(JoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs(); // 传入的参数值
            for (Object arg : args) {
                System.out.println("------arg:" + arg);
            }
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod(); // 调用的方法
            System.out.println("------methodName:"+method.getName());

            Object target = joinPoint.getTarget(); // 调用的目标类，这里是QueryServiceImpl
            System.out.println("------targetName:"+target.getClass().getName());

            StringBuffer urlBuffer = new StringBuffer();
            MyInvoke classAnnotation = target.getClass().getAnnotation(MyInvoke.class);
            if(Objects.nonNull(classAnnotation)){
                urlBuffer.append(classAnnotation.url());
            }

            Field[] fields = target.getClass().getDeclaredFields(); // 目标类的所有属性
            for (Field field : fields) {
                field.setAccessible(true);
                Class<?> clazz = field.getType(); // 属性的类型
                MyInvoke fieldAnnotation = field.getAnnotation(MyInvoke.class);
                // 为有MyInvoke注解的属性，创建代理对象
                if(Objects.nonNull(fieldAnnotation)){  // TODO 有点问题
                    Object proxyObject = Rpcfx.create(clazz, fieldAnnotation.uri());
                    System.out.println("------proxyObject:" + proxyObject);
                    // 自动生成被代理对象
                    field.set(target, proxyObject);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

    }
}
