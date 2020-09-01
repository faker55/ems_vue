11.项目准备

  需求分析   模块  功能  
  库表设计   数据库
  详细设计   流程图伪代码方式
  编码环节
  	a.环境准备
  	b.正式进入编码环节

  测试 
  部署上线  

 2.技术选型

   前端: vue + axios 
   后端: springboot + mybatis + mysql + tomcat  + Redis 

 ==================================================================
 3.需求分析

 	用户模块
	 	a.用户登录
	 	b.用户注册
	 	c.验证码实现
	 	d.欢迎xx用户展示
	 	e.安全退出
	 	f.员工列表展示

	员工模块
	 	g.员工添加
	 	h.员工删除
	 	i.员工修改
	 	j.员工列表加入redis缓存实现

4.库表设计
	
	1.分析系统中有那些表   2.分析表与表之间关系   3.分析表中字段
	
	用户表
			id  username realname password sex status registerTime
	员工表
			id  name path(头像) salary age
	
	2.创建库表
		create table t_user(
			id int(6) primary key auto_increment,
			username varchar(60),
			realname varchar(60),
			password varchar(50),
			sex      varchar(4),
			status   varchar(4),
			registerTime timestamp
		);
	
		create table t_emp(
			id int(6) primary key auto_increment,
			name varchar(40),
			path varchar(100),
			salary double(10,2),
			age  int(3)
		);
	
	3.库表入库   emp
===============================================================
5.编码环节
	a.环境搭建
		springboot +  mybatis + mysql  引入员工管理系统页面

		项目名: ems_vue  
		项目中包:
			src/main/java
				com.baizhi.xxx
						  .util
						  .dao
						  .service
						  ......
			src/main/resource
					application.yml(properties)		 springboot配置文件
					application-dev.yml	 测试
					application-prod.yml 生产配置
					com/baizhi/mapper/   用来存放mybatis的mapper配置文件
					com/baizhi/sql		 用来放项目中数据库文件
					static               用来存放静态资源
	
		项目编码: UTF-8
=====================================================================
6.模块化编码
	用户模块

# a.用户登录     ----> 完成

+ 1:先写Service接口

  ```java
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
  ```

+ 2:实现接口

  ```java
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
  ```

+ 3 控制器

  ```java
  /**
       * 用来处理用户登录
       * @param user user
       * @return Map集合
       */
      @PostMapping("/login.do")
      public  Map<String,Object> login(@RequestBody User user){
          HashMap<String, Object> map = new HashMap<>();
  
          try {
              User userDB = userService.login(user);
              map.put("state",true);
              map.put("msg","登录成功");
  
          } catch (Exception e) {
              e.printStackTrace();
              map.put("state",false);
              map.put("msg",e.getMessage());
          }
  
          return map;
      }
  ```

+ 4 前端页面--login.html

  ```java
  1:vue模板
  2:v-model 双向绑定数据
  3:提交表单的类型为button
  
  <script src="./js/vue.js"></script>
  <script src="./js/axios.min.js"></script>
  <script>
  	var app = new Vue({
  		el:"#wrap",
  		data:{
  			user:{} //user对象用来保存用户数据
  		},
  		methods:{
  			//用户登录
  			login(){
  				// console.log(this.user);
  				//发送axios.请求
  				axios.post("http://localhost:8080/ems_vue/user/login.do",this.user).then(res=>{
  					console.log(res.data);
  					if(res.data.state){
  						alert(res.data.msg+",点击确定主页!");
  						location.href="/ems_vue/emplist.html";
  					}else{
  						alert(res.data.msg);
  					}
  				})
  			}
  		}
  		
  
  </script>
  
  <input type="button" @click="login"  class="button" value="Submit &raquo;" />
  ```

  

# b:用户注册     ----> 完成

+ 1 写对应的用户表的实体类---根据数据库

  ```java
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
  ```

+ 2：mybatis--写接口

  ```java
  package com.itnoob.dao;
  
  import com.itnoob.entity.User;
  import org.apache.ibatis.annotations.Mapper;
  
  /**
   * @author noob
   * @created 2020-08 23 15:34
   */
  @Mapper  //用来创建DAO对象
  @Repository
  public interface UserDao {
      void save(User user);
  }
  ```

+ 3:写对应的mapper

  ```xml
  <?xml version="1.0" encoding="utf-8" ?>
  <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
  <!--        namespace是用于绑定Dao接口的，即面向接口编程-->
  <mapper namespace="com.itnoob.dao.UserDao">
  
  <!--    save-->
      <insert id="save" parameterType="com.itnoob.entity.User" useGeneratedKeys="true" keyProperty="id">
          insert into t_user values (#{id},#{username},#{realname},#{password},#{sex},#{statuc},#{registerTime})
  
      </insert>
  
  </mapper>
  ```

+ 4:也业务层Service---接口

  ```java
  package com.itnoob.service;
  
  import com.itnoob.entity.User;
  
  /**
   * @author noob
   * @created 2020-08 23 15:44
   */
  public interface UserService {
      //用户注册
      void register(User user);
  }
  ```

  

+ 5：业务层Service的--接口实现

  ```java
  package com.itnoob.service.impl;
  
  import com.itnoob.dao.UserDao;
  import com.itnoob.service.UserService;
  import com.itnoob.entity.User;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.stereotype.Service;
  import org.springframework.transaction.annotation.Transactional;
  
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
       * 用户测试的业务层
       * @param user
       */
      @Override
      public void register(User user) {
          //1：生成用户的状态
          user.setStatus("已激活");
          //2:设置才注册的时间
          user.setRegisterTime(new Date());
          //3l调用Dao
          userDao.save(user);
      }
  }
  
  ```

+ 6 控制器-编写注册的方法

  ```java
  package com.itnoob.controller; 
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
                  map.put("msg","提示:注册失败");
          }
          return map;
  
      }
  ```

+ 7 前台对象绑定

  + 7.1
  + 先定义一个对象

![V1](C:\Users\周军\Desktop\V1.png)

+ 7.2 ：绑定数据-v-model

+ ![V2](C:\Users\周军\Desktop\V2.png)

+ ```java
  var app = new Vue({
  		el:"#wrap",
  		data:{
  			url:"",
  			user:{
  				sex: "男"//设置默认值
  			},
  			code:"", //验证码的信息
  		},
  ```

+ 7.3  绑定验证码

  ![V3](C:\Users\周军\Desktop\V3.png)

8：给Submit绑定点击事件---注意是button

```html
<p>
	<input type="button" @click="register" class="button" value="Submit &raquo;" />
</p>
```

9: vue中的axios请求

```javascript
//用来注册用户信息
			register(){
				axios.post("http://localhost:8080/ems_vue/user/register?code="+this.code,this.user).then(res=>{
					console.log(res.data);
				})
			},
```



10:解决用户名字重复注册的问题

```java
package com.itnoob.dao;

@Mapper
@Repository
public interface UserDao {
    void save(User user);

    User findByUserName(String username);   //添加一个新方法
}
```

11：编写对应mapper.xml sql语句

```xml
<select id="findByUserName" parameterType="string"  resultType="com.itnoob.entity.User">
        select id,username,realname,password,sex,status,registerTime 
        from t_user where username=#{username}
    </select>
```

12:修改UserServiceImpl,java

```java
 package com.itnoob.service.impl;
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
```

13:修改UserController.java

```java
 map.put("msg","提示:"+e.getMessage());
```

14:修改前端提示regist.html

```java
//用来注册用户信息
			register(){
				axios.post("http://localhost:8080/ems_vue/user/register?code="+this.code,this.user).then(res=>{
					console.log(res.data);
					if(res.data.status){
						//返回true则跳转
						alert(res.data.msg+",点击确定，跳转到登录界面");
						location.href="/ems_vue/login.html";

					}else {
						alert(res.data.msg);
					}

				})
			}
```







# c:验证码实现   ----> 完成

+ 发送一个请求到后台，通过验证码的VerifyCodeUtils.java工具类生成

  ```java
  @RestController
  @CrossOrigin
  @RequestMapping("/user")
  public class UserController {
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
  }
  ```

+ 前端页面处理：

  ```java
  regist.html
  
  1：绑定数据
  验证码:
  	<img id="num" :src="url" />
       <a href="javascript:;" @click="getImg" >换一张</a>
  
  
  
  
  
  <script src="./js/vue.js"></script>
  <script src="./js/axios.min.js"></script>
  <script>
  	var app = new Vue({
  		el:"#wrap",
  		data:{
  			url:"",
  		},
  		methods:{
  			//用来更换验证码
  			getImg(){
  				this.getSrc();
  			},
  			//获取验证码的函数
  			getSrc(){
  				var _this = this;
  				axios.get("http://localhost:8080/ems_vue/user/getImage?time="+Math.random()).then(res=>{
  					console.log(res.data())
  					_this.uri = res.data;
  				})
  			}
  		},
  		created(){
  			//获取验证码
  			this.getSrc();
  		}
  	})
  
  </script>
  
  ```

+ 注意点：

  ```java
  "data:image/png;base64,"   格式不要少了
  ```


# d.欢迎xx用户展示 ----> 完成

+ 前后端不分离的时候用是是HttpServletSession ，保存用户的登录信息

+ 前后端分离：将登录的用户信息放入localStorage key value

  ```java
  添加代码：UserController.java ---》login.do
    map.put("user",userDB);
  
  添加代码：login.html
  //将用户的信息放入LocalStorage key value
  //JSON.stringify() 方法用于将 JavaScript 值转换为 JSON 字符串
  localStorage.setItem("user",JSON.stringify(res.data.user));
  alert(res.data.msg+",点击确定主页!");
  location.href="/ems_vue/emplist.html";
  ```

+ emplist.html 代码部分

  ```java
  1:user数据不为空的时候，就展示名字
  <h1>
  	Welcome!  <span v-show="user!=null"> {{ user.realname}}</span>
  </h1>
  2：
  <script>
  	var app = new Vue({
  		el:"#wrap",
  		data:{ //数据
  			user:{} //用来存放用户登录信息
  		},
  		methods:{  //自定义函数
  		},
  		created(){
  			var userString = localStorage.getItem("user");
  			if(userString){
  				this.user = JSON.parse(userString);
  			}else{
  				alert("您尚未登录，点击确定跳转到登录页面");
  				location.href="/ems_vue/login.html";
  			}
  
  		}
  	})
  
  </script>
  
  ```

  

#  e.安全退出      -----> 完成
​	 	1：添加vue@click事件

```java
<a href="javascript:;" @click="logout"> 安全退出 </a>	

```

​	2：添加一个logout方法

```java

methods:{  //自定义函数
			//处理安全退出
			logout(){
				localStorage.removeItem("user");
				location.reload(true); //刷新页面
			}
		},
```

3：修改其它页面的安全退出-emplist.html,addxx,update,jtml

```html
<div id="header">
		<div id="rightheader">
	<p>
		2009/11/20 
    <br />
    <a href="javascript:;" @click="logout"> 安全退出 </a>
	</p>
		</div>
			<div id="topheader">
				<h1 id="title">
					<a href="/ems_vue/emplist.html">main</a>
						</h1>
		</div>
			<div id="navigation">
		</div>
</div>
```

js也要添加到对应的

```html
<script src="./js/vue.js"></script>
<script src="./js/axios.min.js"></script>
<script>
	var app = new Vue({
		el:"#wrap",
		data:{ //数据
			user:{} //用来存放用户登录信息
		},
		methods:{  //自定义函数
			//处理安全退出
			logout(){
				localStorage.removeItem("user");
				location.reload(true); //刷新页面
			}
		},
		created(){
			var userString = localStorage.getItem("user");
			if(userString){
				this.user = JSON.parse(userString);
			}else{
				alert("您尚未登录，点击确定跳转到登录页面");
				location.href="/ems_vue/login.html";
			}

		}
	})

</script>
```



	员工模块
		f.员工列表展示  ------>完成
	 	g.员工添加	  ------>完成
	 	h.员工删除	  ------>完成
	 	i.员工修改      ----->完成
	 	j.员工列表加入redis缓存实现---------->完成

# f.员工列表展示  ------>完成

+ 1 创建实体对象

+ ```java
  package com.itnoob.entity;
  
  import lombok.Data;
  import lombok.experimental.Accessors;
  
  import java.io.Serializable;
  
  @Data
  @Accessors(chain = true)
  public class Emp  implements Serializable {
      private String id;
      private String name;
      private String path;
      private Double salary;
      private Integer age;
  
  }
  ```

+ 2 创建mybatis接口

  ```java
  package com.itnoob.dao;
  
  import com.itnoob.entity.Emp;
  
  import java.util.List;
  
  /**
   * @author noob
   * @created 2020-08 24 14:59
   */
  public interface EmpDao {
      
      List<Emp> findAll();
  }
  ```

  3:mybatis实现mapper.xml sql编写 ---EmpDaoMapper.xml

  ```java
  <?xml version="1.0" encoding="utf-8" ?>
  <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
  <!--        namespace是用于绑定Dao接口的，即面向接口编程-->
  <mapper namespace="com.itnoob.dao.EmpDao">
  
  
      <select id="findAll" resultType="com.itnoob.entity.Emp">
          select id,name,path,salary,age from t_emp 
      </select>
      
  </mapper>
  ```

  

+ 4 构建业务层--EmpService

  ```java
  package com.itnoob.service;
  
  import com.itnoob.entity.Emp;
  
  import java.util.List;
  
  /**
   * @author noob
   * @created 2020-08 24 15:03
   */
  public interface EmpService {
      List<Emp> findAll();
  }
  ```

+ 5 构建业务层实现--EmpServiceImpl

  ```java
  package com.itnoob.service.impl;
  
  import com.itnoob.dao.EmpDao;
  import com.itnoob.entity.Emp;
  import com.itnoob.service.EmpService;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.stereotype.Service;
  import org.springframework.transaction.annotation.Propagation;
  import org.springframework.transaction.annotation.Transactional;
  
  import java.util.List;
  
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
  }
  ```

  + 前端的数据修改

  + emplist.html

  + ```java
    1:emps:[],
    2: 没有逗号分隔
    var _this=this;
    //查询所有信息
    axios.get("http://localhost:8080/ems_vue/emp/findAll").then(res=>{
        _this.emps = res.data;
    });
    
    
    3:v-for 遍历
    <tr  v-for="(emp,index) in emps "  :key="emp.id" :class="index%2==0?'row1':'row2'">
    							<td>
    								{{ emp.id}}
    							</td>
    							<td>
    								{{ emp.name}}
    							</td>
    							<td>
    								<img :src="emp.path" style="height: 60px;">
    							</td>
    							<td>
    								{{emp.salary}}
    							</td>
    							<td>
    								{{emp.age}}
    							</td>
    							<td>
    								<a href="emplist.html">delete emp</a>&nbsp;<a href="updateEmp.html">update emp</a>
    							</td>
    						</tr>
    
    ```

    

# g.员工添加	  ------>完成

+ 1 前端页面：--addEmp.html

  ```java
  1:修改为button，避免提交跳转
  <p>
  	<input type="button" class="button" value="Confirm"  @click="saveEmp"/>
  </p>
  
  2:定义一个员工对象
  emp:{}  //定义一个员工信息对象
  
  3：v-model 绑定数据
  
  	<input type="text" class="inputgri"   v-model="emp.name"  name="name" />
      <input type="file"  ref="myPhoto" name="photo" />
           省略其它的
      
      
  			
  ```

  2：vue

  ```java
  	//保存员工的信息
  			saveEmp(){
  				console.log(this.emp);
  				console.log(this.$refs.myPhoto.files[0]); //获取ref 绑定的文件信息
  				//文件上传时 请求方式必须是post  enctype必须为multipart/form-data
  				
  				var formData = new FormData();
  				formData.append("name",this.emp.name);
  				formData.append("salary",this.emp.salary);
  				formData.append("age",this.emp.age);
  				formData.append("photo",this.$refs.myPhoto.files[0]);
  				axios({
  					method:"post",
  					url:"http://localhost:8080/ems_vue/emp/save",
  					data:formData,
  					headers:{
  						'content-type':'multipart/form-data'
  					}
  				}).then(res=>{
  					console.log(res.data)
  				})
  			}
  ```

+ 3 后台接口开发

  + 3.1 EmpController

    ```java
    //保存员工信息
        @PostMapping("/save")
        public Map<String,Object> save(Emp emp, MultipartFile photo){
            log.info("员工信息:[{}]",emp.toString());
            log.info("头像信息:[{}]",photo.getOriginalFilename());
            Map<String,Object> map = new HashMap<>();
            return map;
        }
    
    
    ```

  + 2:配置yml文件和新建文件夹photos

    ```yaml
    1:声明上传的目录application.yml  
    photo:
      dir: E:\\vue\\ems_vue\\src\\main\\resources\\static\\photos
      
    2:
    #datasource
    spring:
      application:
        name: user
      datasource:
        type: com.alibaba.druid.pool.DruidDataSource
        driver-class-name: com.mysql.cj.jdbc.Driver
        url: jdbc:mysql://localhost:3306/emp?serverTimezone=UTC&useSSl=false
        username: root
        password: mysql
      thymeleaf:
        check-template: false
      resources:
        static-locations: classpath:/static/,file:${photos.dir}   
        
       
    3:注意路径配置
    <tr  v-for="(emp,index) in emps "  :key="emp.id" :class="index%2==0?'row1':'row2'">
    							<td>
    								{{ emp.id}}
    							</td>
    							<td>
    								{{ emp.name}}
    							</td>
    							<td>
    								<img :src="'/ems_vue/'+emp.path" style="height: 60px;">
      
    
    ```

    + EmpController.java

      ```java
      //保存员工信息
          @PostMapping("/save")
          public Map<String,Object> save(Emp emp, MultipartFile photo) throws IOException {
              log.info("员工信息:[{}]", emp.toString());
              log.info("头像信息:[{}]", photo.getOriginalFilename());
              Map<String, Object> map = new HashMap<>();
      
              try {
                  //头像保存
                  String newFileName = UUID.randomUUID().toString() + "." + FilenameUtils.getExtension(photo.getOriginalFilename());
                  photo.transferTo(new File(realPath, newFileName));
      
                  //设置头像地址
                  emp.setPath(newFileName);
      
      
                  //保存员工信息
                  empService.save(emp);
                  map.put("state", true);
                  map.put("msg", "员工信息保存成功");
              } catch (Exception e) {
                  e.printStackTrace();
                  map.put("state", false);
                  map.put("msg", "员工信息保存失败");
              }
              return map;
          }
      ```

    + vue配置

      ```java
      //保存员工的信息
      			saveEmp(){
      				console.log(this.emp);
      				console.log(this.$refs.myPhoto.files[0]); //获取ref 绑定的文件信息
      				//文件上传时 请求方式必须是post  enctype必须为multipart/form-data
      
      				var formData = new FormData();
      				formData.append("name",this.emp.name);
      				formData.append("salary",this.emp.salary);
      				formData.append("age",this.emp.age);
      				formData.append("photo",this.$refs.myPhoto.files[0]);
      				var _this = this;
      				axios({
      					method:"post",
      					url:"http://localhost:8080/ems_vue/emp/save",
      					data:formData,
      					headers:{
      						'content-type':'multipart/form-data'
      					}
      				}).then(res=>{
      					console.log(res.data)
      					if(res.data.state){
      						if(window.confirm(res.data.msg+",点击确认跳转到列表页面！")){
      							location.href="/ems_vue/emplist.html"
      						}else{
      							_this.emp={};
      						}
      					}else{
      						alert(res.data.msg);
      					}
      				})
      			}
      		}
      ```

    # h:员工删除------>完成

    + 1 EmpDao

      ```java
      void delete(String id);
      ```

    + mybatis --EmpDaoMapper.xml

      ```xml
          <delete id="delete"  parameterType="String">
              delete from t_emp where id = #{id}
          </delete>
      ```

      

    + service常规

      ```java
      void delete(String id);
      ```

    + servieimpl

      ```java
        @Override
          public void delete(String id) {
              empDao.delete(id);
          }
      ```

    + 控制器

      ```java
      
      ```

    + 前端

      ```java
      1:添加单机事件，绑定方法
      <a href="javascript:;" @click="delete(emp.id)">删除</a>
      2:写vue 方法
      
      ```

    + 因为删除员工信息的时候不会删除图片，所以改进一下；

      ```java
      1:void findOne(String id);
      
      
      2: <select id="findOne" parameterType="string"  resultType="com.itnoob.entity.Emp">
               select id,name,path,salary,age from t_emp
               where id = #{id}
          </select>
          
              
              
       3: Emp findOne(String id);
      
      
      4:  @Override
          public Emp findOne(String id) {
              return empDao.findOne(id);
          }
      ```

    + controller

      ```java
      @GetMapping("/delete")
          public Map<String,Object> delete(String id){
                  log.info("删除员工的id:{}",id);
                  Map<String,Object> map = new HashMap<>();
              try {
                  //2 删除员工头像
                  Emp emp = empService.findOne(id);
                  File file = new File(realPath, emp.getPath());
                  if(file.exists()){file.delete();}  //删除头像
                  
      
                  //1：删除员工信息
                  empService.delete(id);
                  map.put("state",true);
                  map.put("msg","删除员工信息成功");
              } catch (Exception e) {
                  e.printStackTrace();
                  map.put("state",false);
                  map.put("msg","删除员工信息失败");
              }
              return map;
          }
      ```



# h:员工更新--->完成

+ 1：标签页面跳转点击事件--传递一个id

  ```html
  <a :href="'/ems_vue/updateEmp.html?id='+emp.id" >修改</a>      
  注意:href 不要忘记:
      格式：''的位置
  ```

  

+ 2： vue 获取对应的id 

  ```javascript
  created(){
  			var userString = localStorage.getItem("user");
  			if(userString){
  				this.user = JSON.parse(userString);
  			}else{
  				alert("您尚未登录，点击确定跳转到登录页面");
  				location.href="/ems_vue/login.html";
  			}
  			//获取对应的id信息
  			var start = location.href.lastIndexOf("=");
  			var id = location.href.substring(start +1);
  			console.log(id);//
  			//查询一个人的信息
  			axios.get("http://localhost:8080/ems_vue/emp/findOne?id="+id).then(res=>{
  				//回显信息
  				console.log(res.data);
  			})
  ```

  

  ​	3:控制器:

  ```java
   /**
       * 根据id查询信息实现
       * @param id
       * @return
       */
      @GetMapping("/findOne")
      public Emp findOne(String id){
           return  empService.findOne(id);
      }
  ```

  4:把回显的数据绑定要html中

  ```java
  data:{ //数据
  			user:{}, //用来存放用户登录信息
  			emp:{} //展示数据的对象
  		},
  
  
  
  回显数据 ：
  //获取对应的id信息
  			var start = location.href.lastIndexOf("=");
  			var id = location.href.substring(start +1);
  			console.log(id);//
  			//查询一个人的信息
  			var _this = this;
  			axios.get("http://localhost:8080/ems_vue/emp/findOne?id="+id).then(res=>{
  				//回显信息
  				console.log(res.data);
  				_this.emp = res.data;
  			})
  ```

  

  5 v-model 双向展示

  ```html
  <tr>
      <td valign="middle" align="right">
          id:
      </td>
      <td valign="middle" align="left">
          {{emp.id}}
      </td>
  </tr>
  <tr>
      <td valign="middle" align="right">
          old photo:
      </td>
      <td valign="middle" align="left">
          <img :src="'/ems_vue/'+emp.path" style="height: 60px;" alt="">
      </td>
  </tr>
  
  
  省略
  
  <input type="text" class="inputgri"  v-model="emp.name" name="name" value="zhangshan"/>
  ```

+ 修改button

  ```html
  <input type="button"  @click="editEmp"  class="button" value="Confirm" />
  ```

+ vue修改

+ ```javascript
  //处理员工信息修改
  editEmp(){
      console.log(this.emp);
      console.log(this.$refs.photo.files[0]);
      //文件上传时 请求方式必须是post  enctype必须为multipart/form-data
      var formData = new FormData();
      formData.append("id",this.emp.id)
      formData.append("name",this.emp.name);
      formData.append("path",this.emp.path);
      formData.append("salary",this.emp.salary);
      formData.append("age",this.emp.age);
      formData.append("photo",this.$refs.photo.files[0]);
      var _this = this;
      axios({
          method:"post",
          url:"http://localhost:8989/ems_vue/emp/update",
          data:formData,
          headers:{
              'content-type':'multipart/form-data'
          }
      }).then(res=>{
          console.log(res.data);
          if(res.data.state){
              if(window.confirm(res.data.msg+",点击确定跳转到列表页面!")){
                  location.href="/ems_vue/emplist.html";
              }
          }else{
              alert(res.data.msg);
          }
      });
  
  }
  ```

+ 后台控制器代码

  ```java
  //修改员工信息
  @PostMapping("update")
  public Map<String, Object> update(Emp emp, MultipartFile photo) throws IOException {
      log.info("员工信息: [{}]", emp.toString());
  
      Map<String, Object> map = new HashMap<>();
      try {
          if(photo!=null&&photo.getSize()!=0){
              log.info("头像信息: [{}]", photo.getOriginalFilename());
              //头像保存
              String newFileName = UUID.randomUUID().toString() + "." + FilenameUtils.getExtension(photo.getOriginalFilename());
              photo.transferTo(new File(realPath, newFileName));
              //设置头像地址
              emp.setPath(newFileName);
          }
          //保存员工信息
          empService.update(emp);
          map.put("state", true);
          map.put("msg", "员工信息保存成功!");
      } catch (Exception e) {
          e.printStackTrace();
          map.put("state", false);
          map.put("msg", "员工信息保存失败!");
      }
      return map;
  }
  ```

  +  mybatis

+ ```xml
  <!--update-->
      <update id="update" parameterType="com.itnoob.entity.Emp">
          update t_emp set
          name =#{name},
          path = #{path},
          salary = #{salary},
          age = #{age}
          where
          id = #{id}
      </update>
  ```







# redis使用

+ 引入redis的依赖

  ```xml
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-redis</artifactId>
      <version>2.3.2.RELEASE</version>
  </dependency>
  ```

  

+ yml配置在spring:

  ```xml
    # redis
    redis:
        host: 127.0.0.1
        port: 6379
        database: 0
  ```

测试一下redis能不能用

```java
package com.itnoob.ems_vue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
class EmsVueApplicationTests {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void test(){
        stringRedisTemplate.opsForValue().set("name1","小周");
    }
}
```

+ 验证去redis-cli去查看





开始redis做缓存:

+ 1:新建cache包

  ```java
  package com.itnoob.cache;
  
  import org.apache.ibatis.cache.Cache;
  
  /**
   * @author noob
   * @created 2020-09 01 11:29
   */
  public class RedisCache  implements Cache {
      private String id;
  
      public RedisCache(String id) {
          this.id = id;
      }
  
      @Override
      public String getId() {
          return this.id;
      }
  
      /**
       * 放入redis缓存
       * @param o
       * @param o1
       */
      @Override
      public void putObject(Object o, Object o1) {
  
      }
  
      /**
       * 从redis中获取缓存
       * @param o
       * @return
       */
      @Override
      public Object getObject(Object o) {
          return null;
      }
  
      @Override
      public Object removeObject(Object o) {
          return null;
      }
  
      @Override
      public void clear() {
  
      }
  
      @Override
      public int getSize() {
          return 0;
      }
  }
  ```

+ 2:需要注入redistemplates，因为我们的cache是mybatis进行实例化的，所以我们的spring拿不到这个对象。我们没有办法直接使用spring注入语法，所以开发一个工具类。

+ ```java
  package com.itnoob.utils;
  
  import org.springframework.beans.BeansException;
  import org.springframework.context.ApplicationContext;
  import org.springframework.context.ApplicationContextAware;
  import org.springframework.stereotype.Component;
  
  @Component
  public class ApplicationContextUtils implements ApplicationContextAware {
  	/**
  	*  自定义一个静态成员变量
  	*/
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
  ```

+ 3：去cache包中获取对象的实例

+ ```java
  package com.itnoob.cache;
  
  import com.itnoob.utils.ApplicationContextUtils;
  import org.apache.ibatis.cache.Cache;
  import org.springframework.context.ApplicationContext;
  import org.springframework.data.redis.core.RedisTemplate;
  import org.springframework.data.redis.serializer.StringRedisSerializer;
  
  /**
   * @author noob
   * @created 2020-09 01 11:29
   */
  public class RedisCache  implements Cache {
      private String id;
  
      public RedisCache(String id) {
          this.id = id;
      }
  
      @Override
      public String getId() {
          return this.id;
      }
  
      /**
       * 放入redis缓存
       * @param o
       * @param o1
       */
      @Override
      public void putObject(Object o, Object o1) {
          
  
      }
  
      /**
       * 从redis中获取缓存
       * @param o
       * @return
       */
      @Override
      public Object getObject(Object o) {
          return null;
      }
  
      @Override
      public Object removeObject(Object o) {
          return null;
      }
  
      @Override
      public void clear() {
  
      }
  
      @Override
      public int getSize() {
          return 0;
      }
  
      /**
       * 封装获取redisTemplate的方法
       * @return
       */
      public RedisTemplate getRedisTemplate(){
          RedisTemplate redisTemplate = (RedisTemplate) ApplicationContextUtils.getBean("redisTemplate");
          redisTemplate.setKeySerializer(new StringRedisSerializer());
          redisTemplate.setHashKeySerializer(new StringRedisSerializer());  //把key设置为string类型的序列化
          return redisTemplate;
      }
  }
  ```

+ 4:把要放入redis的对象类实现序列化implements Serializable

  ```java
  package com.itnoob.entity;
  
  import lombok.Data;
  import lombok.experimental.Accessors;
  
  import java.io.Serializable;
  
  @Data
  @Accessors(chain = true)
  public class Emp  implements Serializable {
      private String id;
      private String name;
      private String path;
      private Double salary;
      private Integer age;
  
  }
  ```

+ 5;

  ```java
  package com.itnoob.cache;
  
  import com.itnoob.utils.ApplicationContextUtils;
  import lombok.extern.slf4j.Slf4j;
  import org.apache.ibatis.cache.Cache;
  import org.springframework.data.redis.core.RedisTemplate;
  import org.springframework.data.redis.serializer.StringRedisSerializer;
  
  /**
   * @author noob
   * @created 2020-09 01 11:29
   */
  
  @Slf4j
  public class RedisCache  implements Cache {
      
      private String id;
  
      public RedisCache(String id) {
          log.info("当前缓存的namespace:[{}]",id);
          this.id = id;
      }
  
      @Override
      public String getId() {
          return this.id;
      }
  
      /**
       * 放入redis缓存
       * @param key
       * @param value
       */
      @Override
      public void putObject(Object key, Object value) {
          log.info("放入缓存key:[{}],放入缓存的value[{}]",key,value);
          getRedisTemplate().opsForHash().put(id,key.toString(),value);
  
  
      }
  
      /**
       * 从redis中获取缓存
       * @param key
       * @return
       */
      @Override
      public Object getObject(Object key) {
  
          log.info("获取缓存key:[{}]",key.toString());
          return getRedisTemplate().opsForHash().get(id,key.toString());
      }
  
      @Override
      public Object removeObject(Object o) {
          return null;
      }
  
      @Override
      public void clear() {
          log.info("清除所有缓存...");
          getRedisTemplate().delete(id);
  
      }
  
      @Override
      public int getSize() {
          return getRedisTemplate().opsForHash().size(id).intValue();
      }
  
      /**
       * 封装获取redisTemplate的方法
       * @return
       */
      public RedisTemplate getRedisTemplate(){
          RedisTemplate redisTemplate = (RedisTemplate) ApplicationContextUtils.getBean("redisTemplate");
          redisTemplate.setKeySerializer(new StringRedisSerializer());
          redisTemplate.setHashKeySerializer(new StringRedisSerializer());  //把key设置为string类型的序列化
          return redisTemplate;
      }
  }
  ```

+ 6:在mapper中配置redis

  ```xml
  <mapper namespace="com.itnoob.dao.EmpDao">
      
      <cache type="com.itnoob.cache.RedisCache" />
  ```

  