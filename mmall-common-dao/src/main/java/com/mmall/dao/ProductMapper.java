package com.mmall.dao;

import com.mmall.pojo.Product;

/**
 *
 * @author 蒙卓明
 * @date 2018-11-21
 */
public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);
}