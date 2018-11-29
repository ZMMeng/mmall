package com.mmall.dao;

import com.mmall.pojo.Category;

import java.util.List;

/**
 * @author 蒙卓明
 * @date 2018-11-21
 */
public interface CategoryMapper {

    /**
     * 根据分类ID删除分类
     *
     * @param id
     * @return
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 插入分类记录
     *
     * @param record 分类
     * @return
     */
    int insert(Category record);

    /**
     * 插入分类记录
     *
     * @param record 分类
     * @return
     */
    int insertSelective(Category record);

    /**
     * 根据分类ID查找分类
     *
     * @param id 分类ID
     * @return
     */
    Category selectByPrimaryKey(Integer id);

    /**
     * 更新分类
     *
     * @param record 分类
     * @return
     */
    int updateByPrimaryKeySelective(Category record);

    /**
     * 更新分类
     *
     * @param record 分类
     * @return
     */
    int updateByPrimaryKey(Category record);

    /**
     * 获取当前分类的所有下一级子分类
     *
     * @param categoryId 分类
     * @return
     */
    List<Category> selectChildCategoriesByParentId(Integer categoryId);
}