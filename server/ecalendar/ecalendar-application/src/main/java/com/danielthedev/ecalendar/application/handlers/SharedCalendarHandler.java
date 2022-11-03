package com.danielthedev.ecalendar.application.handlers;

import java.util.List;

import org.json.JSONObject;

import com.danielthedev.ecalendar.application.context.ECalenderContext;
import com.danielthedev.ecalendar.application.services.ServiceResult;
import com.danielthedev.ecalendar.application.services.SharedCalendarService;
import com.danielthedev.ecalendar.domain.entities.SharedCalendarEntity;
import com.danielthedev.ecalendar.domain.entities.UserEntity;
import com.danielthedev.ecalendar.domain.enums.Permission;

public class SharedCalendarHandler {
	
	private final SharedCalendarService sharedCalendarService = new SharedCalendarService();
	
	public void add(ECalenderContext ctx) {
		UserEntity user = ctx.getLoggedInUser();

		int calendarID = ctx.getPathID("calendarID");
		int userID = ctx.getPathID("userID");
		
		JSONObject json = ctx.getPayload();
		
		if(!json.has("accessPermissions")) ctx.error("missing accessPermissions");
		
		List<Permission> accessPermissions = Permission.parse(json.getInt("accessPermissions"));
		if(accessPermissions == null) ctx.error("invalid accessPermissions");
		
		ServiceResult<SharedCalendarEntity> result = this.sharedCalendarService.addSharedCalendar(user, calendarID, userID, accessPermissions);
		if(!result.isSuccess()) ctx.error(result.getError());
		
		ctx.result(result.getResult().getCalendar().toJSON(user));
	}

	public void edit(ECalenderContext ctx) {
		UserEntity user = ctx.getLoggedInUser();

		int calendarID = ctx.getPathID("calendarID");
		int userID = ctx.getPathID("userID");
		
		JSONObject json = ctx.getPayload();
		
		if(!json.has("accessPermissions")) ctx.error("missing accessPermissions");
		
		List<Permission> accessPermissions = Permission.parse(json.getInt("accessPermissions"));
		if(accessPermissions == null) ctx.error("invalid accessPermissions");
		
		ServiceResult<SharedCalendarEntity> result = this.sharedCalendarService.editSharedCalendar(user, calendarID, userID, accessPermissions);
		if(!result.isSuccess()) ctx.error(result.getError());
		
		System.out.println(result.getResult().toJSON(user));
		
		ctx.result(result.getResult().getCalendar().toJSON(user));
	}

	public void remove(ECalenderContext ctx) {
		UserEntity user = ctx.getLoggedInUser();

		int calendarID = ctx.getPathID("calendarID");
		int userID = ctx.getPathID("userID");

		ServiceResult<Boolean> result = this.sharedCalendarService.removeSharedCalendar(user, calendarID, userID);
		if(!result.isSuccess()) ctx.error(result.getError());
		
		ctx.success();
	}
}
