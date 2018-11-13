package com.mmall.dao;

import com.mmall.pojo.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    Order selectByUserIdAndOrderNo(@Param(value = "userId") Integer userId,
                                   @Param(value = "orderNo") Long orderNo);

    List<Order> selectByUserId(Integer userId);

    List<Order> getAllOrders();

    Order selectByOrderNo(Long orderNo);

    List<Order> selectOrderStatusByCreateTime(@Param(value = "status") Integer status,
                                              @Param("date") String date);

    int updateOrderByOrderId(Integer orderId);
}