package sn.bmbacke.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.bmbacke.exception.ApiException;
import sn.bmbacke.models.Category;
import sn.bmbacke.models.Product;
import sn.bmbacke.payload.dto.ProductDTO;
import sn.bmbacke.payload.mapper.ProductMapper;
import sn.bmbacke.payload.request.CreateProductRequest;
import sn.bmbacke.payload.request.ProductSearchCriteria;
import sn.bmbacke.payload.request.UpdateProductRequest;
import sn.bmbacke.repository.CategoryRepository;
import sn.bmbacke.repository.ProductRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public Page<ProductDTO> searchProducts(ProductSearchCriteria criteria) {
        Pageable pageable = PageRequest.of(
                criteria.getPage(),
                criteria.getSize(),
                criteria.getSortDirection().equalsIgnoreCase("DESC") ?
                        Sort.by(criteria.getSortBy()).descending() :
                        Sort.by(criteria.getSortBy()).ascending()
        );

        Page<Product> products = productRepository.findProductsByCriteria(
                criteria.getName(),
                criteria.getCategoryId(),
                criteria.getMinPrice(),
                criteria.getMaxPrice(),
                criteria.getDiscontinued(),
                criteria.getLowStock(),
                pageable
        );

        return products.map(productMapper::toDto);
    }

    @Transactional
    public ProductDTO createProduct(CreateProductRequest request) {
        validateNewProduct(request);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ApiException("Category not found"));

        Product product = Product.builder()
                .productName(request.getName())
                .category(category)
                .quantityPerUnit(request.getQuantityPerUnit())
                .unitPrice(request.getUnitPrice())
                .unitsInStock(Short.valueOf(String.valueOf(request.getUnitsInStock())))
                .unitsOnOrder(Short.valueOf(String.valueOf(request.getUnitsOnOrder())))
                .reorderLevel(Short.valueOf(String.valueOf(request.getReorderLevel())))
                .discontinued(false)
                .build();

        product = productRepository.save(product);
        return productMapper.toDto(product);
    }

    @Transactional
    public ProductDTO updateProduct(Integer id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ApiException("Product not found"));

        validateUpdateProduct(request, id);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ApiException("Category not found"));

        product.setProductName(request.getName());
        product.setCategory(category);
        product.setQuantityPerUnit(request.getQuantityPerUnit());
        product.setUnitPrice(request.getUnitPrice());
        product.setUnitsInStock(Short.valueOf(String.valueOf(request.getUnitsInStock())));
        product.setUnitsOnOrder(Short.valueOf(String.valueOf(request.getUnitsOnOrder())));
        product.setReorderLevel(Short.valueOf(String.valueOf(request.getReorderLevel())));
        product.setDiscontinued(request.isDiscontinued());

        product = productRepository.save(product);
        return productMapper.toDto(product);
    }

    @Transactional
    public void deleteProduct(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ApiException("Product not found"));

        if (!product.isDiscontinued()) {
            product.setDiscontinued(true);
            productRepository.save(product);
        }
    }

    @Transactional(readOnly = true)
    public ProductDTO getProduct(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ApiException("Product not found"));
        return productMapper.toDto(product);
    }

    private void validateNewProduct(CreateProductRequest request) {
        if (productRepository.existsByNameAndCategoryId(
                request.getName(), request.getCategoryId())) {
            throw new ApiException("Product with this name already exists in the category");
        }
    }

    private void validateUpdateProduct(UpdateProductRequest request, Integer id) {
        if (productRepository.existsByNameAndCategoryIdAndIdNot(
                request.getName(), request.getCategoryId(), id)) {
            throw new ApiException("Product with this name already exists in the category");
        }
    }
}
