package com.mmall.pojo;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Category {
    private Integer id;

    private Integer parentId;

    private String name;

    private Boolean status;

    private Integer sortOrder;

    private Date createTime;

    private Date updateTime;

    //使用Set集合包装对象时，需要重写equals()和hashCode()两个方法，以便于Set集合去重
    //注意equals()和hashCode()两个方法的判断条件应该相同，这里都是使用id去判断

}