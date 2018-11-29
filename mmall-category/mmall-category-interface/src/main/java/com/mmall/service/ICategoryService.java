package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.pojo.CategoryProperty;

import java.util.List;

/**
 * 分类服务
 *
 * @author 蒙卓明
 * @date 2018/11/27
 */
public interface ICategoryService {

    /**
     * 添加分类
     *
     * @param categoryName 分类名
     * @param parentId     父分类ID
     * @return
     */
    ServerResponse<String> addCategory(String categoryName, Integer parentId);

    /**
     * 更新分类名称
     *
     * @param categoryName 新分类名
     * @param categoryId   父分类ID
     * @return
     */
    ServerResponse<String> updateCategoryName(String categoryName, Integer categoryId);

    /**
     * 获取当前分类的所有下一级分类
     *
     * @param categoryId 分类ID
     * @return
     */
    ServerResponse<List<Category>> getChildParallelCategories(Integer categoryId);

    /**
     * 递归获取指定分类的所有后代分类
     *
     * @param categoryId 分类ID
     * @return
     */
    ServerResponse<List<Category>> getChildRecursiveCategories(Integer categoryId);

    /**
     * 为指定分类添加属性
     *
     * @param propertyName 属性名
     * @param categoryId   分类ID
     * @return
     */
    ServerResponse<String> addCategoryProperty(String propertyName, Integer categoryId);

    /**
     * 删除指定属性
     *
     * @param propertyId 属性ID
     * @return
     */
    ServerResponse<String> deleteCategoryProperty(Integer propertyId);

    /**
     * 更改属性名
     *
     * @param propertyName 属性名
     * @param propertyId   属性ID
     * @return
     */
    ServerResponse<String> updateCategoryProperty(String propertyName, Integer propertyId);

    /**
     * 获取某一分类下的所有属性
     *
     * @param categoryId 分类ID
     * @param pageNum    当前页
     * @param pageSize   页面容量
     * @return
     */
    ServerResponse<PageInfo<CategoryProperty>> getAllPropertiesByCategoryId(Integer categoryId, int pageNum,
                                                                            int pageSize);
}
