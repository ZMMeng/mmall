package com.mmall.controller;

import com.mmall.common.ServerResponse;
import com.mmall.constant.Const;
import com.mmall.pojo.User;
import com.mmall.service.ISsoService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisSharededPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author 蒙卓明
 * @date 2018/11/28
 */
@RequestMapping("/manage/user")
@Controller
public class ManageUserController {

    @Autowired
    private ISsoService iSsoService;

    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session,
                                      HttpServletResponse response) {
        ServerResponse<User> loginResponse = iSsoService.login(username, password);
        if (!loginResponse.isSuccess()) {
            return loginResponse;
        }
        User user = loginResponse.getData();
        if (user.getRole() == Const.Role.ROLE_ADMIN) {
            CookieUtil.addCookie(Const.CookieNames.LOGIN, Const.CookieNames.LOGIN
                    + session.getId(), response);
            RedisSharededPoolUtil.setEx(Const.CookieNames.LOGIN + session.getId(),
                    JsonUtil.obj2String(loginResponse.getData()),
                    Const.RedisCacheExTime.REDIS_SESSION_EXTIME);
            return loginResponse;
        }
        return ServerResponse.createByErrorMessage("无权登陆");
    }
}
