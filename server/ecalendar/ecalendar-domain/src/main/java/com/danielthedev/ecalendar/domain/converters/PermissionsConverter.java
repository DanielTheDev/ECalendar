package com.danielthedev.ecalendar.domain.converters;

import java.util.List;

import javax.persistence.AttributeConverter;

import com.danielthedev.ecalendar.domain.enums.Permission;

public class PermissionsConverter implements AttributeConverter<List<Permission>, Integer> {

	@Override
	public Integer convertToDatabaseColumn(List<Permission> attribute) {
		return Permission.createMask(attribute);
	}

	@Override
	public List<Permission> convertToEntityAttribute(Integer dbData) {
		return Permission.parse(dbData);
	}

}
