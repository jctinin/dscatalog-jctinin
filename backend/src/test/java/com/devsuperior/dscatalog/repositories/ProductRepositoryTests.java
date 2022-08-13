package com.devsuperior.dscatalog.repositories;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.devsuperior.dscatalog.entities.Product;

@DataJpaTest
public class ProductRepositoryTests {

	@Autowired
	private ProductRepository productRepository;
	
	private long deletedId;
	private long deleteId;
	
	@BeforeEach
	void setup() throws Exception {
		deletedId = 1L;
		deleteId = 1000L;
	}
	
	@Test
	@DisplayName("Delete remove o objeto quando o id existe.")
	public void deleteShouldDeleteObjectWhenIdExists() {
	
		productRepository.deleteById(deletedId);		
	
		Optional<Product> result = productRepository.findById(deletedId);
		
		assertFalse(result.isPresent()); // isPresent verifica se tem um objeto dentro do Optional
	}
	
	
	@Test
	@DisplayName("Verifica se lança exceção ao deletar com id inválido.")
	public void deleteShouldExceptionWhenNoId() {
		
		assertThrows(EmptyResultDataAccessException.class, () -> {
			productRepository.deleteById(deleteId);
		});
	}
	
}
