package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

/**
 * 用户服务相关
 *
 * @author 蒙卓明
 * @date 2018/11/25
 */
public interface IUserService {

    /**
     * 登陆状态下重置密码
     *
     * @param passwordOld 旧密码
     * @param passwordNew 新密码
     * @param user        用户
     * @return
     */
    ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user);

    /**
     * 根据用户ID获取用户详细信息
     *
     * @param id 用户ID
     * @return
     */
    ServerResponse<User> getUserDetailInfo(Integer id);

    /**
     * 登陆状态下更新用户信息
     *
     * @param user 用户
     * @return
     */
    ServerResponse<User> updateInfo(User user);
}
