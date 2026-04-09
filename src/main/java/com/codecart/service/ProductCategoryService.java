package com.codecart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.codecart.dto.admin.AdminCategorySaveRequest;
import com.codecart.entity.ProductCategory;
import java.util.List;

public interface ProductCategoryService extends IService<ProductCategory> {

    List<ProductCategory> listEnabledCategories();

    List<ProductCategory> listAllCategories();

    ProductCategory createCategory(AdminCategorySaveRequest request);

    ProductCategory updateCategory(Long categoryId, AdminCategorySaveRequest request);

    ProductCategory updateCategoryStatus(Long categoryId, String status);
}
