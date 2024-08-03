package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /**
     * 批量插入口味数据
     * @param flavors
     */
    void insertBatch(List<DishFlavor> flavors);

    /**
     * 根据菜品的id来删除口味的数据，一条一条的
     * @param DishId
     */
    @Delete("delete from dish_flavor where dish_id = #{DishId}")
    void DelteByDishId(long DishId);

    /**
     * 根据菜品的id集合来批量删除口味数据
     * 这个是批量删除，而不是用for循环
     * @param DishIds
     */
    void delteByDishIds(List<Long> DishIds);

    /**
     * 根据菜品的id来查询对应的口味数据
     * @param DishId
     * @return
     */
    @Select("select * from dish_flavor where dish_id = #{DishId}")
    List<DishFlavor> getByDishId(Long DishId);
}
