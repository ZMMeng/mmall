package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

import java.util.Map;

/**
 * Created by 蒙卓明 on 2018/10/21
 */
public interface IUserService {

    ServerResponse<User> login(String username, String password) ;

    ServerResponse<String> register(User user);

    ServerResponse<String> checkValid(String str, String type);

    ServerResponse<String> selectQuestion(String username);

    ServerResponse<String> checkAnswer(String username, String question, String answer);

    ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken);

    ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user);

    ServerResponse<User> updateInfo(User user);

    ServerResponse<User> getUserDetailInfo(Integer userId);

    ServerResponse<String> checkAdminRole(User user);

    ServerResponse<Map<String, String>> getVerifyCode();

    ServerResponse<String> checkVerifyCode(String verifyCodeText, String verifyCode);
}
