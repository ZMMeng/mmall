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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author 蒙卓明
 * @date 2018/11/23
 */
@RequestMapping("/sso")
@Controller
public class SsoController {

    @Autowired
    private ISsoService iSsoService;

    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session,
                                      HttpServletResponse response) {
        ServerResponse<User> loginResponse = iSsoService.login(username, password);
        if (loginResponse.isSuccess()) {
            CookieUtil.addCookie(Const.CookieNames.LOGIN, Const.CookieNames.LOGIN
                    + session.getId(), response);
            RedisSharededPoolUtil.setEx(Const.CookieNames.LOGIN + session.getId(),
                    JsonUtil.obj2String(loginResponse.getData()),
                    Const.RedisCacheExTime.REDIS_SESSION_EXTIME);
        }
        return loginResponse;
    }

    @RequestMapping(value = "check_valid.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> checkValid(String str, String type) {
        return iSsoService.checkValid(str, type);
    }

    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user) {
        return iSsoService.register(user);
    }

    @RequestMapping(value = "get_forget_question.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> getForgetQuestion(String username) {
        return iSsoService.getForgetQuestion(username);
    }

    @RequestMapping(value = "check_forget_answer.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkForgetAnswer(String username, String question, String answer) {
        return iSsoService.checkForgetAnswer(username, question, answer);
    }

    @RequestMapping(value = "reset_forget_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetForgetPassword(String username, String passwordNew, String token) {
        return iSsoService.resetForgetPassword(username, passwordNew, token);
    }

    @RequestMapping(value = "logout.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> logout(HttpServletRequest request, HttpServletResponse response) {
        //获取登陆令牌
        String loginToken = CookieUtil.getCookieValue(Const.CookieNames.LOGIN, request);
        //设置返回的Cookie失效
        CookieUtil.delCookie(Const.CookieNames.LOGIN, request, response);
        //在Redis缓存中删除登陆相关信息
        RedisSharededPoolUtil.del(loginToken);
        return ServerResponse.createBySuccess();
    }
}
