package com.mmall.dao;

import com.mmall.pojo.CategoryProperty;

/**
 *
 * @author 蒙卓明
 * @date 2018-11-21
 */
public interface CategoryPropertyMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CategoryProperty record);

    int insertSelective(CategoryProperty record);

    CategoryProperty selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CategoryProperty record);

    int updateByPrimaryKey(CategoryProperty record);
}