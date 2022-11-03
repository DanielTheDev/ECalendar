package com.danielthedev.ecalendar.domain.enums;

public enum RepeatingType {

	DAILY(1), WEEKLY(2), MONTHLY(3), YEARLY(4);

	private final int type;

	private RepeatingType(int type) {
		this.type = type;
	}

	public static RepeatingType getRepeatingTypeById(int id) {
		for (RepeatingType type : values()) {
			if (type.type == id) {
				return type;
			}
		}
		return null;
	}

	public int getType() {
		return type;
	}
}
