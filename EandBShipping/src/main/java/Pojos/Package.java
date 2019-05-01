package Pojos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Package {

	public int width;
	public int length;
	public int height;
	int weightInPounds;
	public String fromFirstName;
	public String fromLastName;
	public String fromAddr;
	public String fromCity;
	public String fromState;
	public String fromZipCode;
	public String toFirstName;
	public String toLastName;
	public String toAddr;
	public String toCity;
	public String toState;
	public String toZipCode;
	public String trackingNum;
	public String shippingStatus;
	public int shippingCost;
	public String previousLocation;
	public String currentLocation;
	public String Notes;
	
	
}
