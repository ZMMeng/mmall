package com.mmall.util;

import java.math.BigDecimal;

/**
 * BigDecimal工具类
 * <p>
 * Created by 蒙卓明 on 2018/10/27
 */
public class BigDecimalUtil {

    private BigDecimalUtil() {

    }

    /**
     * 两个数精确相加
     *
     * @param v1 被加数
     * @param v2 加数
     * @return
     */
    public static BigDecimal add(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2);
    }

    /**
     * 两个数精确相减
     *
     * @param v1 被减数
     * @param v2 减数
     * @return
     */
    public static BigDecimal subtract(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2);
    }

    /**
     * 两个数精确相乘
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return
     */
    public static BigDecimal multiply(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2);
    }

    /**
     * 两个数精确相除
     *
     * @param v1 被除数
     * @param v2 除数
     * @return
     */
    public static BigDecimal divide(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        //保留两位小数，同时进行四舍五入
        return b1.divide(b2, 2, BigDecimal.ROUND_HALF_UP);
    }
}
