package org.wilson.telegram.models;

public class RespondeeModel {

	boolean attending;
	Integer userId;
	String firstName;
	
	public boolean isAttending() {
		return attending;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setAttending(boolean attending) {
		this.attending = attending;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	
	
}
