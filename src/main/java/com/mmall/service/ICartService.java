package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.vo.CartVo;

/**
 * Created by 蒙卓明 on 2018/10/27
 */
public interface ICartService {

    ServerResponse<CartVo> listCart(Integer userId);

    ServerResponse<CartVo> addProduct(Integer userId, Integer productId, Integer quantity);

    ServerResponse<CartVo> updateCart(Integer userId, Integer productId, Integer quantity);

    ServerResponse<CartVo> deleteProduct(Integer userId, String productIds);

    ServerResponse<CartVo> selectOrUnSelect(Integer userId, Integer productId, Integer check);

    ServerResponse<Integer> getCartProductCount(Integer userId);
}
