package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;

import java.util.List;

/**
 * Created by 蒙卓明 on 2018/10/21
 */
public interface ICategoryService {

    ServerResponse<String> addCategory(String categoryName, Integer parentId);

    ServerResponse<String> updateCategoryName(String categoryName, Integer categoryId);

    ServerResponse<List<Category>> getChildParallelCategories(Integer categoryId);

    ServerResponse<List<Category>> getChildRecursiveCategories(Integer categoryId);
}
