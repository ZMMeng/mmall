package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by 蒙卓明 on 2018/10/21
 */
@Controller
@RequestMapping("/manage/user")
public class UserManagerController {

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
    public ServerResponse<User> login(String username, String password, HttpSession session) {

        ServerResponse<User> response = iUserService.login(username, password);
        if (!response.isSuccess()) {
            return response;
        }

        User user = response.getData();
        if (user.getRole() == Const.Role.ROLE_ADMIN) {
            session.setAttribute(Const.CURRENT_USER, user);
            return response;
        }

        return ServerResponse.createByErrorMessage("无权登陆");
    }
}
