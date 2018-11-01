package com.mmall.dao;

import com.mmall.pojo.OrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);

    List<OrderItem> selectByUserIdAndOrderNo(@Param(value = "userId") Integer userId,
                                             @Param(value = "orderNo") Long orderNo);

    List<OrderItem> selectByOrderNo(Long orderNo);

    int batchInsert(@Param(value = "orderItemList") List<OrderItem> orderItemList);
}