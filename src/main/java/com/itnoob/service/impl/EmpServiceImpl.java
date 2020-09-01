package com.itnoob.service.impl;

import com.itnoob.dao.EmpDao;
import com.itnoob.entity.Emp;
import com.itnoob.service.EmpService;
import com.sun.media.jfxmedia.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @author noob
 * @created 2020-08 24 15:04
 */

@Service
@Transactional
public class EmpServiceImpl implements EmpService {

    @Autowired
    private EmpDao empDao;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Emp> findAll() {
        return empDao.findAll();
    }

    @Override
    public void delete(String id) {
        empDao.delete(id);
    }

    @Override
    public void save(Emp emp) {
        empDao.save(emp);
    }


    @Override
    public Emp findOne(String id) {
        return empDao.findOne(id);
    }

    @Override
    public void  update(Emp emp) {
         empDao.update(emp);
    }
}
