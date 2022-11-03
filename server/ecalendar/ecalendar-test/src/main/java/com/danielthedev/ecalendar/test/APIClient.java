package com.danielthedev.ecalendar.test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class APIClient {

	public static final String BASE_URL = "http://localhost:4403/api/v1/";
	
	public static final String TEST_EMAIL = "test@gmail.com";
	public static final String TEST_USERNAME = "testuser";
	public static final String TEST_PASSWORD = "TestAccount12345!";
	
    public final static APIEndpoint ENDPOINT_USER_LOGIN = new APIEndpoint("/user/login/", "POST", false);
    public final static APIEndpoint ENDPOINT_USER_REGISTER = new APIEndpoint("/user/register", "POST", false);
    public final static APIEndpoint ENDPOINT_USER_GET = new APIEndpoint("/user/get", "POST", false);

    public final static APIEndpoint ENDPOINT_CALENDAR_CREATE = new APIEndpoint("/calendar/create", "POST", true);
    public final static APIEndpoint ENDPOINT_CALENDAR_LIST = new APIEndpoint("/calendar/list", "POST", true);
    public final static APIEndpoint ENDPOINT_CALENDAR_DELETE = new APIEndpoint("/calendar/delete/{calendarID}", "POST", true);
    public final static APIEndpoint ENDPOINT_CALENDAR_EDIT = new APIEndpoint("/calendar/edit/{calendarID}", "POST", true);

    public final static APIEndpoint ENDPOINT_SHARED_CALENDAR_ADD = new APIEndpoint("/calendar/{calendarID}/share/add/{userID}", "POST", true);
    public final static APIEndpoint ENDPOINT_SHARED_CALENDAR_EDIT = new APIEndpoint("/calendar/{calendarID}/share/edit/{userID}", "POST", true);
    public final static APIEndpoint ENDPOINT_SHARED_CALENDAR_REMOVE = new APIEndpoint("/calendar/{calendarID}/share/remove/{userID}", "POST", true);

    public final static APIEndpoint ENDPOINT_CALENDAR_ITEM_CREATE = new APIEndpoint("/calendar/{calendarID}/create", "POST", true);
    public final static APIEndpoint ENDPOINT_CALENDAR_ITEM_GET = new APIEndpoint("/calendar/{calendarID}/get", "POST", true);
    public final static APIEndpoint ENDPOINT_CALENDAR_ITEM_DELETE = new APIEndpoint("/calendar/{calendarID}/delete/{calendarItemID}", "POST", true);
    public final static APIEndpoint ENDPOINT_CALENDAR_ITEM_EDIT = new APIEndpoint("/calendar/{calendarID}/edit/{calendarItemID}", "POST", true);
	
	public static JSONObject call(APIEndpoint endpoint, JSONObject payload) throws ClientProtocolException, IOException {
		HttpPost post = new HttpPost(BASE_URL + endpoint.getEndpoint());
		
		if(endpoint.isAuth()) {
			JSONObject tokenresult = call(ENDPOINT_USER_LOGIN, json().put("email", TEST_EMAIL).put("password", TEST_PASSWORD));
			String token = tokenresult.getJSONObject("result").getString("token");
			post.setHeader(HttpHeaders.AUTHORIZATION, token);
		}
		HttpClient builder = HttpClientBuilder.create().build();
		post.setEntity(new StringEntity(payload.toString()));
		HttpResponse response = builder.execute(post);
		return new JSONObject(EntityUtils.toString(response.getEntity()));
	}
	
	public static JSONObject json() {
		return new JSONObject();
	}
	
	public static class APIEndpoint { 
		
		private final String endpoint;
		private final String method;
		private final boolean auth;
		
		public APIEndpoint(String endpoint, String method, boolean auth) {
			this.endpoint = endpoint;
			this.method = method;
			this.auth = auth;
		}
		
		public APIEndpoint replaceVariables(Object... keypairs) {
			String endpoint = this.endpoint;
			
			Object[] keys = Arrays.copyOfRange(keypairs, 0, keypairs.length/2);
			Object[] values = Arrays.copyOfRange(keypairs, keypairs.length/2, keypairs.length);
			
			for(int x = 0; x < keys.length; x++) {
				endpoint = endpoint.replace("{"+Objects.toString(keys[x])+"}", Objects.toString(values[x]));
			}

			return new APIEndpoint(endpoint, this.method, this.auth);
		}

		public String getEndpoint() {
			return endpoint;
		}

		public String getMethod() {
			return method;
		}

		public boolean isAuth() {
			return auth;
		}
		
		
	}
}
