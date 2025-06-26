package Project_ITSS.controller;

import Project_ITSS.common.entity.Product;
import Project_ITSS.common.entity.Book;
import Project_ITSS.common.entity.CD;
import Project_ITSS.common.entity.DVD;
import Project_ITSS.service.ProductUpdateService;
import Project_ITSS.service.LoggerUpdateService;
import Project_ITSS.common.validation.ValidationException;
import Project_ITSS.dto.ProductResponse;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductUpdateController Unit Tests")
class ProductUpdateControllerTest {

    @Mock
    private ProductUpdateService productService;

    @Mock
    private LoggerUpdateService loggerService;

    @InjectMocks
    private ProductUpdateController productUpdateController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productUpdateController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("GET /updating/available - Check Valid Updating Tests")
    class CheckValidUpdatingTests {

        @Test
        @DisplayName("Should return true when updating is available")
        void checkValidUpdating_UpdatingAvailable_ReturnsTrue() throws Exception {
            // Given
            when(loggerService.checkValidUpdateProducts()).thenReturn(true);

            // When & Then
            mockMvc.perform(get("/updating/available")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));

            verify(loggerService).checkValidUpdateProducts();
        }

        @Test
        @DisplayName("Should return false when updating is not available")
        void checkValidUpdating_UpdatingNotAvailable_ReturnsFalse() throws Exception {
            // Given
            when(loggerService.checkValidUpdateProducts()).thenReturn(false);

            // When & Then
            mockMvc.perform(get("/updating/available")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().string("false"));

            verify(loggerService).checkValidUpdateProducts();
        }

        @Test
        @DisplayName("Should handle logger service throwing RuntimeException")
        void checkValidUpdating_LoggerServiceThrowsRuntimeException_ReturnsInternalServerError() throws Exception {
            // Given
            when(loggerService.checkValidUpdateProducts())
                .thenThrow(new RuntimeException("Logger database connection failed"));

            // When & Then
            mockMvc.perform(get("/updating/available")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());

            verify(loggerService).checkValidUpdateProducts();
        }

        @Test
        @DisplayName("Should handle logger service throwing ValidationException")
        void checkValidUpdating_LoggerServiceThrowsValidationException_ReturnsBadRequest() throws Exception {
            // Given
            when(loggerService.checkValidUpdateProducts())
                .thenThrow(new ValidationException("Failed to check update products validity"));

            // When & Then
            mockMvc.perform(get("/updating/available")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(loggerService).checkValidUpdateProducts();
        }

        @Test
        @DisplayName("Should handle multiple concurrent availability checks")
        void checkValidUpdating_MultipleConcurrentChecks_HandlesCorrectly() throws Exception {
            // Given
            when(loggerService.checkValidUpdateProducts()).thenReturn(true);

            // When & Then - Simulate multiple concurrent requests
            for (int i = 0; i < 5; i++) {
                mockMvc.perform(get("/updating/available")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().string("true"));
            }

            verify(loggerService, times(5)).checkValidUpdateProducts();
        }

        @Test
        @DisplayName("Should handle request without content type")
        void checkValidUpdating_NoContentType_ReturnsCorrectly() throws Exception {
            // Given
            when(loggerService.checkValidUpdateProducts()).thenReturn(false);

            // When & Then
            mockMvc.perform(get("/updating/available"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("false"));

            verify(loggerService).checkValidUpdateProducts();
        }
    }

    @Nested
    @DisplayName("GET /UpdatingRequested - Request to Update Product Tests")
    class RequestToUpdateProductTests {

        @Test
        @DisplayName("Should return success message for updating request")
        void requestToUpdateProduct_ValidRequest_ReturnsSuccessMessage() throws Exception {
            // When & Then
            mockMvc.perform(get("/UpdatingRequested")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Product update request received"));
        }

        @Test
        @DisplayName("Should handle multiple concurrent requests")
        void requestToUpdateProduct_MultipleConcurrentRequests_HandlesCorrectly() throws Exception {
            // When & Then - Simulate multiple requests
            for (int i = 0; i < 5; i++) {
                mockMvc.perform(get("/UpdatingRequested")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().string("Product update request received"));
            }
        }

        @Test
        @DisplayName("Should handle request without content type")
        void requestToUpdateProduct_NoContentType_ReturnsSuccessMessage() throws Exception {
            // When & Then
            mockMvc.perform(get("/UpdatingRequested"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Product update request received"));
        }
    }

    @Nested
    @DisplayName("POST /updating/ProductInfo - Update Product Info Tests")
    class UpdateProductInfoTests {

        @Nested
        @DisplayName("Book Product Update Tests")
        class BookProductUpdateTests {

            @Test
            @DisplayName("Should update valid book successfully")
            void updateProductInfo_ValidBook_ReturnsSuccessResponse() throws Exception {
                // Given
                Book book = createValidBookForUpdate();
                int productId = 123;
                
                when(productService.updateProduct(any(Product.class))).thenReturn(productId);
                doNothing().when(loggerService).saveLogger(any(Product.class));

                // When & Then
                mockMvc.perform(post("/updating/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.status").value(1))
                        .andExpect(jsonPath("$.message").value("Product added successfully"))
                        .andExpect(jsonPath("$.product_id").value(productId))
                        .andExpect(jsonPath("$.errorCode").doesNotExist())
                        .andExpect(jsonPath("$.error").doesNotExist());

                verify(productService).updateProduct(any(Product.class));
                verify(loggerService).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle book with missing product ID")
            void updateProductInfo_BookMissingProductId_ThrowsValidationException() throws Exception {
                // Given
                Book book = createValidBookForUpdate();
                book.setProductId(0); // Invalid ID for update
                
                when(productService.updateProduct(any(Product.class)))
                    .thenThrow(new ValidationException("Product ID is required for update"));

                // When & Then
                mockMvc.perform(post("/updating/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                        .andExpect(status().isBadRequest());

                verify(productService).updateProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle book with negative product ID")
            void updateProductInfo_BookNegativeProductId_ThrowsValidationException() throws Exception {
                // Given
                Book book = createValidBookForUpdate();
                book.setProductId(-1);
                
                when(productService.updateProduct(any(Product.class)))
                    .thenThrow(new ValidationException("Product ID is required for update"));

                // When & Then
                mockMvc.perform(post("/updating/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                        .andExpect(status().isBadRequest());

                verify(productService).updateProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle book with missing title")
            void updateProductInfo_BookMissingTitle_ThrowsValidationException() throws Exception {
                // Given
                Book book = createValidBookForUpdate();
                book.setTitle(null);
                
                when(productService.updateProduct(any(Product.class)))
                    .thenThrow(new ValidationException("Product title is required"));

                // When & Then
                mockMvc.perform(post("/updating/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                        .andExpect(status().isBadRequest());

                verify(productService).updateProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle book with missing book ID")
            void updateProductInfo_BookMissingBookId_ThrowsValidationException() throws Exception {
                // Given
                Book book = createValidBookForUpdate();
                book.setBookId(0);
                
                when(productService.updateProduct(any(Product.class)))
                    .thenThrow(new ValidationException("Book ID is required for update"));

                // When & Then
                mockMvc.perform(post("/updating/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                        .andExpect(status().isBadRequest());

                verify(productService).updateProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle book with invalid authors")
            void updateProductInfo_BookInvalidAuthors_ThrowsValidationException() throws Exception {
                // Given
                Book book = createValidBookForUpdate();
                book.setAuthors("");
                
                when(productService.updateProduct(any(Product.class)))
                    .thenThrow(new ValidationException("Book authors are required"));

                // When & Then
                mockMvc.perform(post("/updating/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                        .andExpect(status().isBadRequest());

                verify(productService).updateProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle book with invalid page count")
            void updateProductInfo_BookInvalidPageCount_ThrowsValidationException() throws Exception {
                // Given
                Book book = createValidBookForUpdate();
                book.setPageCount(-10);
                
                when(productService.updateProduct(any(Product.class)))
                    .thenThrow(new ValidationException("Book page count must be greater than 0"));

                // When & Then
                mockMvc.perform(post("/updating/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                        .andExpect(status().isBadRequest());

                verify(productService).updateProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle book with zero page count")
            void updateProductInfo_BookZeroPageCount_ThrowsValidationException() throws Exception {
                // Given
                Book book = createValidBookForUpdate();
                book.setPageCount(0);
                
                when(productService.updateProduct(any(Product.class)))
                    .thenThrow(new ValidationException("Book page count must be greater than 0"));

                // When & Then
                mockMvc.perform(post("/updating/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                        .andExpect(status().isBadRequest());

                verify(productService).updateProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }
        }

        @Nested
        @DisplayName("CD Product Update Tests")
        class CDProductUpdateTests {

            @Test
            @DisplayName("Should update valid CD successfully")
            void updateProductInfo_ValidCD_ReturnsSuccessResponse() throws Exception {
                // Given
                CD cd = createValidCDForUpdate();
                int productId = 124;
                
                when(productService.updateProduct(any(Product.class))).thenReturn(productId);
                doNothing().when(loggerService).saveLogger(any(Product.class));

                // When & Then
                mockMvc.perform(post("/updating/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cd)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.status").value(1))
                        .andExpect(jsonPath("$.message").value("Product added successfully"))
                        .andExpect(jsonPath("$.product_id").value(productId));

                verify(productService).updateProduct(any(Product.class));
                verify(loggerService).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle CD with missing CD ID")
            void updateProductInfo_CDMissingCdId_ThrowsValidationException() throws Exception {
                // Given
                CD cd = createValidCDForUpdate();
                cd.setCdId(0);
                
                when(productService.updateProduct(any(Product.class)))
                    .thenThrow(new ValidationException("CD ID is required for update"));

                // When & Then
                mockMvc.perform(post("/updating/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cd)))
                        .andExpect(status().isBadRequest());

                verify(productService).updateProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle CD with missing artists")
            void updateProductInfo_CDMissingArtists_ThrowsValidationException() throws Exception {
                // Given
                CD cd = createValidCDForUpdate();
                cd.setArtists(null);
                
                when(productService.updateProduct(any(Product.class)))
                    .thenThrow(new ValidationException("CD artists are required"));

                // When & Then
                mockMvc.perform(post("/updating/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cd)))
                        .andExpect(status().isBadRequest());

                verify(productService).updateProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle CD with empty track list")
            void updateProductInfo_CDEmptyTrackList_ThrowsValidationException() throws Exception {
                // Given
                CD cd = createValidCDForUpdate();
                cd.setTrackList("");
                
                when(productService.updateProduct(any(Product.class)))
                    .thenThrow(new ValidationException("CD track list is required"));

                // When & Then
                mockMvc.perform(post("/updating/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cd)))
                        .andExpect(status().isBadRequest());

                verify(productService).updateProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle CD with missing record label")
            void updateProductInfo_CDMissingRecordLabel_ThrowsValidationException() throws Exception {
                // Given
                CD cd = createValidCDForUpdate();
                cd.setRecordLabel(null);
                
                when(productService.updateProduct(any(Product.class)))
                    .thenThrow(new ValidationException("CD record label is required"));

                // When & Then
                mockMvc.perform(post("/updating/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cd)))
                        .andExpect(status().isBadRequest());

                verify(productService).updateProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle CD with missing genre")
            void updateProductInfo_CDMissingGenre_ThrowsValidationException() throws Exception {
                // Given
                CD cd = createValidCDForUpdate();
                cd.setGenre(null);
                
                when(productService.updateProduct(any(Product.class)))
                    .thenThrow(new ValidationException("CD genre is required"));

                // When & Then
                mockMvc.perform(post("/updating/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cd)))
                        .andExpect(status().isBadRequest());

                verify(productService).updateProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }
        }

        @Nested
        @DisplayName("DVD Product Update Tests")
        class DVDProductUpdateTests {

            @Test
            @DisplayName("Should update valid DVD successfully")
            void updateProductInfo_ValidDVD_ReturnsSuccessResponse() throws Exception {
                // Given
                DVD dvd = createValidDVDForUpdate();
                int productId = 125;
                
                when(productService.updateProduct(any(Product.class))).thenReturn(productId);
                doNothing().when(loggerService).saveLogger(any(Product.class));

                // When & Then
                mockMvc.perform(post("/updating/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dvd)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.status").value(1))
                        .andExpect(jsonPath("$.message").value("Product added successfully"))
                        .andExpect(jsonPath("$.product_id").value(productId));

                verify(productService).updateProduct(any(Product.class));
                verify(loggerService).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle DVD with missing DVD ID")
            void updateProductInfo_DVDMissingDvdId_ThrowsValidationException() throws Exception {
                // Given
                DVD dvd = createValidDVDForUpdate();
                dvd.setDvdId(0);
                
                when(productService.updateProduct(any(Product.class)))
                    .thenThrow(new ValidationException("DVD ID is required for update"));

                // When & Then
                mockMvc.perform(post("/updating/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dvd)))
                        .andExpect(status().isBadRequest());

                verify(productService).updateProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle DVD with missing director")
            void updateProductInfo_DVDMissingDirector_ThrowsValidationException() throws Exception {
                // Given
                DVD dvd = createValidDVDForUpdate();
                dvd.setDirector(null);
                
                when(productService.updateProduct(any(Product.class)))
                    .thenThrow(new ValidationException("DVD director is required"));

                // When & Then
                mockMvc.perform(post("/updating/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dvd)))
                        .andExpect(status().isBadRequest());

                verify(productService).updateProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle DVD with empty studio")
            void updateProductInfo_DVDEmptyStudio_ThrowsValidationException() throws Exception {
                // Given
                DVD dvd = createValidDVDForUpdate();
                dvd.setStudio("");
                
                when(productService.updateProduct(any(Product.class)))
                    .thenThrow(new ValidationException("DVD studio is required"));

                // When & Then
                mockMvc.perform(post("/updating/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dvd)))
                        .andExpect(status().isBadRequest());

                verify(productService).updateProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle DVD with missing DVD type")
            void updateProductInfo_DVDMissingType_ThrowsValidationException() throws Exception {
                // Given
                DVD dvd = createValidDVDForUpdate();
                dvd.setDvdType(null);
                
                when(productService.updateProduct(any(Product.class)))
                    .thenThrow(new ValidationException("DVD type is required"));

                // When & Then
                mockMvc.perform(post("/updating/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dvd)))
                        .andExpect(status().isBadRequest());

                verify(productService).updateProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle DVD with missing genre")
            void updateProductInfo_DVDMissingGenre_ThrowsValidationException() throws Exception {
                // Given
                DVD dvd = createValidDVDForUpdate();
                dvd.setGenre(null);
                
                when(productService.updateProduct(any(Product.class)))
                    .thenThrow(new ValidationException("DVD genre is required"));

                // When & Then
                mockMvc.perform(post("/updating/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dvd)))
                        .andExpect(status().isBadRequest());

                verify(productService).updateProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }
        }

        @Nested
        @DisplayName("Common Product Update Validation Tests")
        class CommonProductUpdateValidationTests {

            @Test
            @DisplayName("Should handle null product")
            void updateProductInfo_NullProduct_ThrowsValidationException() throws Exception {
                // When & Then
                mockMvc.perform(post("/updating/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("null"))
                        .andExpect(status().isBadRequest());

                verify(productService, never()).updateProduct(any());
                verify(loggerService, never()).saveLogger(any());
            }

            @Test
            @DisplayName("Should handle product with negative price")
            void updateProductInfo_NegativePrice_ThrowsValidationException() throws Exception {
                // Given
                Book book = createValidBookForUpdate();
                book.setPrice(-1000);
                
                when(productService.updateProduct(any(Product.class)))
                    .thenThrow(new ValidationException("Product price must be greater than 0"));

                // When & Then
                mockMvc.perform(post("/updating/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                        .andExpect(status().isBadRequest());

                verify(productService).updateProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle product with zero price")
            void updateProductInfo_ZeroPrice_ThrowsValidationException() throws Exception {
                // Given
                Book book = createValidBookForUpdate();
                book.setPrice(0);
                
                when(productService.updateProduct(any(Product.class)))
                    .thenThrow(new ValidationException("Product price must be greater than 0"));

                // When & Then
                mockMvc.perform(post("/updating/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                        .andExpect(status().isBadRequest());

                verify(productService).updateProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle product with negative weight")
            void updateProductInfo_NegativeWeight_ThrowsValidationException() throws Exception {
                // Given
                Book book = createValidBookForUpdate();
                book.setWeight(-0.5f);
                
                when(productService.updateProduct(any(Product.class)))
                    .thenThrow(new ValidationException("Product weight must be greater than 0"));

                // When & Then
                mockMvc.perform(post("/updating/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                        .andExpect(status().isBadRequest());

                verify(productService).updateProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle product with negative quantity")
            void updateProductInfo_NegativeQuantity_ThrowsValidationException() throws Exception {
                // Given
                Book book = createValidBookForUpdate();
                book.setQuantity(-5);
                
                when(productService.updateProduct(any(Product.class)))
                    .thenThrow(new ValidationException("Product quantity cannot be negative"));

                // When & Then
                mockMvc.perform(post("/updating/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                        .andExpect(status().isBadRequest());

                verify(productService).updateProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle product with missing type")
            void updateProductInfo_MissingType_ThrowsValidationException() throws Exception {
                // Given
                Book book = createValidBookForUpdate();
                book.setType(null);
                
                when(productService.updateProduct(any(Product.class)))
                    .thenThrow(new ValidationException("Product type is required"));

                // When & Then
                mockMvc.perform(post("/updating/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                        .andExpect(status().isBadRequest());

                verify(productService).updateProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle product with invalid type")
            void updateProductInfo_InvalidType_ThrowsValidationException() throws Exception {
                // Given
                Book book = createValidBookForUpdate();
                book.setType("magazine");
                
                when(productService.updateProduct(any(Product.class)))
                    .thenThrow(new ValidationException("Invalid product type: magazine"));

                // When & Then
                mockMvc.perform(post("/updating/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                        .andExpect(status().isBadRequest());

                verify(productService).updateProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle empty title")
            void updateProductInfo_EmptyTitle_ThrowsValidationException() throws Exception {
                // Given
                Book book = createValidBookForUpdate();
                book.setTitle("");
                
                when(productService.updateProduct(any(Product.class)))
                    .thenThrow(new ValidationException("Product title is required"));

                // When & Then
                mockMvc.perform(post("/updating/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                        .andExpect(status().isBadRequest());

                verify(productService).updateProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle whitespace-only title")
            void updateProductInfo_WhitespaceTitle_ThrowsValidationException() throws Exception {
                // Given
                Book book = createValidBookForUpdate();
                book.setTitle("   ");
                
                when(productService.updateProduct(any(Product.class)))
                    .thenThrow(new ValidationException("Product title is required"));

                // When & Then
                mockMvc.perform(post("/updating/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                        .andExpect(status().isBadRequest());

                verify(productService).updateProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle product not found during update")
            void updateProductInfo_ProductNotFound_ThrowsValidationException() throws Exception {
                // Given
                Book book = createValidBookForUpdate();
                book.setProductId(999); // Non-existent product ID
                
                when(productService.updateProduct(any(Product.class)))
                    .thenThrow(new ValidationException("No product found with ID: 999"));

                // When & Then
                mockMvc.perform(post("/updating/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                        .andExpect(status().isBadRequest());

                verify(productService).updateProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }
        }

        @Nested
        @DisplayName("Service Exception Handling Tests")
        class ServiceExceptionHandlingTests {

            @Test
            @DisplayName("Should handle service throwing RuntimeException")
            void updateProductInfo_ServiceThrowsRuntimeException_ReturnsInternalServerError() throws Exception {
                // Given
                Book book = createValidBookForUpdate();
                
                when(productService.updateProduct(any(Product.class)))
                    .thenThrow(new RuntimeException("Database connection failed"));

                // When & Then
                mockMvc.perform(post("/updating/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                        .andExpect(status().isInternalServerError());

                verify(productService).updateProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle logger service throwing exception but still return success")
            void updateProductInfo_LoggerServiceThrowsException_StillReturnsSuccess() throws Exception {
                // Given
                Book book = createValidBookForUpdate();
                int productId = 127;
                
                when(productService.updateProduct(any(Product.class))).thenReturn(productId);
                doThrow(new RuntimeException("Logger service failed"))
                    .when(loggerService).saveLogger(any(Product.class));

                // When & Then - Should still return success even if logger fails
                mockMvc.perform(post("/updating/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.status").value(1))
                        .andExpect(jsonPath("$.product_id").value(productId));

                verify(productService).updateProduct(any(Product.class));
                verify(loggerService).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle unexpected ValidationException message")
            void updateProductInfo_UnexpectedValidationException_ReturnsBadRequest() throws Exception {
                // Given
                Book book = createValidBookForUpdate();
                
                when(productService.updateProduct(any(Product.class)))
                    .thenThrow(new ValidationException("Unexpected validation error occurred during update"));

                // When & Then
                mockMvc.perform(post("/updating/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                        .andExpect(status().isBadRequest());

                verify(productService).updateProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle concurrent modification exception")
            void updateProductInfo_ConcurrentModificationException_ReturnsInternalServerError() throws Exception {
                // Given
                Book book = createValidBookForUpdate();
                
                when(productService.updateProduct(any(Product.class)))
                    .thenThrow(new RuntimeException("Optimistic locking failed - product was modified by another user"));

                // When & Then
                mockMvc.perform(post("/updating/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                        .andExpect(status().isInternalServerError());

                verify(productService).updateProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }
        }

        @Nested
        @DisplayName("JSON Parsing Tests")
        class JsonParsingTests {

            @Test
            @DisplayName("Should handle malformed JSON")
            void updateProductInfo_MalformedJson_ReturnsBadRequest() throws Exception {
                // When & Then
                mockMvc.perform(post("/updating/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json"))
                        .andExpect(status().isBadRequest());

                verify(productService, never()).updateProduct(any());
                verify(loggerService, never()).saveLogger(any());
            }

            @Test
            @DisplayName("Should handle empty JSON object")
            void updateProductInfo_EmptyJsonObject_HandlesGracefully() throws Exception {
                // When & Then - Empty JSON object will fail JSON parsing due to missing type discriminator
                mockMvc.perform(post("/updating/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                        .andExpect(status().isBadRequest());

                // Service should not be called due to JSON parsing failure
                verify(productService, never()).updateProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any());
            }

            @Test
            @DisplayName("Should handle JSON with extra unknown fields")
            void updateProductInfo_JsonWithExtraFields_IgnoresExtraFields() throws Exception {
                // Given
                String jsonWithExtraFields = """
                    {
                        "productId": 123,
                        "title": "Updated Test Book",
                        "price": 150000,
                        "weight": 0.6,
                        "quantity": 15,
                        "type": "book",
                        "bookId": 123,
                        "authors": "Updated Test Author",
                        "genre": "Updated Fiction",
                        "pageCount": 350,
                        "publishers": "Updated Test Publisher",
                        "publicationDate": "2025-01-15",
                        "coverType": "Hardcover",
                        "extraField": "should be ignored",
                        "anotherExtraField": 999
                    }
                    """;
                int productId = 128;
                
                when(productService.updateProduct(any(Product.class))).thenReturn(productId);
                doNothing().when(loggerService).saveLogger(any(Product.class));

                // When & Then
                mockMvc.perform(post("/updating/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithExtraFields))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.status").value(1))
                        .andExpect(jsonPath("$.product_id").value(productId));

                verify(productService).updateProduct(any(Product.class));
                verify(loggerService).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle JSON with missing required fields")
            void updateProductInfo_JsonMissingRequiredFields_HandlesGracefully() throws Exception {
                // Given
                String incompleteJson = """
                    {
                        "title": "Incomplete Book",
                        "type": "book"
                    }
                    """;

                when(productService.updateProduct(any(Product.class)))
                    .thenThrow(new ValidationException("Product ID is required for update"));

                // When & Then
                mockMvc.perform(post("/updating/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(incompleteJson))
                        .andExpect(status().isBadRequest());

                verify(productService).updateProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }
        }
    }

    @Nested
    @DisplayName("Integration Flow Tests")
    class IntegrationFlowTests {

        @Test
        @DisplayName("Should handle complete product update flow")
        void completeProductUpdateFlow_ValidProduct_Success() throws Exception {
            // Step 1: Check availability
            when(loggerService.checkValidUpdateProducts()).thenReturn(true);
            
            mockMvc.perform(get("/updating/available"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));

            // Step 2: Request to update product
            mockMvc.perform(get("/UpdatingRequested"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Product update request received"));

            // Step 3: Submit product update info
            Book book = createValidBookForUpdate();
            int productId = 129;
            
            when(productService.updateProduct(any(Product.class))).thenReturn(productId);
            doNothing().when(loggerService).saveLogger(any(Product.class));

            mockMvc.perform(post("/updating/ProductInfo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(book)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(1))
                    .andExpect(jsonPath("$.product_id").value(productId));

            // Verify all service calls
            verify(loggerService).checkValidUpdateProducts();
            verify(productService).updateProduct(any(Product.class));
            verify(loggerService).saveLogger(any(Product.class));
        }

        @Test
        @DisplayName("Should handle flow when updating not available")
        void completeProductUpdateFlow_UpdatingNotAvailable_ReturnsUnavailable() throws Exception {
            // Given
            when(loggerService.checkValidUpdateProducts()).thenReturn(false);

            // When & Then
            mockMvc.perform(get("/updating/available"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("false"));

            verify(loggerService).checkValidUpdateProducts();
            // Should not proceed to update product when not available
        }

        @Test
        @DisplayName("Should handle update flow with validation failure")
        void completeProductUpdateFlow_ValidationFailure_StopsFlow() throws Exception {
            // Step 1: Check availability - success
            when(loggerService.checkValidUpdateProducts()).thenReturn(true);
            
            mockMvc.perform(get("/updating/available"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));

            // Step 2: Try to update with invalid product - failure
            Book invalidBook = createValidBookForUpdate();
            invalidBook.setPrice(-100); // Invalid price
            
            when(productService.updateProduct(any(Product.class)))
                .thenThrow(new ValidationException("Product price must be greater than 0"));

            mockMvc.perform(post("/updating/ProductInfo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidBook)))
                    .andExpect(status().isBadRequest());

            // Verify that logger was not called for failed update
            verify(loggerService).checkValidUpdateProducts();
            verify(productService).updateProduct(any(Product.class));
            verify(loggerService, never()).saveLogger(any(Product.class));
        }
    }

    @Nested
    @DisplayName("Edge Cases and Boundary Tests")
    class EdgeCasesAndBoundaryTests {

        @Test
        @DisplayName("Should handle product with maximum integer values")
        void updateProductInfo_MaximumValues_HandlesCorrectly() throws Exception {
            // Given
            Book book = createValidBookForUpdate();
            book.setPrice(Integer.MAX_VALUE);
            book.setQuantity(Integer.MAX_VALUE);
            book.setPageCount(Integer.MAX_VALUE);
            book.setProductId(Long.MAX_VALUE);
            book.setBookId(Long.MAX_VALUE);
            int productId = 130;
            
            when(productService.updateProduct(any(Product.class))).thenReturn(productId);
            doNothing().when(loggerService).saveLogger(any(Product.class));

            // When & Then
            mockMvc.perform(post("/updating/ProductInfo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(book)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(1))
                    .andExpect(jsonPath("$.product_id").value(productId));

            verify(productService).updateProduct(any(Product.class));
            verify(loggerService).saveLogger(any(Product.class));
        }

        @Test
        @DisplayName("Should handle product with very long title")
        void updateProductInfo_VeryLongTitle_HandlesCorrectly() throws Exception {
            // Given
            Book book = createValidBookForUpdate();
            String longTitle = "Updated " + "A".repeat(1000); // Very long title
            book.setTitle(longTitle);
            int productId = 131;
            
            when(productService.updateProduct(any(Product.class))).thenReturn(productId);
            doNothing().when(loggerService).saveLogger(any(Product.class));

            // When & Then
            mockMvc.perform(post("/updating/ProductInfo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(book)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(1))
                    .andExpect(jsonPath("$.product_id").value(productId));

            verify(productService).updateProduct(any(Product.class));
            verify(loggerService).saveLogger(any(Product.class));
        }

        @Test
        @DisplayName("Should handle product with special characters in fields")
        void updateProductInfo_SpecialCharacters_HandlesCorrectly() throws Exception {
            // Given
            Book book = createValidBookForUpdate();
            book.setTitle("Updated Book with Special Characters: àáâãäåæçèéêë");
            book.setAuthors("Updated Tác giả: Nguyễn Văn A & Company");
            book.setGenre("Updated Fiction/Fantasy & Science-Fiction");
            int productId = 132;
            
            when(productService.updateProduct(any(Product.class))).thenReturn(productId);
            doNothing().when(loggerService).saveLogger(any(Product.class));

            // When & Then
            mockMvc.perform(post("/updating/ProductInfo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(book)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(1))
                    .andExpect(jsonPath("$.product_id").value(productId));

            verify(productService).updateProduct(any(Product.class));
            verify(loggerService).saveLogger(any(Product.class));
        }

        @Test
        @DisplayName("Should handle zero quantity product update")
        void updateProductInfo_ZeroQuantity_HandlesCorrectly() throws Exception {
            // Given
            Book book = createValidBookForUpdate();
            book.setQuantity(0); // Zero quantity should be allowed for updates
            int productId = 133;
            
            when(productService.updateProduct(any(Product.class))).thenReturn(productId);
            doNothing().when(loggerService).saveLogger(any(Product.class));

            // When & Then
            mockMvc.perform(post("/updating/ProductInfo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(book)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(1))
                    .andExpect(jsonPath("$.product_id").value(productId));

            verify(productService).updateProduct(any(Product.class));
            verify(loggerService).saveLogger(any(Product.class));
        }

        @Test
        @DisplayName("Should handle very small weight values")
        void updateProductInfo_VerySmallWeight_HandlesCorrectly() throws Exception {
            // Given
            Book book = createValidBookForUpdate();
            book.setWeight(0.001f); // Very small weight
            int productId = 134;
            
            when(productService.updateProduct(any(Product.class))).thenReturn(productId);
            doNothing().when(loggerService).saveLogger(any(Product.class));

            // When & Then
            mockMvc.perform(post("/updating/ProductInfo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(book)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(1))
                    .andExpect(jsonPath("$.product_id").value(productId));

            verify(productService).updateProduct(any(Product.class));
            verify(loggerService).saveLogger(any(Product.class));
        }

        @Test
        @DisplayName("Should handle product update with changed product type")
        void updateProductInfo_ChangedProductType_ThrowsValidationException() throws Exception {
            // Given
            Book book = createValidBookForUpdate();
            book.setType("cd"); // Changing type from book to cd should not be allowed
            
            when(productService.updateProduct(any(Product.class)))
                .thenThrow(new ValidationException("Product type cannot be changed during update"));

            // When & Then
            mockMvc.perform(post("/updating/ProductInfo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(book)))
                    .andExpect(status().isBadRequest());

            verify(productService).updateProduct(any(Product.class));
            verify(loggerService, never()).saveLogger(any(Product.class));
        }

        @Test
        @DisplayName("Should handle concurrent update attempts")
        void updateProductInfo_ConcurrentUpdates_HandlesCorrectly() throws Exception {
            // Given
            Book book = createValidBookForUpdate();
            int productId = 135;
            
            when(productService.updateProduct(any(Product.class))).thenReturn(productId);
            doNothing().when(loggerService).saveLogger(any(Product.class));

            // When & Then - Simulate concurrent update requests
            for (int i = 0; i < 3; i++) {
                mockMvc.perform(post("/updating/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.status").value(1))
                        .andExpect(jsonPath("$.product_id").value(productId));
            }

            verify(productService, times(3)).updateProduct(any(Product.class));
            verify(loggerService, times(3)).saveLogger(any(Product.class));
        }
    }

    @Nested
    @DisplayName("Performance and Load Tests")
    class PerformanceAndLoadTests {

        @Test
        @DisplayName("Should handle large product data efficiently")
        void updateProductInfo_LargeProductData_HandlesEfficiently() throws Exception {
            // Given
            Book book = createValidBookForUpdate();
            book.setIntroduction("A".repeat(10000)); // Large introduction text
            book.setAuthors("Author1, Author2, Author3, " + "Co-Author ".repeat(100));
            int productId = 136;
            
            when(productService.updateProduct(any(Product.class))).thenReturn(productId);
            doNothing().when(loggerService).saveLogger(any(Product.class));

            // When & Then
            long startTime = System.currentTimeMillis();
            
            mockMvc.perform(post("/updating/ProductInfo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(book)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(1))
                    .andExpect(jsonPath("$.product_id").value(productId));
            
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            
            // Should complete within reasonable time (adjust threshold as needed)
            assert executionTime < 5000; // 5 seconds threshold
            
            verify(productService).updateProduct(any(Product.class));
            verify(loggerService).saveLogger(any(Product.class));
        }
    }

    // Helper methods for creating test data
    private Book createValidBookForUpdate() {
        Book book = new Book();
        book.setProductId(123L); // Required for update
        book.setBookId(123L); // Required for book update
        book.setTitle("Updated Test Book");
        book.setPrice(150000);
        book.setWeight(0.6f);
        book.setQuantity(15);
        book.setRushOrderSupported(true);
        book.setImageUrl("http://example.com/updated-book.jpg");
        book.setBarcode("1234567890123");
        book.setImportDate("2025-01-15");
        book.setIntroduction("Updated test book introduction");
        book.setType("book");
        book.setAuthors("Updated Test Author");
        book.setGenre("Updated Fiction");
        book.setPageCount(350);
        book.setPublishers("Updated Test Publisher");
        book.setPublicationDate("2025-01-15");
        book.setCoverType("Hardcover");
        return book;
    }

    private CD createValidCDForUpdate() {
        CD cd = new CD();
        cd.setProductId(124L); // Required for update
        cd.setCdId(124L); // Required for CD update
        cd.setTitle("Updated Test CD");
        cd.setPrice(250000);
        cd.setWeight(0.25f);
        cd.setQuantity(8);
        cd.setRushOrderSupported(false);
        cd.setImageUrl("http://example.com/updated-cd.jpg");
        cd.setBarcode("0987654321098");
        cd.setImportDate("2025-01-15");
        cd.setIntroduction("Updated test CD introduction");
        cd.setType("cd");
        cd.setArtists("Updated Test Artist");
        cd.setGenre("Updated Rock");
        cd.setTrackList("Updated Track 1, Updated Track 2, Updated Track 3");
        cd.setRecordLabel("Updated Test Label");
        cd.setReleaseDate("2025-01-15");
        return cd;
    }

    private DVD createValidDVDForUpdate() {
        DVD dvd = new DVD();
        dvd.setProductId(125L); // Required for update
        dvd.setDvdId(125L); // Required for DVD update
        dvd.setTitle("Updated Test DVD");
        dvd.setPrice(350000);
        dvd.setWeight(0.35f);
        dvd.setQuantity(12);
        dvd.setRushOrderSupported(true);
        dvd.setImageUrl("http://example.com/updated-dvd.jpg");
        dvd.setBarcode("1122334455667");
        dvd.setImportDate("2025-01-15");
        dvd.setIntroduction("Updated test DVD introduction");
        dvd.setType("dvd");
        dvd.setDirector("Updated Test Director");
        dvd.setGenre("Updated Action");
        dvd.setStudio("Updated Test Studio");
        dvd.setDvdType("4K Ultra HD");
        dvd.setReleaseDate("2025-01-15");
        return dvd;
    }
}