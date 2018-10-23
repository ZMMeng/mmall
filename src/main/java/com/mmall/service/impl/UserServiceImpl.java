package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by 蒙卓明 on 2018/10/21
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {

        int resultCount = userMapper.checkUsername(username);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("用户名不存在");
        }

        //密码登陆MD5
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, md5Password);

        if (user == null) {
            return ServerResponse.createByErrorMessage("密码错误");
        }

        return ServerResponse.createBySuccess("登陆成功", user);
    }

    @Override
    public ServerResponse<String> register(User user) {

        ServerResponse<String> validResponse = checkValid(user.getUsername(), Const.USERNAME);
        if (!validResponse.isSuccess()) {
            return validResponse;
        }

        validResponse = checkValid(user.getEmail(), Const.EMAIL);
        if (!validResponse.isSuccess()) {
            return validResponse;
        }

        //默认设置为普通用户
        user.setRole(Const.Role.ROLE_CUSTOMER);

        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int resultCount = userMapper.insert(user);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("注册失败");
        }

        return ServerResponse.createBySuccessMessage("注册成功");
    }

    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        //注意isNotBlank()和isNotEmpty()的区别
        if (org.apache.commons.lang3.StringUtils.isNotBlank(type)) {
            //开始校验
            int resultCount;
            switch (type) {
                case Const.USERNAME:
                    resultCount = userMapper.checkUsername(str);
                    if (resultCount > 0) {
                        return ServerResponse.createByErrorMessage("用户名已存在");
                    } else {
                        return ServerResponse.createBySuccessMessage("校验成功");
                    }
                case Const.EMAIL:
                    resultCount = userMapper.checkEmail(str);
                    if (resultCount > 0) {
                        return ServerResponse.createByErrorMessage("Email已存在");
                    } else {
                        return ServerResponse.createBySuccessMessage("校验成功");
                    }
                default:
                    return ServerResponse.createByErrorMessage("参数错误");
            }
        } else {
            return ServerResponse.createByErrorMessage("参数错误");
        }

    }

    @Override
    public ServerResponse<String> selectQuestion(String username) {
        ServerResponse<String> validResponse = this.checkValid(username, Const.USERNAME);
        if (validResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("用户不存在");
        }

        String question = userMapper.selectQuestionByUsername(username);
        if (org.apache.commons.lang3.StringUtils.isNotBlank(question)) {
            return ServerResponse.createBySuccess(question);
        }

        return ServerResponse.createByErrorMessage("找回密码的问题为空");
    }

    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if (resultCount > 0) {

            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题答案错误");
    }

    @Override
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {

        if (org.apache.commons.lang3.StringUtils.isBlank(forgetToken)) {
            return ServerResponse.createByErrorMessage("参数错误，无token");
        }

        ServerResponse<String> validResponse = this.checkValid(username, Const.USERNAME);
        if (validResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("用户不存在");
        }

        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if (org.apache.commons.lang3.StringUtils.isBlank(token)) {
            return ServerResponse.createByErrorMessage("token无效或过期");
        }

        if (!org.apache.commons.lang3.StringUtils.equals(forgetToken, token)) {
            return ServerResponse.createByErrorMessage("token错误，请重新获取重置密码的token");
        }

        String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);

        int resultCount = userMapper.updatePasswordByUsername(username, md5Password);
        if (resultCount == 0) {
            ServerResponse.createByErrorMessage("修改密码失败");
        }

        return ServerResponse.createBySuccessMessage("修改密码成功");
    }

    @Override
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user) {

        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld), user.getId());
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("旧密码错误");
        }

        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        resultCount = userMapper.updateByPrimaryKeySelective(user);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("修改密码失败");
        }

        return ServerResponse.createBySuccessMessage("修改密码成功");
    }

    @Override
    public ServerResponse<User> updateInfo(User user) {

        int resultCount = userMapper.checkEmailByUserId(user.getEmail(), user.getId());
        if (resultCount > 0) {
            return ServerResponse.createByErrorMessage("email已存在，请更换email再尝试");
        }

        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        resultCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("更新个人信息失败");
        }

        return ServerResponse.createBySuccess("更新个人信息成功", updateUser);
    }

    @Override
    public ServerResponse<User> getUserDetailInfo(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    public ServerResponse<String> checkAdminRole(User user) {
        if (user != null && user.getRole() == Const.Role.ROLE_ADMIN) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByErrorMessage("当前用户无权限操作，需要管理员权限");
    }
}
