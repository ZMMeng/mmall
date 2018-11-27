package com.mmall.dao;

import com.mmall.pojo.Cart;

/**
 *
 * @author 蒙卓明
 * @date 2018-11-21
 */
public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);
}