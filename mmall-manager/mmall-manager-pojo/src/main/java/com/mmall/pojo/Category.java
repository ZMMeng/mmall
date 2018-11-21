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
public class Category {
    private Integer id;

    private Integer parentId;

    private String name;

    private Boolean status;

    private Integer sortOrder;

    private Date createTime;

    private Date updateTime;
}