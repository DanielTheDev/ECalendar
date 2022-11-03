package com.danielthedev.ecalendar.domain.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "SharedCalendarItem")
public class SharedCalendarItemEntity implements IEntity {
	
	@Id
	@Column(name="ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ID;
	
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userID")
	private UserEntity user;
	
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "calendarItemID")
	private CalendarItemEntity calendarItem;
	
	public SharedCalendarItemEntity(UserEntity user, CalendarItemEntity calendarItem) {
		this.user = user;
		this.calendarItem = calendarItem;
	}

	private SharedCalendarItemEntity() {}

	public int getID() {
		return ID;
	}

	public UserEntity getUser() {
		return user;
	}

	public CalendarItemEntity getCalendarItem() {
		return calendarItem;
	}

}
