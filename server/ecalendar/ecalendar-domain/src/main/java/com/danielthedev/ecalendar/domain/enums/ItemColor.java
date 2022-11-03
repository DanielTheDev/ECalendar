package com.danielthedev.ecalendar.domain.enums;

public enum ItemColor {

	RED(1), PINK(2), LIGHT_BLUE(3), ORANGE(4), GREY(5), AQUA(6), GOLD(7), PURPLE(8);

	private final int type;

	private ItemColor(int type) {
		this.type = type;
	}

	public static ItemColor getItemColorById(int id) {
		for (ItemColor color : values()) {
			if (color.type == id) {
				return color;
			}
		}
		return null;
	}

	public int getType() {
		return type;
	}

}
