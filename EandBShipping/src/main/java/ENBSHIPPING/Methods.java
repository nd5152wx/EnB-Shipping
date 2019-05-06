/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ENBSHIPPING;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.DeleteOptions;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;
import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

import java.io.*;
import java.nio.charset.StandardCharsets;

import Pojos.Package;
import Pojos.Person;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.System.console;
import static java.rmi.server.ObjID.read;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.swing.JFileChooser;
import static jdk.nashorn.tools.ShellFunctions.input;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 *
 * @author nd5152wx
 */
public class Methods {

	// beginning of shipping variables
	private static String packageToSearch = ""; // This is the package ID and used to search for current locations
	private static String fName = "";// used to do searches for either a sender or recipient
	private static String lName = ""; // used to do searches for either a sender or recipient
	private static int senderZipcode = 0; // used to determine shipping costs
	private static int recipientZipcode = 0; // used to determine shipping costs
	private String login = "";
	private String password = "";
	// radius of the Earth in miles
	public static final double EARTH_RADIUS = 3949.99;
	final static double SHIPPING_COST = .05;
	// radius of the Earth in miles
	public static final double EARTH_RADIUS_MILES = 3963;
	private static String EELogin = ""; // used to retrieve Admin or EE login
	ObjectMapper mapper = new ObjectMapper();
	static Scanner console = new Scanner(System.in);
	Package packageObject = new Package();
	Person personObject = new Person();
	Person personObject2 = new Person();

	// use driver 3.4.3. Paste in connection string in quotes
	MongoClientURI uri = new MongoClientURI("mongodb://Joe:Parker1966@cluster0-shard-"
			+ "00-00-68epl.mongodb.net:27017,cluster0-shard-00-01-68epl.mongodb.net:27017,"
			+ "cluster0-shard-00-02-68epl.mongodb.net:27017/test?ssl=true&replicaSet="
			+ "Cluster0-shard-0&authSource=admin&retryWrites=true");

	// MongoClientURI uri = new MongoClientURI("");

	MongoClient mongoClient = new MongoClient(uri);

	// access a database
	MongoDatabase database = mongoClient.getDatabase("EnBShipping");

	// access a collection
	MongoCollection<Document> collectionEE = database.getCollection("Employee");

	// access a collection
	MongoCollection<Document> collectionZipCode = database.getCollection("ZipCode");

	// access a collection
	MongoCollection<Document> collectionPackage = database.getCollection("Package");


	// ship a new package
	public void shipNewPackage() {
		double width, length, height, weight;
		double shippingCost = 0;
		String trackingNum;
		String senderFirstName;
		String senderLastName;
		String senderAddress;
		String senderCity;
		String senderState;
		String senderZipCode;
		String recipientFirstName;
		String recipientLastName;
		String recipientAddress;
		String recipientCity;
		String recipientState;
		String recipientZipCode;
		String shippingStatus; // keeps track of whether package has been shipped/delivered
		String previousLocation; // keeps track of the previous stop of the delivery
		String currentLocation; // the current city the package is in
		String specialNotes; // note such as "place in garage"/who signed for package
		final double SHIPPING_COST = .05;// cost per mile for shipping

		// get the package specifics
		System.out.println("What is the width of the package in inches?");
		width = console.nextDouble();
		console.nextLine();
		System.out.println("What is the length of the package in inches?");
		length = console.nextDouble();
		console.nextLine();
		System.out.println("What is the height of the package in inches?");
		height = console.nextDouble();
		console.nextLine();
		System.out.println("What is the weight of the package in pounds?");
		weight = console.nextDouble();
		console.nextLine();// after int or double
		System.out.println("What is the sender's first name?");
		senderFirstName = console.nextLine();
		System.out.println("What is the sender's last name?");
		senderLastName = console.nextLine();
		System.out.println("What is the sender's address?");
		senderAddress = console.nextLine();

		/*
		 * test section width = 12; length = 14; height = 24; weight = 16;
		 * senderFirstName = "Jason"; senderLastName = "Richter"; senderAddress =
		 * "117 7th st NW";
		 */

		System.out.println("What is the sender's city?");
		senderCity = console.nextLine();

		System.out.println("What is the sender's state?");
		senderState = console.nextLine();

		System.out.println("What is the sender's 5 digit zip code?");
		senderZipCode = console.nextLine();

		// get recipient information
		System.out.println("What is the recipient's first name?");
		recipientFirstName = console.nextLine();
		System.out.println("What is the recipient's last name?");
		recipientLastName = console.nextLine();
		System.out.println("What is the recipient's address?");
		recipientAddress = console.nextLine();

		/*
		 * recipientFirstName = "Bart"; recipientLastName = "Simpson"; recipientAddress
		 * = "687 E West Road Way SW";
		 */

		System.out.println("What is the recipient's city?");
		recipientCity = console.nextLine();

		System.out.println("What is the recipient's state?");
		recipientState = console.nextLine();

		System.out.println("What is the recipient's 5 digit zip code?");
		recipientZipCode = console.nextLine();

		System.out.println("Are there any special notes for the delivery person?");
		specialNotes = console.nextLine();

		shippingStatus = "NotShipped";

		// set the current location of the package
		currentLocation = senderCity;

		// set tracking number
		trackingNum = createTrackingNum(recipientZipCode);

		// insert new package into the database
		Document doc = new Document("trackingNum", trackingNum).append("width", width).append("length", length)
				.append("height", height).append("weightInPounds", weight).append("fromFirstName", senderFirstName)
				.append("fromLastName", senderLastName).append("fromAddr", senderAddress).append("fromCity", senderCity)
				.append("fromState", senderState).append("fromZipCode", senderZipCode)
				.append("toFirstName", recipientFirstName).append("toLastName", recipientLastName)
				.append("toAddr", recipientAddress).append("toCity", recipientCity).append("toState", recipientState)
				.append("toZipCode", recipientZipCode).append("shippingStatus", shippingStatus)
				.append("shippingCost", shippingCost).append("previousLocation", currentLocation)
				.append("currentLocation", currentLocation).append("Notes", specialNotes);

		// insert the document into the database
		collectionPackage.insertOne(doc);

		/*
		 * Old code // Since the tracking number is the ObjecId, have to generate the
		 * package and // then retrieve it to get the "tracking number" // and then pull
		 * it back out. // This retrieves the entire document that matches the sender's
		 * last name Document myDoc = (Document)
		 * collectionPackage.find(eq("fromLastName", senderLastName)).first(); if (myDoc
		 * == null) { System.out.
		 * println("I'm sorry, but that package did not register in the system."); }
		 * 
		 * // This extracts the ObjectId from the retrieved document ObjectId trackId =
		 * (ObjectId) (myDoc.get("_id"));
		 * 
		 * // This retrieves the entire document that matches the tracking number
		 * Document newDoc = (Document) collectionPackage.find(eq("_id",
		 * trackId)).first();
		 */

		// print out the this document to be used as a label
		System.out.println("Here is the shipping label for your package:\n\n");
		printLabel(trackingNum);

	}// end shipNewPackage

	// update the current location of a package that is enroute to its destination
	// Plus it updates previous location
	public void updateCurrentLocation() { // ******This works except updating zipcodes

		try {
			// Search for a package by the ID number
			System.out.println("What is the tracking number of the package you wish to update?");
			packageToSearch = console.next();

			// cast tracking number as an ObjectId
			ObjectId trackId = new ObjectId(packageToSearch);

			// This retrieves the entire document that matches the tracking number (Using
			// _id)
			Document myDoc = (Document) collectionPackage.find(eq("_id", trackId)).first();

			// if there is a null pointer exception, the document does not exist
			if (myDoc == null) {
				System.out.println("I'm sorry, but we do not have that package in our system.");
			}
			// This extracts the current location from the retrieved document
			String currentCity = (String) (myDoc.get("currentLocation"));

			// This extracts the previous location of the package
			String previousCity = (String) (myDoc.get("previousLocation"));

			if (currentCity == null) { // if the current location is null
				System.out.println("Im sorry, but the current location of the package is not available.");
			}

			System.out.println("What is the new location of the package?");
			String newCity = console.next();
			
			System.out.println("Would you like to change the status of this package? Y/N");
			String input  =  console.next();
			if(input.equalsIgnoreCase("y")) {
				System.out.println("What is the new status of the package?");
				String status = console.next();
				
				// update the document for new status
				collectionPackage.updateOne(eq("shippingStatus"),
						new Document("$set", new Document("shippingStatus", status)));
			}

			// update the document for previous location
			collectionPackage.updateOne(eq("previousLocation", previousCity),
					new Document("$set", new Document("previousLocation", currentCity)));

			// update the document for new location
			collectionPackage.updateOne(eq("currentLocation", currentCity),
					new Document("$set", new Document("currentLocation", newCity)));

		} // end try
		catch (InputMismatchException e) {
			console.next();
		} catch (NullPointerException r) {
			System.out.println("/n" + r.toString());
		} catch (Exception e) {
			System.out.println("/n" + e.toString());

		} finally {
		} // end catch

	}// end

	// add a new employee
	public void addNewEmployee() throws InvalidKeySpecException, FileNotFoundException, IOException { // ****This works except doesn't put
																	// put address in database
		// The admin starts out by entering the employee information
		System.out.println("What is the employee's first name?");
		fName = console.next();
		System.out.println("What is the employee's last name?");
		lName = console.next();
		System.out.println("What is the employee's address?");
		String addr = console.nextLine();
		// consume remaining new line
		console.next();
		System.out.println("What city do they live in?");
		String cityEE = console.nextLine();
		// consume remaining new line
		console.next();
		System.out.println("What state?");
		String stateEE = console.next();
		System.out.println("What is the employee's zip code?");
		String zip = console.next();
		System.out.println("What is the employee's phone number?");
		String phoneNo = console.next();
		System.out.println("What is the employee's rate of pay?");
		double pRate = console.nextDouble();
		System.out.println("What is the employee's start date?  DD-MM-YYYY");
		String sDate = console.next(); // how to do a type Date??
		
		System.out.println("Will this employee be an admin? Y/N");
		String admin = console.next();
		boolean isAdmin = false;
		if(admin.equalsIgnoreCase("y")) {
			isAdmin = true;
		}

		// The employee enters in this information
		System.out.println("Please have the employee enter a login and password for their account.\n");

		System.out.println("\nEnter the login for your account: ");
		String login = console.next();
		System.out.println("Login is: " + login);

		System.out.println("\nEnter the password for your account: ");
		String password = console.next();
		System.out.println("Password is: " + password);

		// generate a random salt to hash the password
		
		String salt = generateSalt();
		String hash = securePass(password, salt);
				
		// create and insert a new employee document
		Document doc = new Document("login", login).append("salt", salt).append("hash", hash)
				.append("firstName", fName).append("lastName", lName).append("address", addr).append("city", cityEE)
				.append("state", stateEE).append("zipCode", zip).append("phoneNum", phoneNo).append("payRate", pRate)
				.append("startDate", sDate).append(admin, isAdmin);

		// insert the document into the database
		collectionEE.insertOne(doc);

	}// end addNewEmployee

	// track a package by its tracking number (id #)
	// ****This should return a result of current location and shipping status or
	// print here
	public void trackPackageByTrackingNumber() {

		try {

			// Search for a package by the ID number
			System.out.println("What is the tracking number of the package you wish to locate?");
			packageToSearch = console.next();

			Iterable<Document> myDocIterable = collectionPackage.find(eq("trackingNum", packageToSearch));
			myDocIterable.forEach(document -> {
				try {
					packageObject = mapper.readValue(document.toJson(), Package.class);
				} catch (JsonParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonMappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("\nTracking Number: " + packageObject.trackingNum + "\nCurrent location: "
						+ packageObject.currentLocation + "\nPrevious location: " + packageObject.previousLocation);
			});
		} // end try
		catch (InputMismatchException e) {
			console.next();

		} // end mismatch catch
		catch (Exception e) {
			System.out.println("/n" + e.toString());

		} finally {
		} // end catch

	}// end trackPackage

	// calculate the shipping cost based on zip to zip and weight
	public double calculateShippingCost() {
		System.out.println("What is the sending zipcode?");
		senderZipcode = console.nextInt();

		System.out.println("What is the recipient's zipcode?");
		recipientZipcode = console.nextInt();

		System.out.println("What is the weight of the package?");
		double weight = console.nextDouble();

		try {
			// This retrieves the entire document that matches the sender's zip code
			Document sendZip = (Document) collectionZipCode.find(eq("Zipcode", senderZipcode)).first();
			if (sendZip == null) {
				System.out.println("I'm sorry, but we do not have that zip code in our system.");
			}
			double fromLatitude = (double) (sendZip.get("Lat"));
			double fromLongitude = (double) (sendZip.get("Long"));

			// This retrieves the entire document that matches the recipient's zip code
			Document toZip = (Document) collectionZipCode.find(eq("Zipcode", recipientZipcode)).first();
			if (toZip == null) {
				System.out.println("I'm sorry, but we do not have that zip code in our system.");
			}
			double toLatitude = (double) (toZip.get("Lat"));
			double toLongitude = (double) (toZip.get("Long"));

			// code to determine distance between two zip codes
			double distanceBetweenZipcodes = distance2(fromLatitude, fromLongitude, toLatitude, toLongitude);

			System.out.println("The distance between zip codes " + senderZipcode + " and" + recipientZipcode + "is "
					+ distanceBetweenZipcodes + " miles.");

			return (distanceBetweenZipcodes * SHIPPING_COST + weight);

		} // end try
		catch (InputMismatchException e) {
			console.next();
		} catch (NullPointerException r) {
			System.out.println("/n" + r.toString());
		} catch (Exception e) {
			System.out.println("/n" + e.toString());

		} finally {
		} // end catch

		return 0;

	}

	public String securePass(String password, String salt) throws FileNotFoundException, IOException {

		String generatedPassword = "";
		//System.out.println("Passed to secure: " + salt);
		//System.out.println("The password: " + password);
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(salt.getBytes(StandardCharsets.UTF_8));
			byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			generatedPassword = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return generatedPassword;
	}

	public String generateSalt() {
		SecureRandom rand = new SecureRandom();
		String newSalt = "";
		byte[] salt = new byte[20];
		rand.nextBytes(salt);
		// 32 - 126
		for (int i = 0; i < salt.length; i++) {
			if (salt[i] > 126) {
				salt[i] = 126;
			}
			if (salt[i] < 32) {
				salt[i] = 32;
			}
			newSalt = newSalt + salt[i];
		}
		return newSalt;
	}

	// Returns Spherical distance in miles given the latitude
	// and longitude of two points (depends on constant RADIUS)
	public static double distance(double lat1, double long1, double lat2, double long2) {
		lat1 = Math.toRadians(lat1);
		long1 = Math.toRadians(long1);
		lat2 = Math.toRadians(lat2);
		long2 = Math.toRadians(long2);
		double theCos = Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(long1 - long2);
		double arcLength = Math.acos(theCos);
		return arcLength * EARTH_RADIUS;
	}

	// attempt II at distance method
	// Returns Spherical distance in miles given the latitude
	// and longitude of two points (depends on constant RADIUS)
	public static double distance2(double lat1, double long1, double lat2, double long2) {
		// named Math.toRadians converts degrees to radians
		lat1 = Math.toRadians(lat1);
		long1 = Math.toRadians(long1);
		lat2 = Math.toRadians(lat2);
		long2 = Math.toRadians(long2);

		// Haversine formula
		double distancelongInRadians = long2 - long1;
		double distancelatInRadians = lat2 - lat1;

		double a = Math.pow(Math.sin(distancelatInRadians / 2), 2)
				+ Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(distancelongInRadians / 2), 2);

		double c = 2 * Math.asin(Math.sqrt(a));

		// calculate the result
		return (c * EARTH_RADIUS_MILES);
	}

	// need to retrieve the hashed password from database
	private String getPassword(String login) throws Exception {
		String temp;
		ArrayList<Person> results = new ArrayList<Person>();

		try {
			Iterable<Document> myDocIterable = collectionEE.find(eq("login", login));
			myDocIterable.forEach(document -> {
				try {
					personObject2 = mapper.readValue(document.toJson(), Person.class);
				} catch (JsonParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonMappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				results.add(personObject2);
			});

		} // end try
		catch (InputMismatchException e) {
			console.next();

		} // end mismatch catch
		catch (Exception e) {
			System.out.println("/n" + e.toString());

		} finally {
		} // end catch
		temp = results.get(0).getHash();
		return temp;
	}// end getPassword()

	private String getSalt(String login) throws Exception {
		String temp;
		ArrayList<Person> results = new ArrayList<Person>();

		try {
			Iterable<Document> myDocIterable = collectionEE.find(eq("login", login));
			myDocIterable.forEach(document -> {
				try {
					personObject = mapper.readValue(document.toJson(), Person.class);
				} catch (JsonParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonMappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				results.add(personObject);
			});

		} // end try
		catch (InputMismatchException e) {
			console.next();

		} // end mismatch catch
		catch (Exception e) {
			System.out.println("/n" + e.toString());

		} finally {
		} // end catch
		temp = results.get(0).getSalt();
		return temp;
	}

	// checks password and returns a boolean
	public boolean checkPassword(String login, String password) throws Exception {
		boolean pass = false;
		String salt = getSalt(login);
		String hash = getPassword(login);
		String input = securePass(password, salt);
		//System.out.println(hash);
		//System.out.println(input);
		if (hash.equals(input)) {
			pass = true;
		}
		return pass;
	}

	public void printLabel(String packageToSearch) {
		try {

			Iterable<Document> myDocIterable = collectionPackage.find(eq("trackingNum", packageToSearch));
			myDocIterable.forEach(document -> {
				try {
					packageObject = mapper.readValue(document.toJson(), Package.class);
				} catch (JsonParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonMappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				String output = ("*************************" + "\nTracking Number: " + packageObject.trackingNum
						+ "\n\nMailed from ZIP: " + packageObject.fromZipCode + "\n\nShip to: \n\n"
						+ packageObject.toFirstName + " " + packageObject.toLastName + "\n" + packageObject.toAddr
						+ "\n" + packageObject.toCity + ", " + packageObject.toState + " " + packageObject.toZipCode
						+ "\n*************************");

				try (PrintWriter out = new PrintWriter(packageToSearch + ".txt")) {
					out.println(output);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("Shipping label saved to file!");
				/*
				 * Print in console shipping label System.out.println(output);
				 */
			});
		} // end try
		catch (InputMismatchException e) {
			console.next();

		} // end mismatch catch
		catch (Exception e) {
			System.out.println("/n" + e.toString());

		} finally {
		} // end catch
	}// end print label

	protected String createTrackingNum(String zip) {
		// generates a tracking number with 5 digit zip code followed by 10 random
		// digits
		String val = zip; // start with reciever zip
		val += "-";

		// char or numbers (10), random 0-9 A-Z
		for (int i = 0; i < 11;) {
			int ranAny = 48 + (new Random()).nextInt(90 - 65);

			if (!(57 < ranAny && ranAny <= 65)) {
				char c = (char) ranAny;
				val += c;
				i++;
			}

		}

		return val;
	}

}// end class
