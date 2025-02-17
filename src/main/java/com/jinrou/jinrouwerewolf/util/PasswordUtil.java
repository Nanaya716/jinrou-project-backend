package com.jinrou.jinrouwerewolf.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @Author: nanaya
 * @Date: 2024/12/21/1:34
 * @Email: qiyewuyin@gmail.com\714991699@qq.com
 * @QQ: 714991699
 * @Description:
 */
@Service
public class PasswordUtil {

    private final BCryptPasswordEncoder passwordEncoder;

    // 使用构造函数注入 BCryptPasswordEncoder
    public PasswordUtil() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public PasswordUtil(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    // 加密密码
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    // 验证密码
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

}
