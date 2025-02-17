package com.jinrou.jinrouwerewolf.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinrou.jinrouwerewolf.entity.User;
/**
 * @Author: nanaya
 * @Date: 2024/07/08/7:45
 * @Email: qiyewuyin@gmail.com\714991699@qq.com
 * @QQ: 714991699
 * @Description:
 */
public interface UserService extends IService<User> {
//    User getUserById(int id);
//    User AuthorizeUser(String username, String password);
    User loadUserByUsername(String username);
    User loadUserByAccount(String account);
}
