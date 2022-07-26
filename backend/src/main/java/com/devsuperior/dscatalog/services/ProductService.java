package com.devsuperior.dscatalog.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class ProductService {

	@Autowired
	private ProductRepository repository;
	
	@Autowired
	private CategoryRepository categoryRepository;

	@Transactional(readOnly = true)
	public Page<ProductDTO> findAllPaged(PageRequest pageRequest) {

		Page<Product> list = repository.findAll(pageRequest);

		return list.map(cat -> new ProductDTO(cat));

	}

	@Transactional(readOnly = true)
	public ProductDTO findById(Long id) {

		Optional<Product> obj = repository.findById(id);
		Product entity = obj.orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));

		return new ProductDTO(entity, entity.getCategories());
	}

	@Transactional(readOnly = true)
	public ProductDTO insert(ProductDTO productDto) {

		Product entity = new Product();
		this.copyDtoToEntity(productDto, entity);
		entity = repository.save(entity); // envia os dados para o repositório

		return new ProductDTO(entity); // retorna o dto com os dados retornados do save convertendo em dto novamente.
	}

	@Transactional
	public ProductDTO update(Long id, ProductDTO productDto) {
		try {

			Product entity = repository.getReferenceById(id);
			this.copyDtoToEntity(productDto, entity);
			entity = repository.save(entity);
			return new ProductDTO(entity);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id {id} not found");
		}
	}

	public void delete(Long id) {
		try {
			repository.deleteById(id);

		} catch (EmptyResultDataAccessException e) {
			throw new DatabaseException("Id not found " + id);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Integrity violation");
		}
	}
	
	private void copyDtoToEntity(ProductDTO productDto, Product entity) {
		entity.setName(productDto.getName());
		entity.setDescription(productDto.getDescription());
		entity.setPrice(productDto.getPrice());
		entity.setImgUrl(productDto.getImgUrl());
		entity.setDate(productDto.getDate());
		
		entity.getCategories().clear();
		
		for(CategoryDTO catDto : productDto.getCategories()) {
			Category category = categoryRepository.getReferenceById(catDto.getId());
			entity.getCategories().add(category);
		}
	}

}
