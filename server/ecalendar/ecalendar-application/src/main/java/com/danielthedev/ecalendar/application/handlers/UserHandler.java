package com.danielthedev.ecalendar.application.handlers;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.json.JSONObject;

import com.danielthedev.ecalendar.application.context.ECalenderContext;
import com.danielthedev.ecalendar.application.context.JWTToken;
import com.danielthedev.ecalendar.application.services.ServiceResult;
import com.danielthedev.ecalendar.application.services.UserService;
import com.danielthedev.ecalendar.domain.entities.UserEntity;

public class UserHandler {

	private final static Pattern EMAIL_PATTERN = Pattern.compile("^.+@.+\\..+$");
	private final static List<Character> PASSWORD_CHAR_LIST = Arrays.asList('#', '@', '*', '=', '!');

	private final UserService userService = new UserService();

	public void login(ECalenderContext ctx) {
		JSONObject json = ctx.getPayload();
		
		if(!json.has("email")) ctx.error("missing email");
		if(!json.has("password")) ctx.error("missing password");
		
		String email = json.getString("email");
		String password = json.getString("password");
		
		if(!this.isEmailValid(email)) ctx.error("email invalid");
		if(!this.isPasswordValid(password)) ctx.error("password invalid");
		
		JWTToken token = this.userService.login(email, password);
		if(token == null) ctx.error("email or password invalid");
		
		ctx.result(ctx.json().put("token", token.toString()));
	}
	
	public void get(ECalenderContext ctx) {
		JSONObject json = ctx.getPayload();
		
		if(!json.has("username")) ctx.error("missing username");
		
		String username = json.getString("username");
		if(!this.isUsernameValid(username)) ctx.error("username invalid");
		
		UserEntity user = this.userService.getUser(username);
		
		if(user == null) ctx.error("user not found");
		
		ctx.result(user.toJSON());
	}

	public void register(ECalenderContext ctx) {
		JSONObject json = ctx.getPayload();
		if(!json.has("email")) ctx.error("missing email");
		if(!json.has("username")) ctx.error("missing username");
		if(!json.has("password")) ctx.error("missing password");
		
		String email = json.getString("email");
		String password = json.getString("password");
		String username = json.getString("username");
		
		if(!this.isUsernameValid(username)) ctx.error("username invalid");
		if(!this.isEmailValid(email)) ctx.error("email invalid");
		if(!this.isPasswordValid(password)) ctx.error("password invalid");
		
		ServiceResult<UserEntity> result = this.userService.register(email, username, password);
		
		if(!result.isSuccess()) ctx.error(result.getError());
		
		ctx.result(ctx.json()
			.put("ID", result.getResult().getID())
			.put("email", result.getResult().getEmail())
			.put("username", result.getResult().getUsername())
		);
	}
	
	private boolean isEmailValid(String email) {
		return email.length() >= 0 && email.length() < 320 && !email.contains(" ") && EMAIL_PATTERN.matcher(email).matches();
	}
	
	private boolean isPasswordValid(String password) {
		boolean hasSpecialChar = false;
		boolean hasUppercase = false;
		boolean hasLowercase = false;
		for (char c : password.toCharArray()) {
			if (Character.isUpperCase(c)) {
				hasUppercase = true;
			} else if (Character.isLowerCase(c)) {
				hasLowercase = true;
			} else if (Character.isDigit(c) || PASSWORD_CHAR_LIST.contains(c)) {
				hasSpecialChar = true;
			}
		}
		return password.length() >= 12 && password.length() <= 24 && hasUppercase && hasLowercase && hasSpecialChar;
	}
	
	private boolean isUsernameValid(String username) {
		return username.length() >= 3 && username.length() <= 32;
	}
}
