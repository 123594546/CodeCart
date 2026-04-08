package com.codecart.controller;

import com.codecart.common.result.ApiResult;
import com.codecart.entity.ProductCategory;
import com.codecart.service.ProductCategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class ProductCategoryController {

    private final ProductCategoryService productCategoryService;

    @GetMapping
    public ApiResult<List<ProductCategory>> listEnabledCategories() {
        return ApiResult.success(productCategoryService.listEnabledCategories());
    }
}
