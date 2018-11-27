package com.mmall.constant;

/**
 * 常量类
 *
 * @author 蒙卓明
 * @date 2018/11/24
 */
public class Const {

    public static final String USERNAME = "username";

    public static final String EMAIL = "email";
    public static final String TOKEN_PREFIX = "token_";

    public interface Role {
        int ROLE_CUSTOMER = 1;
        int ROLE_ADMIN = 0;
    }

    public interface CookieNames {
        String LOGIN = "mmall_login_token";
        String VERIFY_CODE = "mmall_verify_code_token";
    }

    public interface CookieExTime {
        int COOKIE_MAX_AGE = 60 * 60 * 24 * 365;
    }

    public interface RedisCacheExTime {

        int REDIS_SESSION_EXTIME = 60 * 30;
        int REDIS_FORGET_TOKEN_EXTIME = 60 * 60 * 12;
        int REDIS_VERIFY_CODE_EXTIME = 60 * 30;
    }
}
