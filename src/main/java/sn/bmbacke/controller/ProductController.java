package sn.bmbacke.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sn.bmbacke.payload.dto.ProductDTO;
import sn.bmbacke.payload.request.CreateProductRequest;
import sn.bmbacke.payload.request.ProductSearchCriteria;
import sn.bmbacke.payload.request.UpdateProductRequest;
import sn.bmbacke.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private final ProductService productService;

    @PostMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCT_MANAGER')")
    public ResponseEntity<Page<ProductDTO>> searchProducts(@RequestBody ProductSearchCriteria criteria) {
        return ResponseEntity.ok(productService.searchProducts(criteria));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCT_MANAGER')")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Integer id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCT_MANAGER')")
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody CreateProductRequest request) {
        ProductDTO product = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCT_MANAGER')")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCT_MANAGER')")
    public ResponseEntity<List<ProductDTO>> getLowStockProducts() {
        return ResponseEntity.ok(productService.getLowStockProducts());
    }
}
