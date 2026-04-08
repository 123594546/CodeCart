package com.codecart.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codecart.entity.ProductCategory;
import com.codecart.mapper.ProductCategoryMapper;
import com.codecart.service.ProductCategoryService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductCategoryServiceImpl
        extends ServiceImpl<ProductCategoryMapper, ProductCategory>
        implements ProductCategoryService {

    @Override
    public List<ProductCategory> listEnabledCategories() {
        return list(Wrappers.<ProductCategory>lambdaQuery()
                .eq(ProductCategory::getStatus, "ENABLED")
                .orderByAsc(ProductCategory::getSortNo, ProductCategory::getId));
    }
}
