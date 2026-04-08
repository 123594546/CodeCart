package com.codecart.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codecart.entity.Product;
import com.codecart.mapper.ProductMapper;
import com.codecart.service.ProductService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Override
    public List<Product> listOnSaleProducts() {
        return list(Wrappers.<Product>lambdaQuery()
                .eq(Product::getStatus, "ON_SALE")
                .orderByAsc(Product::getSortNo, Product::getId));
    }

    @Override
    public List<Product> listOnSaleProductsByCategory(Long categoryId) {
        return list(Wrappers.<Product>lambdaQuery()
                .eq(Product::getCategoryId, categoryId)
                .eq(Product::getStatus, "ON_SALE")
                .orderByAsc(Product::getSortNo, Product::getId));
    }

    @Override
    public Product getOnSaleProductDetail(Long productId) {
        return getOne(Wrappers.<Product>lambdaQuery()
                .eq(Product::getId, productId)
                .eq(Product::getStatus, "ON_SALE")
                .last("LIMIT 1"));
    }
}
