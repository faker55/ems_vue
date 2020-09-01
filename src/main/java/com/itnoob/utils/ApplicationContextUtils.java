package com.itnoob.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextUtils.applicationContext = applicationContext;
    }


    /**
     * 根据名字获取对象
     * redisTemplate  stringRedisTemplate
     */

    public  static Object getBean(String name){

        return applicationContext.getBean(name);
    }

}
