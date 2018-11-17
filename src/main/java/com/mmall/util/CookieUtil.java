package com.mmall.util;

import com.mmall.common.Const;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Cookie相关工具类
 * Created by 蒙卓明 on 2018/11/10
 */
@Slf4j
public class CookieUtil {

    //.bigdata.com在tomcat 8.5之后，不再支持，它使用的是org.apache.tomcat.util.http.Rfc6265CookieProcessor
    //tomcat 8.0使用的是org.apache.tomcat.util.http.LegacyCookieProcessor
    private final static String COOKIE_DOMAIN = "bigdata.com";
    private final static String COOKIE_NAME = "mmall_login_token";

    /**
     * 服务器向客户端写入cookie
     *
     * @param response 响应
     * @param token    令牌
     */
    public static void writeLoginToken(HttpServletResponse response, String token) {
        addCookie(COOKIE_NAME, token, response);
    }

    public static void addCookie(String cookieName, String cookieValue, HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setDomain(COOKIE_DOMAIN);
        //设置cookie在项目的根目录下
        cookie.setPath("/");
        //设置Cookie不被JavaScript脚本获取
        cookie.setHttpOnly(true);

        //单位是s
        //不设置maxAge，cookie不写入硬盘，只在内存中，只在当前页面有效
        cookie.setMaxAge(Const.CookieExTime.COOKIE_MAX_AGE);
        log.info("write cookieName: {}, cookieValue: {}", cookie.getName(), cookie.getValue());
        response.addCookie(cookie);
    }

    /**
     * 从用户请求中读取cookie
     *
     * @param request 请求
     * @return
     */
    public static String readLoginToken(HttpServletRequest request) {
        return getCookieValue(COOKIE_NAME, request);
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
     * 删除Cookie
     *
     * @param request  请求
     * @param response 响应
     */
    public static void delLoginToken(HttpServletRequest request, HttpServletResponse response) {
        delCookie(COOKIE_NAME, request, response);
    }

    /**
     * 删除指定Cookie
     * @param cookieName Cookie属性名
     * @param request 请求
     * @param response 响应
     */
    public static void delCookie(String cookieName, HttpServletRequest request,
                                 HttpServletResponse response) {
        if (StringUtils.isEmpty(cookieName)) {
            return ;
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
