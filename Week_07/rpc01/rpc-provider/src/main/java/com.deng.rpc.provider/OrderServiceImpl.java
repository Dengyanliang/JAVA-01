package com.deng.rpc.provider;


import com.deng.rpc.api.Order;
import com.deng.rpc.api.OrderService;

public class OrderServiceImpl implements OrderService {

    @Override
    public Order findOrderById(int id) {
        return new Order(id, "Cuijing" + System.currentTimeMillis(), 9.9f);
    }
}
