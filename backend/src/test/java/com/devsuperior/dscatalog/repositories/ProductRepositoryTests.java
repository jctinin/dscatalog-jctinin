package com.devsuperior.dscatalog.repositories;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.tests.Factory;

@DataJpaTest
public class ProductRepositoryTests {

	@Autowired
	private ProductRepository productRepository;

	private long objId;
	private long deletedId;
	private long deleteId;
	private long countTotalParoducts;

	@BeforeEach
	void setup() throws Exception {
		deletedId = 1L;
		deleteId = 1000L;
		countTotalParoducts = 25L;
		objId = 1L;
	}

	@Test
	@DisplayName("Verifica se findById retorna o objeto quando o id existe")
	public void findByIdShouldReturnObjectWhenIdExists() {
		Optional<Product> product = productRepository.findById(objId);

		Assertions.assertTrue(product.isPresent());
	}

	@Test
	@DisplayName("Verifica se findById retorna o objeto vazio quando o id não existe")
	public void findByIdShouldReturnEmptyObjectWhenIdNotExists() {
		Optional<Product> product = productRepository.findById(deleteId);

		Assertions.assertTrue(product.isEmpty());
	}

	@Test
	@DisplayName("Save deve persistir com auto incremento quando id for nulo")
	public void saveShouldPersistWithAutoIncrementWhenIdIsNull() {

		Product product = Factory.createProduct();
		product.setId(null);

		product = productRepository.save(product);

		Assertions.assertNotNull(product.getId());
		Assertions.assertEquals(product.getId(), countTotalParoducts + 1);

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
