package com.deng.rpc.consumer.aop;


import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD,ElementType.TYPE,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MyInvoke {
    String url() default "";
    String uri() default "";
}
