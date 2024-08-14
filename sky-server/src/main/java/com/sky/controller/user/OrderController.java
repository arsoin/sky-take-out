package com.sky.controller.user;

import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.OrderDetail;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController(value = "userOrderController")
@RequestMapping("/user/order")
@Slf4j
@Api(tags = "用户端相关接口")
public class OrderController {
    @Autowired
    private OrderService orderService;


    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    @ApiOperation(value = "用户下单")
    @PostMapping("/submit")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        log.info("用户下单：{}",ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO =  orderService.submitOrder(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    /**
     * 订单支付  这个就是给微信小程序那边准备 调起微信支付所需要的一系列数据
     *
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        return Result.success(orderPaymentVO);
    }

    /**
     *
     * @param page
     * @param pageSize
     * @param status 订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消 你要查哪个状态的订单历史记录
     * @return
     */
    @ApiOperation(value = "历史订单查询")
    @GetMapping("/historyOrders")
    public Result<PageResult> page(int page ,int pageSize,int status){
        log.info("历史订单查询，{}，{}，{}",page,pageSize,status);
        PageResult pageResult =  orderService.pageQuery4User(page,pageSize,status);
        return Result.success(pageResult);
    }

    /**
     * 根据历史记录查询订单详情
     * @param id
     * @return
     */
    @ApiOperation(value = "根据历史记录查询订单详情")
    @GetMapping("/orderDetail/{id}")
    public Result<OrderVO> details(@PathVariable(value = "id") Long id){
        log.info("根据历史记录查询订单详情：{}",id);
        OrderVO orderVO =  orderService.details(id);
        return Result.success(orderVO);
    }

    /**
     * 用户取消订单
     *
     * @return
     */
    @PutMapping("/cancel/{id}")
    @ApiOperation("取消订单")
    public Result cancel(@PathVariable("id") Long id) throws Exception {
        orderService.userCancelById(id);
        return Result.success();
    }

    /**
     * 再来一单
     *
     * @param id
     * @return
     */
    @PostMapping("/repetition/{id}")
    @ApiOperation("再来一单")
    public Result repetition(@PathVariable Long id) {
        orderService.repetition(id);
        return Result.success();
    }

    /**
     * 用户催单
     * @param id
     * @return
     */
    @ApiOperation(value = "用户催单")
    @GetMapping("/reminder/{id}")
    public Result reminder(@PathVariable(value = "id") Long id){
        log.info("用户催单：{}",id);
        orderService.reminder(id);
        return Result.success();
    }
}
