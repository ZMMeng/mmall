package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;
import com.mmall.pojo.User;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Created by 蒙卓明 on 2018/10/27
 */
@RequestMapping("/shipping")
@Controller
public class ShippingController {

    @Autowired
    private IShippingService iShippingService;

    /**
     * 新建收货地址
     *
     * @param shipping 收货地址
     * @param session  session
     * @return
     */
    @RequestMapping(value = "add_address.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String, Integer>> addAddress(Shipping shipping, HttpSession session) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.addAddress(user.getId(), shipping);
    }

    /**
     * 删除收货地址
     *
     * @param shippingId 收货地址ID
     * @param session    session
     * @return
     */
    @RequestMapping(value = "delete_address.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> deleteAddress(Integer shippingId, HttpSession session) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }

        return iShippingService.deleteAddress(user.getId(), shippingId);
    }

    /**
     * 更新收货地址
     *
     * @param shipping 收货地址
     * @param session  session
     * @return
     */
    @RequestMapping(value = "update_address.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> updateAddress(Shipping shipping, HttpSession session) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }

        return iShippingService.updateAddress(user.getId(), shipping);
    }

    /**
     * 根据收货地址ID查询收货地址
     *
     * @param shippingId 收货 地址ID
     * @param session    session
     * @return
     */
    @RequestMapping(value = "select_address.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<Shipping> selectAddress(Integer shippingId, HttpSession session) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }

        return iShippingService.selectAddress(user.getId(), shippingId);
    }

    /**
     * 分页获取当前登陆用户的所有收货地址
     *
     * @param pageNum  当前页
     * @param pageSize 页面容量
     * @param session  session
     * @return
     */
    @RequestMapping(value = "list_addresses.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<PageInfo<Shipping>> listAddresses(
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            HttpSession session) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc());
        }

        return iShippingService.listAddresses(user.getId(), pageNum, pageSize);
    }
}
