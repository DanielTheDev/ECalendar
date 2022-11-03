package com.danielthedev.ecalendar.domain.entities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.json.JSONObject;

import com.danielthedev.ecalendar.domain.converters.ItemColorConverter;
import com.danielthedev.ecalendar.domain.converters.NotificationsConverter;
import com.danielthedev.ecalendar.domain.enums.ItemColor;
import com.danielthedev.ecalendar.domain.enums.Notification;

@Entity
@Table(name = "CalendarItem")
public class CalendarItemEntity implements IEntity {

	@Transient
	public final static SimpleDateFormat API_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	static {
		API_DATE_FORMAT.setLenient(false);
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private int ID;
	
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "calendarID")
	private CalendarEntity calendar;

	@Column(name = "title", nullable = false, length = 60)
	private String title;

	@Column(name = "description", length = 255)
	private String description;

	@Column(name = "startDate", nullable = false)
	private Date startDate;

	@Column(name = "endDate", nullable = false)
	private Date endDate;

	@Convert(converter = ItemColorConverter.class)
	@Column(name = "item_color", nullable = false)
	private ItemColor itemColor;

	@Convert(converter = NotificationsConverter.class)
	@Column(name = "notifications", nullable = false)
	private List<Notification> notifications;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	@JoinColumn(name = "repeatingAttributeID", nullable = true, columnDefinition = "integer DEFAULT 0")
	@NotFound(action = NotFoundAction.IGNORE)
	private RepeatingAttribute repeatingAttribute;
	
	@OneToMany(mappedBy = "calendarItem", fetch = FetchType.LAZY)
	private List<SharedCalendarItemEntity> sharedCalendarItems = new ArrayList<SharedCalendarItemEntity>();


	public CalendarItemEntity(CalendarEntity calendar, String title, String description, Date startDate, Date endDate, ItemColor itemColor,
			List<Notification> notifications, RepeatingAttribute repeatingAttribute) {
		this.calendar = calendar;
		this.title = title;
		this.description = description;
		this.startDate = startDate;
		this.endDate = endDate;
		this.itemColor = itemColor;
		this.notifications = notifications;
		this.repeatingAttribute = repeatingAttribute;
	}
	
	public void update(CalendarEntity calendar, String title, String description, Date startDate, Date endDate, ItemColor color, List<Notification> notifications, RepeatingAttribute repeatingAttribute) {
		this.calendar = calendar;
		this.title = title;
		this.description = description;
		this.startDate = startDate;
		this.endDate = endDate;
		this.itemColor = color;
		this.notifications = notifications;
		this.repeatingAttribute = repeatingAttribute;
	}

	private CalendarItemEntity() {}

	public int getID() {
		return ID;
	}

	public CalendarEntity getCalendar() {
		return calendar;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public ItemColor getItemColor() {
		return itemColor;
	}

	public List<Notification> getNotifications() {
		return notifications;
	}

	public RepeatingAttribute getRepeatingAttribute() {
		return repeatingAttribute;
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("ID", this.ID);
		json.put("calendarID", this.calendar.getID());
		json.put("title", this.title);
		json.put("description", this.description);
		json.put("startDate", API_DATE_FORMAT.format(this.startDate));
		json.put("endDate", API_DATE_FORMAT.format(this.endDate));
		json.put("color", this.itemColor.getType());
		json.put("notifications", Notification.createMask(this.notifications));
		
		if(this.repeatingAttribute != null) {
			json.put("repeat", this.repeatingAttribute.toJSON());
		}
		return json;
		
	}
}
