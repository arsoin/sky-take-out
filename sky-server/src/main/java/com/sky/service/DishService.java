package com.sky.service;

import com.sky.dto.DishDTO;


public interface DishService {

    /**
     * 新增菜品,和对应的口味，这两个表
     * @param dishDTO
     */
    public void saveWitFlavor(DishDTO dishDTO);
}
