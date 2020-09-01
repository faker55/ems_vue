package com.itnoob.service;

import com.itnoob.entity.Emp;
import java.util.List;

/**
 * @author noob
 * @created 2020-08 24 15:03
 */

public interface EmpService {
    List<Emp> findAll();

    void save(Emp emp);

    void delete(String id);

    Emp findOne(String id);


    void update(Emp emp);
}
