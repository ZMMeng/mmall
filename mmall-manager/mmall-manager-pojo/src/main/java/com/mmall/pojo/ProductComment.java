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
public class ProductComment {
    private Integer id;

    private Integer productId;

    private Long orderNo;

    private Integer userId;

    private String title;

    private String content;

    private Integer auditStatus;

    private Date createTime;

    private Date updateTime;
}