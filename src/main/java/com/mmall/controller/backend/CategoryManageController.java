package com.mmall.controller.backend;

import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
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
     * @param request      请求
     * @return
     */
    @RequestMapping(value = "add_category.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> addCategory(
            String categoryName, @RequestParam(value = "parentId", defaultValue = "0") int parentId,
            HttpServletRequest request) {

        //判断是否登录
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        //从请求的Cookie中获取登陆令牌属性值
        String loginToken = CookieUtil.readLoginToken(request);
        if (loginToken == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        //根据登陆令牌属性值，从Redis缓存中获取User对象的JSON字符串
        String userJsonStr = RedisPoolUtil.get(loginToken);
        //反序列化为User对象
        User user = JsonUtil.string2Obj(userJsonStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "请先登录");
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
     * @param request      请求
     * @return
     */
    @RequestMapping(value = "set_category_name.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> setCategoryName(String categoryName, int categoryId,
                                                  HttpServletRequest request) {

        //判断是否登录
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        //从请求的Cookie中获取登陆令牌属性值
        String loginToken = CookieUtil.readLoginToken(request);
        if (loginToken == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        //根据登陆令牌属性值，从Redis缓存中获取User对象的JSON字符串
        String userJsonStr = RedisPoolUtil.get(loginToken);
        //反序列化为User对象
        User user = JsonUtil.string2Obj(userJsonStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "请先登录");
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
     *
     * @param categoryId 当前分类id
     * @param request    请求
     * @return
     */
    @RequestMapping(value = "get_child_parallel_categories.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Category>> getChildParallelCategories(
            @RequestParam(value = "categoryId", defaultValue = "0") int categoryId,
            HttpServletRequest request) {

        //判断是否登录
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        //从请求的Cookie中获取登陆令牌属性值
        String loginToken = CookieUtil.readLoginToken(request);
        if (loginToken == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        //根据登陆令牌属性值，从Redis缓存中获取User对象的JSON字符串
        String userJsonStr = RedisPoolUtil.get(loginToken);
        //反序列化为User对象
        User user = JsonUtil.string2Obj(userJsonStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "请先登录");
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
     *
     * @param categoryId 当前分类id
     * @param request    请求
     * @return
     */
    @RequestMapping(value = "get_child_recursive_categories.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Category>> getChildRecursiveCategories(
            @RequestParam(value = "categoryId", defaultValue = "0") int categoryId,
            HttpServletRequest request) {

        //判断是否登录
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        //从请求的Cookie中获取登陆令牌属性值
        String loginToken = CookieUtil.readLoginToken(request);
        if (loginToken == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        //根据登陆令牌属性值，从Redis缓存中获取User对象的JSON字符串
        String userJsonStr = RedisPoolUtil.get(loginToken);
        //反序列化为User对象
        User user = JsonUtil.string2Obj(userJsonStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "请先登录");
        }

        //判断是否是管理员
        ServerResponse<String> adminResponse = iUserService.checkAdminRole(user);
        if (!adminResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("当前用户无权限操作，需要管理员权限");
        }

        return iCategoryService.getChildRecursiveCategories(categoryId);
    }
}
