package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.services.exceptions.DatabaseException;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import java.util.List;
import java.util.Optional;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class MovieServiceTests {
	
	@InjectMocks
	private MovieService service;

	@Mock
	private MovieRepository repository;

	private MovieDTO movieDTO;
	private MovieEntity movieEntity;
	private Page<MovieEntity> movieEntityPage;
	private List<MovieEntity> movieEntityListToPage;
	private Pageable pageable;

	private String defaultTitle;

	private Long existingId, nonExistingId, integrityIdexisting;

	@BeforeEach
	void setUp(){
		movieDTO = MovieFactory.createMovieDTO();
		movieEntity = MovieFactory.createMovieEntity();
		movieEntityListToPage = List.of(movieEntity);
		pageable = PageRequest.of(0, 10);
		defaultTitle = "Movie";
		movieEntityPage = new PageImpl<>(movieEntityListToPage, pageable, 1);

		existingId =1L;
		nonExistingId =2L;
		integrityIdexisting =3L;


		when(repository.searchByTitle(any(), any())).thenReturn(movieEntityPage);

		when(repository.findById(existingId)).thenReturn(Optional.of(movieEntity));
		when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

		when(repository.save(any())).thenReturn(movieEntity);

		when(repository.getReferenceById(existingId)).thenReturn(movieEntity);
		when(repository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

		when(repository.existsById(existingId)).thenReturn(true);
		when(repository.existsById(nonExistingId)).thenReturn(false);
		when(repository.existsById(integrityIdexisting)).thenReturn(true);

		doThrow(DataIntegrityViolationException.class)
				.when(repository)
				.deleteById(integrityIdexisting);


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
		MovieDTO result = service.insert(movieDTO);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getImage(), movieEntity.getImage());

	}
	
	@Test
	public void updateShouldReturnMovieDTOWhenIdExists() {
		MovieDTO result = service.update(existingId, movieDTO);

		Assertions.assertNotNull(existingId);
		verify(repository, times(1)).getReferenceById(existingId);
	}
	
	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.update(nonExistingId, movieDTO);
		});
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingId);
		});

	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
	});

	}
	
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
		Assertions.assertThrows(DatabaseException.class, () -> {
			service.delete(integrityIdexisting);
		});
	}
}

