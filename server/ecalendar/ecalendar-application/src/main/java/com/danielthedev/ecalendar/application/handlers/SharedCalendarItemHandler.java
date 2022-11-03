package com.danielthedev.ecalendar.application.handlers;

import com.danielthedev.ecalendar.application.context.ECalenderContext;

public class SharedCalendarItemHandler {

	public void add(ECalenderContext ctx) {
		int calendarID = ctx.getPathID("calendarID");
		int calendarItemID = ctx.getPathID("calendarItemID");
		int userID = ctx.getPathID("userID");
		ctx.error("not available");
	}

	public void remove(ECalenderContext ctx) {
		int calendarID = ctx.getPathID("calendarID");
		int calendarItemID = ctx.getPathID("calendarItemID");
		int userID = ctx.getPathID("userID");
		ctx.error("not available");
	}

	public void get(ECalenderContext ctx) {
		int calendarID = ctx.getPathID("calendarID");
		int calendarItemID = ctx.getPathID("calendarItemID");
		ctx.error("not available");
	}
}
