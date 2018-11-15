package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by 蒙卓明 on 2018/11/14
 */
@Component
@Slf4j
public class RedissonManager {

    private Config config = new Config();

    @Getter
    private Redisson redisson;

    //Redis1服务器IP
    private static String redis1Ip = PropertiesUtil.getProperty("redis1.ip");
    //Redis服务器端口
    private static Integer redis1Port = Integer.parseInt(PropertiesUtil.getProperty("redis1.port"));

    //Redis1服务器IP
    private static String redis2Ip = PropertiesUtil.getProperty("redis2.ip");
    //Redis服务器端口
    private static Integer redis2Port = Integer.parseInt(PropertiesUtil.getProperty("redis2.port"));

    @PostConstruct
    private void init() {
        config.useSingleServer().setAddress(redis1Ip + ":" + redis1Port);

        try {
            redisson = (Redisson) Redisson.create(config);
            log.info("初始化Redisson结束");
        } catch (Exception e) {
            log.error("redission init error", e);
        }
    }
}
