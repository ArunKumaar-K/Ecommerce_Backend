
package com.arun.ecommerce.service;

import com.arun.ecommerce.dto.ProductPatchRequest;
import com.arun.ecommerce.dto.ProductRequest;
import com.arun.ecommerce.dto.ProductResponse;
import com.arun.ecommerce.entity.Product;
import com.arun.ecommerce.exception.ProductNotFoundException;
import com.arun.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Product product = new Product();

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());

        Product savedProduct = productRepository.save(product);

        return toResponse(savedProduct);
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductResponse getProductById(Long id) {
        Product product = findProductOrThrow(id);
        return toResponse(product);
    }

    @Transactional
    public ProductResponse updateProduct(
            Long id, ProductRequest request) {

        Product product = findProductOrThrow(id);

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());

        Product savedProduct = productRepository.save(product);

        return toResponse(savedProduct);
    }
    
    

	@Transactional
	public ProductResponse patchProduct(
	        Long id, ProductPatchRequest request) {
	
	    Product product = findProductOrThrow(id);
	
	    if (request.name() != null) {
	        product.setName(request.name());
	    }
	
	    if (request.description() != null) {
	        product.setDescription(request.description());
	    }
	
	    if (request.price() != null) {
	        product.setPrice(request.price());
	    }
	
	    if (request.stockQuantity() != null) {
	        product.setStockQuantity(request.stockQuantity());
	    }
	
	    Product savedProduct = productRepository.save(product);
	
	    return toResponse(savedProduct);
	}


    @Transactional
    public void deleteProduct(Long id) {
        Product product = findProductOrThrow(id);
        productRepository.delete(product);
    }

    private Product findProductOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with ID: " + id
                        )
                );
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity()
        );
    }
}
