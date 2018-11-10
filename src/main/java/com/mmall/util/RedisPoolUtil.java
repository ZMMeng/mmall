package com.mmall.util;

import com.mmall.common.RedisPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

/**
 * Redis工具类
 * Created by 蒙卓明 on 2018/11/9
 */
@Slf4j
public class RedisPoolUtil {

    /**
     * 在Redis缓存中添加键值对
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static String set(String key, String value) {
        Jedis jedis = null;
        String result = null;

        try {
            jedis = RedisPool.getJedis();
            result = jedis.set(key, value);
        } catch (Exception e) {
            log.error("set key: {} value: {} error: {}", key, value, e);
            //出现异常，表示连接发生损坏
            RedisPool.returnBrokenJedis(jedis);
            return result;
        }
        //未发生异常，表示连接正常
        RedisPool.returnJedis(jedis);
        return result;
    }

    /**
     * 在Redis缓存中添加键值对，同时设置该键值对的过期时间
     *
     * @param key    键
     * @param value  值
     * @param exTime 过期时间，单位是s
     * @return
     */
    public static String setEx(String key, String value, int exTime) {
        Jedis jedis = null;
        String result = null;

        try {
            jedis = RedisPool.getJedis();
            result = jedis.setex(key, exTime, value);
        } catch (Exception e) {
            log.error("set key: {} value: {} error: {}", key, value, e);
            //出现异常，表示连接发生损坏
            RedisPool.returnBrokenJedis(jedis);
            return result;
        }
        //未发生异常，表示连接正常
        RedisPool.returnJedis(jedis);
        return result;
    }

    /**
     * 重新设置键的有效期
     *
     * @param key    键
     * @param exTime 新的有效期，单位是s
     * @return
     */
    public static Long expire(String key, int exTime) {
        Jedis jedis = null;
        Long result = null;

        try {
            jedis = RedisPool.getJedis();
            result = jedis.expire(key, exTime);
        } catch (Exception e) {
            log.error("expire key: {} error: {}", key, e);
            //出现异常，表示连接发生损坏
            RedisPool.returnBrokenJedis(jedis);
            return result;
        }
        //未发生异常，表示连接正常
        RedisPool.returnJedis(jedis);
        return result;
    }

    /**
     * 从Redis中根据键获取相应值
     *
     * @param key 键
     * @return
     */
    public static String get(String key) {
        Jedis jedis = null;
        String result = null;

        try {
            jedis = RedisPool.getJedis();
            result = jedis.get(key);
        } catch (Exception e) {
            log.error("get key: {} error: {}", key, e);
            //出现异常，表示连接发生损坏
            RedisPool.returnBrokenJedis(jedis);
            return result;
        }
        //未发生异常，表示连接正常
        RedisPool.returnJedis(jedis);
        return result;
    }

    /**
     * 删除Redis缓存中指定的键
     *
     * @param key 键
     * @return
     */
    public static Long del(String key) {
        Jedis jedis = null;
        Long result = null;

        try {
            jedis = RedisPool.getJedis();
            result = jedis.del(key);
        } catch (Exception e) {
            log.error("del key: {} error: {}", key, e);
            //出现异常，表示连接发生损坏
            RedisPool.returnBrokenJedis(jedis);
            return result;
        }
        //未发生异常，表示连接正常
        RedisPool.returnJedis(jedis);
        return result;
    }

}
