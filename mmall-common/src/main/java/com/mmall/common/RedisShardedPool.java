package com.mmall.common;

import com.google.common.collect.Lists;
import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.util.List;

/**
 * Redis连接池
 *
 * @author 蒙卓明
 * @date 2018/11/25
 */
public class RedisShardedPool {

    /**
     * ShardedJedis连接池
     */
    private static ShardedJedisPool pool;
    /**
     * 最大连接数
     */
    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total",
            "20"));
    /**
     * 最大空闲连接实例个数
     */
    private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle",
            "10"));
    /**
     * 最小空闲连接实例个数
     */
    private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle",
            "2"));
    /**
     * 在borrow一个jedis实例时，是否要进行验证操作
     * 如果赋值为true，则得到的jedis实例肯定可用
     */
    private static Boolean testOnBorrow = Boolean.parseBoolean(
            PropertiesUtil.getProperty("redis.test.borrow", "true"));
    /**
     * 在return一个jedis实例时，是否要进行验证操作
     * 如果赋值为true，则返回的jedis实例肯定可用
     */
    private static Boolean testOnReturn = Boolean.parseBoolean(
            PropertiesUtil.getProperty("redis.test.return", "true"));

    private static String redisClusterStr = PropertiesUtil.getProperty("redis.cluster");

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

        List<JedisShardInfo> jedisShardInfoList = Lists.newArrayList();
        String[] redisIpPorts = redisClusterStr.split(",");
        for (String redisIpPort : redisIpPorts) {
            String[] strs = redisIpPort.split(":");
            String redisIp = strs[0];
            Integer redisPort = Integer.parseInt(strs[1]);
            JedisShardInfo info = new JedisShardInfo(redisIp, redisPort);
            jedisShardInfoList.add(info);
        }

        //Hashing.MURMUR_HASH 表示采用一致性Hash算法
        //Sharded.DEFAULT_KEY_TAG_PATTERN 表示
        pool = new ShardedJedisPool(config, jedisShardInfoList, Hashing.MURMUR_HASH,
                Sharded.DEFAULT_KEY_TAG_PATTERN);
    }

    static {
        initPool();
    }

    /**
     * 从连接池中获取Redis连接
     *
     * @return
     */
    public static ShardedJedis getJedis() {
        return pool.getResource();
    }

    /**
     * 向连接池归还连接
     */
    public static void returnJedis(ShardedJedis shardedJedis) {
        if (shardedJedis != null) {
            //returnResource()和returnBrokenResource()已经废弃
            //应改用close()方法
            shardedJedis.close();
        }
    }
}
