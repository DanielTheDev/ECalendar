package com.danielthedev.ecalendar.application.services;

import java.util.ArrayList;
import java.util.List;

import com.danielthedev.ecalendar.domain.entities.CalendarEntity;
import com.danielthedev.ecalendar.domain.entities.UserEntity;
import com.danielthedev.ecalendar.persistence.repositories.CalendarRepository;

public class CalendarService {

	private final CalendarRepository calendarRepository = new CalendarRepository();
	
	public List<Object> getCalendarList(UserEntity user) {
		
		List<Object> calendars = new ArrayList<>();
		
		calendars.addAll(user.getOwnedCalendars());
		calendars.addAll(user.getSharedCalendars());

		return calendars;
	}

	public ServiceResult<CalendarEntity> createCalendar(UserEntity user, String name) {
		boolean duplicate = user.getOwnedCalendars().stream().anyMatch(calendar->calendar.getName().equalsIgnoreCase(name));
		if(duplicate) {
			return new ServiceResult<CalendarEntity>("calendar with that name already exists");
		}
		CalendarEntity calendar = this.calendarRepository.createCalendar(new CalendarEntity(name, user));
		
		return new ServiceResult<CalendarEntity>(calendar);
	}
	
	public ServiceResult<Boolean> deleteCalendar(UserEntity user, int calendarID) {
		
		if(user.getOwnedCalendars().size() + user.getSharedCalendars().size() == 1) {
			return new ServiceResult<Boolean>("must remain at least one calendar");
		}
		
		CalendarEntity calendar = user.getCalendarByID(calendarID);
		
		if(calendar == null) return new ServiceResult<Boolean>("calendar with that id does not exists");
		
		this.calendarRepository.deleteCalendar(calendar);
		
		return new ServiceResult<Boolean>(true);
	}

	public ServiceResult<CalendarEntity> editCalendar(UserEntity user, int calendarID, String name) {
		CalendarEntity calendar = user.getCalendarByID(calendarID);
		
		if(calendar == null) return new ServiceResult<CalendarEntity>("calendar with that id does not exists");
		
		return new ServiceResult<CalendarEntity>(this.calendarRepository.editCalendar(calendar.update(name)));
	}
}
