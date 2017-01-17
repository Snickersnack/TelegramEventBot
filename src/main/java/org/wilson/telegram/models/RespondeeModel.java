package org.wilson.telegram.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "total_responses")

public class RespondeeModel {

	Long id;
	boolean attending;
	Integer userId;
	String firstName;
	
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "response_id")
	public Long getId() {
		return id;
	}
	
	@Column(name = "attending")
	public boolean isAttending() {
		return attending;
	}
	
	@Column(name = "user_id")
	public Integer getUserId() {
		return userId;
	}
	
	@Column(name = "user_name")
	public String getFirstName() {
		return firstName;
	}
	
	public void setAttending(boolean attending) {
		this.attending = attending;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	@Override
	public boolean equals(Object o){
		if(!(o instanceof RespondeeModel)){
			return false;
		}
		RespondeeModel obj = (RespondeeModel)o;
		return obj.getUserId().equals(this.userId);
	}
	
	@Override
	public int hashCode(){
		int hash = 17;
		hash = 31 * hash + userId.hashCode();
		return hash;
	}
	
}
