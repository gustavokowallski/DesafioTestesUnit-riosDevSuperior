package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.ScoreFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class ScoreServiceTests {
	
	@InjectMocks
	private ScoreService service;

	@Mock
	private MovieRepository movieRepository;

	@Mock
	private ScoreRepository scoreRepository;

	@Mock
	private UserService userService;

	private ScoreDTO scoreDTO;
	private MovieEntity movie;
	private UserEntity user;

	private ScoreEntity score;


	@BeforeEach
	void setUp(){
		scoreDTO = ScoreFactory.createScoreDTO();
		movie = MovieFactory.createMovieEntity();
		user = UserFactory.createUserEntity();

		score = ScoreFactory.createScoreEntity();
		score.setValue(ScoreFactory.scoreValue);

		movie.getScores().add(score);


		Mockito.when(userService.authenticated())
				.thenReturn(user);

		Mockito.when(movieRepository.findById(any()))
				.thenReturn(Optional.of(movie));

		Mockito.when(movieRepository.save(any())).thenReturn(movie);

		Mockito.when(scoreRepository.saveAndFlush(any())).thenReturn(score);


	}
	
	@Test
	public void saveScoreShouldReturnMovieDTO() {

		MovieDTO result = service.saveScore(scoreDTO);

		Assertions.assertNotNull(result);
	}
	
	@Test
	public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {
		Mockito.when(movieRepository.findById(any()))
				.thenReturn(Optional.empty());
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.saveScore(scoreDTO);
		});

	}
}
