package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.projections.UserDetailsProjection;
import com.devsuperior.dsmovie.repositories.UserRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.UserDetailsFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import com.devsuperior.dsmovie.utils.CustomUserUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class UserServiceTests {

	@InjectMocks
	private UserService service;


	@Mock
	private CustomUserUtil customUserUtil;

	@Mock
	private UserRepository repository;


	private UserEntity user;
	private List<UserDetailsProjection> userDetailsProjection, emptyUserDetailsList;
	private String validUserName, invalidUserName;

	@BeforeEach
	void setUp() {
		user = UserFactory.createUserEntity();
		validUserName = "validUserName";
		invalidUserName = "invalidUserName";
		userDetailsProjection = UserDetailsFactory.createCustomAdminUser(validUserName);
		emptyUserDetailsList = new ArrayList<>();


		Mockito.when(customUserUtil.getLoggedUsername())
				.thenReturn(validUserName);

		Mockito.when(repository.findByUsername(validUserName))
				.thenReturn(Optional.of(user));

		Mockito.when(repository.searchUserAndRolesByUsername(validUserName))
				.thenReturn(userDetailsProjection);

		Mockito.when(repository.searchUserAndRolesByUsername(invalidUserName))
				.thenReturn(emptyUserDetailsList);

	}

	@Test
	public void authenticatedShouldReturnUserEntityWhenUserExists() {
		UserEntity result = service.authenticated();

		Assertions.assertNotNull(result);
		verify(customUserUtil, times(1)).getLoggedUsername();
		verify(repository, times(1)).findByUsername(validUserName);
	}

	@Test
	public void authenticatedShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
		Mockito.when(customUserUtil.getLoggedUsername())
				.thenThrow(ResourceNotFoundException.class);
		Assertions.assertThrows(UsernameNotFoundException.class, () -> {
			service.authenticated();
		});
	}

	@Test
	public void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {
		UserDetails result = service.loadUserByUsername(validUserName);
		Assertions.assertNotNull(result);
	}

	@Test
	public void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
		Assertions.assertThrows(UsernameNotFoundException.class, () -> {
			service.loadUserByUsername(invalidUserName);
		});
	}

}