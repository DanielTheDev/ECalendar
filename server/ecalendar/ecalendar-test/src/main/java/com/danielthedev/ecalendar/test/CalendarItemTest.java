package com.danielthedev.ecalendar.test;

import static com.danielthedev.ecalendar.test.APIClient.*;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class CalendarItemTest {

	@Test
	public void wrong_calendar_id() throws ClientProtocolException, JSONException, IOException {
		JSONObject result = call(ENDPOINT_USER_LOGIN, json().put("email", "thisiswrong").put("password", TEST_PASSWORD));
		assertEquals(result.getBoolean("success"), false);
		assertEquals(result.getString("error"), "email invalid");
	}

	@Test
	public void invalid_title() throws ClientProtocolException, JSONException, IOException {
		String title_2char = "ab";
		String title_10char = "abcdefhijk";
		String title_61char = "abcdefhijkabcdefhijkabcdefhijkcaerabcdefhijkabcdefhijkabcdefh";
		
		assertEquals(title_2char.length(), 2);
		assertEquals(title_10char.length(), 10);
		assertEquals(title_61char.length(), 61);

		JSONObject result_2chars = call(ENDPOINT_CALENDAR_ITEM_CREATE.replaceVariables("calendarID", 7), this.createItem(title_2char, "", "2021-06-21 10:00", "2021-07-21 10:00", 1, 0, 1, 1, "2021-08-21 10:00"));
		JSONObject result_10chars = call(ENDPOINT_CALENDAR_ITEM_CREATE.replaceVariables("calendarID", 7),this.createItem(title_10char, "", "2021-06-21 10:00", "2021-07-21 10:00", 1, 0, 1, 1, "2021-08-21 10:00"));
		JSONObject result_61chars = call(ENDPOINT_CALENDAR_ITEM_CREATE.replaceVariables("calendarID", 7), this.createItem(title_61char, "", "2021-06-21 10:00", "2021-07-21 10:00", 1, 0, 1, 1, "2021-08-21 10:00"));
	
		assertEquals(result_2chars.getBoolean("success"), false);
		assertEquals(result_2chars.getString("error"), "title invalid");
		
		assertEquals(result_61chars.getBoolean("success"), false);
		assertEquals(result_61chars.getString("error"), "title invalid");
		
		assertEquals(result_10chars.getBoolean("success"), true);
		
		// min: 3, max: 60
	}

	@Test
	public void invalid_description() throws ClientProtocolException, IOException {
		String title_256char = "abcdefbfhdgrhdmshsdabcdaswkeyrhdmshsdabwkeyrhdmshsdabcdcdefbfhdgfhajslwkeyrhdmshsdabcdefbfhdgfhajslwkeyrhdmshsdabcdefbfhdgfhajslwkeyrhdmshsdabcdefbfhdgfhajslwkeyrhdmshsdabcdefbfhdgfhajslwkeyrhdmshsdabcdefbfhdgfhajslwkeyrhdmshsdabcdefbfhdgfhajslwkeyrhdmshsd";
		String title_10char = "abcdeflkoj";
		
		assertEquals(title_256char.length(), 256);
		assertEquals(title_10char.length(), 10);

		JSONObject result_256chars = call(ENDPOINT_CALENDAR_ITEM_CREATE.replaceVariables("calendarID", 7), this.createItem("my title",title_256char, "2021-06-21 10:00", "2021-07-21 10:00", 1, 0, 1, 1, "2021-08-21 10:00"));
		JSONObject result_10chars = call(ENDPOINT_CALENDAR_ITEM_CREATE.replaceVariables("calendarID", 7),this.createItem("my title", title_10char, "2021-06-21 10:00", "2021-07-21 10:00", 1, 0, 1, 1, "2021-08-21 10:00"));
	
		assertEquals(result_256chars.getBoolean("success"), false);
		assertEquals(result_256chars.getString("error"), "description invalid");
		
		assertEquals(result_10chars.getBoolean("success"), true);
		// max: 255
	}

	@Test
	public void invalid_start_date() throws ClientProtocolException, IOException {
		JSONObject result = call(ENDPOINT_CALENDAR_ITEM_CREATE.replaceVariables("calendarID", 7),this.createItem("my title", "", "thiscannotbegood", "2021-06-21 10:00", 10, 1, 1, 1, "2021-09-21 10:00"));
		assertEquals(result.getBoolean("success"), false);
		assertEquals(result.getString("error"), "invalid startDate");
	}

	@Test
	public void invalid_end_date() throws ClientProtocolException, IOException {
		JSONObject result = call(ENDPOINT_CALENDAR_ITEM_CREATE.replaceVariables("calendarID", 7),this.createItem("my title", "", "2021-06-21 10:00", "thiscannotbegood", 10, 1, 1, 1, "2021-09-21 10:00"));
		assertEquals(result.getBoolean("success"), false);
		assertEquals(result.getString("error"), "invalid endDate");
	}

	@Test
	public void start_date_before_end_date() throws ClientProtocolException, IOException {
		JSONObject result = call(ENDPOINT_CALENDAR_ITEM_CREATE.replaceVariables("calendarID", 7),this.createItem("my title", "", "2021-06-21 10:00", "2021-05-21 10:00", 10, 1, 1, 1, "2021-09-21 10:00"));
		assertEquals(result.getBoolean("success"), false);
		assertEquals(result.getString("error"), "startDate must be before endDate");
	}

	@Test
	public void invalid_color() throws ClientProtocolException, IOException {
		JSONObject result = call(ENDPOINT_CALENDAR_ITEM_CREATE.replaceVariables("calendarID", 7),this.createItem("my title", "", "2021-06-21 10:00", "2021-07-21 10:00", 10, 1, 1, 1, "2021-09-21 10:00"));
		assertEquals(result.getBoolean("success"), false);
		assertEquals(result.getString("error"), "invalid color");
	}

	@Test
	public void invalid_notification_mask() throws ClientProtocolException, IOException {
		JSONObject result = call(ENDPOINT_CALENDAR_ITEM_CREATE.replaceVariables("calendarID", 7),this.createItem("my title", "", "2021-06-21 10:00", "2021-07-21 10:00", 1, -1, 1, 1, "2021-09-21 10:00"));
		assertEquals(result.getBoolean("success"), false);
		assertEquals(result.getString("error"), "invalid notifications");
	}

	@Test
	public void invalid_repeating_interval_type() throws ClientProtocolException, IOException {
		JSONObject result = call(ENDPOINT_CALENDAR_ITEM_CREATE.replaceVariables("calendarID", 7),this.createItem("my title", "", "2021-06-21 10:00", "2021-07-21 10:00", 1, 0, -1, 1, "2021-09-21 10:00"));
		assertEquals(result.getBoolean("success"), false);
		assertEquals(result.getString("error"), "invalid repeatingAttribute.intervalType");
	}

	@Test
	public void invalid_repeating_interval_amount() throws ClientProtocolException, IOException {
		JSONObject result = call(ENDPOINT_CALENDAR_ITEM_CREATE.replaceVariables("calendarID", 7),this.createItem("my title", "", "2021-06-21 10:00", "2021-07-21 10:00", 1, 0, 1, -1, "2021-09-21 10:00"));
		assertEquals(result.getBoolean("success"), false);
		assertEquals(result.getString("error"), "invalid repeatingAttribute.amount");
	}

	@Test
	public void repeating_stop_date_before_end_date() throws ClientProtocolException, IOException {
		JSONObject result = call(ENDPOINT_CALENDAR_ITEM_CREATE.replaceVariables("calendarID", 7),this.createItem("my title", "", "2021-06-21 10:00", "2021-07-21 10:00", 1, 0, 1, 1, "2021-06-21 10:00"));
		assertEquals(result.getBoolean("success"), false);
		assertEquals(result.getString("error"), "invalid repeatingAttribute.stopDate");
		
	}

	@Test
	public void successful_calendaritem() throws ClientProtocolException, IOException {
		JSONObject result = call(ENDPOINT_CALENDAR_ITEM_CREATE.replaceVariables("calendarID", 7),this.createItem("my title", "", "2021-06-21 10:00", "2021-07-21 10:00", 1, 0, 1, 1, "2021-08-21 10:00"));
		assertEquals(result.getBoolean("success"), true);

	}

	public JSONObject createItem(String title, String description, String startDate, String endDate, int color, int notifications, int repeatingType, int repeatingAmount, String repeatingStopDate) {
		JSONObject json = new JSONObject();
		json.put("title", title);
		json.put("description", description);
		json.put("startDate", startDate);
		json.put("endDate", endDate);
		json.put("color", color);
		json.put("notifications", notifications);
		
		JSONObject innerJSON = new JSONObject();
		innerJSON.put("intervalType", repeatingType);
		innerJSON.put("amount", repeatingAmount);
		innerJSON.put("stopDate", repeatingStopDate);
		
		json.put("repeat", innerJSON);
		
		return json;
	}
}
