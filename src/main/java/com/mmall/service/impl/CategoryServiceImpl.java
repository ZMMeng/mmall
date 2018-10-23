package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Created by 蒙卓明 on 2018/10/21
 */
@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService{

    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;

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

        int resultCount = categoryMapper.insert(category);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("添加品类失败");
        }
        return ServerResponse.createBySuccessMessage("添加品类成功");
    }

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

    @Override
    public ServerResponse<List<Category>> getChildParallelCategories(Integer categoryId) {

        List<Category> categories = categoryMapper.selectChildCategoriesByParentId(categoryId);
        if (CollectionUtils.isEmpty(categories)) {
            logger.info("未找到当前分类的子分类");
        }
        return ServerResponse.createBySuccess(categories);
    }


    @Override
    public ServerResponse<List<Category>> getChildRecursiveCategories(Integer categoryId) {
        Set<Category> categorySet = Sets.newHashSet();
        findChildCategories(categoryId, categorySet);
        List<Category> categoryList = Lists.newArrayList(categorySet);
        return ServerResponse.createBySuccess(categoryList);
    }

    /**
     * 递归获取当前分类下的所有子分类
     * @param categoryId
     * @param categorySet
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
