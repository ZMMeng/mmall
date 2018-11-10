package com.mmall.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * 时间字符串工具类，使用joda-time包
 * <p>
 * Created by 蒙卓明 on 2018/10/24
 */
public class DateTimeUtil {

    //joda-time

    public static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 默认模式的时间字符串转Date类型
     *
     * @param dateTimeStr 时间字符串
     * @return
     */
    public static Date strToDate(String dateTimeStr) {
        return strToDate(dateTimeStr, STANDARD_FORMAT);
    }

    /**
     * Date对象转默认模式的时间字符串
     *
     * @param date Date对象
     * @return
     */
    public static String dateToStr(Date date) {
        return dateToStr(date, STANDARD_FORMAT);
    }

    /**
     * 指定模式的时间字符串转Date对象
     *
     * @param dateTimeStr 时间字符串
     * @param formatStr   模式
     * @return
     */
    public static Date strToDate(String dateTimeStr, String formatStr) {

        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(formatStr);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    /**
     * Date对象转指定模式的时间字符串
     *
     * @param date      Date对象
     * @param formatStr 模式
     * @return
     */
    public static String dateToStr(Date date, String formatStr) {

        if (date == null) {
            return StringUtils.EMPTY;
        }

        DateTime dateTime = new DateTime(date);
        return dateTime.toString(formatStr);
    }
}
