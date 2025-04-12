package com.jinrou.jinrouwerewolf.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jinrou.jinrouwerewolf.entity.Game.Room;
import com.jinrou.jinrouwerewolf.entity.PasswordChangeObject;
import com.jinrou.jinrouwerewolf.entity.Result;
import com.jinrou.jinrouwerewolf.entity.User;
import com.jinrou.jinrouwerewolf.service.Impl.RedisService;
import com.jinrou.jinrouwerewolf.service.Impl.UserServiceImpl;
import com.jinrou.jinrouwerewolf.util.JwtUtil;
import com.jinrou.jinrouwerewolf.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;


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
        user.setUsername((String) requestBody.get("username"));
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

        if(user.getPassword() != null){
            String encodedPassword = passwordUtil.encode(user.getPassword());
            user.setPassword(encodedPassword);
        }
        if(userService.updateUser(user) != 0){
            // 如果修改成功，生成 Token
            String token = jwtUtil.getToken(user);
            //将token存进redis
            redisService.setValueBySeconds("jwt_token:" + user.getUserId(), token, 604800); //7天 单位秒

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("token", token);
            user.setPassword(null);
            responseData.put("user", user);


            // 返回成功结果和 token
            return Result.success("修改成功", responseData);
        }

            return Result.error("更新失败：请咨询管理员");
    }

    @PostMapping("changePassword")
    public Result changePassword(@RequestBody PasswordChangeObject passwordChangeObject) {
        System.out.println(passwordChangeObject);
        //查询数据库里用户，获得密码
        User DatabaseUser = userService.loadUserByAccount(passwordChangeObject.getAccount());

        //检验旧密码是否正确
        if(!passwordUtil.matches(passwordChangeObject.getOldPassword(), DatabaseUser.getPassword())){
            return Result.error("旧密码错误");
        }
        if(passwordChangeObject.getNewPassword() == null){
            return Result.error("新密码不能为空");
        }
        if(DatabaseUser.getPassword() != null){
            String encodedPassword = passwordUtil.encode(passwordChangeObject.getNewPassword());
            DatabaseUser.setPassword(encodedPassword);
        }
        if(userService.updateUser(DatabaseUser) != 0){
            // 如果修改成功，生成 Token
            String token = jwtUtil.getToken(DatabaseUser);
            //将token存进redis
            redisService.setValueBySeconds("jwt_token:" + DatabaseUser.getUserId(), token, 604800); //7天 单位秒

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("token", token);
            DatabaseUser.setPassword(null);
            responseData.put("user", DatabaseUser);


            // 返回成功结果和 token
            return Result.success("登录成功", responseData);
        }

        return Result.error("更新失败：请咨询管理员");
    }

    @PostMapping("/logout")
    public Result logout(@RequestHeader("Authorization") String token) {
        token = token.replace("Bearer ", "");  // 去掉 "Bearer " 前缀
        // 检查并提取 Bearer Token
        User user = jwtUtil.getTokenBody(token);
//        user.getUserId();
        try {
            // 使 Token 失效，从 Redis 中删除 Token
            redisService.delete("jwt_token:" + user.getUserId());
            // 返回成功的响应
            return Result.success("登出成功");
        } catch (Exception e) {
            return Result.error("登出失败: " + e.getMessage());
        }
    }


    @GetMapping("check")
    public Result doCheck() {
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
        return Result.success("发送验证码成功");
    }

    @PostMapping("/getUserByUserId")
    public Result getRoomsWithStatus(@RequestBody User user) {
        Integer userId = user.getUserId();
        System.out.println(userId);
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("user_id", userId);
        User getedUser = userService.getOne(wrapper);
        System.out.println(getedUser);
        return Result.success(getedUser);
    }

    @PostMapping("/validateUser")
    public Result validateUser(@RequestBody User user) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("user_id", user.getUserId());
        User getedUser = userService.getOne(wrapper);
        getedUser.setPassword(null);
        boolean valid = getedUser.equals(user);
        if(valid) {
//            // 如果验证成功，生成 Token
//            String token = jwtUtil.getToken(existingUser);
//            //将token存进redis
//            redisService.setValueBySeconds("jwt_token:" + existingUser.getUserId(), token, 604800); //7天 单位秒

//            Map<String, Object> responseData = new HashMap<>();
//            responseData.put("token", token);
//            existingUser.setPassword(null);
//            responseData.put("user", existingUser);

            // 返回成功结果
            return Result.success("验证成功", "验证成功");
        }else{
            return Result.error("本地缓存与服务器不一致，请重新登陆","本地缓存与服务器不一致，请重新登陆");
        }
    }
}
