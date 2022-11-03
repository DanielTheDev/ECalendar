package com.danielthedev.ecalendar.domain.converters;

import javax.persistence.AttributeConverter;

import com.danielthedev.ecalendar.domain.enums.ItemColor;

public class ItemColorConverter implements AttributeConverter<ItemColor, Integer> {

	@Override
	public Integer convertToDatabaseColumn(ItemColor attribute) {
		return attribute.getType();
	}

	@Override
	public ItemColor convertToEntityAttribute(Integer dbData) {
		return ItemColor.getItemColorById(dbData);
	}

}
