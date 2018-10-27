package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by 蒙卓明 on 2018/10/27
 */
@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    /**
     * 新建收货地址
     *
     * @param userId   用户ID
     * @param shipping 收货地址
     * @return
     */
    @Override
    public ServerResponse<Map<String, Integer>> addAddress(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        int resultCount = shippingMapper.insert(shipping);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("新建地址失败");
        }
        Map<String, Integer> resultMap = Maps.newHashMap();
        resultMap.put("shippingId", shipping.getId());
        return ServerResponse.createBySuccess("新建地址成功", resultMap);
    }

    /**
     * 删除收货地址
     *
     * @param userId     用户ID
     * @param shippingId 收货地址id
     * @return
     */
    @Override
    public ServerResponse<String> deleteAddress(Integer userId, Integer shippingId) {

        //注意这里不能简单调用deleteByPrimaryKey(shippingId)，如果是非相应用户的话会造成横向越权
        int resultCount = shippingMapper.deleteByShippingIdAndUserId(shippingId, userId);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("删除地址失败");
        }
        return ServerResponse.createBySuccessMessage("删除地址成功");
    }

    /**
     * 更新收货地址
     *
     * @param userId   用户ID
     * @param shipping 收货地址
     * @return
     */
    @Override
    public ServerResponse<String> updateAddress(Integer userId, Shipping shipping) {

        //将Shipping中的userId设置为当前登陆用户的userId，以防前端传送的是另外一个用户的Shipping对象
        shipping.setUserId(userId);
        int resultCount = shippingMapper.updateByUserId(shipping);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("更新地址失败");
        }
        return ServerResponse.createBySuccessMessage("更新地址成功");
    }


    /**
     * 根据收货地址ID查询相应的地址信息
     *
     * @param userId     用户ID
     * @param shippingId 收货地址ID
     * @return
     */
    @Override
    public ServerResponse<Shipping> selectAddress(Integer userId, Integer shippingId) {

        Shipping shipping = shippingMapper.selectByShippingIdAndUserId(shippingId, userId);
        if (shipping == null) {
            return ServerResponse.createByErrorMessage("无法查询到该地址");
        }
        return ServerResponse.createBySuccess("查询成功", shipping);
    }

    /**
     * 分页显示当前登陆用户的所有收货地址信息
     *
     * @param userId   用户ID
     * @param pageNum  当前页
     * @param pageSize 页面容量
     * @return
     */
    @Override
    public ServerResponse<PageInfo<Shipping>> listAddresses(Integer userId, int pageNum, int pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        List<Shipping> shippingList = shippingMapper.selectByUserId(userId);
        PageInfo<Shipping> pageInfo = new PageInfo<>(shippingList);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
