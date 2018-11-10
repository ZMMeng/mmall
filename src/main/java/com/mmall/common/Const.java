package com.mmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Created by 蒙卓明 on 2018/10/21
 */
public class Const {

    public static final String CURRENT_USER = "currentUser";
    public static final String EMAIL = "email";
    public static final String USERNAME = "username";

    //声明分组常量

    /**
     * 产品列表排序相关参数
     */
    public interface ProductListOrderBy {
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc", "price_asc");
    }

    /**
     * 角色相关参数
     */
    public interface Role {
        int ROLE_CUSTOMER = 0; //普通用户
        int ROLE_ADMIN = 1; //管理员
    }

    /**
     * 购物车相关参数
     */
    public interface CartMarker {
        //产品在购物栏中为选中状态
        int CHECKED = 1;
        //产品在购物栏中为未选中状态
        int UN_CHECKED = 0;
        //购物栏中的产品数量已超过产品库存
        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
        //购物栏中的产品数量未超过产品库存
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
    }

    /**
     * 支付宝回调相关参数
     */
    public interface AlipayCallback {

        String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
        String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";

        String RESPONSE_SUCCESS = "success";
        String RESPONSE_FAILED = "failed";

    }

    /**
     * Redis缓存相关参数
     */
    public interface RedisCacheExTime {
        // 30 min
        int REDIS_SESSION_EXTIME = 60 * 30;

        int REDIS_FORGET_TOKEN_EXTIME = 60 * 60 * 12;
    }

    public interface CookieExTime {
        // 1 year
        int COOKIE_MAX_AGE = 60 * 60 * 24 * 365;
    }

    /**
     * 产品状态枚举
     */
    public enum ProductStatusEnum {
        ON_SALE("在售", 1),
        ;

        private String value;
        private int code;

        ProductStatusEnum(String value, int code) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

    /**
     * 支付类型枚举
     */
    public enum PaymentTypeEnum {

        ONLINE_PAY("在线支付", 1)
        ;

        private String value;
        private int code;

        PaymentTypeEnum(String value, int code) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static PaymentTypeEnum codeOf(int code) {
            for (PaymentTypeEnum pte : values()) {
                if (pte.getCode() == code) {
                    return pte;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }
    }

    /**
     * 订单状态枚举
     */
    public enum OrderStatusEnum {

        CANCELED("已取消", 0),
        NO_PAY("未支付", 10),
        PAID("已付款", 20),
        SHIPPED("已发货", 40),
        ORDER_SUCCESS("订单完成", 50),
        ORDER_CLOSE("订单关闭", 60)
        ;

        private String value;
        private int code;

        OrderStatusEnum(String value, int code) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static OrderStatusEnum codeOf(int code) {
            for (OrderStatusEnum ose : values()) {
                if (ose.getCode() == code) {
                    return ose;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }
    }

    /**
     * 支付平台枚举
     */
    public enum PayPlatformEnum {

        ALIPAY("支付宝", 1)
        ;

        private String value;
        private int code;

        PayPlatformEnum(String value, int code) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

}
