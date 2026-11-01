
package com.arun.ecommerce.controller;

import com.arun.ecommerce.dto.ProductRequest;
import com.arun.ecommerce.dto.ProductResponse;
import com.arun.ecommerce.dto.ProductPatchRequest;
import com.arun.ecommerce.exception.GlobalExceptionHandler;
import com.arun.ecommerce.exception.ProductNotFoundException;
import com.arun.ecommerce.service.ProductService;


import tools.jackson.databind.ObjectMapper;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import(GlobalExceptionHandler.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    private ProductResponse sampleResponse() {
        return new ProductResponse(
                1L,
                "Keyboard",
                "Mechanical keyboard",
                new BigDecimal("2499.00"),
                20
        );
    }

    @Test
    void createProductShouldReturn201() throws Exception {
        when(productService.createProduct(any(ProductRequest.class)))
                .thenReturn(sampleResponse());

        ProductRequest request = new ProductRequest(
                "Keyboard",
                "Mechanical keyboard",
                new BigDecimal("2499.00"),
                20
        );

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Keyboard"));
    }

    @Test
    void getAllProductsShouldReturn200() throws Exception {
        when(productService.getAllProducts())
                .thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Keyboard"));
    }

    @Test
    void getProductByIdShouldReturn200() throws Exception {
        when(productService.getProductById(1L))
                .thenReturn(sampleResponse());

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void missingProductShouldReturn404() throws Exception {
        when(productService.getProductById(99L))
                .thenThrow(new ProductNotFoundException(
                        "Product not found with ID: 99"
                ));

        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Product not found with ID: 99"));
    }

    @Test
    void invalidProductShouldReturn400() throws Exception {
        String invalidJson = """
                {
                  "name": "   ",
                  "description": "Invalid product",
                  "price": -50,
                  "stockQuantity": -5
                }
                """;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.name").exists())
                .andExpect(jsonPath("$.validationErrors.price").exists())
                .andExpect(jsonPath("$.validationErrors.stockQuantity").exists());

        verify(productService, never())
                .createProduct(any(ProductRequest.class));
    }

    @Test
    void updateProductShouldReturn200() throws Exception {
        ProductRequest request = new ProductRequest(
                "Gaming Keyboard",
                "RGB mechanical keyboard",
                new BigDecimal("2999.00"),
                15
        );

        ProductResponse updated = new ProductResponse(
                1L,
                "Gaming Keyboard",
                "RGB mechanical keyboard",
                new BigDecimal("2999.00"),
                15
        );

        when(productService.updateProduct(eq(1L), any(ProductRequest.class)))
                .thenReturn(updated);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Gaming Keyboard"))
                .andExpect(jsonPath("$.stockQuantity").value(15));
    }

    @Test
    void deleteProductShouldReturn204() throws Exception {
        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        verify(productService).deleteProduct(1L);
    }
    

	@Test
	void patchProductShouldReturn200() throws Exception {
	    ProductPatchRequest request = new ProductPatchRequest(
	            null,
	            null,
	            new BigDecimal("2999.00"),
	            null
	    );
	
	    ProductResponse updated = new ProductResponse(
	            1L,
	            "Keyboard",
	            "Mechanical keyboard",
	            new BigDecimal("2999.00"),
	            20
	    );
	
	    when(productService.patchProduct(eq(1L), any(ProductPatchRequest.class)))
	            .thenReturn(updated);
	
	    mockMvc.perform(patch("/api/products/1")
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .content(objectMapper.writeValueAsString(request)))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.id").value(1))
	            .andExpect(jsonPath("$.price").value(2999.00))
	            .andExpect(jsonPath("$.stockQuantity").value(20));
	
	    verify(productService).patchProduct(
	            eq(1L), any(ProductPatchRequest.class));
	}
	
	@Test
	void patchProductShouldReturn404WhenMissing() throws Exception {
	    ProductPatchRequest request = new ProductPatchRequest(
	            null,
	            null,
	            new BigDecimal("2999.00"),
	            null
	    );
	
	    when(productService.patchProduct(eq(99L), any(ProductPatchRequest.class)))
	            .thenThrow(new ProductNotFoundException(
	                    "Product not found with ID: 99"
	            ));
	
	    mockMvc.perform(patch("/api/products/99")
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .content(objectMapper.writeValueAsString(request)))
	            .andExpect(status().isNotFound())
	            .andExpect(jsonPath("$.status").value(404))
	            .andExpect(jsonPath("$.message")
	                    .value("Product not found with ID: 99"));
	}

}
