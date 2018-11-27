package com.mmall.util;

import com.mmall.constant.Const;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Cookie工具类
 *
 * @author 蒙卓明
 * @date 2018/11/25
 */
@Slf4j
public class CookieUtil {

    private final static String COOKIE_DOMAIN = PropertiesUtil.getProperty("cookie.domain", "");

    /**
     * 服务器向客户端写入Cookie
     *
     * @param cookieName  Cookie属性名
     * @param cookieValue Cookie属性值
     * @param response    响应
     */
    public static void addCookie(String cookieName, String cookieValue, HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setDomain(COOKIE_DOMAIN);
        //设置cookie在项目的根目录下
        cookie.setPath("/");
        //设置Cookie不被JavaScript脚本获取
        cookie.setHttpOnly(true);
        cookie.setMaxAge(Const.CookieExTime.COOKIE_MAX_AGE);
        log.info("write cookieName: {}, cookieValue: {}", cookie.getName(), cookie.getValue());
        response.addCookie(cookie);
    }

    /**
     * 获取指定Cookie属性值
     *
     * @param cookieName Cookie属性名
     * @param request    请求
     * @return
     */
    public static String getCookieValue(String cookieName, HttpServletRequest request) {

        if (StringUtils.isEmpty(cookieName)) {
            return null;
        }
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            log.info("read cookieName: {}, cookieValue: {}", cookie.getName(), cookie.getValue());
            if (StringUtils.equals(cookie.getName(), cookieName)) {
                log.info("return cookieName: {}, cookieValue: {}", cookie.getName(), cookie.getValue());
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * 删除指定Cookie
     *
     * @param cookieName Cookie属性名
     * @param request    请求
     * @param response   响应
     */
    public static void delCookie(String cookieName, HttpServletRequest request,
                                 HttpServletResponse response) {
        if (StringUtils.isEmpty(cookieName)) {
            return;
        }
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return;
        }
        for (Cookie cookie : cookies) {
            if (StringUtils.equals(cookie.getName(), cookieName)) {
                cookie.setDomain(cookieName);
                cookie.setPath("/");
                //将maxAge设置成0，表示删除cookie
                cookie.setMaxAge(0);
                log.info("del cookieName: {}, cookieValue: {}", cookie.getName(), cookie.getValue());
                response.addCookie(cookie);
                return;
            }
        }
    }
}
