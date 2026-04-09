package com.codecart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.codecart.dto.admin.AdminProductSaveRequest;
import com.codecart.entity.Product;
import java.util.List;

public interface ProductService extends IService<Product> {

    List<Product> listOnSaleProducts();

    List<Product> listOnSaleProductsByCategory(Long categoryId);

    Product getOnSaleProductDetail(Long productId);

    List<Product> listAdminProducts(Long categoryId, String status);

    Product createProduct(AdminProductSaveRequest request);

    Product updateProduct(Long productId, AdminProductSaveRequest request);

    Product updateProductStatus(Long productId, String status);
}
