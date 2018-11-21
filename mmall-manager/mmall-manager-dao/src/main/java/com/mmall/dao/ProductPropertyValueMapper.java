package com.mmall.dao;

import com.mmall.pojo.ProductPropertyValue;

/**
 *
 * @author 蒙卓明
 * @date 2018-11-21
 */
public interface ProductPropertyValueMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ProductPropertyValue record);

    int insertSelective(ProductPropertyValue record);

    ProductPropertyValue selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ProductPropertyValue record);

    int updateByPrimaryKey(ProductPropertyValue record);
}