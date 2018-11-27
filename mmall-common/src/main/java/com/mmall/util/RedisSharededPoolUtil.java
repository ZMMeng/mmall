package com.mmall.util;

import com.mmall.common.RedisShardedPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.ShardedJedis;

/**
 * @author 蒙卓明
 * @date 2018/11/25
 */
@Slf4j
public class RedisSharededPoolUtil {

    /**
     * 在Redis缓存中添加键值对
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static String set(String key, String value) {
        ShardedJedis jedis = null;
        String result = null;

        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.set(key, value);
        } catch (Exception e) {
            log.error("set key: {} value: {} error: {}", key, value, e);
        } finally {
            RedisShardedPool.returnJedis(jedis);
        }
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
        ShardedJedis jedis = null;
        String result = null;

        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.setex(key, exTime, value);
        } catch (Exception e) {
            log.error("set key: {} value: {} error: {}", key, value, e);
        } finally {
            RedisShardedPool.returnJedis(jedis);
        }
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
        ShardedJedis jedis = null;
        Long result = null;

        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.expire(key, exTime);
        } catch (Exception e) {
            log.error("expire key: {} error: {}", key, e);
        } finally {
            RedisShardedPool.returnJedis(jedis);
        }
        return result;
    }

    /**
     * 从Redis中根据键获取相应值
     *
     * @param key 键
     * @return
     */
    public static String get(String key) {
        ShardedJedis jedis = null;
        String result = null;

        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.get(key);
        } catch (Exception e) {
            log.error("get key: {} error: {}", key, e);
        } finally {
            RedisShardedPool.returnJedis(jedis);
        }
        return result;
    }

    /**
     * 删除Redis缓存中指定的键
     *
     * @param key 键
     * @return
     */
    public static Long del(String key) {
        ShardedJedis jedis = null;
        Long result = null;

        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.del(key);
        } catch (Exception e) {
            log.error("del key: {} error: {}", key, e);
        } finally {
            RedisShardedPool.returnJedis(jedis);
        }
        return result;
    }

    /**
     * 在Redis缓存中设置键值对
     *
     * @param key   键
     * @param value 值
     * @return 如果键已存在，返回0；如果键不存在，返回1
     */
    public static Long setnx(String key, String value) {

        ShardedJedis jedis = null;
        Long result = null;

        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.setnx(key, value);
        } catch (Exception e) {
            log.error("setnx key: {} error: {}", key, e);
        } finally {
            RedisShardedPool.returnJedis(jedis);
        }
        return result;
    }

    /**
     * 设置键的新值，同时返回旧值，getset具有原子性
     *
     * @param key      键
     * @param newValue 新值
     * @return 旧值
     */
    public static String getSet(String key, String newValue) {

        ShardedJedis jedis = null;
        String oldValue = null;

        try {
            jedis = RedisShardedPool.getJedis();
            oldValue = jedis.getSet(key, newValue);
        } catch (Exception e) {
            log.error("getset key: {} value: {} error: {}", key, newValue, e);
        } finally {
            RedisShardedPool.returnJedis(jedis);
        }
        return oldValue;
    }
}
