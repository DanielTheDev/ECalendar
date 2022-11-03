package com.danielthedev.ecalendar.domain.converters;

import java.util.List;

import javax.persistence.AttributeConverter;

import com.danielthedev.ecalendar.domain.enums.Notification;

public class NotificationsConverter implements AttributeConverter<List<Notification>, Integer> {

	@Override
	public Integer convertToDatabaseColumn(List<Notification> attribute) {
		return Notification.createMask(attribute);
	}

	@Override
	public List<Notification> convertToEntityAttribute(Integer dbData) {
		return Notification.parse(dbData);
	}

}
