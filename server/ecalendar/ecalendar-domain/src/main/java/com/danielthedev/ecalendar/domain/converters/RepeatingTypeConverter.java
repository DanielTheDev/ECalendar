package com.danielthedev.ecalendar.domain.converters;

import javax.persistence.AttributeConverter;

import com.danielthedev.ecalendar.domain.enums.RepeatingType;

public class RepeatingTypeConverter implements AttributeConverter<RepeatingType, Integer> {

	@Override
	public Integer convertToDatabaseColumn(RepeatingType attribute) {
		return attribute.getType();
	}

	@Override
	public RepeatingType convertToEntityAttribute(Integer dbData) {
		return RepeatingType.getRepeatingTypeById(dbData);
	}

}
