package com.jinrou.jinrouwerewolf.conf;

import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.jinrou.jinrouwerewolf.TypeHandler.ListToStringHandler;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: nanaya
 * @Date: 2025/01/20/0:39
 * @Email: qiyewuyin@gmail.com\714991699@qq.com
 * @QQ: 714991699
 * @Description:
 */
@Configuration
@MapperScan("com.jinrou.jinrouwerewolf.dao")
public class MyBatisConfig {
    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> {
            configuration.getTypeHandlerRegistry().register(ListToStringHandler.class);
        };
    }
}

