package com.mmall.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * 时间字符串工具类，使用joda-time包
 *
 * Created by 蒙卓明 on 2018/10/24
 */
public class DateTimeUtil {

    //joda-time

    private static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static Date strToDate(String dateTimeStr) {
        return strToDate(dateTimeStr, STANDARD_FORMAT);
    }

    public static String dateToStr(Date date) {
        return dateToStr(date, STANDARD_FORMAT);
    }

    public static Date strToDate(String dateTimeStr, String formatStr) {

        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(formatStr);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    public static String dateToStr(Date date, String formatStr) {

        if (date == null) {
            return StringUtils.EMPTY;
        }

        DateTime dateTime = new DateTime(date);
        return dateTime.toString(formatStr);
    }
}
