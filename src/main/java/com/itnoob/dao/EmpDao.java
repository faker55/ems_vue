package com.itnoob.dao;

import com.itnoob.entity.Emp;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author noob
 * @created 2020-08 24 14:59
 */

@Mapper
@Repository
public interface EmpDao {

    List<Emp> findAll();


    void save(Emp emp);

    void delete(String id);

    Emp findOne(String id);

    void  update(Emp emp);
}
