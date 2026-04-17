package mate.academy.springbootintro.controller;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Set;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.springbootintro.dto.book.BookDto;
import mate.academy.springbootintro.dto.book.CreateBookRequestDto;
import mate.academy.springbootintro.model.Category;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTest {

    private static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @SneakyThrows
    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @SneakyThrows
    @BeforeEach
    void beforeEach(@Autowired DataSource dataSource) {
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/books/add-book-to-books-table.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/categories/add-horror-category-to-categories-table.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/bookscategories/assign-categories-to-books.sql"));
        }
    }

    @SneakyThrows
    @AfterEach
    void cleanUpAfterTest(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    private static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/bookscategories/delete-books-categories.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/books/delete-book.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/categories/delete-category.sql"));
        }
    }

    @Test
    @WithMockUser(username = "User", roles = {"USER"})
    @DisplayName("getAll with authorized user should return page of BookDto")
    void getAll_WithAuthorizedUser_ShouldReturnPageBookDto() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[1].id").value(2));
    }

    @Test
    @DisplayName("getAll with unauthorized user should return 401")
    void getAll_WithUnauthorizedUser_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "User", roles = {"USER"})
    @DisplayName("getBookById with valid id should return BookDto")
    void getBookById_WithValidId_ShouldReturnBookDto() throws Exception {
        mockMvc.perform(get("/books/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Book A"))
                .andExpect(jsonPath("$.author").value("Author A"));
    }

    @Test
    @WithMockUser(username = "User", roles = {"USER"})
    @DisplayName("getBookById with invalid id should return 404")
    void getBookById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/books/{id}", 3L))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("createBook with valid request should return BookDto")
    void createBook_WithValidRequest_ShouldReturnBookDto() throws Exception {
        Category category = new Category()
                .setId(1L)
                .setDescription("Horror category")
                .setName("Horror");

        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("Book C")
                .setDescription("Book")
                .setPrice(BigDecimal.TEN)
                .setIsbn("3211245325")
                .setCategoryIds(Set.of(1L))
                .setAuthor("Author B")
                .setCoverImage("img.png");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult mvcResult = mockMvc.perform(post("/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        BookDto actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                BookDto.class
        );

        BookDto expected = new BookDto()
                .setTitle(requestDto.getTitle())
                .setDescription(requestDto.getDescription())
                .setPrice(requestDto.getPrice())
                .setIsbn(requestDto.getIsbn())
                .setAuthor(requestDto.getAuthor())
                .setCoverImage(requestDto.getCoverImage())
                .setCategory(actual.getCategory())
                .setId(actual.getId());

        assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("createBook with existing data should return server error")
    void createBook_WithExistingData_ShouldReturnServerError() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("Book A")
                .setDescription("Book")
                .setPrice(BigDecimal.valueOf(55.15))
                .setIsbn("23132143212")
                .setCategoryIds(Set.of(1L))
                .setAuthor("Author B")
                .setCoverImage("img.png");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("updateBook with valid request should return updated BookDto")
    void updateBook_WithValidRequest_ShouldReturnBookDto() throws Exception {
        Long requestId = 1L;

        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("Book C")
                .setDescription("Book")
                .setPrice(BigDecimal.TEN)
                .setIsbn("3211245325")
                .setCategoryIds(Set.of(1L))
                .setAuthor("Author B")
                .setCoverImage("img.png");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult mvcResult = mockMvc.perform(put("/books/{id}", requestId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                BookDto.class
        );

        BookDto expected = new BookDto()
                .setTitle(requestDto.getTitle())
                .setDescription(requestDto.getDescription())
                .setPrice(requestDto.getPrice())
                .setIsbn(requestDto.getIsbn())
                .setAuthor(requestDto.getAuthor())
                .setCoverImage(requestDto.getCoverImage())
                .setCategory(actual.getCategory())
                .setId(actual.getId());

        assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("deleteBook with valid id should return no content")
    void deleteBook_WithValidId_ShouldReturnNoContent() throws Exception {
        Long requestId = 1L;

        mockMvc.perform(delete("/books/{id}", requestId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/books/{id}", requestId))
                .andExpect(status().isNotFound());
    }
}