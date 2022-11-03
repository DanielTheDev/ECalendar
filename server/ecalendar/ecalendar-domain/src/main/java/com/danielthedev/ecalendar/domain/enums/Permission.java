package com.danielthedev.ecalendar.domain.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Permission {

	ITEM_CREATE(1), ITEM_DELETE(2), ITEM_EDIT(4);

	private final int mask;

	Permission(int mask) {
		this.mask = mask;
	}

	public int getMask() {
		return mask;
	}

	public static boolean hasMask(int mask, Permission permission) {
		return (permission.mask & mask) == permission.mask;
	}

	public static List<Permission> parse(int mask) {
		if (mask > getHighestMask() || mask < getLowestMask())
			return null;

		List<Permission> list = new ArrayList<Permission>();

		for (Permission value : values()) {
			if (hasMask(mask, value)) {
				list.add(value);
			}
		}

		return list;
	}

	public static int createMask(Iterable<Permission> permissions) {
		int mask = 0;
		for (Permission item : permissions) {
			mask |= item.mask;
		}
		return mask;
	}

	public static int createMask(Permission... permissions) {
		return createMask(Arrays.asList(permissions));
	}

	public static int getHighestMask() {
		return ITEM_CREATE.mask | ITEM_DELETE.mask | ITEM_EDIT.mask;
	}

	public static int getLowestMask() {
		return 0;
	}

	public static boolean hasPermission(List<Permission> accessPermissions, Permission target) {
		return accessPermissions.stream().anyMatch(permission->permission == target);
	}
}

