package com.mmall.service.impl;

import com.mmall.common.ServerResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.Md5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 蒙卓明
 * @date 2018/11/25
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 登陆状态下重置密码
     *
     * @param passwordOld 旧密码
     * @param passwordNew 新密码
     * @param user        用户
     * @return
     */
    @Override
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user) {

        //判断用户输入的旧密码
        int resultCount = userMapper.selectPasswordById(Md5Util.md5EncodeUtf8(passwordOld), user.getId());
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("旧密码错误");
        }

        User userForUpdate = new User();
        userForUpdate.setId(user.getId());
        userForUpdate.setPassword(Md5Util.md5EncodeUtf8(passwordNew));
        resultCount = userMapper.updateByPrimaryKeySelective(userForUpdate);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("修改密码失败");
        }
        return ServerResponse.createBySuccessMessage("修改密码成功");
    }

    /**
     * 根据用户ID获取用户详细信息
     *
     * @param id 用户ID
     * @return
     */
    @Override
    public ServerResponse<User> getUserDetailInfo(Integer id) {
        User user = userMapper.selectByPrimaryKey(id);
        if (user == null) {
            return ServerResponse.createByErrorMessage("未找到该用户");
        }
        //不显示用户密码
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    /**
     * 登陆状态下更新用户信息
     *
     * @param user 用户
     * @return
     */
    @Override
    public ServerResponse<User> updateInfo(User user) {

        int resultCount = userMapper.selectByIdAndEmail(user.getId(), user.getEmail());
        if (resultCount > 0) {
            return ServerResponse.createByErrorMessage("该邮箱已存在，请更换邮箱");
        }

        User userForUpdate = new User();
        userForUpdate.setId(user.getId());
        userForUpdate.setEmail(user.getEmail());
        userForUpdate.setPhone(user.getPhone());
        userForUpdate.setQuestion(user.getQuestion());
        userForUpdate.setAnswer(user.getAnswer());

        resultCount = userMapper.updateByPrimaryKeySelective(userForUpdate);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("更新个人信息失败");
        }

        return ServerResponse.createBySuccess("更新个人信息成功", userForUpdate);
    }
}
