package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by 蒙卓明 on 2018/10/23
 */
@Service("iProductService")
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ICategoryService iCategoryService;

    /**
     * 根据产品ID是否为null来新增或更新产品
     *
     * @param product 产品
     * @return
     */
    @Override
    public ServerResponse<String> insertOrUpdateProduct(Product product) {

        if (product == null) {
            ServerResponse.createByErrorMessage("新增或更新产品参数错误");
        }

        if (product.getId() == null) {
            //新增产品
            int resultCount = productMapper.insert(product);
            if (resultCount == 0) {
                return ServerResponse.createByErrorMessage("新增产品失败");
            }
            return ServerResponse.createBySuccessMessage("新增产品成功");
        }

        //更新产品
        int resultCount = productMapper.updateByPrimaryKeySelective(product);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("更新产品失败");
        }
        return ServerResponse.createBySuccessMessage("更新产品成功");
    }

    /**
     * 产品上架或下架
     *
     * @param productId 产品ID
     * @param status    是否在售
     * @return
     */
    @Override
    public ServerResponse<String> setSaleStatus(Integer productId, Integer status) {

        if (productId == null || status == null) {
            ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        //组装新的Product对象
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);

        //选择性更新
        int resultCount = productMapper.updateByPrimaryKeySelective(product);
        if (resultCount == 0) {
            ServerResponse.createByErrorMessage("修改产品销售状态失败");
        }

        return ServerResponse.createBySuccessMessage("修改产品销售状态成功");
    }

    /**
     * 获取产品详情
     *
     * @param productId 产品ID
     * @return
     */
    @Override
    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId) {

        if (productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createByErrorMessage("产品已下架或产品不存在");
        }

        ProductDetailVo productDetailVo = assembleProductDetailVo(product);

        return ServerResponse.createBySuccess(productDetailVo);
    }

    /**
     * 分页获取所有产品信息
     *
     * @param pageNum  当前页
     * @param pageSize 页面容量
     * @return
     */
    @Override
    public ServerResponse<PageInfo<ProductListVo>> getProductList(int pageNum, int pageSize) {
        //设置首页
        PageHelper.startPage(pageNum, pageSize);

        List<Product> products = productMapper.getAllProduct();
        List<ProductListVo> productListVos = Lists.newArrayList();
        for (Product product : products) {
            ProductListVo productListVo = assembleProductListVo(product);
            productListVos.add(productListVo);
        }
        //使用PageInfo包装查询结果，只需要将pageInfo交给页面就可以
        PageInfo<ProductListVo> productPageInfo = new PageInfo<>(productListVos);
        //productPageInfo.setList(productListVos);
        return ServerResponse.createBySuccess(productPageInfo);
    }

    /**
     * 根据产品名或ID搜索产品
     *
     * @param productName 产品名称
     * @param productId   产品ID
     * @param pageNum     当前页
     * @param pageSize    页面容量
     * @return
     */
    @Override
    public ServerResponse<PageInfo<ProductListVo>> searchProduct(String productName, Integer productId,
                                                                 int pageNum, int pageSize) {
        //设置首页
        PageHelper.startPage(pageNum, pageSize);

        if (StringUtils.isNotBlank(productName)) {
            //使用like %XXX% 语法
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product> products = productMapper.selectProductsByNameAndProductId(productName, productId);
        List<ProductListVo> productListVos = Lists.newArrayList();
        for (Product product : products) {
            ProductListVo productListVo = assembleProductListVo(product);
            productListVos.add(productListVo);
        }
        PageInfo<ProductListVo> productPageInfo = new PageInfo<>(productListVos);
        //productPageInfo.setList(productListVos);
        return ServerResponse.createBySuccess(productPageInfo);
    }

    /**
     * 前台页面根据产品ID获取产品详情
     *
     * @param productId 产品ID
     * @return
     */
    @Override
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId) {

        if (productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            //查不到相关产品
            return ServerResponse.createByErrorMessage("产品已下架或产品不存在");
        }
        if (product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()) {
            //产品非在售状态
            return ServerResponse.createByErrorMessage("产品已下架或已删除");
        }

        ProductDetailVo productDetailVo = assembleProductDetailVo(product);

        return ServerResponse.createBySuccess(productDetailVo);
    }

    /**
     * 在前台页面根据关键词和分类ID进行产品搜索
     *
     * @param keyword    关键词
     * @param categoryId 分类ID
     * @param orderBy    排序规则
     * @param pageNum    当前页
     * @param pageSize   页容量
     * @return
     */
    @Override
    public ServerResponse<PageInfo<ProductListVo>> searchProductByKeywordCategory(
            String keyword, Integer categoryId, String orderBy, int pageNum, int pageSize) {

        if (categoryId == null && StringUtils.isBlank(keyword)) {
            //关键词和分类ID不能同时为空
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        List<Integer> categoryIdList = Lists.newArrayList();
        List<ProductListVo> productListVoList = Lists.newArrayList();
        if (categoryId != null) {
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if (category == null && StringUtils.isBlank(keyword)) {
                //关键字为空，且未查找到相应的分类
                PageHelper.startPage(pageNum, pageSize);
                PageInfo<ProductListVo> pageInfo = new PageInfo<>();
                pageInfo.setList(productListVoList);
                return ServerResponse.createBySuccess(pageInfo);
            }
            //递归获取该分类ID对应的品类及其所有的后代品类，并取出分类ID组成一个List
            List<Category> categoryList = iCategoryService.getChildRecursiveCategories(categoryId).getData();
            for (Category categoryItem : categoryList) {
                categoryIdList.add(categoryItem.getId());
            }
        }

        if (StringUtils.isNotBlank(keyword)) {
            //关键词不为空
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
        }

        PageHelper.startPage(pageNum, pageSize);

        if (StringUtils.isNotBlank(orderBy)) {
            if (Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)) {
                String[] orderByArr = orderBy.split("_");
                //升降序设置
                PageHelper.orderBy(orderByArr[0] + " " + orderByArr[1]);
            }
        }

        //使用三元运算符，根据关键字和分裂ID列表是否为空来传入不同的参数，到数据库中查询相应的产品
        List<Product> productList = productMapper.selectProductsByNameAndCategoryIds(
                StringUtils.isBlank(keyword) ? null : keyword,
                categoryIdList.size() == 0 ? null : categoryIdList);

        for (Product product : productList) {
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }

        PageInfo<ProductListVo> pageInfo = new PageInfo<>(productListVoList);
        //pageInfo.setList(productListVoList);

        return ServerResponse.createBySuccess(pageInfo);
    }

    /**
     * 将Product组装成ProductListVo对象
     *
     * @param product 产品
     * @return
     */
    private ProductListVo assembleProductListVo(Product product) {

        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setName(product.getName());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setMainImage(product.getMainImage());
        productListVo.setStatus(product.getStatus());
        productListVo.setPrice(product.getPrice());

        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix",
                "http://img.bigdata.com/"));
        return productListVo;
    }

    /**
     * 将Product对象组装成ProductDetailVo对象
     *
     * @param product 产品
     * @return
     */
    private ProductDetailVo assembleProductDetailVo(Product product) {

        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImage(product.getSubImage());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());

        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix",
                "http://img.bigdata.com/"));
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category == null) {
            //默认根节点
            productDetailVo.setParentId(0);
        } else {
            productDetailVo.setParentId(category.getParentId());
        }
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));

        return productDetailVo;
    }
}
