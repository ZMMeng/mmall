package com.mmall.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStreamReader;
import java.util.Properties;

/**
 * 读取配置工具类
 *
 * @author 蒙卓明
 * @date 2018/11/24
 */
@Slf4j
public class PropertiesUtil {

    private static Properties properties;

    static {
        String fileName = "public.system.properties";
        properties = new Properties();
        try {
            properties.load(new InputStreamReader(
                    PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName),
                    "UTF-8"));
        } catch (Exception e) {
            log.error("配置文件读取异常", e);
        }
    }

    /**
     * 从配置中获取指定属性的属性值
     *
     * @param key 属性
     * @return
     */
    public static String getProperty(String key) {
        String value = properties.getProperty(key.trim());
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return value.trim();
    }

    /**
     * 从配置中获取指定属性的属性值，如属性值为空，则返回默认值
     *
     * @param key          属性
     * @param defaultValue 默认值
     * @return
     */
    public static String getProperty(String key, String defaultValue) {
        String value = properties.getProperty(key.trim());
        if (StringUtils.isBlank(value)) {
            value = defaultValue;
        }
        return value.trim();
    }
}
