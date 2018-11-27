package com.mmall.dao;

import com.mmall.pojo.Category;

/**
 *
 * @author 蒙卓明
 * @date 2018-11-21
 */
public interface CategoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Category record);

    int insertSelective(Category record);

    Category selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Category record);

    int updateByPrimaryKey(Category record);
}