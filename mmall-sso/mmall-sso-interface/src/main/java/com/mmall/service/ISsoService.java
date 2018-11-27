package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

/**
 * 用户登录注册以及忘记密码相关服务接口
 *
 * @author 蒙卓明
 * @date 2018/11/23
 */
public interface ISsoService {

    /**
     * 登录
     *
     * @param username 用户名
     * @param password 密码
     * @return
     */
    ServerResponse<User> login(String username, String password);

    /**
     * 检查用户输入参数是否有效
     *
     * @param str  参数值
     * @param type 参数类型
     * @return
     */
    ServerResponse<String> checkValid(String str, String type);

    /**
     * 注册
     *
     * @param user 用户注册信息
     * @return
     */
    ServerResponse<String> register(User user);

    /**
     * 获取指定用户的忘记密码提示问题
     *
     * @param username 用户名
     * @return
     */
    ServerResponse<String> getForgetQuestion(String username);

    /**
     * 校验密码提示问题是否回答正确
     *
     * @param username 用户名
     * @param question 密码提示问题
     * @param answer   密码提示问题答案
     * @return
     */
    ServerResponse<String> checkForgetAnswer(String username, String question, String answer);

    /**
     * 重置密码
     *
     * @param username    用户名
     * @param passwordNew 新密码
     * @param token       令牌
     * @return
     */
    ServerResponse<String> resetForgetPassword(String username, String passwordNew, String token);
}
