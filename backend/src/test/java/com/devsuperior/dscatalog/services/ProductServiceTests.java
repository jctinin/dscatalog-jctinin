package com.devsuperior.dscatalog.services;



import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class ProductServiceTests {

	@InjectMocks
	private ProductService productService;

	@Mock
	private ProductRepository productRepository;

	private long existingId;
	private long nonExistingId;
	private long dependentId;

	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		dependentId = 4L;
		
		
		Mockito.doNothing().when(productRepository).deleteById(existingId);
		Mockito.doThrow(EmptyResultDataAccessException.class).when(productRepository).deleteById(nonExistingId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(productRepository).deleteById(dependentId);
		
	}
	
	@Test
	@DisplayName("Verifica se o método delete não lança erro caso o ID seja válido.")
	public void deleteShouldDoNothingWhenIdExists() {

		Assertions.assertDoesNotThrow(() -> {
			productService.delete(existingId);
		});
	
		Mockito.verify(productRepository, Mockito.times(1)).deleteById(existingId);
		
	}
	
	@Test
	@DisplayName("Método delete deve enviar uma exceção quando o id não existe")
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		Assertions.assertThrows(ResourceNotFoundException.class, ()-> {
			productService.delete(nonExistingId);
		});
		
		Mockito.verify(productRepository, Mockito.times(1)).deleteById(nonExistingId);
	}
	
	@Test
	@DisplayName("Método delete deve lançar exeção ao tentar remover um id vinculado a outro")
	public void deleteShouldThrowDatabaseException() {
		Assertions.assertThrows(DatabaseException.class, () -> {
			productService.delete(dependentId);
		});
		Mockito.verify(productRepository, Mockito.times(1)).deleteById(dependentId);
	}

}
