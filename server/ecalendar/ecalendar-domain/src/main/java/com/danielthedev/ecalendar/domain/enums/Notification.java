package com.danielthedev.ecalendar.domain.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Notification {

	POPUP(1), EMAIL(2);

	private final int mask;

	Notification(int mask) {
		this.mask = mask;
	}

	public int getMask() {
		return mask;
	}

	public static boolean hasMask(int mask, Notification notifcation) {
		return (notifcation.mask & mask) == notifcation.mask;
	}

	public static List<Notification> parse(int mask) {
		if (mask > getHighestMask() || mask < getLowestMask())
			return null;

		List<Notification> list = new ArrayList<Notification>();

		for (Notification value : values()) {
			if (hasMask(mask, value)) {
				list.add(value);
			}
		}

		return list;
	}

	public static int createMask(Iterable<Notification> notifcations) {
		int mask = 0;
		for (Notification item : notifcations) {
			mask |= item.mask;
		}
		return mask;
	}

	public static int createMask(Notification... notifcations) {
		return createMask(Arrays.asList(notifcations));
	}

	public static int getHighestMask() {
		return POPUP.mask | EMAIL.mask;
	}

	public static int getLowestMask() {
		return 0;
	}
}
