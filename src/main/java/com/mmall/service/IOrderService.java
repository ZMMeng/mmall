package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.vo.OrderProductVo;
import com.mmall.vo.OrderVo;

import java.util.Map;

/**
 * Created by 蒙卓明 on 2018/10/28
 */
public interface IOrderService {

    ServerResponse<OrderVo> createOrder(Integer userId, Integer shippingId);

    ServerResponse<String> cancelOrder(Integer userId, Long orderNo);

    ServerResponse<OrderProductVo> getOrderCartProductList(Integer userId);

    ServerResponse<OrderVo> getOrderDetail(Integer userId, Long orderNo);

    ServerResponse<PageInfo<OrderVo>> getOrderList(Integer userId, int pageNum, int pageSize);

    ServerResponse<PageInfo<OrderVo>> manageOrderList(int pageNum, int pageSize);

    ServerResponse<OrderVo> manageOrderDetail(Long orderNo);

    ServerResponse<PageInfo<OrderVo>> manageSearchOrder(Long orderNo, int pageNum, int pageSize);

    ServerResponse<String> manageSendOrderGoods(Long orderNo);

    ServerResponse<Map<String, String>> payOrder(Integer userId, Long orderNo, String path);

    ServerResponse<String> alipayCallback(Map<String, String> paramterMap);

    ServerResponse<String> queryOrderPayStatus(Integer userId, Long orderNo);
}
