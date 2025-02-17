package com.jinrou.jinrouwerewolf.controller;


import com.jinrou.jinrouwerewolf.entity.Result;
import com.jinrou.jinrouwerewolf.entity.User;
import com.jinrou.jinrouwerewolf.service.Impl.RedisService;
import com.jinrou.jinrouwerewolf.service.Impl.UserServiceImpl;
import com.jinrou.jinrouwerewolf.util.JwtUtil;
import com.jinrou.jinrouwerewolf.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * @Author: nanaya
 * @Date: 2024/07/08/21:10
 * @Email: qiyewuyin@gmail.com\714991699@qq.com
 * @QQ: 714991699
 * @Description:
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private PasswordUtil passwordUtil;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private RedisService redisService;

    @PostMapping("/login")
    public Result login(@RequestBody User user) {

        try {
            //接受前端传过来的User信息 auth用于表示用户名和密码的身份验证信息。
            // 验证用户名和密码
            User existingUser = userService.loadUserByAccount(user.getAccount());
            if (existingUser == null || !passwordUtil.matches(user.getPassword(), existingUser.getPassword())) {
                return Result.error("账号或密码错误");
            }

            // 如果验证成功，生成 Token
            String token = jwtUtil.getToken(existingUser);
            //将token存进redis
            System.out.println("存数据到redis");
            redisService.setValueBySeconds("jwt_token:" + existingUser.getUserId(), token, 604800); //7天 单位秒

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("token", token);
            existingUser.setPassword(null);
            responseData.put("user", existingUser);


            // 返回成功结果和 token
            return Result.success("登录成功", responseData);

        } catch (Exception e) {
            return Result.error("登录失败:错误信息:" + e.getMessage());
        }
    }

    @PostMapping("register")
    public Result doRegister(@RequestBody Map<String,Object> requestBody) {
        User user = new User();

        user.setAccount((String) requestBody.get("account"));
        user.setPassword((String) requestBody.get("password"));
        user.setEmail((String) requestBody.get("email"));
        String code = (String) requestBody.get("code");

        if (user.getPassword() == null && user.getPassword().isEmpty()) {
            return Result.error("密码不能为空");
        }
        if (user.getAccount() == null && user.getAccount().isEmpty()) {
            return Result.error("账号不能为空");
        }
        User userDetails = (User) userService.loadUserByAccount(user.getAccount());
        if (userDetails != null) {
            return Result.error("账号已存在");
        }
        if (!code.equals("114514")) {
            return Result.error("验证码错误");
        } else {
            //存数据操作

            String encodedPassword = passwordUtil.encode(user.getPassword());
            user.setPassword(encodedPassword);
            user.setUserState("UNAUTHORIZED");
            user.setUserCreateTime(new Date());

            userService.save(user);
            return Result.success(user);
        }
    }

    @PostMapping("updateUserDetail")
    public Result updateUserDetail(@RequestBody User user) {
        //查询数据库里用户，获得密码
        User DatabaseUser = userService.loadUserByAccount(user.getAccount());
        //密码为null则不修改密码，反之修改
        if(user.getPassword() != null){
            String encodedPassword = passwordUtil.encode(user.getPassword());
            user.setPassword(encodedPassword);
        }
        if(userService.updateUser(user) != 0){
            // 如果修改成功，生成 Token
            String token = jwtUtil.getToken(user);
            //将token存进redis
            System.out.println("存数据到redis");
            redisService.setValueBySeconds("jwt_token:" + user.getUserId(), token, 604800); //7天 单位秒

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("token", token);
            user.setPassword(null);
            responseData.put("user", user);


            // 返回成功结果和 token
            return Result.success("登录成功", responseData);
        }

            return Result.error("更新失败：请咨询管理员");
    }

    @PostMapping("/logout")
    public Result logout(@RequestHeader("Authorization") String token) {
        System.out.println("进入logout");
        token = token.replace("Bearer ", "");  // 去掉 "Bearer " 前缀
        // 检查并提取 Bearer Token

        try {
            // 使 Token 失效，从 Redis 中删除 Token
            redisService.delete("jwt_token:" + token);
            System.out.println("登出成功token:"+ token);
            // 返回成功的响应
            return Result.success("登出成功");
        } catch (Exception e) {
            return Result.error("登出失败: " + e.getMessage());
        }
    }


    @GetMapping("check")
    public Result doCheck() {
        System.out.println("验证成功");
        return Result.success();
    }



//    @GetMapping("me")
//    public Result me(@RequestHeader("Authorization") String token) {
//
//        return Result.success();
//    }

    @PostMapping("/valid-register-email")
    public Result send_email(@RequestBody String email) {
        // 生成验证码
        String registerCode = "114514";
        // 发送验证码到用户邮箱
        System.out.println("registerCode = " + registerCode + "email = " + email);
        return Result.success("发送验证码成功");
    }

}
