package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.service.IProductService;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by 蒙卓明 on 2018/10/25
 */
@RequestMapping("/product")
@Controller
public class ProductController {

    @Autowired
    private IProductService iProductService;

    /**
     * 获取商品详情
     *
     * @param productId 产品ID
     * @return
     */
    @RequestMapping(value = "get_product_detail.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId) {
        return iProductService.getProductDetail(productId);
    }

    /**
     * 根据关键字和分类ID进行产品搜索
     *
     * @param keyword    关键字
     * @param categoryId 产品ID
     * @param orderBy    排序方式
     * @param pageNum    当前页
     * @param pageSize   页面容量
     * @return
     */
    @RequestMapping(value = "search_product.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<PageInfo<ProductListVo>> searchProduct(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "orderBy", defaultValue = "") String orderBy,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return iProductService.searchProductByKeywordCategory(keyword, categoryId, orderBy, pageNum, pageSize);
    }


}
