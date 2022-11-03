package com.danielthedev.ecalendar.domain.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.NaturalId;
import org.json.JSONObject;

@Entity
@Table(name = "User", uniqueConstraints = { @UniqueConstraint(columnNames = { "email" , "username"})})
public class UserEntity implements IEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private int ID;

	@NaturalId
	@Column(name = "email", length = 320, nullable = false)
	private String email;

	@Column(name = "username", length = 32, nullable = false)
	private String username;

	@Column(name = "password", length = 64, nullable = false)
	private String password;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<SharedCalendarEntity> sharedCalendars = new ArrayList<SharedCalendarEntity>();
	
	@OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
	private List<CalendarEntity> ownedCalendars = new ArrayList<CalendarEntity>();
	
	public UserEntity(String email, String username, String password) {
		this.email = email;
		this.username = username;
		this.password = password;
	}

	private UserEntity() {}

	public int getID() {
		return ID;
	}

	public String getEmail() {
		return email;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public List<SharedCalendarEntity> getSharedCalendars() {
		return sharedCalendars;
	}

	public List<CalendarEntity> getOwnedCalendars() {
		return ownedCalendars;
	}

	public CalendarEntity getCalendarByID(int calendarID) {
		Optional<CalendarEntity> op = this.getOwnedCalendars().stream().filter(calendar->calendar.getID() == calendarID).findFirst();
		if(op.isEmpty()) {
			return null;
		} else {
			return op.get();
		}
	} 
	
	public SharedCalendarEntity getSharedCalendarByID(int calendarID) {
		Optional<SharedCalendarEntity> op = this.getSharedCalendars().stream().filter(calendar->calendar.getCalendar().getID() == calendarID).findFirst();
		if(op.isEmpty()) {
			return null;
		} else {
			return op.get();
		}
	} 


	public JSONObject toJSON() {
		return new JSONObject()
				.put("ID", this.getID())
				.put("username", this.getUsername());
	}
}
