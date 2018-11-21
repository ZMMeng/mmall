package com.mmall.dao;

import com.mmall.pojo.ProductComment;

/**
 *
 * @author 蒙卓明
 * @date 2018-11-21
 */
public interface ProductCommentMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ProductComment record);

    int insertSelective(ProductComment record);

    ProductComment selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ProductComment record);

    int updateByPrimaryKey(ProductComment record);
}