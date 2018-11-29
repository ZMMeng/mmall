package com.mmall.service.impl;

import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.constant.Const;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.ISsoService;
import com.mmall.util.Md5Util;
import com.mmall.util.RedisSharededPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * 用户登录注册以及忘记密码相关服务实现
 *
 * @author 蒙卓明
 * @date 2018/11/23
 */
@Service("iSsoService")
public class SsoServiceImpl implements ISsoService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 登录
     *
     * @param username 用户名
     * @param password 密码
     * @return
     */
    @Override
    public ServerResponse<User> login(String username, String password) {

        int resultCount = userMapper.selectByUsername(username);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        //需要对用户的密码进行MD5加密
        String md5Password = Md5Util.md5EncodeUtf8(password);
        User user = userMapper.selectByUsernameAndPassword(username, md5Password);
        if (user == null) {
            return ServerResponse.createByErrorMessage("密码错误");
        }
        return ServerResponse.createBySuccess("登陆成功", user);
    }

    /**
     * 检查用户输入参数是否有效
     *
     * @param str  参数值
     * @param type 参数类型
     * @return
     */
    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        if (StringUtils.isBlank(type)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        int resultCount;
        switch (type) {
            case Const.USERNAME:
                resultCount = userMapper.selectByUsername(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorMessage("用户名已存在");
                } else {
                    return ServerResponse.createBySuccessMessage("校验成功");
                }
            case Const.EMAIL:
                resultCount = userMapper.selectByEmail(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorMessage("邮箱已存在");
                } else {
                    return ServerResponse.createBySuccessMessage("校验成功");
                }
            default:
                return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                        ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
    }

    /**
     * 注册
     *
     * @param user 用户注册信息
     * @return
     */
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

        //对密码进行MD5加密
        user.setPassword(Md5Util.md5EncodeUtf8(user.getPassword()));

        int resultCount = userMapper.insertSelective(user);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("注册失败");
        }

        return ServerResponse.createBySuccessMessage("注册成功");
    }

    /**
     * 获取指定用户的忘记密码提示问题
     *
     * @param username 用户名
     * @return
     */
    @Override
    public ServerResponse<String> getForgetQuestion(String username) {

        ServerResponse<String> validResponse = this.checkValid(username, Const.USERNAME);
        if (validResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("用户不存在");
        }

        String question = userMapper.selectQuestionByUsername(username);
        if (StringUtils.isBlank(question)) {
            return ServerResponse.createByErrorMessage("密码提示问题为空");
        }

        return ServerResponse.createBySuccess(question);
    }

    /**
     * 校验密码提示问题是否回答正确
     *
     * @param username 用户名
     * @param question 密码提示问题
     * @param answer   密码提示问题答案
     * @return
     */
    @Override
    public ServerResponse<String> checkForgetAnswer(String username, String question, String answer) {

        int resultCount = userMapper.selectByUsernameAndQuestionAndAnswer(username, question, answer);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("密码提示问题答案错误");
        }
        String forgetToken = UUID.randomUUID().toString();
        RedisSharededPoolUtil.setEx(Const.TOKEN_PREFIX + username, forgetToken,
                Const.RedisCacheExTime.REDIS_FORGET_TOKEN_EXTIME);
        return ServerResponse.createBySuccess(forgetToken);
    }

    /**
     * 重置密码
     *
     * @param username    用户名
     * @param passwordNew 新密码
     * @param token       令牌
     * @return
     */
    @Override
    public ServerResponse<String> resetForgetPassword(String username, String passwordNew, String token) {
        if (StringUtils.isBlank(token)) {
            return ServerResponse.createByErrorMessage("参数错误，无token");
        }
        ServerResponse<String> validResponse = checkValid(username, Const.USERNAME);
        if (validResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("用户不存在");
        }

        String forgetToken = RedisSharededPoolUtil.get(Const.TOKEN_PREFIX + username);
        if (StringUtils.isBlank(forgetToken)) {
            return ServerResponse.createByErrorMessage("token无效或过期");
        }
        if (!StringUtils.equals(forgetToken, token)) {
            return ServerResponse.createByErrorMessage("token错误，请重新获取重置密码的token");
        }

        String md5Password = Md5Util.md5EncodeUtf8(passwordNew);
        int resultCount = userMapper.updatePasswordByUsername(username, md5Password);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("修改密码失败");
        }

        //更新密码成功后，要将token从缓存中删除
        RedisSharededPoolUtil.del(Const.TOKEN_PREFIX + username);
        return ServerResponse.createBySuccessMessage("修改密码成功");
    }

    /**
     * 查看用户是否具有管理员权限
     *
     * @param user 用户
     * @return
     */
    @Override
    public ServerResponse<String> checkAdminRole(User user) {
        if (user != null && user.getRole() == Const.Role.ROLE_ADMIN) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByErrorMessage("当前用户无权限操作，需要管理员权限");
    }
}
