package com.codecart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.codecart.entity.ProductCategory;
import java.util.List;

public interface ProductCategoryService extends IService<ProductCategory> {

    List<ProductCategory> listEnabledCategories();
}
