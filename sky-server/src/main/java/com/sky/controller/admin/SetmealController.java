package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping
@Slf4j
@Api(tags = "套餐相关的接口")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    /**
     * 新增套餐
     * @param setmealDTO
     * @return
     */
    @PostMapping("/admin/setmeal")
    @ApiOperation(value = "新增套餐")
    public Result save (@RequestBody SetmealDTO setmealDTO){
        log.info("新增套餐内容：{}",setmealDTO);
        setmealService.saveWithDish(setmealDTO);
        return Result.success();
    }

    /**
     * 套餐分页查询功能
     * @param setmealPageQueryDTO
     * @return
     */
    @ApiOperation(value = "套餐分页查询功能")
    @GetMapping("/admin/setmeal/page")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("套餐查询：{}",setmealPageQueryDTO);
        PageResult pageResult =  setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 删除套餐
     * @return
     */
    @ApiOperation(value = "删除套餐")
    @DeleteMapping("/admin/setmeal")
    public Result delete(@RequestParam List<Long> ids){
        log.info("删除{}套餐",ids);
        setmealService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 根据套餐id查询套餐信息
     * @param id
     * @return
     */
    @ApiOperation(value = "查询套餐信息")
    @GetMapping("/admin/setmeal/{id}")
    public Result<SetmealVO> getById(@PathVariable Long id){
        log.info("根据套餐id查询套餐信息：{}",id);
        SetmealVO setmealVO =  setmealService.getByIdWithDish(id);
        return Result.success(setmealVO);
    }

    /**
     * 修改套餐
     * @return
     */
    @ApiOperation(value = "修改套餐")
    @PutMapping("/admin/setmeal")
    public Result update(@RequestBody SetmealDTO setmealDTO){
        log.info("修改套餐{}",setmealDTO);
        setmealService.update(setmealDTO);
        return Result.success();
    }

    /**
     * 套餐起售停售
     * @param status
     * @param id
     * @return
     */
    @ApiOperation(value = "套餐起售停售")
    @PostMapping("/admin/setmeal/status/{status}")
    public Result startOrStop(@RequestParam Integer status, Long id){
        log.info("套餐起售停售：{}，{}",status,id);
        setmealService.startOrStop(status,id);
        return Result.success();
    }

}
