package com.deng.rpc.consumer.impl;

import com.deng.rpc.api.Order;
import com.deng.rpc.api.OrderService;
import com.deng.rpc.api.User;
import com.deng.rpc.api.UserService;
import com.deng.rpc.consumer.aop.MyInvoke;
import com.deng.rpc.consumer.service.QueryService;
import org.springframework.stereotype.Component;

@Component
@MyInvoke(url = "http://localhost:8082")
public class QueryServiceImpl implements QueryService {

    @MyInvoke(uri = "/invoke")
    private UserService userService;

    @MyInvoke(uri = "/invoke")
    private OrderService orderService;

    @Override
    public Order getOrderById(String url,int id) {
        return orderService.findOrderById(id);
    }

    @Override
    public User getUserById(String url,int id) {
        return userService.findById(id);
    }
}
