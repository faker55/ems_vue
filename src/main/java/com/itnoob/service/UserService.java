package com.itnoob.service;

import com.itnoob.entity.User;

/**
 * @author noob
 * @created 2020-08 23 15:44
 */
public interface UserService {
    //用户注册
    void register(User user);

    //用户登录
    User login(User user);
}
