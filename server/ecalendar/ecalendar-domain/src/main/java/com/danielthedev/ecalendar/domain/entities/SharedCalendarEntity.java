package com.danielthedev.ecalendar.domain.entities;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.json.JSONObject;

import com.danielthedev.ecalendar.domain.converters.PermissionsConverter;
import com.danielthedev.ecalendar.domain.enums.Permission;

@Entity
@Table(name = "SharedCalendar")
public class SharedCalendarEntity implements IEntity {

	@Id
	@Column(name="ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ID;
	
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userID")
	private UserEntity user;
	
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "calendarID")
	private CalendarEntity calendar;
	
	@Convert(converter = PermissionsConverter.class)
	@Column(name = "accessPermissions", nullable = false)
	private List<Permission> accessPermissions;
	
	public SharedCalendarEntity(UserEntity user, CalendarEntity calendar, List<Permission> accessPermissions) {
		this.user = user;
		this.calendar = calendar;
		this.accessPermissions = accessPermissions;
	}

	private SharedCalendarEntity() {}

	public SharedCalendarEntity update(List<Permission> accessPermissions) {
		this.accessPermissions = accessPermissions;
		return this;
	}
	
	public int getID() {
		return ID;
	}

	public UserEntity getUser() {
		return user;
	}

	public CalendarEntity getCalendar() {
		return calendar;
	}

	public List<Permission> getAccessPermissions() {
		return accessPermissions;
	}
	
	public JSONObject toJSON(UserEntity user) {
		JSONObject calendar = this.getCalendar().toJSON(user);
		if(this.getCalendar().getOwner().getID() != user.getID()) {
			calendar.put("owner", this.getCalendar().getOwner().toJSON());
		}
		
		return new JSONObject()
				.put("accessPermissions", Permission.createMask(this.getAccessPermissions()))
				.put("calendar", calendar);
	}
}
