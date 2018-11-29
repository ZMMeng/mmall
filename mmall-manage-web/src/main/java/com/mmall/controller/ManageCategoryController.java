package com.mmall.controller;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.pojo.CategoryProperty;
import com.mmall.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


/**
 * @author 蒙卓明
 * @date 2018/11/28
 */
@RequestMapping("/manage/category")
@Controller
public class ManageCategoryController {

    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping(value = "add_category.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> addCategory(
            String categoryName, @RequestParam(value = "parentId", defaultValue = "0") int parentId) {
        return iCategoryService.addCategory(categoryName, parentId);
    }

    @RequestMapping(value = "set_category_name.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> setCategoryName(String categoryName, int categoryId) {
        return iCategoryService.updateCategoryName(categoryName, categoryId);
    }

    @RequestMapping(value = "get_child_parallel_categories.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Category>> getChildParallelCategories(
            @RequestParam(value = "categoryId", defaultValue = "0") int categoryId) {
        return iCategoryService.getChildParallelCategories(categoryId);
    }

    @RequestMapping(value = "get_child_recursive_categories.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Category>> getChildRecursiveCategories(
            @RequestParam(value = "categoryId", defaultValue = "0") int categoryId) {
        return iCategoryService.getChildRecursiveCategories(categoryId);
    }

    @RequestMapping(value = "add_category_property.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> addCategoryProperty(String propertyName, Integer categoryId) {
        return iCategoryService.addCategoryProperty(propertyName, categoryId);
    }

    @RequestMapping(value = "delete_category_property.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> deleteCategoryProperty(Integer propertyId) {
        return iCategoryService.deleteCategoryProperty(propertyId);
    }

    @RequestMapping(value = "update_category_property.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> updateCategoryProperty(String propertyName, Integer propertyId) {
        return iCategoryService.updateCategoryProperty(propertyName, propertyId);
    }

    @RequestMapping(value = "get_all_properties.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<PageInfo<CategoryProperty>> getAllPropertiesByCategoryId(
            Integer categoryId, @RequestParam(value = "pageNum", defaultValue = "1")int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10")int pageSize) {
        return iCategoryService.getAllPropertiesByCategoryId(categoryId, pageNum, pageSize);
    }
}
