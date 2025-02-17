package com.jinrou.jinrouwerewolf; /**
 * @Author: nanaya
 * @Date: 2024/07/13/22:52
 * @Email: qiyewuyin@gmail.com\714991699@qq.com
 * @QQ: 714991699
 * @Description:
 */

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jinrou.jinrouwerewolf.entity.Game.GameSettings;
import com.jinrou.jinrouwerewolf.entity.User;
import com.jinrou.jinrouwerewolf.service.GameSettingsService;
import com.jinrou.jinrouwerewolf.service.Impl.RedisService;
import com.jinrou.jinrouwerewolf.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class RedisTest {
    @Autowired
    private RedisService redisService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private GameSettingsService gameSettingsService;

    @Test
        //测试redis
    void contextLoads2() {
        //添加缓存键值对name:mijiu并设置过期时间为1小时
//        redisTemplate.opsForValue().set("name","mijiu",10, TimeUnit.SECONDS);
//        redisService.setValue("name","mijiu",10, TimeUnit.SECONDS);
//        redisTemplate.opsForValue().set("name","mijiu",10, TimeUnit.SECONDS);
//        redisService.setValue("k1","v1");
//        System.out.println("已插入k1 v1");
//        redisService.setValueBySeconds("name","mijiu",30);
//        redisService.setValueBySeconds("name","mijiu",30);
//        String value2 = (String) redisService.getValue("name");
//        String value = (String) redisService.getValue("k1");
//        String jwtToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiaWF0IjoxNzIwOTAyMjEzLCJleHAiOjkyMjMzNzIwMzY4NTQ3NzUsInVzZXIiOnsidXNlcklkIjoxLCJ1c2VybmFtZSI6ImFkbWluIiwicGFzc3dvcmQiOiIkMmEkMTAkUEhUdm41WEgzLkkyMlJ0eW9pY0Y1dXNYR0hqd1hRalFlMHVJR2NrUi9URHNJa1hKTnFMSEciLCJ1c2VyU3RhdGUiOiJVTkFVVEhPUklaRUQiLCJlbWFpbCI6bnVsbCwiaWNvblVybCI6bnVsbCwidXNlckNyZWF0ZVRpbWUiOiIyMDI0LTA3LTEwIDE5OjU2OjUyIiwidXNlckxhc3RPbmxpbmVUaW1lIjoiMjAyNC0wNy0xMCAxOTo1Njo1MiIsImF1dGhvcml0aWVzIjpbeyJhdXRob3JpdHkiOiJST0xFX1VOQVVUSE9SSVpFRCJ9XSwiZW5hYmxlZCI6dHJ1ZSwiYWNjb3VudE5vbkV4cGlyZWQiOnRydWUsImNyZWRlbnRpYWxzTm9uRXhwaXJlZCI6dHJ1ZSwiYWNjb3VudE5vbkxvY2tlZCI6dHJ1ZX19.KRhwbBGNmT9JWFs29BAycWZKJXZ7k3pDyt89j4TlYyo";
//        Claims claims = jwtUtil.getTokenBody(jwtToken);
////        User user = (User)claims.get("user");
//        String userJson = JSON.toJSONString(claims.get("user"));
//        User user = JSON.parseObject(userJson, User.class);
////        System.out.println(userJson);
////        System.out.println(claims.get("user"));
//        System.out.println(user);
//        String value = (String) redisService.getValue(String.valueOf(1));
//        System.out.println(value);

        GameSettings settings = new GameSettings();
        settings.setGameSettingId(1);
        settings.setIsFirstVictims(true);
        settings.setIsHopeMode(false);
        settings.setDayDuration(300);
        settings.setNightDuration(180);
        settings.setVoteDuration(60);
        settings.setMorningDuration(30);
        settings.setNSecondRule(10);
        settings.setHunterContinuousGuarding(false);
        // 创建空的 ArrayList
        List<String> list = new ArrayList<>();

        // 添加元素
        list.add("人狼");
        list.add("人狼");
        list.add("占卜师");

        settings.setIdentityList(list);

        gameSettingsService.insert(settings);

    }
}
