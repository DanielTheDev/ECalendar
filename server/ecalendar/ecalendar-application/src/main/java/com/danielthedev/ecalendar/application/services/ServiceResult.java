package com.danielthedev.ecalendar.application.services;

public class ServiceResult<T> {

	private final boolean success;
	private final String error;
	private final T result;
	
	public ServiceResult(boolean success, String error, T result) {
		this.success = success;
		this.error = error;
		this.result = result;
	}
	
	public ServiceResult(String error) {
		this(false, error, null);
	}
	
	public ServiceResult(T result) {
		this(true, null, result);
	}

	public boolean isSuccess() {
		return success;
	}

	public String getError() {
		return error;
	}

	public T getResult() {
		return result;
	}
}
