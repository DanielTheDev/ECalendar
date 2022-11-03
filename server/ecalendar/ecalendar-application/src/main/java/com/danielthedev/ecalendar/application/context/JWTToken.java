package com.danielthedev.ecalendar.application.context;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONObject;

public class JWTToken {

	private final static String SECRET = "GIvyJ4BclACplDNXfIWhoSJsDlNAampAR2l1kqbnYw6SCHouOlHNhQOdQLSBIAyV";
	private final static String HASH_PROTOCOL = "HmacSHA256";
	private final static String JWT_HASH_PROTOCOL = "HS256";
	private final static String JWT_PROTOCOL = "JWT";
	
	private final JSONObject header;
	private final JWTPayload payload;
	private final String signature;

	public JWTToken(String token) {
		String[] content = token.split("\\.");
		this.header = new JSONObject(this.decodeBase64URL(content[0]));
		if (!header.getString("alg").equals(JWT_HASH_PROTOCOL) || !header.getString("type").equals(JWT_PROTOCOL)) {
			throw new UnsupportedOperationException();
		}
		this.payload = new JWTPayload(new JSONObject(this.decodeBase64URL(content[1])));
		this.signature = content[2];
	}
	
	public JWTToken(JWTPayload payload) {
		this.payload = payload;
		this.header = new JSONObject();
		this.header.put("alg", JWT_HASH_PROTOCOL);
		this.header.put("type", JWT_PROTOCOL);
		try {
			this.signature = this.hmacSha256(this.encodeBase64URL(this.header.toString()) + "." + this.encodeBase64URL(payload.toString()));
		} catch (InvalidKeyException | NoSuchAlgorithmException e) {
			throw new UnsupportedOperationException("invalid JWT algorithm");
		}		
	}
	
	public boolean verifySignature() {
		try {
			String content = this.hmacSha256(this.encodeBase64URL(this.header.toString()) + "." + this.encodeBase64URL(payload.toString()));
			return content.equals(this.signature);
		} catch (InvalidKeyException | NoSuchAlgorithmException e) {
			return false;
		}
	}

	private String hmacSha256(String content) throws NoSuchAlgorithmException, InvalidKeyException {
		Mac mac = Mac.getInstance(HASH_PROTOCOL);
		mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), HASH_PROTOCOL));
		return this.encodeBase64URL(mac.doFinal(content.getBytes(StandardCharsets.UTF_8)));
	}

	private String decodeBase64URL(String str) {
		return new String(Base64.getUrlDecoder().decode(str), StandardCharsets.UTF_8);
	}

	private String encodeBase64URL(String str) {
		return this.encodeBase64URL(str.getBytes(StandardCharsets.UTF_8));
	}

	private String encodeBase64URL(byte[] bytes) {
		return new String(Base64.getUrlEncoder().encode(bytes), StandardCharsets.UTF_8).replace("=", "");
	}

	public String getSignature() {
		return signature;
	}

	public JSONObject getHeader() {
		return header;
	}

	public JWTPayload getPayload() {
		return payload;
	}
	
	@Override
	public String toString() {
		return this.encodeBase64URL(this.header.toString()) + "." + this.encodeBase64URL(payload.toString()) + "." + this.signature;
	}

	public static class JWTPayload {

		public static int KEY_DURATION_DAYS = 30;

		private final int userId;
		private final Date expireDate;
		private final String username;

		public JWTPayload(int userId, String username) {
			this.userId = userId;

			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.DATE, KEY_DURATION_DAYS);

			this.expireDate = cal.getTime();
			this.username = username;
		}

		public JWTPayload(JSONObject json) {
			if (!json.has("userId") || !json.has("expireDate") || !json.has("username")) {
				throw new NullPointerException();
			} else {
				this.userId = json.getInt("userId");
				this.expireDate = new Date(json.getLong("expireDate"));
				this.username = json.getString("username");
			}
		}

		public int getUserId() {
			return userId;
		}

		public Date getExpireDate() {
			return expireDate;
		}

		public String getUsername() {
			return username;
		}

		public boolean isExpired() {
			return this.getExpireDate().before(new Date());
		}

		public JSONObject toJSON() {
			JSONObject json = new JSONObject();
			json.put("userId", this.userId);
			json.put("expireDate", this.expireDate.getTime());
			json.put("username", this.username);
			return json;
		}
		
		@Override
		public String toString() {
			return this.toJSON().toString();
		}
	}
}

