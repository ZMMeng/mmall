package com.mmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by 蒙卓明 on 2018/10/27
 */
@Service("iCartService")
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    /**
     * 列出购物车的所有信息
     *
     * @param userId 用户ID
     * @return
     */
    @Override
    public ServerResponse<CartVo> listCart(Integer userId) {
        CartVo cartVo = getCartVo(userId);
        if (cartVo == null) {
            return ServerResponse.createByErrorMessage("数据库发生异常");
        }
        return ServerResponse.createBySuccess(cartVo);
    }

    /**
     * 添加购物栏
     *
     * @param userId    用户ID
     * @param productId 产品ID
     * @param quantity  购买数量
     * @return
     */
    @Override
    public ServerResponse<CartVo> addProduct(Integer userId, Integer productId, Integer quantity) {

        if (productId == null || quantity == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Cart cart = cartMapper.selectByUserIdAndProductId(userId, productId);
        if (cart == null) {
            //没有相应的购物栏
            Cart cartItem = new Cart();
            cartItem.setUserId(userId);
            cartItem.setProductId(productId);
            cartItem.setQuantity(quantity);
            cartItem.setChecked(Const.CartMarker.CHECKED);
            int resultCount = cartMapper.insert(cartItem);
            if (resultCount == 0) {
                return ServerResponse.createByErrorMessage("新增购物栏失败");
            }
        } else {
            //已经有相应的购物栏
            quantity = cart.getQuantity() + quantity;
            cart.setQuantity(quantity);
            int resultCount = cartMapper.updateByPrimaryKeySelective(cart);
            if (resultCount == 0) {
                return ServerResponse.createByErrorMessage("更新购物栏产品数量失败");
            }
        }

        return listCart(userId);
    }

    /**
     * 更新购物车中的产品数量
     *
     * @param userId    用户ID
     * @param productId 产品ID
     * @param quantity  购买数量
     * @return
     */
    @Override
    public ServerResponse<CartVo> updateCart(Integer userId, Integer productId, Integer quantity) {

        if (productId == null || quantity == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Cart cart = cartMapper.selectByUserIdAndProductId(userId, productId);
        if (cart != null) {
            cart.setQuantity(quantity);
        }

        int resultCount = cartMapper.updateByPrimaryKeySelective(cart);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("更新购物栏产品数量失败");
        }
        return listCart(userId);
    }

    /**
     * 删除用户购物车中的产品
     *
     * @param userId     用户ID
     * @param productIds 产品ID列表，以","分隔
     * @return
     */
    @Override
    public ServerResponse<CartVo> deleteProduct(Integer userId, String productIds) {

        List<String> productIdList = Splitter.on(",").splitToList(productIds);
        if (CollectionUtils.isEmpty(productIdList)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        int resultCount = cartMapper.deleteByUserIdAndProductIdList(userId, productIdList);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("删除购物车中产品失败");
        }

        return listCart(userId);
    }

    /**
     * 对购物车中的产品进行全选或全反选
     *
     * @param userId 用户ID
     * @param check  全选或全反选
     * @return
     */
    @Override
    public ServerResponse<CartVo> selectOrUnSelect(Integer userId, Integer productId, Integer check) {
        cartMapper.checkedOrUncheckedProduct(userId, productId, check);
        return listCart(userId);
    }

    /**
     * 获取用户的购物车下，所有购买产品的数量总和
     *
     * @param userId 用户ID
     * @return
     */
    @Override
    public ServerResponse<Integer> getCartProductCount(Integer userId) {

        if (userId == null) {
            return ServerResponse.createBySuccess(0);
        }
        return ServerResponse.createBySuccess(cartMapper.selectCartProductCount(userId));
    }

    /**
     * 组装CartVo对象，购物车模块核心方法，感觉还是有点问题
     *
     * @param userId 用户ID
     * @return
     */
    private CartVo getCartVo(Integer userId) {
        CartVo cartVo = new CartVo();
        List<Cart> cartList = cartMapper.selectByUserId(userId);
        List<CartProductVo> cartProductVoList = Lists.newArrayList();

        //购物车的总价，一个购物车包括多个购物栏
        //使用String构造器
        BigDecimal cartTotalPrice = new BigDecimal("0");

        if (CollectionUtils.isNotEmpty(cartList)) {
            for (Cart cartItem : cartList) {
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(cartItem.getUserId());
                cartProductVo.setProductId(cartItem.getProductId());
                cartProductVo.setQuantity(cartItem.getQuantity());

                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
                if (product != null) {
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());
                    //比较产品库存和购买数量，并标记购买数量是否超过产品库存
                    int buyLimitCount;
                    if (product.getStock() >= cartItem.getQuantity()) {
                        //库存充足
                        buyLimitCount = cartItem.getQuantity();
                        cartProductVo.setLimitQuantity(Const.CartMarker.LIMIT_NUM_SUCCESS);
                    } else {
                        //库存不足
                        buyLimitCount = product.getStock();
                        cartProductVo.setLimitQuantity(Const.CartMarker.LIMIT_NUM_FAIL);
                        //在数据库中，对将购买数量更新为产品库存
                        Cart cartForQuantityUpdate = new Cart();
                        cartForQuantityUpdate.setId(cartItem.getId());
                        cartForQuantityUpdate.setQuantity(buyLimitCount);
                        int resultCount = cartMapper.updateByPrimaryKeySelective(cartForQuantityUpdate);
                        if (resultCount == 0) {
                            // 更新失败，未考虑，目前只是简单地返回null值
                            return null;
                        }
                    }
                    cartProductVo.setQuantity(buyLimitCount);
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.multiply(product.getPrice().doubleValue(),
                            cartItem.getQuantity()));
                    cartProductVo.setProductCheck(cartItem.getChecked());

                    if (cartItem.getChecked() == Const.CartMarker.CHECKED) {
                        //该购物栏的产品已经勾上
                        cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(),
                                cartProductVo.getProductTotalPrice().doubleValue());
                    }
                    cartProductVoList.add(cartProductVo);
                }

            }
        }

        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix",
                "http://img.bigdata.com/"));
        cartVo.setAllCheck(getAllCheckedStatus(userId));

        return cartVo;
    }

    /**
     * 判断用户的购物车下的产品是否都是勾选状态
     *
     * @param userId 用户ID
     * @return
     */
    private boolean getAllCheckedStatus(Integer userId) {
        return userId != null && cartMapper.selectCartProductCheckStatusByUserId(userId) == 0;
    }
}
