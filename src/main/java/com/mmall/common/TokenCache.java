package com.mmall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Guava缓存
 * Created by 蒙卓明 on 2018/10/21
 */
@Slf4j
public class TokenCache {

    public static final String TOKEN_PREFIX = "token_";

    /**
     * 设置Guava缓存相关参数，并进行初始化
     * 初始容量
     * 最大内存
     * 超时时间
     */
    private static LoadingCache<String, String> localCache = CacheBuilder.newBuilder().initialCapacity(1000)
            .maximumSize(10000).expireAfterAccess(12, TimeUnit.HOURS).build(new CacheLoader<String, String>() {

                /**
                 * 默认的方法实现，当调用get取值时，如果key无相应值，则调用此法加载
                 * @param s
                 * @return
                 * @throws Exception
                 */
                @Override
                public String load(String s) throws Exception {
                    return "null";
                }
            });

    /**
     * 向缓存中放入键值对
     *
     * @param key   键
     * @param value 值
     */
    public static void setKey(String key, String value) {
        localCache.put(key, value);
    }

    /**
     * 根据键，从缓存中获取相应的值
     *
     * @param key 键
     * @return
     */
    public static String getKey(String key) {
        String value = null;

        try {
            value = localCache.get(key);
            if ("null".equals(value)) {
                return null;
            }
            return value;
        } catch (ExecutionException e) {
            log.error("localCache get error", e);
        }

        return null;
    }
}
