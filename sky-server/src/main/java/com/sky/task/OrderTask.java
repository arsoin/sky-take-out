package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务类，定时来处理订单状态
 */
@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 处理超时订单
     */
    @Scheduled(cron = "0 * * * * ?") //每分钟触发一次
    public void processTimeoutOrder(){
        log.info("定时处理超时订单：{}", LocalDateTime.now());

        //查询有没有超时订单
        //select * from orders where status = (待付款状态) and order_time < (当前时间-15分钟)
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, LocalDateTime.now().plusMinutes(-15));
        if(ordersList != null && !ordersList.isEmpty()){
            for(Orders order : ordersList){
                order.setStatus(Orders.CANCELLED);
                order.setCancelReason("订单超时已取消");
                order.setCancelTime(LocalDateTime.now());
                orderMapper.update(order);
            }
        }
    }

    /**
     * 处理一直处于派送中的订单
     */
    @Scheduled(cron = "0 0 1 * * ?") // 每天凌晨一点触发
    public void processDeliveryOrder(){
        log.info("定时处理一直处于派送中的订单：{}",LocalDateTime.now());
        //查询有没有一直处于派送中的订单
        //select * from orders where status = (派送中) and order_time (当前时间 - 1小时)
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, LocalDateTime.now().plusHours(-1));
        if(ordersList != null && !ordersList.isEmpty()){
            for(Orders order : ordersList){
                order.setStatus(Orders.COMPLETED);
                orderMapper.update(order);
            }
        }
    }
}
