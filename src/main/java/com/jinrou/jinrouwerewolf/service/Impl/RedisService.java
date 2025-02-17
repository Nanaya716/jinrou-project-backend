package com.jinrou.jinrouwerewolf.service.Impl;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author: nanaya
 * @Date: 2024/07/13/22:48
 * @Email: qiyewuyin@gmail.com\714991699@qq.com
 * @QQ: 714991699
 * @Description:
 */
@Service
public class RedisService{
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private static final String MESSAGE_INDEX_PREFIX = "room:messageIndex:"; // 房间消息索引前缀

    @Autowired
    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 其他操作...
    //    删除单个key
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    //    删除多个key
    public void deleteKeys(List<String> keys) {
        redisTemplate.delete(keys);
    }

    //    指定key的失效时间
    public void setExpire(String key, long time) {
        redisTemplate.expire(key, time, TimeUnit.MINUTES);
    }

    //    根据key获取过期时间
    public long getExpire(String key) {
        Long expire = redisTemplate.getExpire(key);
        return expire;
    }

    //刷新key过期时间
    public boolean expireKey(String key,long time) {
        return redisTemplate.expire(key, time, TimeUnit.SECONDS);
    }

    //    判断key是否存在
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    public void setValue(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void setValueBySeconds(String key, Object value, long time) {
        redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
    }

    public Object getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // 获取下一个消息的索引，如果没有索引则初始化
    public int getNextMessageIndexAndIncr(String roomId) {
        String key = MESSAGE_INDEX_PREFIX + roomId;
        // Redis 自增，键不存在时会自动初始化为 0
        Long newIndex = redisTemplate.opsForValue().increment(key);
        // 返回结果，将 Long 转为 int
        return newIndex != null ? newIndex.intValue() : 0;
    }

    public int getNextMessageIndex(String roomId) {
        String key = MESSAGE_INDEX_PREFIX + roomId;

        // Redis 自增，键不存在时会自动初始化为 0
        Long newIndex = Long.parseLong((String) redisTemplate.opsForValue().get(key));
        // 返回结果，将 Long 转为 int
        return newIndex != null ? newIndex.intValue() : 0;
    }

    public Long generatePlayerId(int roomId) {
        // 使用房间 ID 作为前缀，确保每个房间内的玩家编号独立
        String key = "game:room:" + roomId + ":roomPlayerId";
        Long playerNumber = null;
                // 检查 Redis 中是否已有该键的值
        Boolean hasKey = redisTemplate.hasKey(key);

        // 如果键不存在，则初始化索引值为 1
        if (hasKey == null || !hasKey) {
            redisTemplate.opsForValue().set(key, "1");
            playerNumber = 1L;
        }else{
            // 使用 Redis 的 INCR 生成自增 ID
            playerNumber = redisTemplate.opsForValue().increment(key);
        }

        // 格式化玩家编号，例如添加前缀
        return playerNumber;
    }


}
