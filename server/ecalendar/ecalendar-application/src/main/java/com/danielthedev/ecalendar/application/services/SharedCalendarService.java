package com.danielthedev.ecalendar.application.services;

import java.util.List;
import java.util.Optional;

import com.danielthedev.ecalendar.domain.entities.CalendarEntity;
import com.danielthedev.ecalendar.domain.entities.SharedCalendarEntity;
import com.danielthedev.ecalendar.domain.entities.UserEntity;
import com.danielthedev.ecalendar.domain.enums.Permission;
import com.danielthedev.ecalendar.persistence.repositories.SharedCalendarRepository;
import com.danielthedev.ecalendar.persistence.repositories.UserRepository;

public class SharedCalendarService {

	private final SharedCalendarRepository sharedCalendarRepository = new SharedCalendarRepository();
	private final UserRepository userRepository = new UserRepository();
	
	public ServiceResult<Boolean> removeSharedCalendar(UserEntity user, int calendarID, int userID) {
		UserEntity target = this.userRepository.getUserWithCalendarsById(userID);
		
		if(target == null) {
			return new ServiceResult<Boolean>("userID is invalid");
		} else if(target.getID() == user.getID()) {
			SharedCalendarEntity shared = user.getSharedCalendarByID(calendarID);
			if(shared == null) {
				return new ServiceResult<Boolean>("cannot remove share with yourself");
			} else {
				this.sharedCalendarRepository.removeSharedCalendar(shared);
				return new ServiceResult<Boolean>(true);	
			}
			
		}

		CalendarEntity calendarEntity = user.getCalendarByID(calendarID);
		
		if(calendarEntity == null) {
			return new ServiceResult<Boolean>("calendarID is invalid");
		}

		if(target.getSharedCalendarByID(calendarID) == null) {
			return new ServiceResult<Boolean>("calendar is not shared with this user");
		}
		
		this.sharedCalendarRepository.removeSharedCalendar(calendarEntity, target);
		
		return new ServiceResult<Boolean>(true);	
	}
	
	
	public ServiceResult<SharedCalendarEntity> addSharedCalendar(UserEntity user, int calendarID, int userID, List<Permission> accessPermissions) {
		UserEntity target = this.userRepository.getUserWithCalendarsById(userID);	
		
		if(target == null) {
			return new ServiceResult<SharedCalendarEntity>("userID is invalid");
		} else if(target.getID() == user.getID()) {
			return new ServiceResult<SharedCalendarEntity>("cannot share calendar with yourself");
		}

		CalendarEntity calendarEntity = user.getCalendarByID(calendarID);
		
		if(calendarEntity == null) {
			return new ServiceResult<SharedCalendarEntity>("calendarID is invalid");
		} else if(target.getSharedCalendarByID(calendarID) != null) {
			return new ServiceResult<SharedCalendarEntity>("calendar is already being shared with this user");
		}
		
		SharedCalendarEntity sharedCalendar = new SharedCalendarEntity(target, calendarEntity, accessPermissions);
		
		calendarEntity.getSharedCalendars().add(sharedCalendar);
		
		SharedCalendarEntity sharedCalendarEntity = this.sharedCalendarRepository.createSharedCalendar(sharedCalendar);
		
		return new ServiceResult<SharedCalendarEntity>(sharedCalendarEntity);
	}
	
	
	public ServiceResult<SharedCalendarEntity> editSharedCalendar(UserEntity user, int calendarID, int userID, List<Permission> accessPermissions) {
		
		UserEntity target = this.userRepository.getUserById(userID);
		
		if(target == null) {
			return new ServiceResult<SharedCalendarEntity>("userID is invalid");
		} else if(target.getID() == user.getID()) {
			return new ServiceResult<SharedCalendarEntity>("cannot share calendar with yourself");
		}
		
		CalendarEntity calendarEntity = user.getCalendarByID(calendarID);
		
		Optional<SharedCalendarEntity> result = calendarEntity.getSharedCalendars().stream().filter(shared->shared.getUser().getID() == target.getID()).findFirst();

		if(result.isEmpty()) {
			return new ServiceResult<SharedCalendarEntity>("calendar is not shared with this user");
		}
		
		SharedCalendarEntity sharedCalendar = result.get();
	
		sharedCalendar.update(accessPermissions);
		sharedCalendar = this.sharedCalendarRepository.editSharedCalendar(sharedCalendar);
		
		return new ServiceResult<SharedCalendarEntity>(sharedCalendar);
	}
}
