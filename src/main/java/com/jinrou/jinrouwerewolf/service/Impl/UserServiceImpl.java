package com.jinrou.jinrouwerewolf.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinrou.jinrouwerewolf.dao.UserDao;
import com.jinrou.jinrouwerewolf.entity.Result;
import com.jinrou.jinrouwerewolf.entity.User;
import com.jinrou.jinrouwerewolf.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: nanaya
 * @Date: 2024/07/08/7:46
 * @Email: qiyewuyin@gmail.com\714991699@qq.com
 * @QQ: 714991699
 * @Description:
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public User loadUserByUsername(String username){
        System.out.println("loadUserByUserName方法调用：username："+username);
        //1、根据用户名查询用户信息
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);

        return userDao.selectOne(wrapper);
    }

    public User loadUserByAccount(String account){
        System.out.println("loadUserByAccount方法调用：account："+account);
        //1、根据用户名查询用户信息
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("account", account);

        return userDao.selectOne(wrapper);
    }


    public int updateUser(User user) {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            //构建查询条件  取User类中的username属性名称和user中用户名比对
            wrapper.eq(User::getAccount, user.getAccount());
            if (userDao.selectList(wrapper).size() == 1) {
                return userDao.update(user, wrapper);
            } else{
                return 0;
            }
    }

}

