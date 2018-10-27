package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;

import java.util.Map;

/**
 * Created by 蒙卓明 on 2018/10/27
 */
public interface IShippingService {

    ServerResponse<Map<String, Integer>> addAddress(Integer userId, Shipping shipping);

    ServerResponse<String> deleteAddress(Integer userId, Integer shippingId);

    ServerResponse<String> updateAddress(Integer userId, Shipping shipping);

    ServerResponse<Shipping> selectAddress(Integer userId, Integer shippingId);

    ServerResponse<PageInfo<Shipping>> listAddresses(Integer userId, int pageNum, int pageSize);
}
