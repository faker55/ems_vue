package com.itnoob.service.impl;

import com.itnoob.dao.UserDao;
import com.itnoob.service.UserService;
import com.itnoob.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Date;

/**
 * @author noob
 * @created 2020-08 23 15:46
 */

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;


    /**
     * 用户注册的业务层
     * @param user
     */
    @Override
    public void register(User user) {
        //0:根据用户输入名判断用户是否存在，避免用户名一样
        User userDB = userDao.findByUserName(user.getUsername());
        if(userDB == null){
            //1：生成用户的状态
            user.setStatus("已激活");
            //2:设置才注册的时间
            user.setRegisterTime(new Date());
            //3l调用Dao
            userDao.save(user);
        }else{
            throw new RuntimeException("用户名已经存在");
        }

    }

    /**
     * 用户登录
     * @param user
     * @return
     */
    @Override
    public User login(User user) {
        //1.根据用户输入用户名进行查询 ---先确认用户名存在,并返回对象user
        User userDB = userDao.findByUserName(user.getUsername());
        if(!ObjectUtils.isEmpty(userDB)){
            //2比较密码
            if(userDB.getPassword().equals(user.getPassword())){
                return userDB;
            }else{
                throw new RuntimeException("密码错误！！！");
            }
        }else{
            throw  new RuntimeException("用户名不存在!!!");
        }

    }
}
