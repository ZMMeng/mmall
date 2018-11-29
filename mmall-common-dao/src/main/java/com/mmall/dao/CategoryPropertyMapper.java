package com.mmall.dao;

import com.mmall.pojo.CategoryProperty;

import java.util.List;

/**
 * @author 蒙卓明
 * @date 2018-11-21
 */
public interface CategoryPropertyMapper {

    /**
     * 根据属性ID删除属性
     *
     * @param id 属性ID
     * @return
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 插入属性
     *
     * @param record 属性
     * @return
     */
    int insert(CategoryProperty record);

    /**
     * 插入属性
     *
     * @param record 属性
     * @return
     */
    int insertSelective(CategoryProperty record);

    /**
     * 根据属性ID查询属性
     *
     * @param id 属性ID
     * @return
     */
    CategoryProperty selectByPrimaryKey(Integer id);

    /**
     * 根据属性ID更新属性
     *
     * @param record 属性
     * @return
     */
    int updateByPrimaryKeySelective(CategoryProperty record);

    /**
     * 根据属性ID更新属性
     *
     * @param record 属性
     * @return
     */
    int updateByPrimaryKey(CategoryProperty record);

    /**
     * 获取某一分类下的所有属性
     *
     * @param categoryId 分类ID
     * @return
     */
    List<CategoryProperty> selectByCategoryId(Integer categoryId);
}