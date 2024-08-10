package com.sky.service;


import com.sky.dto.ShoppingCartDTO;


public interface ShoppingCartService {
    /**
     * 添加购物车业务方法
     * @param shoppingCartDTO
     */
    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);
}
