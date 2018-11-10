package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Jedis工具类
 * Created by 蒙卓明 on 2018/11/9
 */
public class RedisPool {

    //Jedis连接池
    private static JedisPool pool;

    //最大连接数
    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total",
            "20"));

    //最大空闲连接实例个数
    private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle",
            "10"));

    //最小空闲连接实例个数
    private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle",
            "2"));

    //在borrow一个jedis实例时，是否要进行验证操作
    //如果赋值为true，则得到的jedis实例肯定可用
    private static Boolean testOnBorrow = Boolean.parseBoolean(
            PropertiesUtil.getProperty("redis.test.borrow", "true"));

    //在return一个jedis实例时，是否要进行验证操作
    //如果赋值为true，则返回的jedis实例肯定可用
    private static Boolean testOnReturn = Boolean.parseBoolean(
            PropertiesUtil.getProperty("redis.test.return", "true"));

    //Redis服务器IP
    private static String redisIp = PropertiesUtil.getProperty("redis.ip");

    //Redis服务器端口
    private static Integer redisPort = Integer.parseInt(PropertiesUtil.getProperty("redis.port"));

    /**
     * 初始化连接池
     */
    private static void initPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);

        //设置连接池耗尽时，新进的连接请求是否阻塞
        //false直接抛出异常，true是阻塞直到超时
        config.setBlockWhenExhausted(true);

        pool = new JedisPool(config, redisIp, redisPort, 1000 * 2);

    }

    static {
        initPool();
    }

    /**
     * 从连接池中获取Redis连接
     *
     * @return
     */
    public static Jedis getJedis() {
        return pool.getResource();
    }

    /**
     * 向连接池归还连接
     *
     * @param jedis Jedis连接
     */
    public static void returnJedis(Jedis jedis) {
        pool.returnResource(jedis);
    }

    /**
     * 向连接归还损坏的连接
     *
     * @param jedis Jedis连接
     */
    public static void returnBrokenJedis(Jedis jedis) {
        pool.returnBrokenResource(jedis);
    }
}