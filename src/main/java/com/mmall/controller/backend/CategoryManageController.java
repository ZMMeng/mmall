package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by 蒙卓明 on 2018/10/21
 */
@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;

    /**
     * 添加品类
     *
     * @param categoryName 品类名称
     * @param parentId     上级品类id
     * @param session      session
     * @return
     */
    @RequestMapping(value = "add_category.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> addCategory(String categoryName, @RequestParam(value = "parentId", defaultValue = "0")
            int parentId, HttpSession session) {

        //判断是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请先登录");
        }

        //判断是否是管理员
        ServerResponse<String> adminResponse = iUserService.checkAdminRole(user);
        if (!adminResponse.isSuccess()) {
            return adminResponse;
        }

        return iCategoryService.addCategory(categoryName, parentId);
    }

    /**
     * 更新品类名
     *
     * @param categoryName 新品类名
     * @param categoryId   品类id
     * @param session      session
     * @return
     */
    @RequestMapping(value = "set_category_name.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> setCategoryName(String categoryName, int categoryId, HttpSession session) {

        //判断是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请先登录");
        }

        //判断是否是管理员
        ServerResponse<String> adminResponse = iUserService.checkAdminRole(user);
        if (!adminResponse.isSuccess()) {
            return adminResponse;
        }

        return iCategoryService.updateCategoryName(categoryName, categoryId);
    }

    /**
     * 获取当前分类下的所有下一级子分类(非递归)
     * @param categoryId 当前分类id
     * @param session session
     * @return
     */
    @RequestMapping(value = "get_child_parallel_categories.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Category>> getChildParallelCategories(@RequestParam(value = "categoryId", defaultValue =
            "0") int categoryId, HttpSession session) {

        //判断是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请先登录");
        }

        //判断是否是管理员
        ServerResponse<String> adminResponse = iUserService.checkAdminRole(user);
        if (!adminResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("当前用户无权限操作，需要管理员权限");
        }

        //查找当前品类的所有下一级品类
        return iCategoryService.getChildParallelCategories(categoryId);
    }

    /**
     * 递归获取当前分类下的所有子分类
     * @param categoryId 当前分类id
     * @param session session
     * @return
     */
    @RequestMapping(value = "get_child_recursive_categories.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Category>> getChildRecursiveCategories(@RequestParam(value = "categoryId", defaultValue =
            "0") int categoryId, HttpSession session) {

        //判断是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请先登录");
        }

        //判断是否是管理员
        ServerResponse<String> adminResponse = iUserService.checkAdminRole(user);
        if (!adminResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("当前用户无权限操作，需要管理员权限");
        }

        return iCategoryService.getChildRecursiveCategories(categoryId);
    }
}
