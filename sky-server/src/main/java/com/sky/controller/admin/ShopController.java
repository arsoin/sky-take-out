package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController(value = "adminShopController")
@RequestMapping("/admin/shop")
@Slf4j
@Api(tags = "店铺相关注解")
public class ShopController {

    public static final String KEY = "SHOP_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 设置店铺营业状态
     * @param status
     * @return
     */
    @PutMapping("/{status}")
    @ApiOperation(value = "设置店铺营业状态" )
    public Result setStatus(@PathVariable Integer status){
        //这里前端传过来的 status 我们把他存到redis当中
        //根本就不需要操作sql数据库，因此根本就不需要service 和 mapper层
        log.info("设置店铺营业状态为：{}",status ==1? "营业中" : "打烊中");
        redisTemplate.opsForValue().set(KEY ,status);
        return Result.success();
    }

    /**
     * 获取店铺的营业状态,店铺端口
     * @return
     */
    @GetMapping("/status")
    @ApiOperation(value = "获取店铺的营业状态")
    public Result<Integer> getStatus(){
        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);
        log.info("获取到店铺的营业状态为：{}",status == 1? "营业中" : "打烊中");
        return Result.success(status);
    }



}
