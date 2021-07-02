package com.deng.rpc.consumer.service;

import com.deng.rpc.api.Order;
import com.deng.rpc.api.User;

public interface QueryService {

    Order getOrderById(String url,int id);

    User getUserById(String url,int id);
}
