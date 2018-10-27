package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
import com.mmall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

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
     * @param session session
     * @return
     */
    @RequestMapping(value = "list_cart.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<CartVo> listCart(HttpSession session) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
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
     * @param session   session
     * @return
     */
    @RequestMapping(value = "add_product.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<CartVo> addProduct(Integer productId, Integer quantity, HttpSession session) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
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
     * @param session   session
     * @return
     */
    @RequestMapping(value = "update_cart.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<CartVo> updateCart(Integer productId, Integer quantity, HttpSession session) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
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
     * @param session    session
     * @return
     */
    @RequestMapping(value = "delete_product.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<CartVo> deleteProduct(String productIds, HttpSession session) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }

        return iCartService.deleteProduct(user.getId(), productIds);
    }

    /**
     * 全选购物车下所有产品
     *
     * @param session session
     * @return
     */
    @RequestMapping(value = "select_all.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<CartVo> selectAll(HttpSession session) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }

        return iCartService.selectOrUnSelect(user.getId(), null, Const.CartMarker.CHECKED);
    }

    /**
     * 全反选购物车下所有产品
     *
     * @param session session
     * @return
     */
    @RequestMapping(value = "un_select_all.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<CartVo> unSelectAll(HttpSession session) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }

        return iCartService.selectOrUnSelect(user.getId(), null, Const.CartMarker.UN_CHECKED);
    }

    /**
     * 单独选购物车下的某个产品
     *
     * @param session session
     * @return
     */
    @RequestMapping(value = "select.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<CartVo> select(Integer productId, HttpSession session) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }

        return iCartService.selectOrUnSelect(user.getId(), productId, Const.CartMarker.CHECKED);
    }

    /**
     * 单独反选购物车下的某个产品
     *
     * @param session session
     * @return
     */
    @RequestMapping(value = "un_select.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<CartVo> unSelect(Integer productId, HttpSession session) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }

        return iCartService.selectOrUnSelect(user.getId(), productId, Const.CartMarker.UN_CHECKED);
    }

    /**
     * 获取用户在当前购物车下的所有购买产品数量的总和
     *
     * @param session session
     * @return
     */
    @RequestMapping(value = "get_cart_product_count.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<Integer> getCartProductCount(HttpSession session) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createBySuccess(0);
        }
        return iCartService.getCartProductCount(user.getId());
    }
}
