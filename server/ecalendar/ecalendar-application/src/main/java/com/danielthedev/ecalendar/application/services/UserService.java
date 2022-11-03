package com.danielthedev.ecalendar.application.services;

import com.danielthedev.ecalendar.application.context.JWTToken;
import com.danielthedev.ecalendar.application.context.JWTToken.JWTPayload;
import com.danielthedev.ecalendar.domain.entities.CalendarEntity;
import com.danielthedev.ecalendar.domain.entities.UserEntity;
import com.danielthedev.ecalendar.persistence.repositories.CalendarRepository;
import com.danielthedev.ecalendar.persistence.repositories.UserRepository;

public class UserService {

	private final UserRepository userRepository = new UserRepository();
	private final CalendarRepository calendarRepository = new CalendarRepository();
	
	public JWTToken login(String email, String password) {
		UserEntity entity = this.userRepository.getUserByEmail(email);
		JWTToken token = null;
		
		if(entity != null && entity.getPassword().equals(password)) {
			token = new JWTToken(new JWTPayload(entity.getID(), entity.getUsername()));
		}
		return token;
	}

	public ServiceResult<UserEntity> register(String email, String username, String password) {
		
		if(this.userRepository.getUserByEmail(email) != null) {
			return new ServiceResult<UserEntity>("email already exists");
		} else if(this.userRepository.getUserByUsername(username) != null) {
			return new ServiceResult<UserEntity>("username already exists");
		}

		UserEntity userEntity = new UserEntity(email, username, password);
		userEntity = this.userRepository.registerUser(userEntity);
		this.calendarRepository.createCalendar(new CalendarEntity("New Calendar", userEntity));
		return new ServiceResult<UserEntity>(userEntity);
	}

	public UserEntity getUser(String username) {
		UserEntity entity = this.userRepository.getUserByUsername(username);
		return entity;
	}
}
