package com.jinrou.jinrouwerewolf.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: nanaya
 * @Date: 2025/02/24/8:22
 * @Email: qiyewuyin@gmail.com\714991699@qq.com
 * @QQ: 714991699
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeObject {
    private String account;
    private String oldPassword;
    private String newPassword;
}
