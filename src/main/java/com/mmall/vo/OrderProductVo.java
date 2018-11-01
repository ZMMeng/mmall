package com.mmall.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by 蒙卓明 on 2018/10/28
 */
public class OrderProductVo {

    private List<OrderItemVo> orderItemVoList;
    private BigDecimal productTotalPrice;
    private String imageHost;

    public OrderProductVo() {
    }

    public OrderProductVo(List<OrderItemVo> orderItemVoList, BigDecimal productTotalPrice, String imageHost) {
        this.orderItemVoList = orderItemVoList;
        this.productTotalPrice = productTotalPrice;
        this.imageHost = imageHost;
    }

    public List<OrderItemVo> getOrderItemVoList() {
        return orderItemVoList;
    }

    public void setOrderItemVoList(List<OrderItemVo> orderItemVoList) {
        this.orderItemVoList = orderItemVoList;
    }

    public BigDecimal getProductTotalPrice() {
        return productTotalPrice;
    }

    public void setProductTotalPrice(BigDecimal productTotalPrice) {
        this.productTotalPrice = productTotalPrice;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }
}
