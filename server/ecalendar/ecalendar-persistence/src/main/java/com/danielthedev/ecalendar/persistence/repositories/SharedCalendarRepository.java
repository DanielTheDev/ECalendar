package com.danielthedev.ecalendar.persistence.repositories;

import com.danielthedev.ecalendar.domain.entities.CalendarEntity;
import com.danielthedev.ecalendar.domain.entities.SharedCalendarEntity;
import com.danielthedev.ecalendar.domain.entities.UserEntity;

public class SharedCalendarRepository extends AbstractRepository {

	private static final Class<SharedCalendarEntity> entityClass = SharedCalendarEntity.class; 

	public SharedCalendarEntity createSharedCalendar(SharedCalendarEntity sharedCalendarEntity) {
		return super.insertEntity(sharedCalendarEntity);
	}

	public void removeSharedCalendar(CalendarEntity calendarEntity, UserEntity target) {
		SharedCalendarEntity sharedCalendarEntity = super.loadEntity(calendarEntity, (entity)->{
			return entity.getSharedCalendars().stream().filter(sharedCalendar->sharedCalendar.getUser().getID() == target.getID()).findFirst().get();
		});
		super.deleteEntity(sharedCalendarEntity);
	}

	public void removeSharedCalendar(SharedCalendarEntity sharedCalendar) {
		super.deleteEntity(sharedCalendar);
	}
	
	public SharedCalendarEntity editSharedCalendar(SharedCalendarEntity sharedCalendarEntity) {
		return super.updateEntity(sharedCalendarEntity);
	}
	
}
