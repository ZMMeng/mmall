package com.mmall.dao;

import com.mmall.pojo.Order;

/**
 *
 * @author 蒙卓明
 * @date 2018-11-21
 */
public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);
}