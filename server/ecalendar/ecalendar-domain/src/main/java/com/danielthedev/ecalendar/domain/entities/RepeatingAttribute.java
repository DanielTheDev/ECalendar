package com.danielthedev.ecalendar.domain.entities;

import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.json.JSONObject;

import com.danielthedev.ecalendar.domain.converters.RepeatingTypeConverter;
import com.danielthedev.ecalendar.domain.enums.RepeatingType;

@Entity
@Table(name = "RepeatingAttribute")
public class RepeatingAttribute implements IEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private int ID;

	@Convert(converter = RepeatingTypeConverter.class)
	@Column(name = "intervalType", nullable = false)
	private RepeatingType repeatingType;

	@Column(name = "amount", nullable = false)
	private int amount;

	@Column(name = "stopDate", nullable = false)
	private Date stopDate;

	public RepeatingAttribute(RepeatingType repeatingType, int amount, Date stopDate) {
		this.repeatingType = repeatingType;
		this.amount = amount;
		this.stopDate = stopDate;
	}

	private RepeatingAttribute() {}

	public int getID() {
		return ID;
	}

	public RepeatingType getRepeatingType() {
		return repeatingType;
	}
	
	public int getAmount() {
		return amount;
	}

	public Date getStopDate() {
		return stopDate;
	}

	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("intervalType", this.repeatingType.getType());
		json.put("amount", this.amount);
		json.put("stopDate", CalendarItemEntity.API_DATE_FORMAT.format(this.stopDate));
		return json;
	}

}
