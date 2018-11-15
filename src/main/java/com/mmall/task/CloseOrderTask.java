package com.mmall.task;

import com.mmall.common.Const;
import com.mmall.common.RedissonManager;
import com.mmall.service.IOrderService;
import com.mmall.util.PropertiesUtil;
import com.mmall.util.RedisSharededPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

/**
 * 定时任务
 * Created by 蒙卓明 on 2018/11/13
 */
@Component
@Slf4j
public class CloseOrderTask {

    @Autowired
    private IOrderService iOrderService;

    @Autowired
    private RedissonManager redissonManager;

    /**
     * 每隔一分钟进行关闭两小时前未付款的订单操作
     * 只适用于单服务器
     */
    //@Scheduled(cron = "0 */1 * * * ?")
    public void closeOrderTaskV1() {
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour",
                "2"));
        iOrderService.closeOrder(hour);
    }

    /**
     * 每隔一分钟进行关闭两小时前未付款的订单操作
     * 用于集群环境，但是存在死锁缺陷，即可能会有获取锁后，未设置锁的有效期，直接关闭服务器的情况，从而造成死锁
     */
    //@Scheduled(cron = "0 */1 * * * ?")
    public void closeOrderTaskV2() {
        log.info("关闭订单定时任务启动");
        long lockTimeOut = Long.parseLong(PropertiesUtil.getProperty("lock.timeout",
                "5000"));
        Long setnxResult = RedisSharededPoolUtil.setnx(Const.RedisDistributeLock.CLOSE_ORDER_TASK_LOCK,
                String.valueOf(System.currentTimeMillis() + lockTimeOut));

        if (setnxResult != null && setnxResult.intValue() == 1) {
            closeOrder(Const.RedisDistributeLock.CLOSE_ORDER_TASK_LOCK);
        } else {
            log.info("没有获得分布式锁：{}", Const.RedisDistributeLock.CLOSE_ORDER_TASK_LOCK);
        }

        log.info("关闭订单定时任务结束");
    }

    /**
     * 每隔一分钟进行关闭两小时前未付款的订单操作
     * 这种情况判断是否超时，不存在死锁的情况
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void closeOrderTaskV3() {
        log.info("关闭订单定时任务启动");
        long lockTimeOut = Long.parseLong(PropertiesUtil.getProperty("lock.timeout",
                "5000"));
        Long setnxResult = RedisSharededPoolUtil.setnx(Const.RedisDistributeLock.CLOSE_ORDER_TASK_LOCK,
                String.valueOf(System.currentTimeMillis() + lockTimeOut));

        if (setnxResult != null && setnxResult.intValue() == 1) {
            closeOrder(Const.RedisDistributeLock.CLOSE_ORDER_TASK_LOCK);
        } else {
            //没有获得锁，继续判断是否超时
            String lockValueStr = RedisSharededPoolUtil.get(Const.RedisDistributeLock.CLOSE_ORDER_TASK_LOCK);
            if (lockValueStr != null && System.currentTimeMillis() > Long.parseLong(lockValueStr)) {
                //此时锁已经超时
                //尝试设置新值的同时返回旧值，以获取锁
                String lockValueStrOld = RedisSharededPoolUtil.getSet(
                        Const.RedisDistributeLock.CLOSE_ORDER_TASK_LOCK,
                        String.valueOf(System.currentTimeMillis() + lockTimeOut));
                if (lockValueStrOld == null || StringUtils.equals(lockValueStrOld, lockValueStr)) {
                    //真正获取锁
                    closeOrder(Const.RedisDistributeLock.CLOSE_ORDER_TASK_LOCK);
                } else {
                    log.info("没有获得分布式锁：{}", Const.RedisDistributeLock.CLOSE_ORDER_TASK_LOCK);
                }
            } else {
                log.info("没有获得分布式锁：{}", Const.RedisDistributeLock.CLOSE_ORDER_TASK_LOCK);
            }
        }

        log.info("关闭订单定时任务结束");
    }

    /**
     * 每隔一分钟进行关闭两小时前未付款的订单操作
     * 使用Redisson管理分布式锁
     */
    //@Scheduled(cron = "0 */1 * * * ?")
    public void closeOrderTaskV4() {
        //声明锁
        RLock lock = redissonManager.getRedisson().getLock(Const.RedisDistributeLock.CLOSE_ORDER_TASK_LOCK);
        //尝试获取锁
        boolean getLock = false;
        try {
            //第一个参数表示等待时间
            //第二个参数表示锁释放时间
            //等待时间不宜过大，如果进程A在等待的过程中，进程B获取锁完成业务逻辑后释放锁
            //那么进程A将会在这一轮中获取锁，即一轮中有两个进程获取到锁
            //最好将等待时间设置为0
            getLock = lock.tryLock(0, 5, TimeUnit.SECONDS);
            if (getLock) {
                log.info("Redisson获取分布式锁：{}，ThreadName：{}",
                        Const.RedisDistributeLock.CLOSE_ORDER_TASK_LOCK, Thread.currentThread().getName());
                int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour",
                        "2"));
                //iOrderService.closeOrder(hour);
            } else {
                log.info("Redisson没有获取分布式锁：{}，ThreadName：{}",
                        Const.RedisDistributeLock.CLOSE_ORDER_TASK_LOCK, Thread.currentThread().getName());
            }
        } catch (InterruptedException e) {
            log.error("Redisson获取分布式锁发生异常", e);
        } finally {
            if (!getLock) {
                //未获取锁，直接返回
                return;
            }
            //获取锁，需释放锁
            lock.unlock();
            log.info("Redisson释放分布式锁：{}，ThreadName：{}",
                    Const.RedisDistributeLock.CLOSE_ORDER_TASK_LOCK, Thread.currentThread().getName());
        }
    }

    /**
     * 在服务器关闭时，在关闭之前需要执行此方法
     * 以防止获取锁后，未设置锁的有效期，直接关闭服务器
     * 但是针对直接kill进程方式，此方法不执行
     */
    @PreDestroy
    public void delLock() {
        RedisSharededPoolUtil.del(Const.RedisDistributeLock.CLOSE_ORDER_TASK_LOCK);
    }

    /**
     * 成功获取锁后执行的任务
     *
     * @param lockName 锁名
     */
    private void closeOrder(String lockName) {
        //设置有效期
        RedisSharededPoolUtil.expire(lockName, 50);
        log.info("获取{}，ThreadName：{}", Const.RedisDistributeLock.CLOSE_ORDER_TASK_LOCK,
                Thread.currentThread().getName());
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour",
                "2"));
        iOrderService.closeOrder(hour);
        RedisSharededPoolUtil.del(Const.RedisDistributeLock.CLOSE_ORDER_TASK_LOCK);
        log.info("释放{}，ThreadName：{}", Const.RedisDistributeLock.CLOSE_ORDER_TASK_LOCK,
                Thread.currentThread().getName());
    }
}
