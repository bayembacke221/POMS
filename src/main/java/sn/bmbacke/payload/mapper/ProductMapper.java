package sn.bmbacke.payload.mapper;

import org.springframework.stereotype.Component;
import sn.bmbacke.models.Product;
import sn.bmbacke.payload.dto.ProductDTO;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ProductMapper {
    public ProductDTO toDto(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getProductName())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getCategoryName())
                .quantityPerUnit(product.getQuantityPerUnit())
                .unitPrice(product.getUnitPrice())
                .unitsInStock(Integer.valueOf(product.getUnitsInStock()))
                .unitsOnOrder(Integer.valueOf(product.getUnitsOnOrder()))
                .reorderLevel(Integer.valueOf(product.getReorderLevel()))
                .discontinued(product.isDiscontinued())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public List<ProductDTO> toDtoList(List<Product> products) {
        return products.stream()
                .map(this::toDto)
                .toList();
    }
}
