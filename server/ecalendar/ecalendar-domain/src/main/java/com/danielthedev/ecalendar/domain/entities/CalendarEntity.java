package com.danielthedev.ecalendar.domain.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.json.JSONArray;
import org.json.JSONObject;

import com.danielthedev.ecalendar.domain.enums.Permission;


@Entity
@Table(name = "Calendar")
public class CalendarEntity implements IEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private int ID;

	@Column(name = "name", length = 32, nullable = false)
	private String name;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ownerID", nullable = false)
	private UserEntity owner;
	
	@OneToMany(mappedBy = "calendar", fetch = FetchType.LAZY)
	private List<SharedCalendarEntity> sharedCalendars = new ArrayList<SharedCalendarEntity>();
	
	public CalendarEntity(String name, UserEntity owner) {
		this.name = name;
		this.owner = owner;
	}

	private CalendarEntity() {}

	public CalendarEntity update(String name) {
		this.name = name;
		return this;
	}
	
	public int getID() {
		return ID;
	}

	public String getName() {
		return name;
	}

	public UserEntity getOwner() {
		return owner;
	}

	public List<SharedCalendarEntity> getSharedCalendars() {
		return sharedCalendars;
	}
	
	public JSONObject toJSON(UserEntity user) {
		JSONObject json = new JSONObject()
				.put("ID", this.getID())
				.put("name", this.getName());
		
		if(user.getID() == this.owner.getID()) {
			JSONArray sharedUsers = new JSONArray();
			this.sharedCalendars.forEach(sharedCalendar->{
				
				JSONObject sharedUser = new JSONObject();
				
				sharedUser.put("user", sharedCalendar.getUser().toJSON());
				sharedUser.put("accessPermissions", Permission.createMask(sharedCalendar.getAccessPermissions()));	
				sharedUsers.put(sharedUser);
			});
			json.put("sharedUsers", sharedUsers);
		}

		
		return json;
	}
	
}
