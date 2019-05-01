package Pojos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown = true)

public class Person {
	public String login;
	public byte[] salt;
	public byte[] hash;
	public String firstName;
	public String lastName;
	public String address;
	public String city;
	public String state;
	public String zipCode;
	public String phoneNum;
	public double payRate;
	public String startDate;
	
}
