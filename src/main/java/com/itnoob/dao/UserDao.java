package com.itnoob.dao;

import com.itnoob.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author noob
 * @created 2020-08 23 15:34
 */
@Mapper
@Repository
public interface UserDao {
    void save(User user);

    User findByUserName(String username);
}
