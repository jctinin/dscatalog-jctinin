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
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository repository;

	@Transactional(readOnly = true)
	public Page<CategoryDTO> findAllPaged(PageRequest pageRequest) {

		Page<Category> list = repository.findAll(pageRequest);

		return list.map(cat -> new CategoryDTO(cat));

	}

	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {

		Optional<Category> obj = repository.findById(id);
		Category entity = obj.orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));

		return new CategoryDTO(entity);
	}

	@Transactional(readOnly = true)
	public CategoryDTO insert(CategoryDTO category) {

		Category entity = new Category();
		entity.setName(category.getName());
		entity = repository.save(entity); // envia os dados para o repositório

		return new CategoryDTO(entity); // retorna o dto com os dados retornados do save convertendo em dto novamente.
	}

	@Transactional
	public CategoryDTO update(Long id, CategoryDTO category) {
		try {

			Category entity = repository.getReferenceById(id);
			entity.setName(category.getName());
			entity = repository.save(entity);
			return new CategoryDTO(entity);
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

}
