package com.codecart.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codecart.common.constants.BusinessConstants;
import com.codecart.common.exception.BizException;
import com.codecart.dto.admin.AdminCategorySaveRequest;
import com.codecart.entity.ProductCategory;
import com.codecart.mapper.ProductCategoryMapper;
import com.codecart.service.ProductCategoryService;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ProductCategoryServiceImpl
        extends ServiceImpl<ProductCategoryMapper, ProductCategory>
        implements ProductCategoryService {

    private static final Set<String> ALLOWED_CATEGORY_STATUS =
            Set.of(BusinessConstants.CategoryStatus.ENABLED, BusinessConstants.CategoryStatus.DISABLED);

    @Override
    public List<ProductCategory> listEnabledCategories() {
        return list(Wrappers.<ProductCategory>lambdaQuery()
                .eq(ProductCategory::getStatus, BusinessConstants.CategoryStatus.ENABLED)
                .orderByAsc(ProductCategory::getSortNo, ProductCategory::getId));
    }

    @Override
    public List<ProductCategory> listAllCategories() {
        return list(Wrappers.<ProductCategory>lambdaQuery()
                .orderByAsc(ProductCategory::getSortNo, ProductCategory::getId));
    }

    @Override
    public ProductCategory createCategory(AdminCategorySaveRequest request) {
        validateCategoryPayload(request, null);

        ProductCategory category = new ProductCategory();
        category.setCategoryName(request.getCategoryName().trim());
        category.setSortNo(request.getSortNo());
        category.setStatus(request.getStatus().trim());
        category.setRemark(trimToNull(request.getRemark()));
        save(category);
        return getById(category.getId());
    }

    @Override
    public ProductCategory updateCategory(Long categoryId, AdminCategorySaveRequest request) {
        ProductCategory category = getById(categoryId);
        if (category == null) {
            throw new BizException("分类不存在");
        }

        validateCategoryPayload(request, categoryId);
        String normalizedStatus = request.getStatus().trim();
        String remark = trimToNull(request.getRemark());

        if (BusinessConstants.CategoryStatus.DISABLED.equals(normalizedStatus)) {
            int updated = getBaseMapper().updateWhenDisabling(
                    categoryId,
                    request.getCategoryName().trim(),
                    request.getSortNo(),
                    normalizedStatus,
                    remark);
            if (updated == 0) {
                throw new BizException("请先下架该分类下的所有商品");
            }
            return getById(categoryId);
        }

        category.setCategoryName(request.getCategoryName().trim());
        category.setSortNo(request.getSortNo());
        category.setStatus(normalizedStatus);
        category.setRemark(remark);
        updateById(category);
        return getById(categoryId);
    }

    @Override
    public ProductCategory updateCategoryStatus(Long categoryId, String status) {
        ProductCategory category = getById(categoryId);
        if (category == null) {
            throw new BizException("分类不存在");
        }

        String normalizedStatus = normalizeStatus(status);
        if (BusinessConstants.CategoryStatus.DISABLED.equals(normalizedStatus)) {
            int updated = getBaseMapper().updateStatusWhenDisabling(categoryId, normalizedStatus);
            if (updated == 0) {
                throw new BizException("请先下架该分类下的所有商品");
            }
            return getById(categoryId);
        }

        category.setStatus(normalizedStatus);
        updateById(category);
        return getById(categoryId);
    }

    private void validateCategoryPayload(AdminCategorySaveRequest request, Long categoryId) {
        String categoryName = request.getCategoryName().trim();
        String normalizedStatus = normalizeStatus(request.getStatus());
        request.setStatus(normalizedStatus);

        long duplicateCount = count(Wrappers.<ProductCategory>lambdaQuery()
                .eq(ProductCategory::getCategoryName, categoryName)
                .ne(categoryId != null, ProductCategory::getId, categoryId));
        if (duplicateCount > 0) {
            throw new BizException("分类名称已存在");
        }
    }

    private String normalizeStatus(String status) {
        if (!StringUtils.hasText(status)) {
            throw new BizException("分类状态不能为空");
        }
        String normalizedStatus = status.trim();
        if (!ALLOWED_CATEGORY_STATUS.contains(normalizedStatus)) {
            throw new BizException("分类状态不合法");
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
