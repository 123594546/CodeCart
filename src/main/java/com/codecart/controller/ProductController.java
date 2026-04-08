package com.codecart.controller;

import com.codecart.common.result.ApiResult;
import com.codecart.entity.Product;
import com.codecart.service.ProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ApiResult<List<Product>> listOnSaleProducts(
            @RequestParam(value = "categoryId", required = false) Long categoryId) {
        if (categoryId == null) {
            return ApiResult.success(productService.listOnSaleProducts());
        }
        return ApiResult.success(productService.listOnSaleProductsByCategory(categoryId));
    }

    @GetMapping("/{id}")
    public ApiResult<Product> getProductDetail(@PathVariable("id") Long id) {
        Product product = productService.getOnSaleProductDetail(id);
        if (product == null) {
            return ApiResult.fail("商品不存在或已下架");
        }
        return ApiResult.success(product);
    }
}
