package com.danielthedev.ecalendar.application.context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.danielthedev.ecalendar.domain.entities.UserEntity;
import com.danielthedev.ecalendar.persistence.repositories.UserRepository;

import io.javalin.core.util.Header;
import io.javalin.http.Context;

public class ECalenderContext {

	private final Context ctx;
	private final UserRepository userRepository = new UserRepository();

	public ECalenderContext(Context ctx) {
		this.ctx = ctx;
		this.setHeaders();
	}

	public void setHeaders() {
		this.ctx.header(Header.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
		this.ctx.header(Header.ACCESS_CONTROL_ALLOW_METHODS, "POST, OPTIONS");
		this.ctx.header(Header.ACCESS_CONTROL_ALLOW_HEADERS, "Origin, Content-Type, X-Auth-Token, Authorization");
	}
	
	public String getPathParam(String path) {
		
		try {
			return this.ctx.pathParam(path);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public int getPathID(String path) {
		String calendarIDstr = this.getPathParam(path);
		
		if(calendarIDstr == null) this.error("missing " + path);
		
		try {
			int ID = Integer.parseInt(calendarIDstr);
			if(ID < 0) throw new Exception();
			return ID; 
		} catch (Exception e) {
			this.error("invalid " + path);
		}
		return -1;
	}
	
	public <T> T safe(SafeSupplier<T> supplier, String errrorMessage) {
		try {
			return supplier.get();
		} catch (Exception e) {
			this.error(errrorMessage);
		}
		return null;
	}
	
	public UserEntity getLoggedInUser() {
		String token = this.ctx.header("Authorization");
		if(token == null) {
			this.error("no token provided");
		} else {
			try {
				JWTToken jwtToken = new JWTToken(token);
				if(!jwtToken.verifySignature()) this.error("invalid token");
				if(jwtToken.getPayload().isExpired()) this.error("token expired");
				UserEntity user = this.userRepository.getUserWithCalendarsById(jwtToken.getPayload().getUserId());
				if(user == null) this.error("email incorrect");
				return user;
			} catch (Exception e) {
				System.out.println("invalid token");
				this.error("invalid token");
			}
		}
		return null;
	}

	public JSONObject getPayload() {
		JSONObject json = null;
		try {
			json = new JSONObject(this.ctx.body());
		} catch (JSONException e) {
			this.error("invalid json");
		}

		return json;
	}	
	
	public JSONObject json() {
		return new JSONObject();
	}
	
	public JSONArray array() {
		return new JSONArray();
	}
	
	public void success() {
		this.ctx.json(this.json()
				.put("success", true).toMap());
	}
	
	public void result(JSONObject obj) {
		this.ctx.json(this.json()
				.put("success", true)
				.put("result", obj).toMap());
	}

	public void error(String message) {
		this.ctx.json(new JSONObject().put("success", false).put("error", message).toMap());
		throw new RuntimeException();
	}
	
	@FunctionalInterface
	public interface SafeSupplier<T> {
	    public T get() throws Exception;
	}
}
