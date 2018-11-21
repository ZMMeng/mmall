package com.mmall.pojo;

import lombok.*;

import java.util.Date;

/**
 *
 * @author 蒙卓明
 * @date 2018-11-21
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Cart {
    private Integer id;

    private Integer userId;

    private Integer productId;

    private Integer quantity;

    private Boolean checked;

    private Date createTime;

    private Date updateTime;
}