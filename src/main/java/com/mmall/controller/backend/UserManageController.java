package com.mmall.controller.backend;

import com.mmall.common.Const;
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

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by 蒙卓明 on 2018/10/21
 */
@Controller
@RequestMapping("/manage/user")
public class UserManageController {

    @Autowired
    private IUserService iUserService;

    /**
     * 管理员登陆
     * @param username 用户名
     * @param password 密码
     * @param session session
     * @return
     */
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session,
                                      HttpServletResponse response) {

        ServerResponse<User> loginResponse = iUserService.login(username, password);
        if (!loginResponse.isSuccess()) {
            return loginResponse;
        }

        User user = loginResponse.getData();
        if (user.getRole() == Const.Role.ROLE_ADMIN) {
            //session.setAttribute(Const.CURRENT_USER, user);
            //session.setAttribute(Const.CURRENT_USER, response.getData());
            //向客户端写入Cookie，以session的ID作为登陆令牌的属性值
            CookieUtil.writeLoginToken(response, session.getId());
            //将登陆信息写入Redis缓存，key是session的ID，value是User对象
            RedisSharededPoolUtil.setEx(session.getId(), JsonUtil.obj2String(loginResponse.getData()),
                    Const.RedisCacheExTime.REDIS_SESSION_EXTIME);
            return loginResponse;
        }

        return ServerResponse.createByErrorMessage("无权登陆");
    }
}
