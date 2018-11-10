package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import com.mmall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by 蒙卓明 on 2018/10/27
 */
@RequestMapping("/cart")
@Controller
public class CartController {

    @Autowired
    private ICartService iCartService;

    /**
     * 列出购物车下所有产品信息
     *
     * @param request 请求
     * @return
     */
    @RequestMapping(value = "list_cart.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<CartVo> listCart(HttpServletRequest request) {

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
                    ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.listCart(user.getId());
    }

    /**
     * 在购物车中添加产品
     *
     * @param productId 产品ID
     * @param quantity  购买数量
     * @param request   请求
     * @return
     */
    @RequestMapping(value = "add_product.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<CartVo> addProduct(Integer productId, Integer quantity,
                                             HttpServletRequest request) {

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
                    ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.addProduct(user.getId(), productId, quantity);
    }

    /**
     * 更新购物车中产品的购买数量
     *
     * @param productId 产品ID
     * @param quantity  购买数量
     * @param request   请求
     * @return
     */
    @RequestMapping(value = "update_cart.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<CartVo> updateCart(Integer productId, Integer quantity,
                                             HttpServletRequest request) {

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
                    ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.updateCart(user.getId(), productId, quantity);
    }

    /**
     * 删除用户购物车中的产品
     *
     * @param productIds 产品ID列表，以","分隔
     * @param request    请求
     * @return
     */
    @RequestMapping(value = "delete_product.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<CartVo> deleteProduct(String productIds, HttpServletRequest request) {

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
                    ResponseCode.NEED_LOGIN.getDesc());
        }

        return iCartService.deleteProduct(user.getId(), productIds);
    }

    /**
     * 全选购物车下所有产品
     *
     * @param request 请求
     * @return
     */
    @RequestMapping(value = "select_all.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<CartVo> selectAll(HttpServletRequest request) {

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
                    ResponseCode.NEED_LOGIN.getDesc());
        }

        return iCartService.selectOrUnSelect(user.getId(), null, Const.CartMarker.CHECKED);
    }

    /**
     * 全反选购物车下所有产品
     *
     * @param request 请求
     * @return
     */
    @RequestMapping(value = "un_select_all.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<CartVo> unSelectAll(HttpServletRequest request) {

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
                    ResponseCode.NEED_LOGIN.getDesc());
        }

        return iCartService.selectOrUnSelect(user.getId(), null, Const.CartMarker.UN_CHECKED);
    }

    /**
     * 单独选购物车下的某个产品
     *
     * @param request 请求
     * @return
     */
    @RequestMapping(value = "select.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<CartVo> select(Integer productId, HttpServletRequest request) {

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
                    ResponseCode.NEED_LOGIN.getDesc());
        }

        return iCartService.selectOrUnSelect(user.getId(), productId, Const.CartMarker.CHECKED);
    }

    /**
     * 单独反选购物车下的某个产品
     *
     * @param request 请求
     * @return
     */
    @RequestMapping(value = "un_select.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<CartVo> unSelect(Integer productId, HttpServletRequest request) {

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
                    ResponseCode.NEED_LOGIN.getDesc());
        }

        return iCartService.selectOrUnSelect(user.getId(), productId, Const.CartMarker.UN_CHECKED);
    }

    /**
     * 获取用户在当前购物车下的所有购买产品数量的总和
     *
     * @param request 请求
     * @return
     */
    @RequestMapping(value = "get_cart_product_count.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<Integer> getCartProductCount(HttpServletRequest request) {

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
            return ServerResponse.createBySuccess(0);
        }
        return iCartService.getCartProductCount(user.getId());
    }
}
