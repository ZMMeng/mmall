package com.mmall.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by 蒙卓明 on 2018/10/27
 */
public class CartVo {

    private List<CartProductVo> cartProductVoList;
    private BigDecimal cartTotalPrice;
    private Boolean allCheck;
    private String imageHost;

    public CartVo() {
    }

    public CartVo(List<CartProductVo> cartProductVoList, BigDecimal cartTotalPrice, Boolean allCheck, String
            imageHost) {
        this.cartProductVoList = cartProductVoList;
        this.cartTotalPrice = cartTotalPrice;
        this.allCheck = allCheck;
        this.imageHost = imageHost;
    }

    public List<CartProductVo> getCartProductVoList() {
        return cartProductVoList;
    }

    public void setCartProductVoList(List<CartProductVo> cartProductVoList) {
        this.cartProductVoList = cartProductVoList;
    }

    public BigDecimal getCartTotalPrice() {
        return cartTotalPrice;
    }

    public void setCartTotalPrice(BigDecimal cartTotalPrice) {
        this.cartTotalPrice = cartTotalPrice;
    }

    public Boolean getAllCheck() {
        return allCheck;
    }

    public void setAllCheck(Boolean allCheck) {
        this.allCheck = allCheck;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }
}
