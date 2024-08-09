package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api(tags = "菜品相关接口")
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 新增菜品
     *
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation(value = "新增菜品")
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品:{}", dishDTO);
        dishService.saveWitFlavor(dishDTO);

        //清理缓存,这里是可以精准清理的
        String key = "dish_" + dishDTO.getCategoryId();
        cleanCache(key);
        return Result.success();


    }

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    @ApiOperation(value = "菜品分页查询")
    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询：{}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 菜品的批量删除
     *
     * @param ids
     * @return
     */
    @ApiOperation(value = "菜品的批量删除")
    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids) {
        log.info("菜品的批量删除：{}", ids);
        dishService.deleteBatch(ids);

        //批量删除的话就直接把所有的缓存清理就行，不然得先查完数据库，知道了批量删除哪些才能清理
        //获取到所有 dish_*  的key
        //Set keys = redisTemplate.keys("dish_*");
        //redisTemplate.delete(keys);
        cleanCache("dish_*");
        return Result.success();
    }
    /**
     * 根据id来查询菜品
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "根据id来查询菜品")
    public Result<DishVO> getById(@PathVariable Long id) {
        log.info("根据id来查询菜品：{}", id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    /**
     * 修改菜品
     * @param dishDTO
     * @return
     */
    @ApiOperation(value = "修改菜品")
    @PutMapping
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品：{}",dishDTO);
        dishService.updateWithFlavor(dishDTO);
        //修改操作也比较复杂，因为如果你修改了他的分类的话，那么不仅会影响dish表的结构，还会影响分类表的结构，因此这里直接清理缓存了
        //获取到所有 dish_*  的key
        //Set keys = redisTemplate.keys("dish_*");
        //redisTemplate.delete(keys);
        cleanCache("dish_*");
        return Result.success();
    }

    /**
     * 菜品起售停售
     * @param status
     * @param id
     * @return
     */
    @ApiOperation(value = "菜品起售停售")
    @PostMapping("/status/{status}")
    public Result updateStatus(@PathVariable Integer status,Long id){
        log.info("菜品起售停售：{},{}",status,id);
        dishService.startOrStop(status,id);
        cleanCache("dish_*");
        return Result.success();
    }

    /**
     * 根据分类的id来查询这个分类下有哪些菜品
     * @param id
     * @return
     */
    @ApiOperation(value = "根据分类的id来查询这个分类下有哪些菜品")
    @GetMapping("/list")
    public Result<List<Dish>> list (Long id){
        log.info("根据分类的id来查询这个分类下有哪些菜品,分类id:{}",id);
        List<Dish> list =  dishService.list(id);
        return Result.success(list);
    }

    private void cleanCache(String patten){
        Set keys = redisTemplate.keys(patten);
        redisTemplate.delete(keys);
    }
}
