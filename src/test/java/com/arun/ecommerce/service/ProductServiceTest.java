
package com.arun.ecommerce.service;

import com.arun.ecommerce.dto.ProductRequest;
import com.arun.ecommerce.dto.ProductResponse;
import com.arun.ecommerce.dto.ProductPatchRequest;
import com.arun.ecommerce.entity.Product;
import com.arun.ecommerce.exception.ProductNotFoundException;
import com.arun.ecommerce.repository.ProductRepository;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductRequest request;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Keyboard");
        product.setDescription("Mechanical keyboard");
        product.setPrice(new BigDecimal("2499.00"));
        product.setStockQuantity(20);

        request = new ProductRequest(
                "Keyboard",
                "Mechanical keyboard",
                new BigDecimal("2499.00"),
                20
        );
    }

    @Test
    void createProductShouldReturnSavedProduct() {
        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> {
                    Product saved = invocation.getArgument(0);
                    saved.setId(1L);
                    return saved;
                });

        ProductResponse response = productService.createProduct(request);

        assertEquals(1L, response.id());
        assertEquals("Keyboard", response.name());
        assertEquals(new BigDecimal("2499.00"), response.price());

        verify(productRepository).save(any(Product.class));
    }

    @Test
    void getAllProductsShouldReturnProductList() {
        when(productRepository.findAll()).thenReturn(List.of(product));

        List<ProductResponse> responses = productService.getAllProducts();

        assertEquals(1, responses.size());
        assertEquals("Keyboard", responses.get(0).name());

        verify(productRepository).findAll();
    }

    @Test
    void getProductByIdShouldReturnProduct() {
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        ProductResponse response = productService.getProductById(1L);

        assertEquals(1L, response.id());
        assertEquals("Keyboard", response.name());
    }

    @Test
    void getProductByIdShouldThrowWhenMissing() {
        when(productRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> productService.getProductById(99L)
        );
    }

    @Test
    void updateProductShouldUpdateExistingProduct() {
        ProductRequest updatedRequest = new ProductRequest(
                "Gaming Keyboard",
                "RGB mechanical keyboard",
                new BigDecimal("2999.00"),
                15
        );

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponse response =
                productService.updateProduct(1L, updatedRequest);

        assertEquals("Gaming Keyboard", response.name());
        assertEquals(new BigDecimal("2999.00"), response.price());
        assertEquals(15, response.stockQuantity());
    }

    @Test
    void deleteProductShouldDeleteExistingProduct() {
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        verify(productRepository).delete(product);
    }
    

	@Test
	void patchProductShouldUpdateOnlyProvidedFields() {
	    ProductPatchRequest patchRequest = new ProductPatchRequest(
	            null,
	            null,
	            new BigDecimal("2999.00"),
	            null
	    );
	
	    when(productRepository.findById(1L))
	            .thenReturn(Optional.of(product));
	
	    when(productRepository.save(any(Product.class)))
	            .thenAnswer(invocation -> invocation.getArgument(0));
	
	    ProductResponse response =
	            productService.patchProduct(1L, patchRequest);
	
	    assertEquals("Keyboard", response.name());
	    assertEquals("Mechanical keyboard", response.description());
	    assertEquals(new BigDecimal("2999.00"), response.price());
	    assertEquals(20, response.stockQuantity());
	
	    verify(productRepository).findById(1L);
	    verify(productRepository).save(product);
	}
	
	@Test
	void patchProductShouldThrowWhenProductIsMissing() {
	    ProductPatchRequest patchRequest = new ProductPatchRequest(
	            null,
	            null,
	            new BigDecimal("2999.00"),
	            null
	    );
	
	    when(productRepository.findById(99L))
	            .thenReturn(Optional.empty());
	
	    assertThrows(
	            ProductNotFoundException.class,
	            () -> productService.patchProduct(99L, patchRequest)
	    );
	
	    verify(productRepository, never()).save(any(Product.class));
	}

}
