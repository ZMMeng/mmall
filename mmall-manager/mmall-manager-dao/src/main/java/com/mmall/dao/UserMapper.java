package com.mmall.dao;

import com.mmall.pojo.User;

/**
 *
 * @author 蒙卓明
 * @date 2018-11-21
 */
public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
}