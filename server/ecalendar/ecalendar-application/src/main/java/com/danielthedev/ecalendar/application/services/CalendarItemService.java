package com.danielthedev.ecalendar.application.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.danielthedev.ecalendar.domain.entities.CalendarEntity;
import com.danielthedev.ecalendar.domain.entities.CalendarItemEntity;
import com.danielthedev.ecalendar.domain.entities.RepeatingAttribute;
import com.danielthedev.ecalendar.domain.entities.SharedCalendarEntity;
import com.danielthedev.ecalendar.domain.entities.UserEntity;
import com.danielthedev.ecalendar.domain.enums.ItemColor;
import com.danielthedev.ecalendar.domain.enums.Notification;
import com.danielthedev.ecalendar.domain.enums.Permission;
import com.danielthedev.ecalendar.domain.enums.RepeatingType;
import com.danielthedev.ecalendar.persistence.repositories.CalendarItemRepository;

public class CalendarItemService {

	private final CalendarItemRepository calendarItemRepository = new CalendarItemRepository();
	
	public ServiceResult<CalendarItemEntity> createCalendarItem(UserEntity user, int calendarID, String title, String description, Date startDate, Date endDate, ItemColor color, List<Notification> notifications, RepeatingType repeatingType, int repeatingAmount, Date repeatingStopDate) {
		
		CalendarEntity calendarEntity = user.getCalendarByID(calendarID);
		CalendarItemEntity calendarItemEntity = null;
		
		if(calendarEntity == null) {
			
			SharedCalendarEntity sharedCalendarEntity = user.getSharedCalendarByID(calendarID);
			
			if(sharedCalendarEntity == null) {
				return new ServiceResult<CalendarItemEntity>("calendarID invalid");
			} else if(!Permission.hasPermission(sharedCalendarEntity.getAccessPermissions(), Permission.ITEM_CREATE)) {
				return new ServiceResult<CalendarItemEntity>("missing permissions");
			}
			calendarEntity = sharedCalendarEntity.getCalendar();
		}
		
		RepeatingAttribute repeatingAttribute = null;
		if(repeatingType != null) {
			repeatingAttribute = new RepeatingAttribute(repeatingType, repeatingAmount, repeatingStopDate);
		}
		
		calendarItemEntity = new CalendarItemEntity(calendarEntity, title, description, startDate, endDate, color, notifications, repeatingAttribute);
		
		return new ServiceResult<CalendarItemEntity>(this.calendarItemRepository.createCalendarItem(calendarItemEntity));
		
	}

	public ServiceResult<CalendarItemEntity> editCalendarItem(UserEntity user, int calendarItemID, int calendarID, String title, String description, Date startDate, Date endDate, ItemColor color, List<Notification> notifications, RepeatingType repeatingType, int repeatingAmount, Date repeatingStopDate) {
		CalendarEntity calendarEntity = user.getCalendarByID(calendarID);

		if(calendarEntity == null) {
			
			SharedCalendarEntity sharedCalendarEntity = user.getSharedCalendarByID(calendarID);
			
			if(sharedCalendarEntity == null) {
				return new ServiceResult<CalendarItemEntity>("calendarID invalid");
			} else if(!Permission.hasPermission(sharedCalendarEntity.getAccessPermissions(), Permission.ITEM_EDIT)) {
				return new ServiceResult<CalendarItemEntity>("missing permissions");
			}
		}
		
		CalendarItemEntity calendarItem = this.calendarItemRepository.getCalendarItem(calendarEntity, calendarItemID);

		if(calendarItem == null) {
			return new ServiceResult<CalendarItemEntity>("calendarItemID invalid");
		} 
		
		RepeatingAttribute repeatingAttribute = null;
		if(repeatingType != null) {
			repeatingAttribute = new RepeatingAttribute(repeatingType, repeatingAmount, repeatingStopDate);
		}
		calendarItem.update(calendarEntity, title, description, startDate, endDate, color, notifications, repeatingAttribute);
		return new ServiceResult<CalendarItemEntity>(this.calendarItemRepository.editCalendarItem(calendarItem));
	}

	public ServiceResult<Boolean> deleteCalendarItem(UserEntity user, int calendarID, int calendarItemID) {
		
		CalendarEntity calendarEntity = user.getCalendarByID(calendarID);

		if(calendarEntity == null) {
			
			SharedCalendarEntity sharedCalendarEntity = user.getSharedCalendarByID(calendarID);
			
			if(sharedCalendarEntity == null) {
				return new ServiceResult<Boolean>("calendarID invalid");
			} else if(!Permission.hasPermission(sharedCalendarEntity.getAccessPermissions(), Permission.ITEM_DELETE)) {
				return new ServiceResult<Boolean>("missing permissions");
			}
		}
		
		CalendarItemEntity calendarItem = this.calendarItemRepository.getCalendarItem(calendarEntity, calendarItemID);

		if(calendarItem == null) {
			return new ServiceResult<Boolean>("calendarItemID invalid");
		}
		this.calendarItemRepository.deleteCalendarItem(calendarItem);

		return new ServiceResult<Boolean>(true);
	}

	public ServiceResult<List<CalendarItemEntity>> getCalendarItems(UserEntity user, int calendarID, int year, int month) {
		
		CalendarEntity calendarEntity = user.getCalendarByID(calendarID);

		if(calendarEntity == null) {
			
			SharedCalendarEntity sharedCalendarEntity = user.getSharedCalendarByID(calendarID);
			
			if(sharedCalendarEntity == null) {
				return new ServiceResult<List<CalendarItemEntity>>("calendarID invalid");
			} else {
				calendarEntity = sharedCalendarEntity.getCalendar();
			}
		}

		List<CalendarItemEntity> list = new ArrayList<CalendarItemEntity>();
		
		list.addAll(this.calendarItemRepository.getCalendarItems(calendarEntity, year, month));
		list.addAll(this.calendarItemRepository.getRepeatingCalendarItems(calendarEntity));
		return new ServiceResult<List<CalendarItemEntity>>(list);
	}

}
