package com.aminchekov.websiteguardianv1;
import java.util.Date;


public class GuardObject {
	private int responseCode;
	private Date timestamp;
	public int getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
}
