package com.mmall.controller;

import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.constant.Const;
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

/**
 * @author 蒙卓明
 * @date 2018/11/25
 */
@RequestMapping("/user")
@Controller
public class UserController {

    @Autowired
    public IUserService iUserService;

    @RequestMapping(value = "get_user_info.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpServletRequest request) {

        //从请求的Cookie中获取登陆令牌属性值
        String loginToken = CookieUtil.getCookieValue(Const.CookieNames.LOGIN, request);
        if (loginToken == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "请先登录");
        }

        //根据登陆令牌属性值，从Redis缓存中获取User对象的JSON字符串
        String userJsonStr = RedisSharededPoolUtil.get(loginToken);
        //反序列化为User对象
        User user = JsonUtil.string2Obj(userJsonStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "请先登录");
        }
        return ServerResponse.createBySuccess(user);
    }

    @RequestMapping(value = "get_user_detail_info.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> getUserDetailInfo(HttpServletRequest request) {

        //从请求的Cookie中获取登陆令牌属性值
        String loginToken = CookieUtil.getCookieValue(Const.CookieNames.LOGIN, request);
        if (loginToken == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "请先登录");
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

    @RequestMapping(value = "reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew,
                                                HttpServletRequest request) {
        //从请求的Cookie中获取登陆令牌属性值
        String loginToken = CookieUtil.getCookieValue(Const.CookieNames.LOGIN, request);
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
        return iUserService.resetPassword(passwordOld, passwordNew, user);
    }

    @RequestMapping(value = "update_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateInfo(User user, HttpServletRequest request) {

        //从请求的Cookie中获取登陆令牌属性值
        String loginToken = CookieUtil.getCookieValue(Const.CookieNames.LOGIN, request);
        if (loginToken == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }

        //根据登陆令牌属性值，从Redis缓存中获取User对象的JSON字符串
        String userJsonStr = RedisSharededPoolUtil.get(loginToken);
        //反序列化为User对象
        User currentUser = JsonUtil.string2Obj(userJsonStr, User.class);
        if (currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "请先登录");
        }
        //设置将前台页面传输过来的user的id
        user.setId(currentUser.getId());
        //用户名不能被更新
        user.setUsername(currentUser.getUsername());

        ServerResponse<User> updateResponse = iUserService.updateInfo(user);
        if (updateResponse.isSuccess()) {
            //在Redis中存入更新后的用户信息
            RedisSharededPoolUtil.setEx(loginToken, JsonUtil.obj2String(updateResponse.getData()),
                    Const.RedisCacheExTime.REDIS_SESSION_EXTIME);
        }
        return updateResponse;
    }
}
