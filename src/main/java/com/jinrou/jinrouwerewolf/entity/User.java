package com.jinrou.jinrouwerewolf.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.io.Serializable;


/**
 * (User)实体类
 *
 * @author makejava
 * @since 2024-07-08 06:41:40
 */
@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@TableName("user")
public class User implements Serializable {
    private static final long serialVersionUID = -20144545815631805L;
    private Integer userId;
    /**
     * 用户名
     */
    private String account;
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 未认证("UNAUTHORIZED"),
     * 正常("NORMAL"),
     * 已冻结("FROZEN"),
     * 已注销("CANCELLED"),
     * 管理员("ADMIN");
     */

    private String userState;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 头像url
     */
    private String phoneNumber;
    /**
     * 头像url
     */
    private String iconUrl;
    /**
     * 是否公开战绩
     */
    private boolean openHistory;
    private String bio;

    /**
     * 用户创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date userCreateTime;
    /**
     * 用户最后上线时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date userLastOnlineTime;

    public void clearSensitiveData() {
        this.password = null;
        this.email = null;  // 可以根据需要清除其他敏感数据
        this.phoneNumber = null;
    }



}

