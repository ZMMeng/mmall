package com.mmall.pojo;

import lombok.*;

import java.io.Serializable;
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
public class ProductPropertyValue implements Serializable{
    private Integer id;

    private Integer productId;

    private Integer propertyId;

    private String value;

    private Date createTime;

    private Date updateTime;
}