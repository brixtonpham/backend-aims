package Project_ITSS.controller;

import Project_ITSS.common.entity.Product;
import Project_ITSS.common.entity.Book;
import Project_ITSS.common.entity.CD;
import Project_ITSS.common.entity.DVD;
import Project_ITSS.service.ProductViewService;
import Project_ITSS.service.UserViewService;
import Project_ITSS.common.validation.ValidationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductViewController Unit Tests")
class ProductViewControllerTest {

    @Mock
    private ProductViewService productService;

    @Mock
    private UserViewService userService;

    @InjectMocks
    private ProductViewController productViewController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productViewController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("GET /product/all-detail/{id} - Manager Product Detail Tests")
    class GetProductDetailForManagerTests {

        @Test
        @DisplayName("Should return book details for valid ID and type")
        void getProductDetailForManager_ValidBookIdAndType_ReturnsBookDetail() throws Exception {
            // Given
            int productId = 123;
            String type = "book";
            Book mockBook = createMockBook(productId);
            
            when(productService.getFullProductDetail(productId, type)).thenReturn(mockBook);

            // When & Then
            mockMvc.perform(get("/product/all-detail/{id}", productId)
                    .param("type", type)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.productId").value(productId))
                    .andExpect(jsonPath("$.title").value("Test Book"))
                    .andExpect(jsonPath("$.type").value("book"))
                    .andExpect(jsonPath("$.price").value(100000))
                    .andExpect(jsonPath("$.weight").value(0.5))
                    .andExpect(jsonPath("$.quantity").value(10))
                    .andExpect(jsonPath("$.rushOrderSupported").value(true))
                    .andExpect(jsonPath("$.authors").value("Test Author"))
                    .andExpect(jsonPath("$.genre").value("Fiction"))
                    .andExpect(jsonPath("$.pageCount").value(300))
                    .andExpect(jsonPath("$.publishers").value("Test Publisher"))
                    .andExpect(jsonPath("$.publicationDate").value("2025-01-01"))
                    .andExpect(jsonPath("$.coverType").value("Paperback"));

            verify(productService).getFullProductDetail(productId, type);
        }

        @Test
        @DisplayName("Should return CD details for valid ID and type")
        void getProductDetailForManager_ValidCDIdAndType_ReturnsCDDetail() throws Exception {
            // Given
            int productId = 124;
            String type = "cd";
            CD mockCD = createMockCD(productId);
            
            when(productService.getFullProductDetail(productId, type)).thenReturn(mockCD);

            // When & Then
            mockMvc.perform(get("/product/all-detail/{id}", productId)
                    .param("type", type)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.productId").value(productId))
                    .andExpect(jsonPath("$.title").value("Test CD"))
                    .andExpect(jsonPath("$.type").value("cd"))
                    .andExpect(jsonPath("$.price").value(200000))
                    .andExpect(jsonPath("$.artists").value("Test Artist"))
                    .andExpect(jsonPath("$.genre").value("Rock"))
                    .andExpect(jsonPath("$.trackList").value("Track 1, Track 2, Track 3"))
                    .andExpect(jsonPath("$.recordLabel").value("Test Label"))
                    .andExpect(jsonPath("$.releaseDate").value("2025-01-01"));

            verify(productService).getFullProductDetail(productId, type);
        }

        @Test
        @DisplayName("Should return DVD details for valid ID and type")
        void getProductDetailForManager_ValidDVDIdAndType_ReturnsDVDDetail() throws Exception {
            // Given
            int productId = 125;
            String type = "dvd";
            DVD mockDVD = createMockDVD(productId);
            
            when(productService.getFullProductDetail(productId, type)).thenReturn(mockDVD);

            // When & Then
            mockMvc.perform(get("/product/all-detail/{id}", productId)
                    .param("type", type)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.productId").value(productId))
                    .andExpect(jsonPath("$.title").value("Test DVD"))
                    .andExpect(jsonPath("$.type").value("dvd"))
                    .andExpect(jsonPath("$.price").value(300000))
                    .andExpect(jsonPath("$.director").value("Test Director"))
                    .andExpect(jsonPath("$.genre").value("Action"))
                    .andExpect(jsonPath("$.studio").value("Test Studio"))
                    .andExpect(jsonPath("$.dvdType").value("Blu-ray"))
                    .andExpect(jsonPath("$.releaseDate").value("2025-01-01"));

            verify(productService).getFullProductDetail(productId, type);
        }

        @Test
        @DisplayName("Should throw ValidationException for negative ID")
        void getProductDetailForManager_NegativeId_ThrowsValidationException() throws Exception {
            // Given
            int invalidId = -1;
            String type = "book";

            // When & Then
            mockMvc.perform(get("/product/all-detail/{id}", invalidId)
                    .param("type", type)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(productService, never()).getFullProductDetail(anyInt(), anyString());
        }

        @Test
        @DisplayName("Should throw ValidationException for zero ID")
        void getProductDetailForManager_ZeroId_ThrowsValidationException() throws Exception {
            // Given
            int invalidId = 0;
            String type = "book";

            // When & Then
            mockMvc.perform(get("/product/all-detail/{id}", invalidId)
                    .param("type", type)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(productService, never()).getFullProductDetail(anyInt(), anyString());
        }

        @Test
        @DisplayName("Should handle missing type parameter")
        void getProductDetailForManager_MissingTypeParameter_ReturnsBadRequest() throws Exception {
            // Given
            int productId = 123;

            // When & Then
            mockMvc.perform(get("/product/all-detail/{id}", productId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(productService, never()).getFullProductDetail(anyInt(), anyString());
        }

        @Test
        @DisplayName("Should handle service throwing ValidationException")
        void getProductDetailForManager_ServiceThrowsValidationException_ReturnsBadRequest() throws Exception {
            // Given
            int productId = 123;
            String type = "book";
            
            when(productService.getFullProductDetail(productId, type))
                .thenThrow(new ValidationException("Product not found"));

            // When & Then
            mockMvc.perform(get("/product/all-detail/{id}", productId)
                    .param("type", type)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(productService).getFullProductDetail(productId, type);
        }

        @Test
        @DisplayName("Should handle service throwing RuntimeException")
        void getProductDetailForManager_ServiceThrowsRuntimeException_ReturnsInternalServerError() throws Exception {
            // Given
            int productId = 123;
            String type = "book";
            
            when(productService.getFullProductDetail(productId, type))
                .thenThrow(new RuntimeException("Database connection failed"));

            // When & Then
            mockMvc.perform(get("/product/all-detail/{id}", productId)
                    .param("type", type)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());

            verify(productService).getFullProductDetail(productId, type);
        }

        @Test
        @DisplayName("Should handle invalid product type")
        void getProductDetailForManager_InvalidProductType_HandlesGracefully() throws Exception {
            // Given
            int productId = 123;
            String invalidType = "magazine";
            
            when(productService.getFullProductDetail(productId, invalidType))
                .thenThrow(new ValidationException("No repository found for product type: " + invalidType));

            // When & Then
            mockMvc.perform(get("/product/all-detail/{id}", productId)
                    .param("type", invalidType)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(productService).getFullProductDetail(productId, invalidType);
        }

        @Test
        @DisplayName("Should handle empty type parameter")
        void getProductDetailForManager_EmptyTypeParameter_HandlesGracefully() throws Exception {
            // Given
            int productId = 123;
            String emptyType = "";
            
            when(productService.getFullProductDetail(productId, emptyType))
                .thenThrow(new ValidationException("Product type is required"));

            // When & Then
            mockMvc.perform(get("/product/all-detail/{id}", productId)
                    .param("type", emptyType)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(productService).getFullProductDetail(productId, emptyType);
        }
    }

    @Nested
    @DisplayName("GET /product/detail/{id} - Customer Product Detail Tests")
    class GetProductDetailForCustomerTests {

        @Test
        @DisplayName("Should return basic product details for valid ID")
        void getProductDetailForCustomer_ValidId_ReturnsBasicProductDetail() throws Exception {
            // Given
            int productId = 123;
            Product mockProduct = createMockProduct(productId);
            
            when(productService.getBasicProductDetail(productId)).thenReturn(mockProduct);

            // When & Then
            mockMvc.perform(get("/product/detail/{id}", productId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.productId").value(productId))
                    .andExpect(jsonPath("$.title").value("Test Product"))
                    .andExpect(jsonPath("$.price").value(100000))
                    .andExpect(jsonPath("$.weight").value(0.5))
                    .andExpect(jsonPath("$.quantity").value(10))
                    .andExpect(jsonPath("$.rushOrderSupported").value(true))
                    .andExpect(jsonPath("$.imageUrl").value("http://example.com/image.jpg"))
                    .andExpect(jsonPath("$.barcode").value("1234567890"))
                    .andExpect(jsonPath("$.introduction").value("Test product introduction"));

            verify(productService).getBasicProductDetail(productId);
        }

        @Test
        @DisplayName("Should throw ValidationException for negative ID")
        void getProductDetailForCustomer_NegativeId_ThrowsValidationException() throws Exception {
            // Given
            int invalidId = -5;

            // When & Then
            mockMvc.perform(get("/product/detail/{id}", invalidId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(productService, never()).getBasicProductDetail(anyInt());
        }

        @Test
        @DisplayName("Should throw ValidationException for zero ID")
        void getProductDetailForCustomer_ZeroId_ThrowsValidationException() throws Exception {
            // Given
            int invalidId = 0;

            // When & Then
            mockMvc.perform(get("/product/detail/{id}", invalidId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(productService, never()).getBasicProductDetail(anyInt());
        }

        @Test
        @DisplayName("Should handle service throwing ValidationException")
        void getProductDetailForCustomer_ServiceThrowsValidationException_ReturnsBadRequest() throws Exception {
            // Given
            int productId = 999;
            
            when(productService.getBasicProductDetail(productId))
                .thenThrow(new ValidationException("Product not found"));

            // When & Then
            mockMvc.perform(get("/product/detail/{id}", productId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(productService).getBasicProductDetail(productId);
        }

        @Test
        @DisplayName("Should handle service throwing RuntimeException")
        void getProductDetailForCustomer_ServiceThrowsRuntimeException_ReturnsInternalServerError() throws Exception {
            // Given
            int productId = 123;
            
            when(productService.getBasicProductDetail(productId))
                .thenThrow(new RuntimeException("Database timeout"));

            // When & Then
            mockMvc.perform(get("/product/detail/{id}", productId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());

            verify(productService).getBasicProductDetail(productId);
        }

        @Test
        @DisplayName("Should handle large product ID")
        void getProductDetailForCustomer_LargeProductId_HandlesCorrectly() throws Exception {
            // Given
            int largeId = Integer.MAX_VALUE;
            Product mockProduct = createMockProduct(largeId);
            
            when(productService.getBasicProductDetail(largeId)).thenReturn(mockProduct);

            // When & Then
            mockMvc.perform(get("/product/detail/{id}", largeId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.productId").value(largeId));

            verify(productService).getBasicProductDetail(largeId);
        }

        @Test
        @DisplayName("Should handle product with null values gracefully")
        void getProductDetailForCustomer_ProductWithNullValues_HandlesGracefully() throws Exception {
            // Given
            int productId = 123;
            Product productWithNulls = new Product();
            productWithNulls.setProductId((long) productId);
            productWithNulls.setTitle("Basic Product");
            productWithNulls.setPrice(50000);
            // Other fields are null
            
            when(productService.getBasicProductDetail(productId)).thenReturn(productWithNulls);

            // When & Then
            mockMvc.perform(get("/product/detail/{id}", productId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.productId").value(productId))
                    .andExpect(jsonPath("$.title").value("Basic Product"))
                    .andExpect(jsonPath("$.price").value(50000));

            verify(productService).getBasicProductDetail(productId);
        }
    }

    @Nested
    @DisplayName("GET /product/all - All Products Tests")
    class GetAllProductsTests {

        @Test
        @DisplayName("Should return list of all products")
        void getAllProducts_ValidRequest_ReturnsProductList() throws Exception {
            // Given
            List<Product> mockProducts = Arrays.asList(
                createMockProduct(1),
                createMockProduct(2),
                createMockProduct(3)
            );
            
            when(productService.getAllProducts()).thenReturn(mockProducts);

            // When & Then
            mockMvc.perform(get("/product/all")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(3))
                    .andExpect(jsonPath("$[0].productId").value(1))
                    .andExpect(jsonPath("$[0].title").value("Test Product"))
                    .andExpect(jsonPath("$[1].productId").value(2))
                    .andExpect(jsonPath("$[2].productId").value(3));

            verify(productService).getAllProducts();
        }

        @Test
        @DisplayName("Should return empty list when no products exist")
        void getAllProducts_NoProducts_ReturnsEmptyList() throws Exception {
            // Given
            when(productService.getAllProducts()).thenReturn(Collections.emptyList());

            // When & Then
            mockMvc.perform(get("/product/all")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));

            verify(productService).getAllProducts();
        }

        @Test
        @DisplayName("Should handle large product list")
        void getAllProducts_LargeProductList_HandlesCorrectly() throws Exception {
            // Given
            List<Product> largeProductList = Arrays.asList(
                createMockProduct(1), createMockProduct(2), createMockProduct(3),
                createMockProduct(4), createMockProduct(5), createMockProduct(6),
                createMockProduct(7), createMockProduct(8), createMockProduct(9),
                createMockProduct(10)
            );
            
            when(productService.getAllProducts()).thenReturn(largeProductList);

            // When & Then
            mockMvc.perform(get("/product/all")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(10))
                    .andExpect(jsonPath("$[0].productId").value(1))
                    .andExpect(jsonPath("$[9].productId").value(10));

            verify(productService).getAllProducts();
        }

        @Test
        @DisplayName("Should handle service throwing ValidationException")
        void getAllProducts_ServiceThrowsValidationException_ReturnsBadRequest() throws Exception {
            // Given
            when(productService.getAllProducts())
                .thenThrow(new ValidationException("Database access denied"));

            // When & Then
            mockMvc.perform(get("/product/all")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(productService).getAllProducts();
        }

        @Test
        @DisplayName("Should handle service throwing RuntimeException")
        void getAllProducts_ServiceThrowsRuntimeException_ReturnsInternalServerError() throws Exception {
            // Given
            when(productService.getAllProducts())
                .thenThrow(new RuntimeException("Database connection failed"));

            // When & Then
            mockMvc.perform(get("/product/all")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());

            verify(productService).getAllProducts();
        }

        @Test
        @DisplayName("Should return mixed product types")
        void getAllProducts_MixedProductTypes_ReturnsAllTypes() throws Exception {
            // Given
            List<Product> mixedProducts = Arrays.asList(
                createMockBook(1),
                createMockCD(2),
                createMockDVD(3),
                createMockProduct(4)
            );
            
            when(productService.getAllProducts()).thenReturn(mixedProducts);

            // When & Then
            mockMvc.perform(get("/product/all")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(4))
                    .andExpect(jsonPath("$[0].type").value("book"))
                    .andExpect(jsonPath("$[1].type").value("cd"))
                    .andExpect(jsonPath("$[2].type").value("dvd"));

            verify(productService).getAllProducts();
        }

        @Test
        @DisplayName("Should handle null product in list gracefully")
        void getAllProducts_ListContainsNull_FiltersOutNull() throws Exception {
            // Given
            List<Product> productsWithNull = Arrays.asList(
                createMockProduct(1),
                null,
                createMockProduct(3)
            );
            
            when(productService.getAllProducts()).thenReturn(productsWithNull);

            // When & Then
            mockMvc.perform(get("/product/all")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(3))
                    .andExpect(jsonPath("$[0].productId").value(1))
                    .andExpect(jsonPath("$[1]").doesNotExist())
                    .andExpect(jsonPath("$[2].productId").value(3));

            verify(productService).getAllProducts();
        }
    }

    // Helper methods for creating mock objects
    private Book createMockBook(int productId) {
        Book book = new Book();
        book.setProductId((long) productId);
        book.setTitle("Test Book");
        book.setPrice(100000);
        book.setWeight(0.5f);
        book.setQuantity(10);
        book.setRushOrderSupported(true);
        book.setImageUrl("http://example.com/book.jpg");
        book.setBarcode("1234567890");
        book.setImportDate("2025-01-01");
        book.setIntroduction("Test book introduction");
        book.setType("book");
        book.setBookId((long) productId);
        book.setAuthors("Test Author");
        book.setGenre("Fiction");
        book.setPageCount(300);
        book.setPublishers("Test Publisher");
        book.setPublicationDate("2025-01-01");
        book.setCoverType("Paperback");
        return book;
    }

    private CD createMockCD(int productId) {
        CD cd = new CD();
        cd.setProductId((long) productId);
        cd.setTitle("Test CD");
        cd.setPrice(200000);
        cd.setWeight(0.2f);
        cd.setQuantity(5);
        cd.setRushOrderSupported(false);
        cd.setImageUrl("http://example.com/cd.jpg");
        cd.setBarcode("0987654321");
        cd.setImportDate("2025-01-01");
        cd.setIntroduction("Test CD introduction");
        cd.setType("cd");
        cd.setCdId((long) productId);
        cd.setArtists("Test Artist");
        cd.setGenre("Rock");
        cd.setTrackList("Track 1, Track 2, Track 3");
        cd.setRecordLabel("Test Label");
        cd.setReleaseDate("2025-01-01");
        return cd;
    }

    private DVD createMockDVD(int productId) {
        DVD dvd = new DVD();
        dvd.setProductId((long) productId);
        dvd.setTitle("Test DVD");
        dvd.setPrice(300000);
        dvd.setWeight(0.3f);
        dvd.setQuantity(8);
        dvd.setRushOrderSupported(true);
        dvd.setImageUrl("http://example.com/dvd.jpg");
        dvd.setBarcode("1122334455");
        dvd.setImportDate("2025-01-01");
        dvd.setIntroduction("Test DVD introduction");
        dvd.setType("dvd");
        dvd.setDvdId((long) productId);
        dvd.setDirector("Test Director");
        dvd.setGenre("Action");
        dvd.setStudio("Test Studio");
        dvd.setDvdType("Blu-ray");
        dvd.setReleaseDate("2025-01-01");
        return dvd;
    }

    private Product createMockProduct(int productId) {
        Product product = new Product();
        product.setProductId((long) productId);
        product.setTitle("Test Product");
        product.setPrice(100000);
        product.setWeight(0.5f);
        product.setQuantity(10);
        product.setRushOrderSupported(true);
        product.setImageUrl("http://example.com/image.jpg");
        product.setBarcode("1234567890");
        product.setImportDate("2025-01-01");
        product.setIntroduction("Test product introduction");
        product.setType("product");
        return product;
    }
}