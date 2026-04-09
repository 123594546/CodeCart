package com.codecart.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codecart.common.constants.BusinessConstants;
import com.codecart.common.exception.BizException;
import com.codecart.dto.admin.AdminProductSaveRequest;
import com.codecart.entity.Product;
import com.codecart.entity.ProductCategory;
import com.codecart.mapper.ProductCategoryMapper;
import com.codecart.mapper.ProductMapper;
import com.codecart.service.ProductService;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private static final Set<String> ALLOWED_PRODUCT_STATUS =
            Set.of(BusinessConstants.ProductStatus.ON_SALE, BusinessConstants.ProductStatus.OFF_SALE);

    private final ProductCategoryMapper productCategoryMapper;

    @Override
    public List<Product> listOnSaleProducts() {
        return list(Wrappers.<Product>lambdaQuery()
                .eq(Product::getStatus, BusinessConstants.ProductStatus.ON_SALE)
                .inSql(Product::getCategoryId,
                        "SELECT id FROM product_category WHERE deleted = 0 AND status = 'ENABLED'")
                .orderByAsc(Product::getSortNo, Product::getId));
    }

    @Override
    public List<Product> listOnSaleProductsByCategory(Long categoryId) {
        return list(Wrappers.<Product>lambdaQuery()
                .eq(Product::getCategoryId, categoryId)
                .eq(Product::getStatus, BusinessConstants.ProductStatus.ON_SALE)
                .inSql(Product::getCategoryId,
                        "SELECT id FROM product_category WHERE deleted = 0 AND status = 'ENABLED'")
                .orderByAsc(Product::getSortNo, Product::getId));
    }

    @Override
    public Product getOnSaleProductDetail(Long productId) {
        return getOne(Wrappers.<Product>lambdaQuery()
                .eq(Product::getId, productId)
                .eq(Product::getStatus, BusinessConstants.ProductStatus.ON_SALE)
                .inSql(Product::getCategoryId,
                        "SELECT id FROM product_category WHERE deleted = 0 AND status = 'ENABLED'")
                .last("LIMIT 1"));
    }

    @Override
    public List<Product> listAdminProducts(Long categoryId, String status) {
        return list(Wrappers.<Product>lambdaQuery()
                .eq(categoryId != null, Product::getCategoryId, categoryId)
                .eq(StringUtils.hasText(status), Product::getStatus, status)
                .orderByAsc(Product::getSortNo, Product::getId));
    }

    @Override
    public Product createProduct(AdminProductSaveRequest request) {
        validateProductPayload(request);

        Product product = new Product();
        product.setCategoryId(request.getCategoryId());
        product.setProductName(request.getProductName().trim());
        product.setProductCover(trimToNull(request.getProductCover()));
        product.setProductDesc(trimToNull(request.getProductDesc()));
        product.setPrice(request.getPrice());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setTotalStock(0);
        product.setAvailableStock(0);
        product.setSoldCount(0);
        product.setStatus(request.getStatus().trim());
        product.setSortNo(request.getSortNo());
        product.setRemark(trimToNull(request.getRemark()));
        save(product);
        return getById(product.getId());
    }

    @Override
    public Product updateProduct(Long productId, AdminProductSaveRequest request) {
        Product product = getById(productId);
        if (product == null) {
            throw new BizException("商品不存在");
        }

        validateProductPayload(request);

        product.setCategoryId(request.getCategoryId());
        product.setProductName(request.getProductName().trim());
        product.setProductCover(trimToNull(request.getProductCover()));
        product.setProductDesc(trimToNull(request.getProductDesc()));
        product.setPrice(request.getPrice());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setStatus(request.getStatus().trim());
        product.setSortNo(request.getSortNo());
        product.setRemark(trimToNull(request.getRemark()));
        updateById(product);
        return getById(productId);
    }

    @Override
    public Product updateProductStatus(Long productId, String status) {
        Product product = getById(productId);
        if (product == null) {
            throw new BizException("商品不存在");
        }

        String normalizedStatus = normalizeStatus(status);
        if (BusinessConstants.ProductStatus.ON_SALE.equals(normalizedStatus)) {
            ensureCategoryEnabled(product.getCategoryId());
        }

        product.setStatus(normalizedStatus);
        updateById(product);
        return getById(productId);
    }

    private void validateProductPayload(AdminProductSaveRequest request) {
        String normalizedStatus = normalizeStatus(request.getStatus());
        request.setStatus(normalizedStatus);
        ensureCategoryExists(request.getCategoryId());

        if (request.getOriginalPrice() != null && request.getOriginalPrice().compareTo(request.getPrice()) < 0) {
            throw new BizException("原价不能低于售价");
        }

        if (BusinessConstants.ProductStatus.ON_SALE.equals(normalizedStatus)) {
            ensureCategoryEnabled(request.getCategoryId());
        }
    }

    private void ensureCategoryExists(Long categoryId) {
        ProductCategory category = productCategoryMapper.selectById(categoryId);
        if (category == null) {
            throw new BizException("商品分类不存在");
        }
    }

    private void ensureCategoryEnabled(Long categoryId) {
        ProductCategory category = productCategoryMapper.selectById(categoryId);
        if (category == null) {
            throw new BizException("商品分类不存在");
        }
        if (!BusinessConstants.CategoryStatus.ENABLED.equals(category.getStatus())) {
            throw new BizException("所属分类未启用，商品不能上架");
        }
    }

    private String normalizeStatus(String status) {
        if (!StringUtils.hasText(status)) {
            throw new BizException("商品状态不能为空");
        }
        String normalizedStatus = status.trim();
        if (!ALLOWED_PRODUCT_STATUS.contains(normalizedStatus)) {
            throw new BizException("商品状态不合法");
        }
        return normalizedStatus;
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
