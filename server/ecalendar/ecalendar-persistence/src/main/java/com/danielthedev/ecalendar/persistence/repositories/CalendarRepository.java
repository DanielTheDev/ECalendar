package com.danielthedev.ecalendar.persistence.repositories;


import org.hibernate.query.NativeQuery;

import com.danielthedev.ecalendar.domain.entities.CalendarEntity;

public class CalendarRepository extends AbstractRepository {

	private static final Class<CalendarEntity> entityClass = CalendarEntity.class; 

	public CalendarEntity createCalendar(CalendarEntity calendarEntity) {
		return super.insertEntity(calendarEntity);
	}

	public CalendarEntity editCalendar(CalendarEntity update) {
		return super.updateEntity(update);
	}

	public void deleteCalendar(CalendarEntity calendar) {
		super.getDatabase().startTransaction((session)->{
			NativeQuery query = session.createSQLQuery("DELETE CalendarItem, RepeatingAttribute, SharedCalendar FROM CalendarItem\r\n"
					+ "INNER JOIN RepeatingAttribute ON repeatingAttributeID = RepeatingAttribute.ID \r\n"
					+ "INNER JOIN SharedCalendar ON SharedCalendar.calendarID = CalendarItem.calendarID\r\n"
					+ "WHERE CalendarItem.calendarID = :calendar");
			query.setParameter("calendar", calendar.getID());
			query.executeUpdate();
			return null;
		});
		
		super.deleteEntity(calendar);
	}
	
}
