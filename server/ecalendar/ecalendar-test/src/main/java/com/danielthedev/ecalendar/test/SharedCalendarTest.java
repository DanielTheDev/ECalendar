package com.danielthedev.ecalendar.test;

import static com.danielthedev.ecalendar.test.APIClient.*;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import com.danielthedev.ecalendar.domain.enums.Permission;

public class SharedCalendarTest {

	@Test
	public void invalid_shared_user_id() throws ClientProtocolException, JSONException, IOException {
		JSONObject result = call(ENDPOINT_SHARED_CALENDAR_ADD.replaceVariables("calendarID", "userID", 7, 38), json().put("accessPermissions", Permission.createMask(Permission.ITEM_CREATE)));
		assertEquals(result.getBoolean("success"), false);
		assertEquals(result.getString("error"), "userID is invalid");
	}

	@Test
	public void invalid_calendar_id() throws ClientProtocolException, JSONException, IOException {
		JSONObject result = call(ENDPOINT_SHARED_CALENDAR_ADD.replaceVariables("calendarID", "userID", 38, 1), json().put("accessPermissions", Permission.createMask(Permission.ITEM_CREATE)));
		assertEquals(result.getBoolean("success"), false);
		assertEquals(result.getString("error"), "calendarID is invalid");
	}

	@Test
	public void invalid_access_permissions() throws ClientProtocolException, JSONException, IOException {
		JSONObject result = call(ENDPOINT_SHARED_CALENDAR_ADD.replaceVariables("calendarID", "userID", 7, 1), json().put("accessPermissions", 18));
		assertEquals(result.getBoolean("success"), false);
		assertEquals(result.getString("error"), "invalid accessPermissions");
	}

	@Test
	public void successful_shared_calendar() throws ClientProtocolException, JSONException, IOException {
		JSONObject result = call(ENDPOINT_SHARED_CALENDAR_ADD.replaceVariables("calendarID", "userID", 7, 1), json().put("accessPermissions", Permission.createMask(Permission.ITEM_CREATE)));
		assertEquals(result.getBoolean("success"), true);
	}

}
