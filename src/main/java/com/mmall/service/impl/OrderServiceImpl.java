package com.mmall.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.*;
import com.mmall.pojo.*;
import com.mmall.service.IOrderService;
import com.mmall.service.IShippingService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.FTPUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.OrderItemVo;
import com.mmall.vo.OrderProductVo;
import com.mmall.vo.OrderVo;
import com.mmall.vo.ShippingVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by 蒙卓明 on 2018/10/28
 */
@Service("iOrderService")
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private PayInfoMapper payInfoMapper;

    @Autowired
    private IShippingService iShippingService;

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

    private static AlipayTradeService tradeService;

    static {
        /**
         * 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，
         *  如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /**
         * 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
    }

    /**
     * 创建订单
     *
     * @param userId     用户ID
     * @param shippingId 收货地址ID
     * @return
     */
    @Override
    public ServerResponse<OrderVo> createOrder(Integer userId, Integer shippingId) {

        //从购物车中获取已经被勾选的购物栏
        List<Cart> cartList = cartMapper.selectCheckedCartByUserId(userId);

        //获取订单明细
        ServerResponse<List<OrderItem>> serverResponse = getCartOrderItem(userId, cartList);
        if (!serverResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage(serverResponse.getMsg());
        }
        List<OrderItem> orderItemList = serverResponse.getData();
        if (CollectionUtils.isEmpty(orderItemList)) {
            return ServerResponse.createByErrorMessage("购物车为空");
        }
        //计算订单总价
        BigDecimal payment = getOrderTotalPrice(orderItemList);

        //组建订单对象，并插入数据库
        Order order = assembleOrder(userId, shippingId, payment);
        int resultCount = orderMapper.insert(order);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("创建订单失败");
        }
        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderNo(order.getOrderNo());
        }

        //批量插入订单明细
        resultCount = orderItemMapper.batchInsert(orderItemList);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("向数据库批量插入订单明细失败");
        }

        //更新库存
        ServerResponse<String> reduceProductStockResponse = reduceProductStock(orderItemList);
        if (!reduceProductStockResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage(reduceProductStockResponse.getMsg());
        }

        //清空购物车
        ServerResponse<String> cleanCartResponse = cleanCart(cartList);
        if (!cleanCartResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage(cleanCartResponse.getMsg());
        }

        //获取收货地址对象
        ServerResponse<Shipping> shippingServerResponse = iShippingService.selectAddress(userId, shippingId);
        if (!shippingServerResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage(shippingServerResponse.getMsg());
        }
        Shipping shipping = shippingServerResponse.getData();

        OrderVo orderVo = assembleOrderVo(order, orderItemList, shipping);

        return ServerResponse.createBySuccess("创建订单成功", orderVo);
    }

    /**
     * 取消订单
     *
     * @param userId  用户ID
     * @param orderNo 订单号
     * @return
     */
    @Override
    public ServerResponse<String> cancelOrder(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("该用户不存在此订单");
        }
        //只有未付款的订单能取消
        if (order.getStatus() != Const.OrderStatusEnum.NO_PAY.getCode()) {
            return ServerResponse.createByErrorMessage("无法取消订单");
        }
        Order orderForCancel = new Order();
        orderForCancel.setId(order.getId());
        orderForCancel.setStatus(Const.OrderStatusEnum.CANCELED.getCode());
        int resultCount = orderMapper.updateByPrimaryKeySelective(orderForCancel);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("取消订单发生异常");
        }

        return ServerResponse.createBySuccessMessage("取消订单成功");
    }

    /**
     * 获取订单下所有订单项的信息
     *
     * @param userId 用户ID
     * @return
     */
    @Override
    public ServerResponse<OrderProductVo> getOrderCartProductList(Integer userId) {

        List<Cart> cartList = cartMapper.selectCheckedCartByUserId(userId);
        ServerResponse<List<OrderItem>> serverResponse = getCartOrderItem(userId, cartList);
        if (!serverResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage(serverResponse.getMsg());
        }
        List<OrderItem> orderItemList = serverResponse.getData();
        OrderProductVo orderProductVo = assembleOrderProductVo(orderItemList);
        return ServerResponse.createBySuccess(orderProductVo);
    }

    /**
     * 获取订单详情
     *
     * @param userId  用户ID
     * @param orderNo 订单号
     * @return
     */
    @Override
    public ServerResponse<OrderVo> getOrderDetail(Integer userId, Long orderNo) {

        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("该用户不存在此订单");
        }

        List<OrderItem> orderItemList = orderItemMapper.selectByUserIdAndOrderNo(userId, orderNo);
        ServerResponse<Shipping> shippingServerResponse = iShippingService.selectAddress(userId,
                order.getShippingId());
        if (!shippingServerResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage(shippingServerResponse.getMsg());
        }
        Shipping shipping = shippingServerResponse.getData();
        OrderVo orderVo = assembleOrderVo(order, orderItemList, shipping);
        return ServerResponse.createBySuccess(orderVo);
    }

    /**
     * 分页获取当前登陆用户下的所有订单信息
     *
     * @param userId   用户ID
     * @param pageNum  当前页
     * @param pageSize 页面容量
     * @return
     */
    @Override
    public ServerResponse<PageInfo<OrderVo>> getOrderList(Integer userId, int pageNum, int pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectByUserId(userId);
        Map<Order, Shipping> orderShippingMap = Maps.newHashMap();
        for (Order order : orderList) {
            ServerResponse<Shipping> shippingServerResponse = iShippingService.selectAddress(userId,
                    order.getShippingId());
            if (!shippingServerResponse.isSuccess()) {
                return ServerResponse.createByErrorMessage(shippingServerResponse.getMsg());
            }
            Shipping shipping = shippingServerResponse.getData();
            orderShippingMap.put(order, shipping);
        }
        List<OrderVo> orderVoList = assembleOrderVoList(orderShippingMap, userId);
        PageInfo<OrderVo> pageInfo = new PageInfo<>(orderVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    /**
     * 在后台分页获取所有订单信息
     *
     * @param pageNum  当前页
     * @param pageSize 页面容量
     * @return
     */
    @Override
    public ServerResponse<PageInfo<OrderVo>> manageOrderList(int pageNum, int pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.getAllOrders();
        Map<Order, Shipping> orderShippingMap = Maps.newHashMap();
        for (Order order : orderList) {
            ServerResponse<Shipping> shippingServerResponse = iShippingService.selectAddress(
                    order.getUserId(), order.getShippingId());
            if (!shippingServerResponse.isSuccess()) {
                return ServerResponse.createByErrorMessage(shippingServerResponse.getMsg());
            }
            Shipping shipping = shippingServerResponse.getData();
            orderShippingMap.put(order, shipping);
        }
        List<OrderVo> orderVoList = assembleOrderVoList(orderShippingMap, null);
        PageInfo<OrderVo> pageInfo = new PageInfo<>(orderVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    /**
     * 在后台查看订单的详细信息
     *
     * @param orderNo 订单号
     * @return
     */
    @Override
    public ServerResponse<OrderVo> manageOrderDetail(Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("订单不存在");
        }
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(orderNo);
        ServerResponse<Shipping> shippingServerResponse = iShippingService.selectAddress(order.getUserId(),
                order.getShippingId());
        if (!shippingServerResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage(shippingServerResponse.getMsg());
        }
        Shipping shipping = shippingServerResponse.getData();
        OrderVo orderVo = assembleOrderVo(order, orderItemList, shipping);
        return ServerResponse.createBySuccess(orderVo);
    }

    /**
     * 在后台根据订单号搜索订单
     *
     * @param orderNo  订单
     * @param pageNum  当前页
     * @param pageSize 页面容量
     * @return
     */
    @Override
    public ServerResponse<PageInfo<OrderVo>> manageSearchOrder(Long orderNo, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("订单不存在");
        }
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(orderNo);
        ServerResponse<Shipping> shippingServerResponse = iShippingService.selectAddress(order.getUserId(),
                order.getShippingId());
        if (!shippingServerResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage(shippingServerResponse.getMsg());
        }
        Shipping shipping = shippingServerResponse.getData();
        OrderVo orderVo = assembleOrderVo(order, orderItemList, shipping);
        PageInfo<OrderVo> pageInfo = new PageInfo<>(Lists.newArrayList(orderVo));
        return ServerResponse.createBySuccess(pageInfo);
    }

    /**
     * 发货
     *
     * @param orderNo 订单号
     * @return
     */
    @Override
    public ServerResponse<String> manageSendOrderGoods(Long orderNo) {

        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("订单不存在");
        }
        if (order.getStatus() != Const.OrderStatusEnum.PAID.getCode()) {
            return ServerResponse.createByErrorMessage("订单未付款");
        }
        //更新订单状态和发货时间
        order.setStatus(Const.OrderStatusEnum.SHIPPED.getCode());
        order.setSendTime(new Date());
        int resultCount = orderMapper.updateByPrimaryKeySelective(order);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("发货失败");
        }
        return ServerResponse.createBySuccessMessage("发货成功");
    }

    /**
     * 支付订单
     *
     * @param userId  用户ID
     * @param orderNo 订单号
     * @param path    upload目录的绝对路径
     * @return
     */
    @Override
    public ServerResponse<Map<String, String>> payOrder(Integer userId, Long orderNo, String path) {

        Map<String, String> resultMap = Maps.newHashMap();

        //校验订单是否存在
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage(orderNo + "号订单不存在");
        }
        resultMap.put("orderNo", String.valueOf(order.getOrderNo()));

        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = String.valueOf(order.getOrderNo());

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店消费”
        String subject = new StringBuilder().append("happymmall扫码支付，订单号：").append(outTradeNo).toString();

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0.0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品3件共20.00元"
        String body = new StringBuilder().append("订单").append(outTradeNo).append("购买商品共").append(totalAmount)
                .append("元。").toString();

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        String providerId = "2088100200300400500";
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId(providerId);

        // 支付超时，线下扫码交易定义为5分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = Lists.newArrayList();
        List<OrderItem> orderItemList = orderItemMapper.selectByUserIdAndOrderNo(userId, orderNo);
        for (OrderItem orderItem : orderItemList) {
            // 创建一个商品信息
            // 参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
            GoodsDetail goodsDetail = GoodsDetail.newInstance(orderItem.getProductId().toString(),
                    orderItem.getProductName(), BigDecimalUtil.multiply(
                            orderItem.getCurrentUnitPrice().doubleValue(), 100).longValue(),
                    orderItem.getQuantity());
            goodsDetailList.add(goodsDetail);
        }

        // 创建条码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                //支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"))
                .setGoodsDetailList(goodsDetailList);


        // 调用tradePay方法获取当面付应答
        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                LOGGER.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                File directory = new File(path);
                if (!directory.exists()) {
                    directory.setWritable(true);
                    directory.mkdirs();
                }
                // 需要修改为运行机器上的路径
                String qrPath = String.format(path + File.separator + "qr-%s.png", response.getOutTradeNo());
                String qrFileName = String.format("qr-%s.png", response.getOutTradeNo());
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);

                File targetFile = new File(path, qrFileName);
                try {
                    FTPUtil.uploadFile(Lists.newArrayList(targetFile));
                } catch (IOException e) {
                    LOGGER.error("上传二维码异常", e);
                }
                LOGGER.info("qrPath:" + qrPath);

                String qrUrl = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFile.getName();
                resultMap.put("qrUrl", qrUrl);
                return ServerResponse.createBySuccess(resultMap);
            case FAILED:
                LOGGER.error("支付宝预下单失败!!!");
                return ServerResponse.createByErrorMessage("支付宝预下单失败!!!");
            case UNKNOWN:
                LOGGER.error("系统异常，预下单状态未知!!!");
                return ServerResponse.createByErrorMessage("系统异常，预下单状态未知!!!");
            default:
                LOGGER.error("不支持的交易状态，交易返回异常!!!");
                return ServerResponse.createByErrorMessage("不支持的交易状态，交易返回异常!!!");
        }
    }

    /**
     * 处理支付宝的回调信息
     *
     * @param paramterMap 封装支付宝回调信息的Map集合
     * @return
     */
    @Override
    public ServerResponse<String> alipayCallback(Map<String, String> paramterMap) {

        Long orderNo = Long.parseLong(paramterMap.get("out_trade_no"));
        String tradeNo = paramterMap.get("trade_no");
        String tradeStatus = paramterMap.get("trade_status");

        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage(orderNo + "号订单不存在");
        }
        if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()) {
            //订单已经走到已付款
            return ServerResponse.createBySuccessMessage("支付宝重复调用");
        }
        if (Const.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)) {
            //交易成功，修改订单状态和付款时间
            order.setStatus(Const.OrderStatusEnum.PAID.getCode());
            order.setPaymentTime(DateTimeUtil.strToDate(paramterMap.get("gmt_payment")));
            int resultCount = orderMapper.updateByPrimaryKeySelective(order);
            if (resultCount == 0) {
                return ServerResponse.createByErrorMessage("更新订单状态失败");
            }

        }
        PayInfo payInfo = assemblePayInfo(order, tradeNo, tradeStatus);
        int resultCount = payInfoMapper.insert(payInfo);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("付款信息插入失败");
        }
        return ServerResponse.createBySuccessMessage("回调处理成功");
    }

    /**
     * 查询订单是否已付款
     *
     * @param userId  用户ID
     * @param orderNo 订单号
     * @return
     */
    @Override
    public ServerResponse<String> queryOrderPayStatus(Integer userId, Long orderNo) {

        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("订单不存在");
        }

        if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()) {
            //订单已付款
            return ServerResponse.createBySuccessMessage("订单已付款");
        }
        return ServerResponse.createByErrorMessage("订单未付款");
    }

    /**
     * 组装PayInfo对象
     *
     * @param order       订单
     * @param tradeNo     交易号
     * @param tradeStatus 交易状态
     * @return
     */
    private PayInfo assemblePayInfo(Order order, String tradeNo, String tradeStatus) {

        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPayPlatform(Const.PayPlatformEnum.ALIPAY.getCode());
        payInfo.setPlatformNumber(tradeNo);
        payInfo.setPlatformStatus(tradeStatus);
        return payInfo;
    }

    /**
     * 简单打印应答
     *
     * @param response 应答响应
     */
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            LOGGER.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                LOGGER.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            LOGGER.info("body:" + response.getBody());
        }
    }


    /**
     * 组装OrderVo的List集合
     *
     * @param orderShippingMap Order和Shipping对象的Map集合
     * @param userId           用户ID
     * @return
     */
    private List<OrderVo> assembleOrderVoList(Map<Order, Shipping> orderShippingMap, Integer userId) {

        List<OrderVo> orderVoList = Lists.newArrayList();
        for (Map.Entry<Order, Shipping> entry : orderShippingMap.entrySet()) {
            List<OrderItem> orderItemList;
            if (userId == null) {
                orderItemList = orderItemMapper.selectByOrderNo(entry.getKey().getOrderNo());
            } else {
                orderItemList = orderItemMapper.selectByUserIdAndOrderNo(userId,
                        entry.getKey().getOrderNo());
            }
            OrderVo orderVo = assembleOrderVo(entry.getKey(), orderItemList, entry.getValue());
            orderVoList.add(orderVo);
        }
        return orderVoList;
    }

    /**
     * 组装OrderProductVo对象
     *
     * @param orderItemList 订单项列表
     * @return
     */
    private OrderProductVo assembleOrderProductVo(List<OrderItem> orderItemList) {
        OrderProductVo orderProductVo = new OrderProductVo();
        List<OrderItemVo> orderItemVoList = Lists.newArrayList();
        BigDecimal payment = new BigDecimal("0");
        for (OrderItem orderItem : orderItemList) {
            payment = BigDecimalUtil.add(payment.doubleValue(), orderItem.getTotalPrice().doubleValue());
            orderItemVoList.add(assembleOrderItemVo(orderItem));
        }
        orderProductVo.setProductTotalPrice(payment);
        orderProductVo.setOrderItemVoList(orderItemVoList);
        orderProductVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix",
                "http://img.bigdata.com/"));
        return orderProductVo;
    }

    /**
     * 创建订单时清空购物车
     *
     * @param cartList 购物栏集合
     * @return
     */
    private ServerResponse<String> cleanCart(List<Cart> cartList) {
        for (Cart cart : cartList) {
            int resultCount = cartMapper.deleteByPrimaryKey(cart.getId());
            if (resultCount == 0) {
                return ServerResponse.createByErrorMessage("清空购物车失败，原因是删除ID为" + cart.getId()
                        + "的项失败");
            }
        }
        return ServerResponse.createBySuccessMessage("清空购物车成功");
    }

    /**
     * 创建订单时更新产品库存
     *
     * @param orderItemList 订单项列表
     * @return
     */
    private ServerResponse<String> reduceProductStock(List<OrderItem> orderItemList) {
        for (OrderItem orderItem : orderItemList) {
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            if (product == null) {
                return ServerResponse.createByErrorMessage("找不到产品ID为" + orderItem.getProductId()
                        + "的产品");
            }
            product.setStock(product.getStock() - orderItem.getQuantity());
            int resultCount = productMapper.updateByPrimaryKeySelective(product);
            if (resultCount == 0) {
                return ServerResponse.createByErrorMessage("更新产品ID为" + orderItem.getProductId()
                        + "的产品的库存失败");
            }
        }
        return ServerResponse.createBySuccessMessage("更新产品库存成功");
    }

    /**
     * 组装Order对象
     *
     * @param userId     用户ID
     * @param shippingId 收货地址ID
     * @param payment    订单总价
     * @return
     */
    private Order assembleOrder(Integer userId, Integer shippingId, BigDecimal payment) {

        Order order = new Order();
        long orderNo = generateOrderNo();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setShippingId(shippingId);
        order.setPayment(payment);
        order.setPaymentType(Const.PaymentTypeEnum.ONLINE_PAY.getCode());
        order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
        //运费模块，后期接入
        order.setPostage(0);

        //创建阶段，不设置发货、付款等相关信息
        return order;
    }

    /**
     * 订单号的生成，仍存在并发问题
     *
     * @return
     */
    private long generateOrderNo() {
        long currentTime = System.currentTimeMillis();
        return currentTime + new Random().nextInt(100);
    }


    /**
     * 计算订单的总价
     *
     * @param orderItemList 订单项集合
     * @return
     */
    private BigDecimal getOrderTotalPrice(List<OrderItem> orderItemList) {

        BigDecimal payment = new BigDecimal("0");
        for (OrderItem orderItem : orderItemList) {
            payment = BigDecimalUtil.add(payment.doubleValue(), orderItem.getTotalPrice().doubleValue());
        }
        return payment;
    }

    /**
     * 通过购物车创建子订单明细
     *
     * @param userId   用户ID
     * @param cartList 购物栏的集合
     * @return
     */
    private ServerResponse<List<OrderItem>> getCartOrderItem(Integer userId, List<Cart> cartList) {

        List<OrderItem> orderItemList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(cartList)) {
            return ServerResponse.createByErrorMessage("购物车为空");
        }
        //校验购物车的数据，包括产品的状态和数量
        for (Cart cart : cartList) {
            Product product = productMapper.selectByPrimaryKey(cart.getProductId());
            if (product == null) {
                return ServerResponse.createByErrorMessage("找不到产品ID为" + cart.getProductId()
                        + "对应的产品");
            }
            //检验产品是否在售
            if (Const.ProductStatusEnum.ON_SALE.getCode() != product.getStatus()) {
                return ServerResponse.createByErrorMessage("产品" + product.getName() + "不是在售状态");
            }
            //检验库存
            if (cart.getQuantity() > product.getStock()) {
                return ServerResponse.createByErrorMessage("产品" + product.getName() + "库存不足");
            }

            OrderItem orderItem = assembleOrderItem(userId, cart, product);

            orderItemList.add(orderItem);
        }
        return ServerResponse.createBySuccess(orderItemList);
    }

    /**
     * 组装订单项对象
     *
     * @param userId  用户ID
     * @param cart    购物栏
     * @param product 产品
     * @return
     */
    private OrderItem assembleOrderItem(Integer userId, Cart cart, Product product) {

        OrderItem orderItem = new OrderItem();
        orderItem.setUserId(userId);
        orderItem.setProductId(cart.getProductId());
        orderItem.setProductName(product.getName());
        orderItem.setProductImage(product.getMainImage());
        orderItem.setCurrentUnitPrice(product.getPrice());
        orderItem.setQuantity(cart.getQuantity());
        orderItem.setTotalPrice(BigDecimalUtil.multiply(product.getPrice().doubleValue(),
                cart.getQuantity()));
        return orderItem;
    }

    /**
     * 组装ShippingCVo对象
     *
     * @param shipping 收货地址对象
     * @return
     */
    private ShippingVo assembleShippingVo(Shipping shipping) {
        ShippingVo shippingVo = new ShippingVo();
        shippingVo.setReceiverName(shipping.getReceiverName());
        shippingVo.setReceiverPhone(shipping.getReceiverPhone());
        shippingVo.setReceiverMobile(shipping.getReceiverMobile());
        shippingVo.setReceiverProvince(shipping.getReceiverProvince());
        shippingVo.setReceiverCity(shipping.getReceiverCity());
        shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingVo.setReceiverAddress(shipping.getReceiverAddress());
        shippingVo.setReceiverZip(shipping.getReceiverZip());
        return shippingVo;
    }

    /**
     * 组装OrderItemVo对象
     *
     * @param orderItem 订单项对象
     * @return
     */
    private OrderItemVo assembleOrderItemVo(OrderItem orderItem) {
        OrderItemVo orderItemVo = new OrderItemVo();
        orderItemVo.setOrderNo(orderItem.getOrderNo());
        orderItemVo.setProductId(orderItem.getProductId());
        orderItemVo.setProductName(orderItem.getProductName());
        orderItemVo.setProductImage(orderItem.getProductImage());
        orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
        orderItemVo.setQuantity(orderItem.getQuantity());
        orderItemVo.setTotalPrice(orderItem.getTotalPrice());
        orderItemVo.setCreateTime(DateTimeUtil.dateToStr(orderItem.getCreateTime()));
        return orderItemVo;
    }

    /**
     * 组装OrderVo对象
     *
     * @param order         订单
     * @param orderItemList 订单项列表
     * @param shipping      收货地址
     * @return
     */
    private OrderVo assembleOrderVo(Order order, List<OrderItem> orderItemList, Shipping shipping) {
        OrderVo orderVo = new OrderVo();
        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPayment(order.getPayment());
        orderVo.setPaymentType(order.getPaymentType());
        orderVo.setPaymentTypeDesc(Const.PaymentTypeEnum.codeOf(order.getPaymentType()).getValue());
        orderVo.setPostage(order.getPostage());
        orderVo.setStatus(order.getStatus());
        orderVo.setStatusDesc(Const.OrderStatusEnum.codeOf(order.getStatus()).getValue());
        orderVo.setCreateTime(DateTimeUtil.dateToStr(order.getCreateTime()));
        orderVo.setPaymentTime(DateTimeUtil.dateToStr(order.getPaymentTime()));
        orderVo.setSendTime(DateTimeUtil.dateToStr(order.getSendTime()));
        orderVo.setEndTime(DateTimeUtil.dateToStr(order.getEndTime()));
        orderVo.setCloseTime(DateTimeUtil.dateToStr(order.getCloseTime()));

        List<OrderItemVo> orderItemVoList = Lists.newArrayList();
        for (OrderItem orderItem : orderItemList) {
            OrderItemVo orderItemVo = assembleOrderItemVo(orderItem);
            orderItemVoList.add(orderItemVo);
        }
        orderVo.setOrderItemVoList(orderItemVoList);
        orderVo.setShippingId(shipping.getId());
        orderVo.setReceiverName(shipping.getReceiverName());
        ShippingVo shippingVo = assembleShippingVo(shipping);
        orderVo.setShippingVo(shippingVo);
        orderVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix",
                "http://img.bigdata.com/"));

        return orderVo;
    }
}
