package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class MovieServiceTests {
	
	@InjectMocks
	private MovieService service;

	@Mock
	private MovieRepository repository;

	private MovieEntity movieEntity;
	private Page<MovieEntity> movieEntityPage;
	private List<MovieEntity> movieEntityListToPage;
	private Pageable pageable;

	private String defaultTitle;

	private Long existingId, nonExistingId;

	@BeforeEach
	void setUp(){
		movieEntity = MovieFactory.createMovieEntity();
		movieEntityListToPage = List.of(movieEntity);
		pageable = PageRequest.of(0, 10);
		defaultTitle = "Movie";
		movieEntityPage = new PageImpl<>(movieEntityListToPage, pageable, 1);

		existingId =1L;
		nonExistingId =2L;


		Mockito.when(repository.searchByTitle(any(), any())).thenReturn(movieEntityPage);

		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(movieEntity));
		Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

	}

	
	@Test
	public void findAllShouldReturnPagedMovieDTO() {
		Page<MovieDTO> result = service.findAll(defaultTitle, pageable);
		Assertions.assertNotNull(result);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(1, result.getTotalElements());
		Assertions.assertEquals(MovieDTO.class, result.getContent().get(0).getClass());
	}
	
	@Test
	public void findByIdShouldReturnMovieDTOWhenIdExists() {
		MovieDTO result = service.findById(existingId);
		Assertions.assertNotNull(result);
	}
	
	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistingId);
		});
	}
	
	@Test
	public void insertShouldReturnMovieDTO() {
	}
	
	@Test
	public void updateShouldReturnMovieDTOWhenIdExists() {
	}
	
	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
	}
	
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
	}
}
