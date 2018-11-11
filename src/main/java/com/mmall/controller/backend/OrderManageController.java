package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisSharededPoolUtil;
import com.mmall.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by 蒙卓明 on 2018/10/28
 */
@RequestMapping("/manage/order")
@Controller
public class OrderManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IOrderService iOrderService;

    /**
     * 在后台分页获取所有订单信息
     *
     * @param pageNum  当前页
     * @param pageSize 页面容量
     * @param request  请求
     * @return
     */
    @RequestMapping(value = "get_order_list.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<PageInfo<OrderVo>> getOrderList(
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            HttpServletRequest request) {

        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        //从请求的Cookie中获取登陆令牌属性值
        String loginToken = CookieUtil.readLoginToken(request);
        if (loginToken == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        //根据登陆令牌属性值，从Redis缓存中获取User对象的JSON字符串
        String userJsonStr = RedisSharededPoolUtil.get(loginToken);
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

        return iOrderService.manageOrderList(pageNum, pageSize);
    }

    /**
     * 在后台查看订单的详细信息
     *
     * @param orderNo 订单号
     * @param request 请求
     * @return
     */
    @RequestMapping(value = "get_order_detail.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<OrderVo> getOrderDetail(Long orderNo, HttpServletRequest request) {

        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        //从请求的Cookie中获取登陆令牌属性值
        String loginToken = CookieUtil.readLoginToken(request);
        if (loginToken == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        //根据登陆令牌属性值，从Redis缓存中获取User对象的JSON字符串
        String userJsonStr = RedisSharededPoolUtil.get(loginToken);
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
        return iOrderService.manageOrderDetail(orderNo);
    }

    /**
     * 在后台根据订单号搜索订单
     *
     * @param orderNo  订单号
     * @param pageNum  当前页
     * @param pageSize 页面容量
     * @param request  请求
     * @return
     */
    @RequestMapping(value = "search_order.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<PageInfo<OrderVo>> searchOrder(
            Long orderNo,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            HttpServletRequest request) {

        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        //从请求的Cookie中获取登陆令牌属性值
        String loginToken = CookieUtil.readLoginToken(request);
        if (loginToken == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        //根据登陆令牌属性值，从Redis缓存中获取User对象的JSON字符串
        String userJsonStr = RedisSharededPoolUtil.get(loginToken);
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
        return iOrderService.manageSearchOrder(orderNo, pageNum, pageSize);
    }

    /**
     * 发货
     *
     * @param orderNo 订单号
     * @param request 请求
     * @return
     */
    @RequestMapping(value = "send_order_goods.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> sendOrderGoods(Long orderNo, HttpServletRequest request) {

        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        //从请求的Cookie中获取登陆令牌属性值
        String loginToken = CookieUtil.readLoginToken(request);
        if (loginToken == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        //根据登陆令牌属性值，从Redis缓存中获取User对象的JSON字符串
        String userJsonStr = RedisSharededPoolUtil.get(loginToken);
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

        return iOrderService.manageSendOrderGoods(orderNo);
    }


}
