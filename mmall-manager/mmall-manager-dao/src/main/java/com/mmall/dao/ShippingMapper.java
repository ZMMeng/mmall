package com.mmall.dao;

import com.mmall.pojo.Shipping;

/**
 *
 * @author 蒙卓明
 * @date 2018-11-21
 */
public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);
}