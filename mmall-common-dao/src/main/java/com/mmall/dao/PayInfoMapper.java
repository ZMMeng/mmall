package com.mmall.dao;

import com.mmall.pojo.PayInfo;

/**
 *
 * @author 蒙卓明
 * @date 2018-11-21
 */
public interface PayInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PayInfo record);

    int insertSelective(PayInfo record);

    PayInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PayInfo record);

    int updateByPrimaryKey(PayInfo record);
}