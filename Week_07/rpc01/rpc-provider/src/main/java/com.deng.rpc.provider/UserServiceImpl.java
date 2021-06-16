package com.deng.rpc.provider;


import com.deng.rpc.api.User;
import com.deng.rpc.api.UserService;

public class UserServiceImpl implements UserService {

    @Override
    public User findById(int id) {
        return new User(id, "KK" + System.currentTimeMillis());
    }
}
