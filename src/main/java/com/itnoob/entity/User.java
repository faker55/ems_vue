package com.itnoob.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author noob
 * @created 2020-08 23 15:29
 *
 *Accessor的中文含义是存取器，@Accessors用于配置getter和setter方法的生成结果
 * chain的中文含义是链式的，设置为true，则setter方法返回当前对象。
 */
@Data
@Accessors(chain = true)
public class User {
    private String id;//String 类型api 相当多
    private String username;
    private String realname;
    private String password;
    private String sex;
    private String status;
    private Date registerTime;
}
