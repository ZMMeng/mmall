package com.mmall.task;

import com.mmall.service.IOrderService;
import com.mmall.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务
 * Created by 蒙卓明 on 2018/11/13
 */
@Component
@Slf4j
public class CloseOrderTask {

    @Autowired
    private IOrderService iOrderService;

    /**
     * 每隔一分钟进行关闭两小时前未付款的订单操作
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void closeOrderTaskV1() {
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour",
                "2"));
        iOrderService.closeOrder(hour);
    }
}
