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
    public interface ProductListOrderBy {
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc", "price_asc");
    }

    public interface Role {
        int ROLE_CUSTOMER = 0; //普通用户
        int ROLE_ADMIN = 1; //管理员
    }

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

}
