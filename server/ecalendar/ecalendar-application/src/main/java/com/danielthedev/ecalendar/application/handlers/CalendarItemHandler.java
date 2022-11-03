package com.danielthedev.ecalendar.application.handlers;

import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.danielthedev.ecalendar.application.context.ECalenderContext;
import com.danielthedev.ecalendar.application.services.CalendarItemService;
import com.danielthedev.ecalendar.application.services.ServiceResult;
import com.danielthedev.ecalendar.domain.entities.CalendarItemEntity;
import com.danielthedev.ecalendar.domain.entities.UserEntity;
import com.danielthedev.ecalendar.domain.enums.ItemColor;
import com.danielthedev.ecalendar.domain.enums.Notification;
import com.danielthedev.ecalendar.domain.enums.RepeatingType;

public class CalendarItemHandler {

	private final CalendarItemService calendarItemService = new CalendarItemService();
	
	public void get(ECalenderContext ctx) {
		
		UserEntity user = ctx.getLoggedInUser();
		
		JSONObject json = ctx.getPayload();
		
		int calendarID = ctx.getPathID("calendarID");
		
		if(!json.has("year")) ctx.error("missing year");
		
		if(!json.has("month")) ctx.error("missing month");
		
		int year = json.getInt("year");
		if(year < 1960) ctx.error("year invalid");
		
		int month = json.getInt("month");
		if(month < 0 || month > 12) ctx.error("month invalid");
		
		ServiceResult<List<CalendarItemEntity>> result = this.calendarItemService.getCalendarItems(user, calendarID, year, month);
		
		if(!result.isSuccess()) ctx.error(result.getError());
		
		JSONArray calendarItems = ctx.array();
		
		result.getResult().forEach(calendarItem->{
			calendarItems.put(calendarItem.toJSON());
		});
		
		ctx.result(ctx.json().put("list", calendarItems));
		
	}

	public void create(ECalenderContext ctx) {
		UserEntity user = ctx.getLoggedInUser();
		
		JSONObject json = ctx.getPayload();

		int calendarID = ctx.getPathID("calendarID");
		
		if(!json.has("title")) ctx.error("missing title");
		if(!json.has("description")) ctx.error("missing description");
		if(!json.has("startDate")) ctx.error("missing startDate");
		if(!json.has("endDate")) ctx.error("missing endDate");
		if(!json.has("color")) ctx.error("missing color");
		if(!json.has("notifications")) ctx.error("missing notifications");
		
		String title = json.getString("title");
		if(!this.isTitleValid(title)) ctx.error("title invalid");
		
		String description = json.getString("description");
		if(!this.isDescriptionValid(description)) ctx.error("description invalid");
		
		Date startDate = ctx.safe(()->CalendarItemEntity.API_DATE_FORMAT.parse(json.getString("startDate")), "invalid startDate");
		Date endDate = ctx.safe(()->CalendarItemEntity.API_DATE_FORMAT.parse(json.getString("endDate")), "invalid endDate");
		if(startDate.after(endDate)) ctx.error("startDate must be before endDate");
		
		ItemColor color = ItemColor.getItemColorById(json.getInt("color"));
		if(color == null) ctx.error("invalid color");
		
		List<Notification> notifications = Notification.parse(json.getInt("notifications"));
		if(notifications == null) ctx.error("invalid notifications");
		
		
		ServiceResult<CalendarItemEntity> result = null;
		
		if(json.has("repeat")) {
			
			JSONObject repeatJson = json.getJSONObject("repeat");
			
			if(!repeatJson.has("intervalType")) ctx.error("missing repeat.intervalType");
			if(!repeatJson.has("amount")) ctx.error("missing repeat.amount");
			if(!repeatJson.has("stopDate")) ctx.error("missing repeat.stopDate");
			
			RepeatingType repeatingType = RepeatingType.getRepeatingTypeById(repeatJson.getInt("intervalType"));
			int amount = repeatJson.getInt("amount");
			Date stopDate = ctx.safe(()->CalendarItemEntity.API_DATE_FORMAT.parse(repeatJson.getString("stopDate")), "invalid stopDate");
			
			if(repeatingType == null) ctx.error("invalid repeatingAttribute.intervalType");
			if(amount < 1) ctx.error("invalid repeatingAttribute.amount");
			if(stopDate.before(endDate)) ctx.error("invalid repeatingAttribute.stopDate");

			result = this.calendarItemService.createCalendarItem(user, calendarID, title, description, startDate, endDate, color, notifications, repeatingType, amount, stopDate);
		} else {
			result = this.calendarItemService.createCalendarItem(user, calendarID, title, description, startDate, endDate, color, notifications, null, 0, null);
		}
		
		if(!result.isSuccess()) ctx.error(result.getError());
		ctx.result(result.getResult().toJSON());
	}

	public void edit(ECalenderContext ctx) {
		UserEntity user = ctx.getLoggedInUser();
		
		JSONObject json = ctx.getPayload();

		int calendarItemID = ctx.getPathID("calendarItemID");
		int calendarID = ctx.getPathID("calendarID");
		
		if(!json.has("title")) ctx.error("missing title");
		if(!json.has("description")) ctx.error("missing description");
		if(!json.has("startDate")) ctx.error("missing startDate");
		if(!json.has("endDate")) ctx.error("missing endDate");
		if(!json.has("color")) ctx.error("missing color");
		if(!json.has("notifications")) ctx.error("missing notifications");
		
		String title = json.getString("title");
		if(!this.isTitleValid(title)) ctx.error("title invalid");
		
		String description = json.getString("description");
		if(!this.isDescriptionValid(description)) ctx.error("description invalid");
		
		Date startDate = ctx.safe(()->CalendarItemEntity.API_DATE_FORMAT.parse(json.getString("startDate")), "invalid startDate");
		Date endDate = ctx.safe(()->CalendarItemEntity.API_DATE_FORMAT.parse(json.getString("endDate")), "invalid endDate");
		if(startDate.after(endDate)) ctx.error("startDate must be before endDate");
		
		ItemColor color = ItemColor.getItemColorById(json.getInt("color"));
		if(color == null) ctx.error("invalid color");
		
		List<Notification> notifications = Notification.parse(json.getInt("notifications"));
		if(notifications == null) ctx.error("invalid notifications");
		
		ServiceResult<CalendarItemEntity> result = null;
		
		if(json.has("repeat")) {
			
			JSONObject repeatJson = json.getJSONObject("repeat");
			
			if(!repeatJson.has("intervalType")) ctx.error("missing repeat.intervalType");
			if(!repeatJson.has("amount")) ctx.error("missing repeat.amount");
			if(!repeatJson.has("stopDate")) ctx.error("missing repeat.stopDate");
			
			RepeatingType repeatingType = RepeatingType.getRepeatingTypeById(repeatJson.getInt("intervalType"));
			int amount = repeatJson.getInt("amount");
			Date stopDate = ctx.safe(()->CalendarItemEntity.API_DATE_FORMAT.parse(repeatJson.getString("stopDate")), "invalid stopDate");
			
			if(repeatingType == null) ctx.error("invalid repeatingAttribute.intervalType");
			if(amount < 1) ctx.error("invalid repeatingAttribute.amount");
			if(stopDate.before(endDate)) ctx.error("invalid repeatingAttribute.stopDate");

			result = this.calendarItemService.editCalendarItem(user, calendarItemID, calendarID, title, description, startDate, endDate, color, notifications, repeatingType, amount, stopDate);
		} else {
			result = this.calendarItemService.editCalendarItem(user, calendarItemID, calendarID, title, description, startDate, endDate, color, notifications, null, 0, null);
		}
		
		if(!result.isSuccess()) ctx.error(result.getError());
		ctx.result(result.getResult().toJSON());
	}

	public void delete(ECalenderContext ctx) {
		
		UserEntity user = ctx.getLoggedInUser();
		int calendarID = ctx.getPathID("calendarID");
		int calendarItemID = ctx.getPathID("calendarItemID");
		
		ServiceResult<Boolean> result = this.calendarItemService.deleteCalendarItem(user, calendarID, calendarItemID);
		
		if(!result.isSuccess()) ctx.error(result.getError());
		
		ctx.success();
	}


	private boolean isTitleValid(String name) {
		return name.length() >= 3 && name.length() <= 32 && name.matches("^[a-zA-Z0-9\s]+$");
	}
	
	private boolean isDescriptionValid(String description) {
		return description.length() <= 255;
	}
}
