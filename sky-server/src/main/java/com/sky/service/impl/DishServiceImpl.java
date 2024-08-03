package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper  dishMapper;

    @Autowired
    DishFlavorMapper dishFlavorMapper;


    /**
     * 新增菜品,和对应的口味，这两个表
     * 因为要操作两张表，因此需要事务注解，保证数据的一致性
     * @param dishDTO
     */
    @Transactional
    @Override
    public void saveWitFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        //向菜品表插入一条数据
        dishMapper.insert(dish);

        //获取到insert 语句之后，生成给的主键值
        Long id = dish.getId();

        //如果设置了口味，那么会向口味表插入多条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && !flavors.isEmpty()){
            //判断了非空，就可以插入数据了
            flavors.forEach((dishFlavor)-> dishFlavor.setDishId(id));
            dishFlavorMapper.insertBatch(flavors);

        }

    }
}
