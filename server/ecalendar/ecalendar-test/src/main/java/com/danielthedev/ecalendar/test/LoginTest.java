package com.danielthedev.ecalendar.test;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import static com.danielthedev.ecalendar.test.APIClient.*;
import static org.junit.Assert.assertEquals;

public class LoginTest {

	@Test
	public void wrong_email() throws ClientProtocolException, JSONException, IOException {
		JSONObject result = call(ENDPOINT_USER_LOGIN, json().put("email", "thisiswrong").put("password", TEST_PASSWORD));
		assertEquals(result.getBoolean("success"), false);
		assertEquals(result.getString("error"), "email invalid");
	}

	@Test
	public void wrong_password() throws ClientProtocolException, JSONException, IOException {
		JSONObject result = call(ENDPOINT_USER_LOGIN, json().put("email", TEST_EMAIL).put("password", "thisiswrong"));
		assertEquals(result.getBoolean("success"), false);
		System.out.println(result);
		assertEquals(result.getString("error"), "password invalid");
	}

	@Test
	public void successful_login() throws ClientProtocolException, JSONException, IOException {
		JSONObject result = call(ENDPOINT_USER_LOGIN, json().put("email", TEST_EMAIL).put("password", TEST_PASSWORD));
		assertEquals(result.getBoolean("success"), true);
	}

}
