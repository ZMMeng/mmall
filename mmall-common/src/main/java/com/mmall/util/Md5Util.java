package com.mmall.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5工具类
 *
 * @author 蒙卓明
 * @date 2018/11/24
 */
public class Md5Util {

    private static final String[] HEX_DIGITS = {
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f",
    };

    /**
     * byte数字转十六进制字符串
     *
     * @param b byte数值
     * @return
     */
    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n += 256;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return HEX_DIGITS[d1] + HEX_DIGITS[d2];
    }

    /**
     * byte数组转十六进制字符串
     *
     * @param bytes byte数组
     * @return
     */
    private static String byteArrayToHexString(byte[] bytes) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            resultSb.append(byteToHexString(bytes[i]));
        }
        return resultSb.toString();
    }

    /**
     * 返回大写MD5码
     *
     * @param orginStr    原始字符串
     * @param charsetname 字符串编码
     * @return
     */
    private static String md5Encode(String orginStr, String charsetname) {
        String resultString = null;
        try {
            String newStr = new String(orginStr);
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (charsetname != null && !"".equals(charsetname)) {
                resultString = byteArrayToHexString(md.digest(newStr.getBytes(charsetname)));
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return resultString.toUpperCase();
    }

    /**
     * 输入参数加盐值后生成MD5
     *
     * @param orginStr 原始字符串
     * @return
     */
    public static String md5EncodeUtf8(String orginStr) {
        String newStr = orginStr + PropertiesUtil.getProperty("password.salt");
        return md5Encode(newStr, "utf-8");
    }
}
