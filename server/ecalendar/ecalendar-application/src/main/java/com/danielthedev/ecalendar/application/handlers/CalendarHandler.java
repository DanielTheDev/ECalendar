package com.danielthedev.ecalendar.application.handlers;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.danielthedev.ecalendar.application.context.ECalenderContext;
import com.danielthedev.ecalendar.application.services.CalendarService;
import com.danielthedev.ecalendar.application.services.ServiceResult;
import com.danielthedev.ecalendar.domain.entities.CalendarEntity;
import com.danielthedev.ecalendar.domain.entities.SharedCalendarEntity;
import com.danielthedev.ecalendar.domain.entities.UserEntity;

public class CalendarHandler {

	private final CalendarService calendarService = new CalendarService();
	
	public void list(ECalenderContext ctx) {
		UserEntity user = ctx.getLoggedInUser();
		
		List<Object> calendars = this.calendarService.getCalendarList(user);
		
		JSONArray sharedCalendars = ctx.array();
		JSONArray ownedCalendars = ctx.array();
		
		calendars.forEach((c)->{
			if(c instanceof CalendarEntity) {
				ownedCalendars.put(((CalendarEntity) c).toJSON(user));
			} else {
				sharedCalendars.put(((SharedCalendarEntity) c).toJSON(user));
			}
		});

		ctx.result(ctx.json()
			.put("calendars", ownedCalendars)
			.put("sharedcalendars", sharedCalendars)
		);
	}
	
	public void create(ECalenderContext ctx) {
		UserEntity user = ctx.getLoggedInUser();
		JSONObject json = ctx.getPayload();

		if(!json.has("name")) ctx.error("missing name");
		
		String name = json.getString("name");
		
		if(!this.isNameValid(name)) ctx.error("name invalid");
		
		ServiceResult<CalendarEntity> calendarEntity = this.calendarService.createCalendar(user, name);
		
		if(!calendarEntity.isSuccess()) ctx.error(calendarEntity.getError());
		
		ctx.result(calendarEntity.getResult().toJSON(user));
	}

	public void delete(ECalenderContext ctx) {
		UserEntity user = ctx.getLoggedInUser();
		
		int calendarID = ctx.getPathID("calendarID");

		ServiceResult<Boolean> result = this.calendarService.deleteCalendar(user, calendarID);
		
		if(!result.isSuccess()) ctx.error(result.getError());
		
		ctx.success();
	}

	public void edit(ECalenderContext ctx) {
		UserEntity user = ctx.getLoggedInUser();
		JSONObject json = ctx.getPayload();

		if(!json.has("name")) ctx.error("missing name");
		
		String name = json.getString("name");
		
		if(!this.isNameValid(name)) ctx.error("name invalid");
		
		int calendarID = ctx.getPathID("calendarID");
		
		ServiceResult<CalendarEntity> calendarEntity = this.calendarService.editCalendar(user, calendarID, name);
		
		if(!calendarEntity.isSuccess()) ctx.error(calendarEntity.getError());
		
		ctx.result(calendarEntity.getResult().toJSON(user));
	}
	
	private boolean isNameValid(String name) {
		return name.length() >= 3 && name.length() <= 32 && name.matches("^[a-zA-Z0-9\s]+$");
	}	
}
