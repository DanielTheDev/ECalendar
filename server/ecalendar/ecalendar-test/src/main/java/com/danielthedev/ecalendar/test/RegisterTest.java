package com.danielthedev.ecalendar.test;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static com.danielthedev.ecalendar.test.APIClient.*;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

public class RegisterTest {

	@Test
	public void username_min_max_length() throws ClientProtocolException, JSONException, IOException {
		String username_2char = "12";
		String username_33char = "123456789112345678911234567891123";
		String username_12char = "123456789123";
		
		assertEquals(username_2char.length(), 2);
		assertEquals(username_12char.length(), 12);
		assertEquals(username_33char.length(), 33);
		
		JSONObject result_2chars = call(ENDPOINT_USER_REGISTER, json().put("email", TEST_EMAIL).put("password", TEST_PASSWORD).put("username", username_2char));
		JSONObject result_33chars = call(ENDPOINT_USER_REGISTER, json().put("email", TEST_EMAIL).put("password", TEST_PASSWORD).put("username", username_33char));
		JSONObject result_12chars = call(ENDPOINT_USER_REGISTER, json().put("email", TEST_EMAIL).put("password", TEST_PASSWORD).put("username", username_12char));
		
		assertEquals(result_2chars.getBoolean("success"), false);
		assertEquals(result_2chars.getString("error"), "username invalid");
		
		
		assertEquals(result_33chars.getBoolean("success"), false);
		assertEquals(result_33chars.getString("error"), "username invalid");
		
		//this means the username is succesful
		assertEquals(result_12chars.getBoolean("success"), false);
		assertEquals(result_12chars.getString("error"), "email already exists");
		// min: 3 max: 32
	}

	@Test
	public void password_min_max_length() throws ClientProtocolException, JSONException, IOException {
		String password_11char = "12345678912";
		String password_25char = "Abcdefghjiklmnasdfghjqw12";
		String password_15char = "Abcdefhikls4d2!";
		
		assertEquals(password_11char.length(), 11);
		assertEquals(password_15char.length(), 15);
		assertEquals(password_25char.length(), 25);
		
		JSONObject result_11chars = call(ENDPOINT_USER_REGISTER, json().put("email", TEST_EMAIL).put("password", password_11char).put("username", TEST_USERNAME));
		JSONObject result_15chars = call(ENDPOINT_USER_REGISTER, json().put("email", TEST_EMAIL).put("password", password_15char).put("username", TEST_USERNAME));
		JSONObject result_25chars = call(ENDPOINT_USER_REGISTER, json().put("email", TEST_EMAIL).put("password", password_25char).put("username", TEST_USERNAME));
		
		assertEquals(result_11chars.getBoolean("success"), false);
		assertEquals(result_11chars.getString("error"), "password invalid");
		
		
		assertEquals(result_25chars.getBoolean("success"), false);
		assertEquals(result_25chars.getString("error"), "password invalid");
		
		//this means the password is succesful
		assertEquals(result_15chars.getBoolean("success"), false);
		assertEquals(result_15chars.getString("error"), "email already exists");	
		// min: 12 max: 24
	}

	@Test
	public void password_one_capital_letter() throws ClientProtocolException, JSONException, IOException {
		
		String incorrect_password = "abcdefghijkl2";
		String correct_password = "Abcdefghijkl2";
		
		JSONObject correct_result = call(ENDPOINT_USER_REGISTER, json().put("email", TEST_EMAIL).put("password", correct_password).put("username", TEST_USERNAME));
		JSONObject incorrect_result = call(ENDPOINT_USER_REGISTER, json().put("email", TEST_EMAIL).put("password", incorrect_password).put("username", TEST_USERNAME));
		
		
		assertEquals(incorrect_result.getBoolean("success"), false);
		assertEquals(incorrect_result.getString("error"), "password invalid");
		
		//this means the password is succesful
		assertEquals(correct_result.getBoolean("success"), false);
		assertEquals(correct_result.getString("error"), "email already exists");	
	}

	@Test
	public void password_one_number_or_allowed_characters() throws ClientProtocolException, JSONException, IOException {
		
		String incorrect_password = "abcdefghijkl";
		String correct_password = "Abcdefghijkl@";
		
		JSONObject correct_result = call(ENDPOINT_USER_REGISTER, json().put("email", TEST_EMAIL).put("password", correct_password).put("username", TEST_USERNAME));
		JSONObject incorrect_result = call(ENDPOINT_USER_REGISTER, json().put("email", TEST_EMAIL).put("password", incorrect_password).put("username", TEST_USERNAME));
		
		
		assertEquals(incorrect_result.getBoolean("success"), false);
		assertEquals(incorrect_result.getString("error"), "password invalid");
		
		//this means the password is succesful
		assertEquals(correct_result.getBoolean("success"), false);
		assertEquals(correct_result.getString("error"), "email already exists");	
		// [0-9] or [#@*=!]
	}

	@Test
	public void invalid_email_address() throws ClientProtocolException, JSONException, IOException {
		String correct_email = "test@gmail.com";
		String incorrect_email = "@gmail.com.test";
		
		JSONObject correct_result = call(ENDPOINT_USER_REGISTER, json().put("email", correct_email).put("password", TEST_PASSWORD).put("username", TEST_USERNAME));
		JSONObject incorrect_result = call(ENDPOINT_USER_REGISTER, json().put("email", incorrect_email).put("password", TEST_PASSWORD).put("username", TEST_USERNAME));
		
		assertEquals(incorrect_result.getBoolean("success"), false);
		assertEquals(incorrect_result.getString("error"), "email invalid");
		
		//this means the email is succesful
		assertEquals(correct_result.getBoolean("success"), false);
		assertEquals(correct_result.getString("error"), "email already exists");
	}

	@Test
	public void valid_register() throws ClientProtocolException, JSONException, IOException {
		JSONObject result = call(ENDPOINT_USER_REGISTER, json().put("email", "remove@gmail.com").put("password", "Removethispass!").put("username", "removeuser"));
		
		assertEquals(result.getBoolean("success"), true);
	}
}
