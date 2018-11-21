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
public class CategoryProperty {
    private Integer id;

    private Integer categoryId;

    private String attributeName;

    private Date createTime;

    private Date updateTime;
}