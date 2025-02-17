package com.jinrou.jinrouwerewolf.util;

import com.alibaba.fastjson.JSON;
import com.jinrou.jinrouwerewolf.entity.User;
import com.jinrou.jinrouwerewolf.service.Impl.RedisService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Author: nanaya
 * @Date: 2024/07/12/0:14
 * @Email: qiyewuyin@gmail.com\714991699@qq.com
 * @QQ: 714991699
 * @Description:
 */
@Component
@Slf4j
public class JwtUtil {

    //常量
//    @Value("${token.expire}")
//    public static long EXPIRE; //token过期时间,4个小时
    public static final String APP_SECRET = "ukc8BDbRigUDaY6pZFfWus2jZWLPHO"; //秘钥
    private static final String TOKEN_KEY_PREFIX = "jwt_token:";  // Token 在 Redis 中的键前缀
    @Autowired
    private RedisService redisService;

    //生成token字符串的方法
    public String getToken(User user){
        //设置token头部信息
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                //设置token主题部分 ，存储用户信息
                .setSubject(user.getUserId().toString())
                //设置token签发时间
                .setIssuedAt(new Date())
                //设置token过期时间
                .setExpiration(new Date(Long.MAX_VALUE))
                //设置token主体部分 ，存储用户信息
                .claim("user", user)
                //使用HS256算法和APP_SECRET签名
                .signWith(SignatureAlgorithm.HS256, APP_SECRET)
                //生成token字符串
                .compact();
    }

    //验证token字符串是否是有效的  包括验证是否过期
    public boolean checkToken(String jwtToken) {
        if(jwtToken == null || jwtToken.isEmpty()){
            log.error("Jwt is empty");
            return false;
        }
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
            Claims body = claims.getBody();
            //验证是否过期 但改为Redis验证，设置过期时间为MAX 所以总是true
            if ( body.getExpiration().after(new Date(System.currentTimeMillis()))){
                return true;
            } else
                return false;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    public User getTokenBody(String jwtToken){
        if(jwtToken == null || jwtToken.isEmpty()){
            log.error("Jwt is empty");
            return null;
        }
        try {
            Claims claims = Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken).getBody();
            String userJson = JSON.toJSONString(claims.get("user"));
            User user = JSON.parseObject(userJson, User.class);
            return user;
        } catch (Exception e){
            log.error(e.getMessage());
            return null;
        }


    }

}
