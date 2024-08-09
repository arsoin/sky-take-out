package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

//    /**
//     * 根据菜品id和状态来更改套餐的状态
//     * @param setmeal
//     */
//    @AutoFill(value = OperationType.UPDATE)
//    static void update(Setmeal setmeal) {
//
//    }

    /**
     * 根据菜品id来查询套餐id
     * @param dishIds
     * @return
     */
    List<Long> getSetmealIdByDish(List<Long> dishIds);

    /**
     * 建立套餐和菜品的关系
     * @param setmealDishes
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 根据setmal id 删除套餐菜品关系表中的数据
     * @param id
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
    void deleteBySetmealId(Long id);

    /**
     * 根据套餐id来查询菜品信息
     * @param setmealId
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> getBySetmealId(Long setmealId);
}
