package com.itnoob.controller;

import com.itnoob.entity.User;
import com.itnoob.service.UserService;
import com.itnoob.utils.VerifyCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author noob
 * @created 2020-08 23 14:14
 */

@RestController
@CrossOrigin
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     *
     * @param request  为了使验证码的字符串保存早正request的作用域中
     * @return
     * @throws IOException
     * http://localhost:8080/ems_vue/user/getImage
     */
    @GetMapping("/getImage")
    public  String getImageCode(HttpServletRequest request) throws IOException {
        //1:使用工具类生成验证码
        String code = VerifyCodeUtils.generateVerifyCode(4);
        //2:将验证码放入ServletContext作用域
        request.getServletContext().setAttribute("code",code);
        //3:将图片转为base64：Base64是网络上最常见的用于传输8Bit字节码的编码方式之一，Base64就是一种基于64个可打印字符来表示二进制数据的方法
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        VerifyCodeUtils.outputImage(120,30,byteArrayOutputStream,code);
        //4:image展示base64
        String s = "data:image/png;base64,"+Base64Utils.encodeToString(byteArrayOutputStream.toByteArray());

        return s;
    }


    /**
     * 用来处理用户注册的方法
     * @param user
     * @return
     */
    @PostMapping("register")
    public Map<String,Object> register(@RequestBody User user,String code,HttpServletRequest request){
        log.info("当前登录用户的信息: [{}]",user.toString());
        log.info("用户输入的验证码信息: [{}]",code);


        Map<String,Object> map = new HashMap<>();
        //1:调用业务的方法
        try {

            String key = (String) request.getServletContext().getAttribute("code");
            if(key.equalsIgnoreCase(code)){
                userService.register(user);
                map.put("status",true);
                map.put("msg","提示:注册成功");
            }else{
                throw new RuntimeException("验证码不正确");
            }
        } catch (Exception e) {
                e.printStackTrace();
                map.put("status",false);
                map.put("msg","提示:"+e.getMessage());
        }
        return map;

    }

    /**
     * 用来处理用户登录
     * @param user user
     * @return Map集合
     */
    @PostMapping("/login.do")
    public  Map<String,Object> login(@RequestBody User user){

        log.info("当前登录用户的信息:[{}]",user.toString());

        HashMap<String, Object> map = new HashMap<>();

        try {
            User userDB = userService.login(user);
            map.put("state",true);
            map.put("msg","登录成功");
            map.put("user",userDB);

        } catch (Exception e) {
            e.printStackTrace();
            map.put("state",false);
            map.put("msg",e.getMessage());
        }

        return map;
    }

}
