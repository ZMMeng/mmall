package com.mmall.dao;

import com.mmall.pojo.OrderItem;

/**
 *
 * @author 蒙卓明
 * @date 2018-11-21
 */
public interface OrderItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);
}