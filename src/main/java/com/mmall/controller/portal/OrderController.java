package com.mmall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import com.mmall.vo.OrderProductVo;
import com.mmall.vo.OrderVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by 蒙卓明 on 2018/10/28
 */
@RequestMapping("/order")
@Controller
@Slf4j
public class OrderController {

    @Autowired
    private IOrderService iOrderService;

    /**
     * 创建订单
     *
     * @param shippingId 收货地址ID
     * @param request    请求
     * @return
     */
    @RequestMapping(value = "create_order.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<OrderVo> createOrder(Integer shippingId, HttpServletRequest request) {

        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        // 从请求的Cookie中获取登陆令牌属性值
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

        return iOrderService.createOrder(user.getId(), shippingId);
    }

    /**
     * 取消订单
     *
     * @param orderNo 订单号
     * @param request 请求
     * @return
     */
    @RequestMapping(value = "cancel_order.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> cancelOrder(Long orderNo, HttpServletRequest request) {

        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        // 从请求的Cookie中获取登陆令牌属性值
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
        return iOrderService.cancelOrder(user.getId(), orderNo);
    }

    /**
     * 获取订单中订单项信息
     *
     * @param request 请求
     * @return
     */
    @RequestMapping(value = "get_order_cart_product_list.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<OrderProductVo> getOrderCartProductList(HttpServletRequest request) {

        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        // 从请求的Cookie中获取登陆令牌属性值
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
        return iOrderService.getOrderCartProductList(user.getId());
    }

    /**
     * 获取订单详情
     *
     * @param orderNo 订单号
     * @param request 请求
     * @return
     */
    @RequestMapping(value = "get_order_detail.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<OrderVo> getOrderDetail(Long orderNo, HttpServletRequest request) {

        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        // 从请求的Cookie中获取登陆令牌属性值
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
        return iOrderService.getOrderDetail(user.getId(), orderNo);
    }

    /**
     * 分页获取当前用户下的所有订单信息
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
        // 从请求的Cookie中获取登陆令牌属性值
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
        return iOrderService.getOrderList(user.getId(), pageNum, pageSize);
    }

    /**
     * 支付订单
     *
     * @param orderNo 订单号
     * @param request request
     * @return
     */
    @RequestMapping(value = "pay_order.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<Map<String, String>> payOrder(Long orderNo, HttpServletRequest request) {

        //HttpSession session = request.getSession();
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        // 从请求的Cookie中获取登陆令牌属性值
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

        String path = request.getSession().getServletContext().getRealPath("upload");
        return iOrderService.payOrder(user.getId(), orderNo, path);
    }

    /**
     * 接收支付的回调信息
     *
     * @param request request
     * @return
     */
    @RequestMapping(value = "alipay_callback.do", method = RequestMethod.POST)
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request) {

        Map<String, String[]> requestParameterMap = request.getParameterMap();
        Map<String, String> parameterMap = Maps.newHashMap();
        for (Map.Entry<String, String[]> entry : requestParameterMap.entrySet()) {
            String name = entry.getKey();
            String[] values = entry.getValue();
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = i == values.length - 1 ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            parameterMap.put(name, valueStr);
        }
        log.info("支付宝回调，sign: {}，trade_status: {}，parameters ：{}", parameterMap.get("sign"),
                parameterMap.get("trade_status"), parameterMap.toString());

        //验证回调的正确性，即确保收到的回调信息是支付宝发出的
        //将sign_type属性移除，否则验签会报错
        parameterMap.remove("sign_type");
        try {
            boolean alipayRSACheckV2 = AlipaySignature.rsaCheckV2(parameterMap, Configs.getAlipayPublicKey(),
                    "utf-8", Configs.getSignType());
            if (!alipayRSACheckV2) {
                //验签不通过
                return ServerResponse.createByErrorMessage("非法请求，验证不通过");
            }
        } catch (AlipayApiException e) {
            log.error("支付宝验证回调异常", e);
        }

        //TODO 验证各种数据

        ServerResponse<String> response = iOrderService.alipayCallback(parameterMap);
        return response.isSuccess() ? Const.AlipayCallback.RESPONSE_SUCCESS :
                Const.AlipayCallback.RESPONSE_FAILED;
    }

    /**
     * 查询订单是否已付款
     *
     * @param orderNo 订单号
     * @param request 请求
     * @return
     */
    @RequestMapping(value = "query_order_pay_status.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<Boolean> queryOrderPayStatus(Long orderNo, HttpServletRequest request) {

        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        // 从请求的Cookie中获取登陆令牌属性值
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

        ServerResponse<String> response = iOrderService.queryOrderPayStatus(user.getId(), orderNo);
        return response.isSuccess() ? ServerResponse.createBySuccess(true) :
                ServerResponse.createBySuccess(false);
    }
}
