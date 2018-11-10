package com.mmall.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * JSON序列化和反序列化类
 * Created by 蒙卓明 on 2018/11/10
 */
@Slf4j
public class JsonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        //序列化
        //对象的所有字段全部列入，有ALWAYS、NON_NULL、NON_DEFAULT和NON_EMPTY
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.ALWAYS);
        //取消默认转换的timestamp形式
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
        //忽略空bean转JSON的错误
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
        //所有日期格式统一为yyyy-MM-dd HH:mm:ss
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STANDARD_FORMAT));

        //反序列化
        //忽略在JSON字符串中存在，但在Java对象中不存在对应属性的错误
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 将对象序列化为JSON字符串
     *
     * @param obj 对象
     * @param <T>
     * @return
     */
    public static <T> String obj2String(T obj) {

        if (obj == null) {
            return null;
        }

        try {
            return obj instanceof String ? (String) obj : objectMapper.writeValueAsString(obj);
        } catch (IOException e) {
            log.warn("parse Object to String error", e);
            return null;
        }
    }

    /**
     * 将对象序列化为格式化的JSON字符串
     *
     * @param obj 对象
     * @param <T>
     * @return
     */
    public static <T> String obj2StringPretty(T obj) {

        if (obj == null) {
            return null;
        }

        try {
            return obj instanceof String ? (String) obj :
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (IOException e) {
            log.warn("parse Object to String error", e);
            return null;
        }
    }

    /**
     * 将字符串反序列化为对象
     *
     * @param str JSON字符串
     * @param clz 对象类型的Class对象
     * @param <T>
     * @return
     */
    public static <T> T string2Obj(String str, Class<T> clz) {

        if (StringUtils.isBlank(str) || clz == null) {
            return null;
        }

        try {
            return String.class.equals(clz) ? (T) str : objectMapper.readValue(str, clz);
        } catch (IOException e) {
            log.warn("parse String to Object error", e);
            return null;
        }
    }

    /**
     * 将字符串反序列化为对象，解决反序列化为集合类型对象时，类型参数丢失问题，方法一
     *
     * @param str           JSON字符串
     * @param typeReference 对象类型，泛型类型包括类型参数
     * @param <T>
     * @return
     */
    public static <T> T string2Obj(String str, TypeReference<T> typeReference) {

        if (StringUtils.isBlank(str) || typeReference == null) {
            return null;
        }

        try {
            return String.class.equals(typeReference.getType()) ? (T) str :
                    objectMapper.readValue(str, typeReference);
        } catch (Exception e) {
            log.warn("parse String to Object error", e);
            return null;
        }
    }

    /**
     * 将字符串反序列化为泛型集合对象
     *
     * @param str           JSON字符串
     * @param collectionClz 集合类型
     * @param elementClzs   类型参数类型
     * @param <T>
     * @return
     */
    public static <T> T string2Obj(String str, Class<?> collectionClz, Class<?>... elementClzs) {

        if (StringUtils.isBlank(str) || collectionClz == null) {
            return null;
        }

        JavaType javaType = objectMapper.getTypeFactory()
                .constructParametricType(collectionClz, elementClzs);

        try {
            return objectMapper.readValue(str, javaType);
        } catch (IOException e) {
            log.warn("parse String to Object error", e);
            return null;
        }

    }
}
