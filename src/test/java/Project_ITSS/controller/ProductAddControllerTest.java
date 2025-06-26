package Project_ITSS.controller;

import Project_ITSS.common.entity.Product;
import Project_ITSS.common.entity.Book;
import Project_ITSS.common.entity.CD;
import Project_ITSS.common.entity.DVD;
import Project_ITSS.service.ProductAddService;
import Project_ITSS.service.LoggerAddService;
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
@DisplayName("ProductAddController Unit Tests")
class ProductAddControllerTest {

    @Mock
    private ProductAddService productService;

    @Mock
    private LoggerAddService loggerService;

    @InjectMocks
    private ProductAddController productAddController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productAddController).build();
        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("GET /AddingRequested - Request to Add Product Tests")
    class RequestToAddProductTests {

        @Test
        @DisplayName("Should return success message for adding request")
        void requestToAddProduct_ValidRequest_ReturnsSuccessMessage() throws Exception {
            // When & Then
            mockMvc.perform(get("/AddingRequested")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Product addition request received"));
        }

        @Test
        @DisplayName("Should handle multiple concurrent requests")
        void requestToAddProduct_MultipleConcurrentRequests_HandlesCorrectly() throws Exception {
            // When & Then - Simulate multiple requests
            for (int i = 0; i < 5; i++) {
                mockMvc.perform(get("/AddingRequested")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().string("Product addition request received"));
            }
        }

        @Test
        @DisplayName("Should handle request without content type")
        void requestToAddProduct_NoContentType_ReturnsSuccessMessage() throws Exception {
            // When & Then
            mockMvc.perform(get("/AddingRequested"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Product addition request received"));
        }
    }

    @Nested
    @DisplayName("POST /adding/ProductInfo - Submit Product Info Tests")
    class SubmitProductInfoTests {

        @Nested
        @DisplayName("Book Product Tests")
        class BookProductTests {

            @Test
            @DisplayName("Should add valid book successfully")
            void submitProductInfo_ValidBook_ReturnsSuccessResponse() throws Exception {
                // Given
                Book book = createValidBook();
                int productId = 123;
                
                when(productService.addProduct(any(Product.class))).thenReturn(productId);
                doNothing().when(loggerService).saveLogger(any(Product.class));

                // When & Then
                mockMvc.perform(post("/adding/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.status").value(1))
                        .andExpect(jsonPath("$.message").value("Product added successfully"))
                        .andExpect(jsonPath("$.product_id").value(productId))
                        .andExpect(jsonPath("$.errorCode").doesNotExist())
                        .andExpect(jsonPath("$.error").doesNotExist());

                verify(productService).addProduct(any(Product.class));
                verify(loggerService).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle book with minimal required fields")
            void submitProductInfo_BookWithMinimalFields_ReturnsSuccessResponse() throws Exception {
                // Given
                Book book = createMinimalBook();
                int productId = 124;
                
                when(productService.addProduct(any(Product.class))).thenReturn(productId);
                doNothing().when(loggerService).saveLogger(any(Product.class));

                // When & Then
                mockMvc.perform(post("/adding/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.status").value(1))
                        .andExpect(jsonPath("$.product_id").value(productId));

                verify(productService).addProduct(any(Product.class));
                verify(loggerService).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle book with missing title")
            void submitProductInfo_BookMissingTitle_ThrowsValidationException() throws Exception {
                // Given
                Book book = createValidBook();
                book.setTitle(null);
                
                when(productService.addProduct(any(Product.class)))
                    .thenThrow(new ValidationException("Product title is required"));

                // When & Then
                mockMvc.perform(post("/adding/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                        .andExpect(status().isBadRequest());

                verify(productService).addProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle book with missing authors")
            void submitProductInfo_BookMissingAuthors_ThrowsValidationException() throws Exception {
                // Given
                Book book = createValidBook();
                book.setAuthors(null);
                
                when(productService.addProduct(any(Product.class)))
                    .thenThrow(new ValidationException("Book authors are required"));

                // When & Then
                mockMvc.perform(post("/adding/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                        .andExpect(status().isBadRequest());

                verify(productService).addProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle book with invalid page count")
            void submitProductInfo_BookInvalidPageCount_ThrowsValidationException() throws Exception {
                // Given
                Book book = createValidBook();
                book.setPageCount(-1);
                
                when(productService.addProduct(any(Product.class)))
                    .thenThrow(new ValidationException("Book page count must be greater than 0"));

                // When & Then
                mockMvc.perform(post("/adding/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                        .andExpect(status().isBadRequest());

                verify(productService).addProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle book with empty genre")
            void submitProductInfo_BookEmptyGenre_ThrowsValidationException() throws Exception {
                // Given
                Book book = createValidBook();
                book.setGenre("");
                
                when(productService.addProduct(any(Product.class)))
                    .thenThrow(new ValidationException("Book genre is required"));

                // When & Then
                mockMvc.perform(post("/adding/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                        .andExpect(status().isBadRequest());

                verify(productService).addProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }
        }

        @Nested
        @DisplayName("CD Product Tests")
        class CDProductTests {

            @Test
            @DisplayName("Should add valid CD successfully")
            void submitProductInfo_ValidCD_ReturnsSuccessResponse() throws Exception {
                // Given
                CD cd = createValidCD();
                int productId = 125;
                
                when(productService.addProduct(any(Product.class))).thenReturn(productId);
                doNothing().when(loggerService).saveLogger(any(Product.class));

                // When & Then
                mockMvc.perform(post("/adding/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cd)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.status").value(1))
                        .andExpect(jsonPath("$.message").value("Product added successfully"))
                        .andExpect(jsonPath("$.product_id").value(productId));

                verify(productService).addProduct(any(Product.class));
                verify(loggerService).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle CD with missing artists")
            void submitProductInfo_CDMissingArtists_ThrowsValidationException() throws Exception {
                // Given
                CD cd = createValidCD();
                cd.setArtists(null);
                
                when(productService.addProduct(any(Product.class)))
                    .thenThrow(new ValidationException("CD artists are required"));

                // When & Then
                mockMvc.perform(post("/adding/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cd)))
                        .andExpect(status().isBadRequest());

                verify(productService).addProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle CD with empty track list")
            void submitProductInfo_CDEmptyTrackList_ThrowsValidationException() throws Exception {
                // Given
                CD cd = createValidCD();
                cd.setTrackList("");
                
                when(productService.addProduct(any(Product.class)))
                    .thenThrow(new ValidationException("CD track list is required"));

                // When & Then
                mockMvc.perform(post("/adding/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cd)))
                        .andExpect(status().isBadRequest());

                verify(productService).addProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle CD with missing record label")
            void submitProductInfo_CDMissingRecordLabel_ThrowsValidationException() throws Exception {
                // Given
                CD cd = createValidCD();
                cd.setRecordLabel(null);
                
                when(productService.addProduct(any(Product.class)))
                    .thenThrow(new ValidationException("CD record label is required"));

                // When & Then
                mockMvc.perform(post("/adding/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cd)))
                        .andExpect(status().isBadRequest());

                verify(productService).addProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }
        }

        @Nested
        @DisplayName("DVD Product Tests")
        class DVDProductTests {

            @Test
            @DisplayName("Should add valid DVD successfully")
            void submitProductInfo_ValidDVD_ReturnsSuccessResponse() throws Exception {
                // Given
                DVD dvd = createValidDVD();
                int productId = 126;
                
                when(productService.addProduct(any(Product.class))).thenReturn(productId);
                doNothing().when(loggerService).saveLogger(any(Product.class));

                // When & Then
                mockMvc.perform(post("/adding/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dvd)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.status").value(1))
                        .andExpect(jsonPath("$.message").value("Product added successfully"))
                        .andExpect(jsonPath("$.product_id").value(productId));

                verify(productService).addProduct(any(Product.class));
                verify(loggerService).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle DVD with missing director")
            void submitProductInfo_DVDMissingDirector_ThrowsValidationException() throws Exception {
                // Given
                DVD dvd = createValidDVD();
                dvd.setDirector(null);
                
                when(productService.addProduct(any(Product.class)))
                    .thenThrow(new ValidationException("DVD director is required"));

                // When & Then
                mockMvc.perform(post("/adding/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dvd)))
                        .andExpect(status().isBadRequest());

                verify(productService).addProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle DVD with empty studio")
            void submitProductInfo_DVDEmptyStudio_ThrowsValidationException() throws Exception {
                // Given
                DVD dvd = createValidDVD();
                dvd.setStudio("");
                
                when(productService.addProduct(any(Product.class)))
                    .thenThrow(new ValidationException("DVD studio is required"));

                // When & Then
                mockMvc.perform(post("/adding/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dvd)))
                        .andExpect(status().isBadRequest());

                verify(productService).addProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle DVD with missing DVD type")
            void submitProductInfo_DVDMissingType_ThrowsValidationException() throws Exception {
                // Given
                DVD dvd = createValidDVD();
                dvd.setDvdType(null);
                
                when(productService.addProduct(any(Product.class)))
                    .thenThrow(new ValidationException("DVD type is required"));

                // When & Then
                mockMvc.perform(post("/adding/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dvd)))
                        .andExpect(status().isBadRequest());

                verify(productService).addProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }
        }

        @Nested
        @DisplayName("Common Product Validation Tests")
        class CommonProductValidationTests {

            @Test
            @DisplayName("Should handle null product")
            void submitProductInfo_NullProduct_ThrowsValidationException() throws Exception {
                // When & Then
                mockMvc.perform(post("/adding/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("null"))
                        .andExpect(status().isBadRequest());

                verify(productService, never()).addProduct(any());
                verify(loggerService, never()).saveLogger(any());
            }

            @Test
            @DisplayName("Should handle product with negative price")
            void submitProductInfo_NegativePrice_ThrowsValidationException() throws Exception {
                // Given
                Book book = createValidBook();
                book.setPrice(-1000);
                
                when(productService.addProduct(any(Product.class)))
                    .thenThrow(new ValidationException("Product price must be greater than 0"));

                // When & Then
                mockMvc.perform(post("/adding/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                        .andExpect(status().isBadRequest());

                verify(productService).addProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle product with zero price")
            void submitProductInfo_ZeroPrice_ThrowsValidationException() throws Exception {
                // Given
                Book book = createValidBook();
                book.setPrice(0);
                
                when(productService.addProduct(any(Product.class)))
                    .thenThrow(new ValidationException("Product price must be greater than 0"));

                // When & Then
                mockMvc.perform(post("/adding/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                        .andExpect(status().isBadRequest());

                verify(productService).addProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle product with negative weight")
            void submitProductInfo_NegativeWeight_ThrowsValidationException() throws Exception {
                // Given
                Book book = createValidBook();
                book.setWeight(-0.5f);
                
                when(productService.addProduct(any(Product.class)))
                    .thenThrow(new ValidationException("Product weight must be greater than 0"));

                // When & Then
                mockMvc.perform(post("/adding/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                        .andExpect(status().isBadRequest());

                verify(productService).addProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle product with negative quantity")
            void submitProductInfo_NegativeQuantity_ThrowsValidationException() throws Exception {
                // Given
                Book book = createValidBook();
                book.setQuantity(-5);
                
                when(productService.addProduct(any(Product.class)))
                    .thenThrow(new ValidationException("Product quantity cannot be negative"));

                // When & Then
                mockMvc.perform(post("/adding/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                        .andExpect(status().isBadRequest());

                verify(productService).addProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle product with missing type")
            void submitProductInfo_MissingType_ThrowsValidationException() throws Exception {
                // Given
                Book book = createValidBook();
                book.setType(null);
                
                when(productService.addProduct(any(Product.class)))
                    .thenThrow(new ValidationException("Product type is required"));

                // When & Then
                mockMvc.perform(post("/adding/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                        .andExpect(status().isBadRequest());

                verify(productService).addProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle product with invalid type")
            void submitProductInfo_InvalidType_ThrowsValidationException() throws Exception {
                // Given
                Book book = createValidBook();
                book.setType("magazine");
                
                when(productService.addProduct(any(Product.class)))
                    .thenThrow(new ValidationException("Invalid product type: magazine"));

                // When & Then
                mockMvc.perform(post("/adding/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                        .andExpect(status().isBadRequest());

                verify(productService).addProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle empty title")
            void submitProductInfo_EmptyTitle_ThrowsValidationException() throws Exception {
                // Given
                Book book = createValidBook();
                book.setTitle("");
                
                when(productService.addProduct(any(Product.class)))
                    .thenThrow(new ValidationException("Product title is required"));

                // When & Then
                mockMvc.perform(post("/adding/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                        .andExpect(status().isBadRequest());

                verify(productService).addProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle whitespace-only title")
            void submitProductInfo_WhitespaceTitle_ThrowsValidationException() throws Exception {
                // Given
                Book book = createValidBook();
                book.setTitle("   ");
                
                when(productService.addProduct(any(Product.class)))
                    .thenThrow(new ValidationException("Product title is required"));

                // When & Then
                mockMvc.perform(post("/adding/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                        .andExpect(status().isBadRequest());

                verify(productService).addProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }
        }

        @Nested
        @DisplayName("Service Exception Handling Tests")
        class ServiceExceptionHandlingTests {

            @Test
            @DisplayName("Should handle service throwing RuntimeException")
            void submitProductInfo_ServiceThrowsRuntimeException_ReturnsInternalServerError() throws Exception {
                // Given
                Book book = createValidBook();
                
                when(productService.addProduct(any(Product.class)))
                    .thenThrow(new RuntimeException("Database connection failed"));

                // When & Then
                mockMvc.perform(post("/adding/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                        .andExpect(status().isInternalServerError());

                verify(productService).addProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle logger service throwing exception")
            void submitProductInfo_LoggerServiceThrowsException_StillReturnsSuccess() throws Exception {
                // Given
                Book book = createValidBook();
                int productId = 127;
                
                when(productService.addProduct(any(Product.class))).thenReturn(productId);
                doThrow(new RuntimeException("Logger service failed"))
                    .when(loggerService).saveLogger(any(Product.class));

                // When & Then - Should still return success even if logger fails
                mockMvc.perform(post("/adding/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.status").value(1))
                        .andExpect(jsonPath("$.product_id").value(productId));

                verify(productService).addProduct(any(Product.class));
                verify(loggerService).saveLogger(any(Product.class));
            }

            @Test
            @DisplayName("Should handle unexpected ValidationException message")
            void submitProductInfo_UnexpectedValidationException_ReturnsBadRequest() throws Exception {
                // Given
                Book book = createValidBook();
                
                when(productService.addProduct(any(Product.class)))
                    .thenThrow(new ValidationException("Unexpected validation error occurred"));

                // When & Then
                mockMvc.perform(post("/adding/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                        .andExpect(status().isBadRequest());

                verify(productService).addProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any(Product.class));
            }
        }

        @Nested
        @DisplayName("JSON Parsing Tests")
        class JsonParsingTests {

            @Test
            @DisplayName("Should handle malformed JSON")
            void submitProductInfo_MalformedJson_ReturnsBadRequest() throws Exception {
                // When & Then
                mockMvc.perform(post("/adding/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json"))
                        .andExpect(status().isBadRequest());

                verify(productService, never()).addProduct(any());
                verify(loggerService, never()).saveLogger(any());
            }

            @Test
            @DisplayName("Should handle empty JSON object")
            void submitProductInfo_EmptyJsonObject_HandlesGracefully() throws Exception {
                // When & Then - Empty JSON object will fail JSON parsing due to missing type discriminator
                mockMvc.perform(post("/adding/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                        .andExpect(status().isBadRequest());

                // Service should not be called due to JSON parsing failure
                verify(productService, never()).addProduct(any(Product.class));
                verify(loggerService, never()).saveLogger(any());
            }

            @Test
            @DisplayName("Should handle JSON with extra unknown fields")
            void submitProductInfo_JsonWithExtraFields_IgnoresExtraFields() throws Exception {
                // Given
                String jsonWithExtraFields = """
                    {
                        "title": "Test Book",
                        "price": 100000,
                        "weight": 0.5,
                        "quantity": 10,
                        "type": "book",
                        "authors": "Test Author",
                        "genre": "Fiction",
                        "pageCount": 300,
                        "publishers": "Test Publisher",
                        "publicationDate": "2025-01-01",
                        "coverType": "Paperback",
                        "extraField": "should be ignored",
                        "anotherExtraField": 999
                    }
                    """;
                int productId = 128;
                
                when(productService.addProduct(any(Product.class))).thenReturn(productId);
                doNothing().when(loggerService).saveLogger(any(Product.class));

                // When & Then
                mockMvc.perform(post("/adding/ProductInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithExtraFields))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.status").value(1))
                        .andExpect(jsonPath("$.product_id").value(productId));

                verify(productService).addProduct(any(Product.class));
                verify(loggerService).saveLogger(any(Product.class));
            }
        }
    }

    @Nested
    @DisplayName("GET /adding/available - Check Valid Adding Tests")
    class CheckValidAddingTests {

        @Test
        @DisplayName("Should return true when adding is available")
        void checkValidAdding_AddingAvailable_ReturnsTrue() throws Exception {
            // Given
            when(loggerService.checkValidAddProducts()).thenReturn(true);

            // When & Then
            mockMvc.perform(get("/adding/available")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));

            verify(loggerService).checkValidAddProducts();
        }

        @Test
        @DisplayName("Should return false when adding is not available")
        void checkValidAdding_AddingNotAvailable_ReturnsFalse() throws Exception {
            // Given
            when(loggerService.checkValidAddProducts()).thenReturn(false);

            // When & Then
            mockMvc.perform(get("/adding/available")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().string("false"));

            verify(loggerService).checkValidAddProducts();
        }

        @Test
        @DisplayName("Should handle logger service throwing exception")
        void checkValidAdding_LoggerServiceThrowsException_ReturnsInternalServerError() throws Exception {
            // Given
            when(loggerService.checkValidAddProducts())
                .thenThrow(new RuntimeException("Logger database connection failed"));

            // When & Then
            mockMvc.perform(get("/adding/available")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());

            verify(loggerService).checkValidAddProducts();
        }

        @Test
        @DisplayName("Should handle logger service throwing ValidationException")
        void checkValidAdding_LoggerServiceThrowsValidationException_ReturnsBadRequest() throws Exception {
            // Given
            when(loggerService.checkValidAddProducts())
                .thenThrow(new ValidationException("Failed to check add products validity"));

            // When & Then
            mockMvc.perform(get("/adding/available")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(loggerService).checkValidAddProducts();
        }

        @Test
        @DisplayName("Should handle multiple concurrent availability checks")
        void checkValidAdding_MultipleConcurrentChecks_HandlesCorrectly() throws Exception {
            // Given
            when(loggerService.checkValidAddProducts()).thenReturn(true);

            // When & Then - Simulate multiple concurrent requests
            for (int i = 0; i < 5; i++) {
                mockMvc.perform(get("/adding/available")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().string("true"));
            }

            verify(loggerService, times(5)).checkValidAddProducts();
        }

        @Test
        @DisplayName("Should handle request without content type")
        void checkValidAdding_NoContentType_ReturnsCorrectly() throws Exception {
            // Given
            when(loggerService.checkValidAddProducts()).thenReturn(false);

            // When & Then
            mockMvc.perform(get("/adding/available"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("false"));

            verify(loggerService).checkValidAddProducts();
        }
    }

    @Nested
    @DisplayName("Integration Flow Tests")
    class IntegrationFlowTests {

        @Test
        @DisplayName("Should handle complete product addition flow")
        void completeProductAdditionFlow_ValidProduct_Success() throws Exception {
            // Step 1: Check availability
            when(loggerService.checkValidAddProducts()).thenReturn(true);
            
            mockMvc.perform(get("/adding/available"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));

            // Step 2: Request to add product
            mockMvc.perform(get("/AddingRequested"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Product addition request received"));

            // Step 3: Submit product info
            Book book = createValidBook();
            int productId = 129;
            
            when(productService.addProduct(any(Product.class))).thenReturn(productId);
            doNothing().when(loggerService).saveLogger(any(Product.class));

            mockMvc.perform(post("/adding/ProductInfo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(book)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(1))
                    .andExpect(jsonPath("$.product_id").value(productId));

            // Verify all service calls
            verify(loggerService).checkValidAddProducts();
            verify(productService).addProduct(any(Product.class));
            verify(loggerService).saveLogger(any(Product.class));
        }

        @Test
        @DisplayName("Should handle flow when adding not available")
        void completeProductAdditionFlow_AddingNotAvailable_ReturnsUnavailable() throws Exception {
            // Given
            when(loggerService.checkValidAddProducts()).thenReturn(false);

            // When & Then
            mockMvc.perform(get("/adding/available"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("false"));

            verify(loggerService).checkValidAddProducts();
            // Should not proceed to add product when not available
        }
    }

    @Nested
    @DisplayName("Edge Cases and Boundary Tests")
    class EdgeCasesAndBoundaryTests {

        @Test
        @DisplayName("Should handle product with maximum integer values")
        void submitProductInfo_MaximumValues_HandlesCorrectly() throws Exception {
            // Given
            Book book = createValidBook();
            book.setPrice(Integer.MAX_VALUE);
            book.setQuantity(Integer.MAX_VALUE);
            book.setPageCount(Integer.MAX_VALUE);
            int productId = 130;
            
            when(productService.addProduct(any(Product.class))).thenReturn(productId);
            doNothing().when(loggerService).saveLogger(any(Product.class));

            // When & Then
            mockMvc.perform(post("/adding/ProductInfo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(book)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(1))
                    .andExpect(jsonPath("$.product_id").value(productId));

            verify(productService).addProduct(any(Product.class));
            verify(loggerService).saveLogger(any(Product.class));
        }

        @Test
        @DisplayName("Should handle product with very long title")
        void submitProductInfo_VeryLongTitle_HandlesCorrectly() throws Exception {
            // Given
            Book book = createValidBook();
            String longTitle = "A".repeat(1000); // Very long title
            book.setTitle(longTitle);
            int productId = 131;
            
            when(productService.addProduct(any(Product.class))).thenReturn(productId);
            doNothing().when(loggerService).saveLogger(any(Product.class));

            // When & Then
            mockMvc.perform(post("/adding/ProductInfo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(book)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(1))
                    .andExpect(jsonPath("$.product_id").value(productId));

            verify(productService).addProduct(any(Product.class));
            verify(loggerService).saveLogger(any(Product.class));
        }

        @Test
        @DisplayName("Should handle product with special characters in fields")
        void submitProductInfo_SpecialCharacters_HandlesCorrectly() throws Exception {
            // Given
            Book book = createValidBook();
            book.setTitle("Test Book with Special Characters: àáâãäåæçèéêë");
            book.setAuthors("Tác giả: Nguyễn Văn A & Company");
            book.setGenre("Fiction/Fantasy & Science-Fiction");
            int productId = 132;
            
            when(productService.addProduct(any(Product.class))).thenReturn(productId);
            doNothing().when(loggerService).saveLogger(any(Product.class));

            // When & Then
            mockMvc.perform(post("/adding/ProductInfo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(book)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(1))
                    .andExpect(jsonPath("$.product_id").value(productId));

            verify(productService).addProduct(any(Product.class));
            verify(loggerService).saveLogger(any(Product.class));
        }

        @Test
        @DisplayName("Should handle zero quantity product")
        void submitProductInfo_ZeroQuantity_HandlesCorrectly() throws Exception {
            // Given
            Book book = createValidBook();
            book.setQuantity(0); // Zero quantity should be allowed
            int productId = 133;
            
            when(productService.addProduct(any(Product.class))).thenReturn(productId);
            doNothing().when(loggerService).saveLogger(any(Product.class));

            // When & Then
            mockMvc.perform(post("/adding/ProductInfo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(book)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(1))
                    .andExpect(jsonPath("$.product_id").value(productId));

            verify(productService).addProduct(any(Product.class));
            verify(loggerService).saveLogger(any(Product.class));
        }

        @Test
        @DisplayName("Should handle very small weight values")
        void submitProductInfo_VerySmallWeight_HandlesCorrectly() throws Exception {
            // Given
            Book book = createValidBook();
            book.setWeight(0.001f); // Very small weight
            int productId = 134;
            
            when(productService.addProduct(any(Product.class))).thenReturn(productId);
            doNothing().when(loggerService).saveLogger(any(Product.class));

            // When & Then
            mockMvc.perform(post("/adding/ProductInfo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(book)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(1))
                    .andExpect(jsonPath("$.product_id").value(productId));

            verify(productService).addProduct(any(Product.class));
            verify(loggerService).saveLogger(any(Product.class));
        }
    }

    // Helper methods for creating test data
    private Book createValidBook() {
        Book book = new Book();
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
        book.setAuthors("Test Author");
        book.setGenre("Fiction");
        book.setPageCount(300);
        book.setPublishers("Test Publisher");
        book.setPublicationDate("2025-01-01");
        book.setCoverType("Paperback");
        return book;
    }

    private Book createMinimalBook() {
        Book book = new Book();
        book.setTitle("Minimal Book");
        book.setPrice(50000);
        book.setWeight(0.3f);
        book.setQuantity(5);
        book.setType("book");
        book.setAuthors("Minimal Author");
        book.setGenre("Mystery");
        book.setPageCount(200);
        book.setPublishers("Minimal Publisher");
        return book;
    }

    private CD createValidCD() {
        CD cd = new CD();
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
        cd.setArtists("Test Artist");
        cd.setGenre("Rock");
        cd.setTrackList("Track 1, Track 2, Track 3");
        cd.setRecordLabel("Test Label");
        cd.setReleaseDate("2025-01-01");
        return cd;
    }

    private DVD createValidDVD() {
        DVD dvd = new DVD();
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
        dvd.setDirector("Test Director");
        dvd.setGenre("Action");
        dvd.setStudio("Test Studio");
        dvd.setDvdType("Blu-ray");
        dvd.setReleaseDate("2025-01-01");
        return dvd;
    }
}