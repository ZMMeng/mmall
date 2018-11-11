package com.mmall.controller.common;

import com.mmall.common.Const;
import com.mmall.pojo.User;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisSharededPoolUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by 蒙卓明 on 2018/11/10
 */
public class SessionExpireFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        //强转
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isNotEmpty(loginToken)) {
            String userJsonStr = RedisSharededPoolUtil.get(loginToken);
            User user = JsonUtil.string2Obj(userJsonStr, User.class);
            if (user != null) {
                //重置session有效时间
                RedisSharededPoolUtil.expire(loginToken, Const.RedisCacheExTime.REDIS_SESSION_EXTIME);
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
