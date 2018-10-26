package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;

/**
 * Created by 蒙卓明 on 2018/10/23
 */
public interface IProductService {

    ServerResponse<String> insertOrUpdateProduct(Product product);

    ServerResponse<String> setSaleStatus(Integer productId, Integer status);

    ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);

    ServerResponse<PageInfo<ProductListVo>> getProductList(int pageNum, int pageSize);

    ServerResponse<PageInfo<ProductListVo>> searchProduct(String productName, Integer productId,
                                                          int pageNum, int pageSize);

    ServerResponse<ProductDetailVo> getProductDetail(Integer productId);

    ServerResponse<PageInfo<ProductListVo>> searchProductByKeywordCategory(
            String keyword, Integer categoryId, String orderBy, int pageNum, int pageSize);
}
