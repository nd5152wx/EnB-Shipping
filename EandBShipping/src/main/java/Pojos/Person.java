package Pojos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown = true)

public class Person {
	public String login;
	public String salt;
	public String hash;
	public String firstName;
	public String lastName;
	public String address;
	public String city;
	public String state;
	public String zipCode;
	public String phoneNum;
	public double payRate;
	public String startDate;
	
	public String getSalt(){
		return salt;
	}
	
	public String getHash() {
		return hash;
	}
	
}
