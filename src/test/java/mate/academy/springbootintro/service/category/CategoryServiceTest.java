package mate.academy.springbootintro.service.category;

import mate.academy.springbootintro.dto.category.CategoryDto;
import mate.academy.springbootintro.dto.category.CreateCategoryRequestDto;
import mate.academy.springbootintro.exeption.EntityNotFoundException;
import mate.academy.springbootintro.mapper.CategoryMapper;
import mate.academy.springbootintro.model.Category;
import mate.academy.springbootintro.repository.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("findAll with empty categories should return empty page")
    void findAll_WithEmptyCategories_ShouldReturnEmptyPage() {
        Pageable pageable = Pageable.unpaged();

        Mockito.when(categoryRepository.findAll(pageable))
                .thenReturn(Page.empty());

        Page<CategoryDto> actual = categoryService.findAll(pageable);

        Assertions.assertTrue(actual.isEmpty());

        Mockito.verify(categoryRepository).findAll(pageable);
        Mockito.verifyNoInteractions(categoryMapper);
    }

    @Test
    @DisplayName("findAll with multiple categories should return page with CategoryDto")
    void findAll_WithManyCategories_ShouldReturnPageWithCategoriesDto() {
        Pageable pageable = PageRequest.of(0, 2);

        Category category1 = new Category();
        Category category2 = new Category();

        Page<Category> page = new PageImpl<>(List.of(category1, category2), pageable, 2);

        CategoryDto dto1 = new CategoryDto();
        CategoryDto dto2 = new CategoryDto();

        Mockito.when(categoryRepository.findAll(pageable)).thenReturn(page);
        Mockito.when(categoryMapper.toDto(category1)).thenReturn(dto1);
        Mockito.when(categoryMapper.toDto(category2)).thenReturn(dto2);

        Page<CategoryDto> actual = categoryService.findAll(pageable);

        Assertions.assertEquals(2, actual.getContent().size());

        Mockito.verify(categoryRepository).findAll(pageable);
        Mockito.verify(categoryMapper, Mockito.times(2)).toDto(Mockito.any());
    }

    @Test
    @DisplayName("getById with valid id should return CategoryDto")
    void getById_WithValidId_ShouldReturnCategoryDto() {
        Long requestId = 1L;

        Category category = new Category().setId(requestId);
        CategoryDto categoryDto = new CategoryDto().setId(category.getId());

        Mockito.when(categoryRepository.findById(requestId)).thenReturn(Optional.of(category));
        Mockito.when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto actual = categoryService.getById(requestId);

        Assertions.assertEquals(categoryDto, actual);
    }

    @Test
    @DisplayName("getById with invalid id should throw EntityNotFoundException")
    void getById_WithInvalidId_ShouldThrowException() {
        Long requestId = 3L;

        Mockito.when(categoryRepository.findById(requestId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.getById(requestId)
        );

        Assertions.assertTrue(exception.getMessage().contains("3"));

        Mockito.verify(categoryRepository).findById(requestId);
        Mockito.verifyNoInteractions(categoryMapper);
    }

    @Test
    @DisplayName("save with valid request should return CategoryDto")
    void save_WithValidRequest_ShouldReturnCategoryDto() {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();

        Category category = new Category().setId(1L);
        Category savedCategory = new Category().setId(15L);
        CategoryDto categoryDto = new CategoryDto().setId(15L);

        Mockito.when(categoryMapper.toEntity(requestDto)).thenReturn(category);
        Mockito.when(categoryRepository.save(category)).thenReturn(savedCategory);
        Mockito.when(categoryMapper.toDto(savedCategory)).thenReturn(categoryDto);

        CategoryDto actual = categoryService.save(requestDto);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(15L, actual.getId());

        Mockito.verify(categoryRepository).save(category);
        Mockito.verify(categoryMapper).toEntity(requestDto);
        Mockito.verify(categoryMapper).toDto(savedCategory);
    }

    @Test
    @DisplayName("update with valid request should return updated CategoryDto")
    void update_WithValidRequest_ShouldReturnCategoryDto() {
        Long requestId = 1L;

        Category category = new Category()
                .setId(1L)
                .setName("Horror");

        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto()
                .setName("Fantastic");

        Category updatedCategory = new Category()
                .setId(requestId)
                .setName("Fantastic");

        CategoryDto categoryDto = new CategoryDto()
                .setId(updatedCategory.getId())
                .setName(updatedCategory.getName());

        Mockito.when(categoryRepository.findById(requestId))
                .thenReturn(Optional.of(category));

        Mockito.doAnswer(invocation -> {
            Category target = invocation.getArgument(1);
            target.setName(requestDto.getName());
            return null;
        }).when(categoryMapper).updateCategoryFromDto(requestDto, category);

        Mockito.when(categoryRepository.save(category)).thenReturn(updatedCategory);
        Mockito.when(categoryMapper.toDto(updatedCategory)).thenReturn(categoryDto);

        CategoryDto actual = categoryService.update(requestId, requestDto);

        Assertions.assertEquals(categoryDto, actual);

        Mockito.verify(categoryRepository).findById(requestId);
        Mockito.verify(categoryRepository).save(category);
        Mockito.verify(categoryMapper).toDto(updatedCategory);
    }

    @Test
    @DisplayName("update with non existing category should throw EntityNotFoundException")
    void update_WithNonExistingCategory_ShouldThrowException() {
        Long requestId = 1L;

        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto()
                .setName("Fantastic");

        Mockito.when(categoryRepository.findById(requestId))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.update(requestId, requestDto)
        );

        Assertions.assertTrue(exception.getMessage().contains("1"));

        Mockito.verify(categoryRepository).findById(requestId);
        Mockito.verifyNoInteractions(categoryMapper);
    }
}