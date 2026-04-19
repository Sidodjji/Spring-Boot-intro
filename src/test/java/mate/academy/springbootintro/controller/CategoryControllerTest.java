package mate.academy.springbootintro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import mate.academy.springbootintro.dto.category.CategoryDto;
import mate.academy.springbootintro.dto.category.CreateCategoryRequestDto;
import org.junit.jupiter.api.*;
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
import javax.sql.DataSource;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CategoryControllerTest {

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
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("createCategory with valid request should return CategoryDto")
    void createCategory_WithValidRequest_ShouldReturnCategoryDto() throws Exception {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto()
                .setName("Fantastic")
                .setDescription("Fantastic category");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult mvcResult = mockMvc.perform(post("/categories")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                CategoryDto.class
        );

        CategoryDto expected = new CategoryDto()
                .setId(actual.getId())
                .setName(requestDto.getName())
                .setDescription(requestDto.getDescription());

        assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("createCategory with existing data should return server error")
    void createCategory_WithExistingData_ShouldReturnServerError() throws Exception {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto()
                .setName("Horror")
                .setDescription("New Horror category");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/categories")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @WithMockUser(username = "User", roles = {"USER"})
    @DisplayName("getAll with authorized user should return page of CategoryDto")
    void getAll_WithAuthorizedUser_ShouldReturnPageCategoryDto() throws Exception {
        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    @DisplayName("getAll with unauthorized user should return 401")
    void getAll_WithUnauthorizedUser_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/categories"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "User", roles = {"USER"})
    @DisplayName("getCategoryById with valid id should return CategoryDto")
    void getCategoryById_WithValidId_ShouldReturnCategoryDto() throws Exception {
        mockMvc.perform(get("/categories/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Horror"))
                .andExpect(jsonPath("$.description").value("Horror category"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("updateCategory with valid request should return updated CategoryDto")
    void updateCategory_WithValidRequest_ShouldReturnCategoryDto() throws Exception {
        Long requestId = 1L;

        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto()
                .setName("Fantastic")
                .setDescription("New Fantastic category");

        CategoryDto expected = new CategoryDto()
                .setId(requestId)
                .setName(requestDto.getName())
                .setDescription(requestDto.getDescription());

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult mvcResult = mockMvc.perform(post("/categories/{id}", requestId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                CategoryDto.class
        );

        assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "User", roles = {"USER"})
    @DisplayName("getBooksByCategoryId with valid id should return list of books")
    void getBooksByCategoryId_WithValidId_ShouldReturnBookList() throws Exception {
        Long requestId = 1L;

        mockMvc.perform(get("/categories/{id}/books", requestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("deleteCategory with valid id should return no content")
    void deleteCategory_WithValidId_ShouldReturnNoContent() throws Exception {
        Long requestId = 1L;

        mockMvc.perform(delete("/categories/{id}", requestId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/categories/{id}", requestId))
                .andExpect(status().isNotFound());
    }
}