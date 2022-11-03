package com.danielthedev.ecalendar.application;

public class Example {
	
	public static void main(String[] args) {
		ECalendarServer server = new ECalendarServer("localhost", 4403);
		server.startServer();
	}

}
