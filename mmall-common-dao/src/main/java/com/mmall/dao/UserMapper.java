package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

/**
 * @author 蒙卓明
 * @date 2018-11-21
 */
public interface UserMapper {

    /**
     * 根据用户ID删除指定用户
     *
     * @param id
     * @return
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 新增用户
     *
     * @param record 用户
     * @return
     */
    int insert(User record);

    /**
     * 插入用户记录
     *
     * @param record 用户
     * @return
     */
    int insertSelective(User record);

    /**
     * 根据用户ID获取用户详细信息
     *
     * @param id 用户ID
     * @return
     */
    User selectByPrimaryKey(Integer id);

    /**
     * 选择性更新用户信息
     *
     * @param record 用户信息
     * @return
     */
    int updateByPrimaryKeySelective(User record);

    /**
     * 更新用户记录
     *
     * @param record 记录
     * @return
     */
    int updateByPrimaryKey(User record);

    /**
     * 查找指定用户名是否存在
     *
     * @param username 用户名
     * @return
     */
    int selectByUsername(String username);

    /**
     * 查找指定邮箱是否存在
     *
     * @param email 邮箱
     * @return
     */
    int selectByEmail(String email);

    /**
     * 根据用户名和密码查找指定用户是否存在
     *
     * @param username 用户名
     * @param password 密码
     * @return
     */
    User selectByUsernameAndPassword(@Param("username") String username,
                                     @Param("password") String password);

    /**
     * 获取指定用户的密码提示问题
     *
     * @param username 用户名
     * @return
     */
    String selectQuestionByUsername(String username);

    /**
     * 根据用户名、密码提示问题和密码提示问题答案校验用户是否正确会回答密码提示问题
     *
     * @param username 用户名
     * @param question 密码提示问题
     * @param answer   密码提示问题答案
     * @return
     */
    int selectByUsernameAndQuestionAndAnswer(@Param("username") String username,
                                             @Param("question") String question,
                                             @Param("answer") String answer);

    /**
     * 根据用户名更新密码
     *
     * @param username    用户名
     * @param newPassword 新密码
     * @return
     */
    int updatePasswordByUsername(@Param("username") String username,
                                 @Param("newPassword") String newPassword);

    /**
     * 根据用户ID和密码查询记录数
     *
     * @param password 用户密码
     * @param id       用户ID
     * @return
     */
    int selectPasswordById(@Param("password") String password, @Param("id") Integer id);

    /**
     * 根据用户ID和邮箱查询记录数
     *
     * @param id    用户ID
     * @param email 邮箱
     * @return
     */
    int selectByIdAndEmail(@Param("id") Integer id, @Param("email") String email);
}