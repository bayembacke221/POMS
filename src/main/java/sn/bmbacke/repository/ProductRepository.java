package sn.bmbacke.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sn.bmbacke.models.Product;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends GenericRepository<Product, Integer> {
    @Query("SELECT p FROM Product p WHERE " +
            "(:name IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
            "(:minPrice IS NULL OR p.unitPrice >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.unitPrice <= :maxPrice) AND " +
            "(:discontinued IS NULL OR p.discontinued = :discontinued) AND " +
            "(:lowStock IS NULL OR (p.unitsInStock <= p.reorderLevel))")
    Page<Product> findProductsByCriteria(
            @Param("name") String name,
            @Param("categoryId") Integer categoryId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("discontinued") Boolean discontinued,
            @Param("lowStock") Boolean lowStock,
            Pageable pageable
    );

    @Query("SELECT p FROM Product p WHERE p.unitsInStock <= p.reorderLevel AND p.discontinued = false")
    List<Product> findLowStockProducts();

    boolean existsByNameAndCategoryIdAndIdNot(String name, Integer categoryId, Integer id);
    boolean existsByNameAndCategoryId(String name, Integer categoryId);
}
