package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Created by 蒙卓明 on 2018/10/23
 */
@RequestMapping("/manage/product")
@Controller
public class ProductManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IProductService iProductService;

    @Autowired
    private IFileService iFileService;

    /**
     * 保存产品，实现新增产品或更新产品
     *
     * @param product 产品
     * @param session session
     * @return
     */
    @RequestMapping(value = "save_product.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> saveProduct(Product product, HttpSession session) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请先登录");
        }

        //判断是否是管理员
        ServerResponse<String> adminResponse = iUserService.checkAdminRole(user);
        if (!adminResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("当前用户无权限操作，需要管理员权限");
        }

        return iProductService.insertOrUpdateProduct(product);
    }

    /**
     * 产品上架或下架
     *
     * @param id      产品id
     * @param status  销售状态
     * @param session session
     * @return
     */
    @RequestMapping(value = "set_sale_status.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> setSaleStatus(Integer id, Integer status, HttpSession session) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请先登录");
        }

        //判断是否是管理员
        ServerResponse<String> adminResponse = iUserService.checkAdminRole(user);
        if (!adminResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("当前用户无权限操作，需要管理员权限");
        }

        return iProductService.setSaleStatus(id, status);
    }

    /**
     * 获取产品详情
     *
     * @param id      产品id
     * @param session session
     * @return
     */
    @RequestMapping(value = "get_product_detail.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<ProductDetailVo> getProductDetail(Integer id, HttpSession session) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请先登录");
        }

        //判断是否是管理员
        ServerResponse<String> adminResponse = iUserService.checkAdminRole(user);
        if (!adminResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("当前用户无权限操作，需要管理员权限");
        }

        return iProductService.manageProductDetail(id);
    }

    /**
     * 分页获取所有产品信息
     *
     * @param pageNum  当前页数
     * @param pageSize 页面容量
     * @param session  session
     * @return
     */
    @RequestMapping(value = "get_product_list.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<PageInfo<ProductListVo>> getProductList(
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请先登录");
        }

        //判断是否是管理员
        ServerResponse<String> adminResponse = iUserService.checkAdminRole(user);
        if (!adminResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("当前用户无权限操作，需要管理员权限");
        }
        return iProductService.getProductList(pageNum, pageSize);
    }

    /**
     * 根据产品名称和产品ID进行搜索，分页显示搜索结果
     *
     * @param productName 产品名称
     * @param productId   产品ID
     * @param pageNum     当前页数
     * @param pageSize    页面容量
     * @param session     session
     * @return
     */
    @RequestMapping(value = "search_product.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<PageInfo<ProductListVo>> searchProduct(
            String productName, Integer productId,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            HttpSession session) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请先登录");
        }

        //判断是否是管理员
        ServerResponse<String> adminResponse = iUserService.checkAdminRole(user);
        if (!adminResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("当前用户无权限操作，需要管理员权限");
        }

        return iProductService.searchProduct(productName, productId, pageNum, pageSize);
    }

    /**
     * 上传文件
     *
     * @param multipartFile 上传文件名
     * @param request       request
     * @return
     */
    @RequestMapping(value = "upload.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String, String>> upload(
            @RequestParam(value = "multipartFile", required = false) MultipartFile multipartFile,
            HttpServletRequest request) {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请先登录");
        }

        //判断是否是管理员
        ServerResponse<String> adminResponse = iUserService.checkAdminRole(user);
        if (!adminResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("当前用户无权限操作，需要管理员权限");
        }

        String path = session.getServletContext().getRealPath("upload");
        String targetFileName = iFileService.uploadFileName(multipartFile, path);
        String url = PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.bigdata.com/")
                + targetFileName;

        Map<String, String> fileMap = Maps.newHashMap();
        fileMap.put("uri", targetFileName);
        fileMap.put("url", url);

        return ServerResponse.createBySuccess(fileMap);
    }

    /**
     * 富文本中的文件上传
     *
     * @param multipartFile 上传文件名
     * @param request request
     * @param response response
     * @return 使用的是simditor插件，需要返回特殊的JSON数据
     */
    @RequestMapping(value = "upload_rich_text_img.do", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> uploadRichTextImg(
            @RequestParam(value = "multipartFile", required = false) MultipartFile multipartFile,
            HttpServletRequest request, HttpServletResponse response) {

        Map<String, Object> resultMap = Maps.newHashMap();
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            resultMap.put("success", false);
            resultMap.put("msg", "请先登录");
            return resultMap;
        }

        ServerResponse<String> adminResponse = iUserService.checkAdminRole(user);
        if (!adminResponse.isSuccess()) {
            resultMap.put("success", false);
            resultMap.put("msg", "当前用户无权限操作，需要管理员权限");
            return resultMap;
        }

        String path = session.getServletContext().getRealPath("upload");
        String targetFileName = iFileService.uploadFileName(multipartFile, path);
        if (StringUtils.isBlank(targetFileName)) {
            resultMap.put("success", false);
            resultMap.put("msg", "上传失败");
            return resultMap;
        }
        String url = PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.bigdata.com/")
                + targetFileName;
        resultMap.put("success", true);
        resultMap.put("msg", "上传成功");
        resultMap.put("file_path", url);
        //修改response头，以满足simditor插件的响应需要
        response.addHeader("Access-Control-Allow-Headers", "X-File-Name");
        return resultMap;
    }

}
