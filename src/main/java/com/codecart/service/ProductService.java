package com.codecart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.codecart.entity.Product;
import java.util.List;

public interface ProductService extends IService<Product> {

    List<Product> listOnSaleProducts();

    List<Product> listOnSaleProductsByCategory(Long categoryId);

    Product getOnSaleProductDetail(Long productId);
}
