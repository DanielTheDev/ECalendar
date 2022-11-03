package com.danielthedev.ecalendar.test;

import static com.danielthedev.ecalendar.test.APIClient.*;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class CalendarTest {

	@Test
	public void invalid_calendar_name() throws ClientProtocolException, JSONException, IOException {
		String name_2char = "ab";
		String password_10char = "abcdefhijk";
		String password_34char = "abcdefhijkabcdefhijkabcdefhijkcaer";
		
		assertEquals(name_2char.length(), 2);
		assertEquals(password_10char.length(), 10);
		assertEquals(password_34char.length(), 34);
		
		JSONObject result_2chars = call(ENDPOINT_CALENDAR_CREATE, json().put("name", name_2char));
		JSONObject result_10chars = call(ENDPOINT_CALENDAR_CREATE, json().put("name", password_10char));
		JSONObject result_34chars = call(ENDPOINT_CALENDAR_CREATE, json().put("name", password_34char));
		
		assertEquals(result_2chars.getBoolean("success"), false);
		assertEquals(result_2chars.getString("error"), "name invalid");
		
		assertEquals(result_34chars.getBoolean("success"), false);
		assertEquals(result_34chars.getString("error"), "name invalid");
		
		assertEquals(result_10chars.getBoolean("success"), true);
		// min: 3, max: 32
	}

	@Test
	public void successful_calendar() throws ClientProtocolException, JSONException, IOException {
		JSONObject result = call(ENDPOINT_CALENDAR_CREATE, json().put("name", "my calendar"));
		assertEquals(result.getBoolean("success"), true);
	}

}
