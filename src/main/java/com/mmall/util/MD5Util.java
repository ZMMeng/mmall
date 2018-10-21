package com.mmall.util;

import ch.qos.logback.classic.gaffer.PropertyUtil;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by 蒙卓明 on 2018/10/21
 */
public class MD5Util {

    private static final String[] hexDigits = {
            "0", "1", "2", "3", "4", "5", "6", "7",
            "8", "9", "a", "b", "c", "d", "e", "f",
    };

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n += 256;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    private static String byteArrayToHexString(byte[] b) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            resultSb.append(byteToHexString(b[i]));
        }
        return resultSb.toString();
    }

    /**
     * 返回大写MD5码
     * @param orgin 原始字符串
     * @param charsetname 字符串编码
     * @return
     */
    private static String MD5Encode(String orgin, String charsetname) {
        String resultString = null;
        try {
            resultString = new String(orgin);
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (charsetname != null && !"".equals(charsetname)) {
                resultString = byteArrayToHexString(md.digest(resultString.getBytes(charsetname)));
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        return resultString.toUpperCase();
    }

    public static String MD5EncodeUtf8(String orgin) {
        orgin = orgin ;
        return MD5Encode(orgin, "utf-8");
    }
}
