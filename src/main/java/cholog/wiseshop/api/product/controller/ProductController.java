package cholog.wiseshop.api.product.controller;

import cholog.wiseshop.api.product.dto.request.ModifyProductPriceAndStockRequest;
import cholog.wiseshop.api.product.dto.request.ModifyProductRequest;
import cholog.wiseshop.api.product.dto.response.ProductResponse;
import cholog.wiseshop.api.product.service.ProductService;
import cholog.wiseshop.common.auth.Auth;
import cholog.wiseshop.db.member.Member;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/products")
@RestController
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        ProductResponse response = productService.getProduct(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> modifyProduct(
        @Auth Member member,
        @PathVariable Long id,
        @RequestBody ModifyProductRequest request
    ) {
        productService.modifyProduct(member, id, request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/price")
    public ResponseEntity<Void> modifyProductPriceAndStock(
        @Auth Member member,
        @PathVariable Long id,
        @RequestBody ModifyProductPriceAndStockRequest request
    ) {
        productService.modifyProductPriceAndStock(member, id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
        @Auth Member member,
        @PathVariable Long id
    ) {
        productService.deleteProduct(member, id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
