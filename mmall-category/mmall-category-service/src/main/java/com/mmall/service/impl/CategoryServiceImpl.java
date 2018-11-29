package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.CategoryPropertyMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.CategoryProperty;
import com.mmall.service.ICategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @author 蒙卓明
 * @date 2018/11/27
 */
@Service("iCategoryService")
@Slf4j
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CategoryPropertyMapper categoryPropertyMapper;

    /**
     * 添加分类
     *
     * @param categoryName 分类名
     * @param parentId     父分类ID
     * @return
     */
    @Override
    public ServerResponse<String> addCategory(String categoryName, Integer parentId) {
        if (parentId == null || StringUtils.isBlank(categoryName)) {
            return ServerResponse.createByErrorMessage("添加品类参数错误");
        }

        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        //true表示分类可用
        category.setStatus(true);

        int resultCount = categoryMapper.insertSelective(category);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("添加品类失败");
        }
        return ServerResponse.createBySuccessMessage("添加品类成功");
    }

    /**
     * 更新分类名称
     *
     * @param categoryName 新分类名
     * @param categoryId   父分类ID
     * @return
     */
    @Override
    public ServerResponse<String> updateCategoryName(String categoryName, Integer categoryId) {

        if (categoryId == null || StringUtils.isBlank(categoryName)) {
            return ServerResponse.createByErrorMessage("更新品类参数错误");
        }

        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        int resultCount = categoryMapper.updateByPrimaryKeySelective(category);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("更新品类名失败");
        }
        return ServerResponse.createByErrorMessage("更新品类名成功");
    }

    /**
     * 获取当前分类的所有下一级分类
     *
     * @param categoryId 分类ID
     * @return
     */
    @Override
    public ServerResponse<List<Category>> getChildParallelCategories(Integer categoryId) {
        List<Category> categories = categoryMapper.selectChildCategoriesByParentId(categoryId);
        if (CollectionUtils.isEmpty(categories)) {
            log.info("未找到当前分类的子分类");
        }
        return ServerResponse.createBySuccess(categories);
    }

    /**
     * 递归获取指定分类的所有后代分类
     *
     * @param categoryId 分类ID
     * @return
     */
    @Override
    public ServerResponse<List<Category>> getChildRecursiveCategories(Integer categoryId) {
        Set<Category> categorySet = Sets.newHashSet();
        findChildCategories(categoryId, categorySet);
        List<Category> categoryList = Lists.newArrayList(categorySet);
        return ServerResponse.createBySuccess(categoryList);
    }

    /**
     * 为指定分类添加属性
     *
     * @param propertyName 属性名
     * @param categoryId   分类ID
     * @return
     */
    @Override
    public ServerResponse<String> addCategoryProperty(String propertyName, Integer categoryId) {

        CategoryProperty categoryProperty = new CategoryProperty();
        categoryProperty.setCategoryId(categoryId);
        categoryProperty.setAttributeName(propertyName);
        int resultCount = categoryPropertyMapper.insertSelective(categoryProperty);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("添加属性失败");
        }
        return ServerResponse.createBySuccessMessage("添加属性成功");
    }

    /**
     * 删除指定属性
     *
     * @param propertyId 属性ID
     * @return
     */
    @Override
    public ServerResponse<String> deleteCategoryProperty(Integer propertyId) {

        int resultCount = categoryPropertyMapper.deleteByPrimaryKey(propertyId);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("删除属性失败");
        }
        return ServerResponse.createBySuccessMessage("删除属性成功");
    }

    /**
     * 更改属性名
     *
     * @param propertyName 属性名
     * @param propertyId   属性ID
     * @return
     */
    @Override
    public ServerResponse<String> updateCategoryProperty(String propertyName, Integer propertyId) {

        CategoryProperty categoryProperty = new CategoryProperty();
        categoryProperty.setAttributeName(propertyName);
        categoryProperty.setId(propertyId);
        int resultCount = categoryPropertyMapper.updateByPrimaryKeySelective(categoryProperty);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("更新属性失败");
        }
        return ServerResponse.createBySuccessMessage("更新属性成功");
    }

    /**
     * 获取某一分类下的所有属性
     *
     * @param categoryId 分类ID
     * @param pageNum    当前页
     * @param pageSize   页面容量
     * @return
     */
    @Override
    public ServerResponse<PageInfo<CategoryProperty>> getAllPropertiesByCategoryId(Integer categoryId,
                                                                               int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<CategoryProperty> categoryPropertyList = categoryPropertyMapper.selectByCategoryId(categoryId);
        PageInfo<CategoryProperty> pageInfo = new PageInfo<>(categoryPropertyList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    /**
     * 递归获取当前分类下的所有子分类
     *
     * @param categoryId  分类ID
     * @param categorySet 分类集合
     * @return
     */
    private Set<Category> findChildCategories(Integer categoryId, Set<Category> categorySet) {

        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category != null) {
            categorySet.add(category);
        }

        List<Category> categories = categoryMapper.selectChildCategoriesByParentId(categoryId);
        for (Category categoryItem : categories) {
            findChildCategories(categoryItem.getId(), categorySet);
        }
        return categorySet;
    }
}
