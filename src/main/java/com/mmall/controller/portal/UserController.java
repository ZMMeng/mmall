package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
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
 * Created by 蒙卓明 on 2018/10/20
 */
//配置Controller
@Controller
//配置映射url
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService iUserService;

    /**
     * 用户登录，并存入session
     *
     * @param username 用户名
     * @param password 密码
     * @param session  session
     * @param response response
     * @return
     */
    //配置映射url，方法指定是POST请求
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    //指定返回值序列化为json
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session,
                                      HttpServletResponse response) {

        ServerResponse<User> loginResponse = iUserService.login(username, password);

        if (loginResponse.isSuccess()) {
            //session.setAttribute(Const.CURRENT_USER, response.getData());
            //向客户端写入Cookie，以session的ID作为登陆令牌的属性值
            CookieUtil.writeLoginToken(response, session.getId());
            //将登陆信息写入Redis缓存，key是session的ID，value是User对象
            RedisSharededPoolUtil.setEx(session.getId(), JsonUtil.obj2String(loginResponse.getData()),
                    Const.RedisCacheExTime.REDIS_SESSION_EXTIME);
        }

        return loginResponse;
    }

    /**
     * 用户注销登陆，从session中删除相关信息
     *
     * @param request  请求
     * @param response 响应
     * @return
     */
    @RequestMapping(value = "logout.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpServletRequest request, HttpServletResponse response) {

        //session.removeAttribute(Const.CURRENT_USER);

        //从请求的Cookie中获取登陆令牌
        String loginToken = CookieUtil.readLoginToken(request);
        //设置返回的Cookie失效
        CookieUtil.delLoginToken(request, response);
        //在Redis缓存中删除登陆相关信息
        RedisSharededPoolUtil.del(loginToken);
        return ServerResponse.createBySuccess();
    }

    /**
     * 用户注册
     *
     * @param user 用户
     * @return
     */
    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user) {
        return iUserService.register(user);
    }

    /**
     * 校验用户名或email是否已存在
     *
     * @param str  用户输入
     * @param type 类型
     * @return
     */
    @RequestMapping(value = "check_valid.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str, String type) {
        return iUserService.checkValid(str, type);
    }

    /**
     * 从当前登陆用户中获取用户信息
     *
     * @param request 请求
     * @return
     */
    @RequestMapping(value = "get_user_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpServletRequest request) {
        //User user = (User) session.getAttribute(Const.CURRENT_USER);

        //从请求的Cookie中获取登陆令牌属性值
        String loginToken = CookieUtil.readLoginToken(request);
        if (loginToken == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        //根据登陆令牌属性值，从Redis缓存中获取User对象的JSON字符串
        String userJsonStr = RedisSharededPoolUtil.get(loginToken);
        //反序列化为User对象
        User user = JsonUtil.string2Obj(userJsonStr, User.class);
        if (user != null) {
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登录");
    }

    /**
     * 获取密码提示问题
     *
     * @param username 用户名
     * @return
     */
    @RequestMapping(value = "forget_get_question.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username) {
        return iUserService.selectQuestion(username);
    }

    /**
     * 校验密码提示问题是否正确
     *
     * @param username 用户名
     * @param question 密码提示问题
     * @param answer   密码提示问题答案
     * @return
     */
    @RequestMapping(value = "forget_check_answer.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {
        return iUserService.checkAnswer(username, question, answer);
    }

    /**
     * 重置密码
     *
     * @param username    用户名
     * @param passwordNew 新密码
     * @param forgetToken token
     * @return
     */
    @RequestMapping(value = "forget_reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew,
                                                      String forgetToken) {
        return iUserService.forgetResetPassword(username, passwordNew, forgetToken);
    }

    /**
     * 登陆状态下的重置密码
     *
     * @param passwordOld 旧密码
     * @param passwordNew 新密码
     * @param request     request
     * @return
     */
    @RequestMapping(value = "reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew,
                                                HttpServletRequest request) {

        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        //从请求的Cookie中获取登陆令牌属性值
        String loginToken = CookieUtil.readLoginToken(request);
        if (loginToken == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        //根据登陆令牌属性值，从Redis缓存中获取User对象的JSON字符串
        String userJsonStr = RedisSharededPoolUtil.get(loginToken);
        //反序列化为User对象
        User user = JsonUtil.string2Obj(userJsonStr, User.class);

        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }

        return iUserService.resetPassword(passwordOld, passwordNew, user);
    }

    /**
     * 在登陆状态下修改个人信息
     *
     * @param user    用户
     * @param request 请求
     * @return
     */
    @RequestMapping(value = "update_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateInfo(User user, HttpServletRequest request) {

        //User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        //从请求的Cookie中获取登陆令牌属性值
        String loginToken = CookieUtil.readLoginToken(request);
        if (loginToken == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        //根据登陆令牌属性值，从Redis缓存中获取User对象的JSON字符串
        String userJsonStr = RedisSharededPoolUtil.get(loginToken);
        //反序列化为User对象
        User currentUser = JsonUtil.string2Obj(userJsonStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }

        //设置将前台页面传输过来的user的id
        user.setId(currentUser.getId());
        //用户名不能被更新
        user.setUsername(currentUser.getUsername());

        ServerResponse<User> response = iUserService.updateInfo(user);
        if (!response.isSuccess()) {
            return response;
        }

        //session.setAttribute(Const.CURRENT_USER, response.getData());
        RedisSharededPoolUtil.setEx(loginToken, JsonUtil.obj2String(response.getData()),
                Const.RedisCacheExTime.REDIS_SESSION_EXTIME);
        return response;
    }

    /**
     * 获取当前登陆用户的详细信息
     *
     * @param request 请求
     * @return
     */
    @RequestMapping(value = "get_user_detail_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserDetailInfo(HttpServletRequest request) {

        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        //从请求的Cookie中获取登陆令牌属性值
        String loginToken = CookieUtil.readLoginToken(request);
        if (loginToken == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        //根据登陆令牌属性值，从Redis缓存中获取User对象的JSON字符串
        String userJsonStr = RedisSharededPoolUtil.get(loginToken);
        //反序列化为User对象
        User user = JsonUtil.string2Obj(userJsonStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "请先登录");
        }

        return iUserService.getUserDetailInfo(user.getId());
    }
}
