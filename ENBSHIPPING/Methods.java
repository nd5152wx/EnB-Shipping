/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ENBSHIPPING;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.eq;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import static jdk.nashorn.tools.ShellFunctions.input;
import org.bson.Document;
import org.bson.json.JsonParseException;
import org.bson.types.ObjectId;

/**
 *
 * @author nd5152wx
 */
public class Methods {

    //beginning of shipping variables
    private static String packageToSearch = ""; //This is the package ID and used to search for current locations
    private static String fName = "";//used to do searches for either a sender or recipient
    private static String lName = ""; //used to do searches for either a sender or recipient
    private static int senderZipcode = 0; //used to determine shipping costs
    private static int recipientZipcode = 0; //used to determine shipping costs
    private static String EELogin = ""; //used to retrieve Admin or EE login

    final static double SHIPPING_COST = .05;
    // radius of the Earth in miles
    public static final double EARTH_RADIUS_MILES = 3963;

    static Scanner console = new Scanner(System.in);

    //use driver 3.4.3. Paste in connection string in quotes
    MongoClientURI uri = new MongoClientURI("mongodb://Joe:Parker1966@cluster0-shard-"
            + "00-00-68epl.mongodb.net:27017,cluster0-shard-00-01-68epl.mongodb.net:27017,"
            + "cluster0-shard-00-02-68epl.mongodb.net:27017/test?ssl=true&replicaSet="
            + "Cluster0-shard-0&authSource=admin&retryWrites=true");

    //  MongoClientURI uri = new MongoClientURI("");
    MongoClient mongoClient = new MongoClient(uri);

    //access a database
    MongoDatabase database = mongoClient.getDatabase("EnBShipping");

    //access a collection
    MongoCollection<Document> collectionEE = database.getCollection("Employee");

    //access a collection
    MongoCollection<Document> collectionZipCode = database.getCollection("USZipCodes");

    //access a collection
    MongoCollection<Document> collectionPackage = database.getCollection("Package");

    //access a collection
    MongoCollection<Document> collectionAdmin = database.getCollection("Administrator");

    //ship a new package
    public void shipNewPackage() {
        double width, length, height, weight;   
        double shippingCost = 0;
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
        String shippingStatus; //keeps track of whether package has been shipped/delivered
        String previousLocation; //keeps track of the previous stop of the delivery
        String currentLocation; //the current city the package is in
        String specialNotes; //note such as "place in garage"/who signed for package
        final double SHIPPING_COST = .05;//cost per mile for shipping

        /*
         //get the package specifics
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
         console.next();

         //get the sender information
         console.nextLine();//after int or double
         System.out.println("What is the sender's first name?");
         senderFirstName = console.nextLine();

         System.out.println("What is the sender's last name?");
         senderLastName = console.nextLine();

         System.out.println("What is the sender's address?");
         senderAddress = console.nextLine();
               
         */
        
        width = 12;
        length = 14;
        height = 24;
        weight = 16;
        senderFirstName = "John";
        senderLastName = "Simpson";
        senderAddress = "786 W East Way Road NE";
         
        System.out.println("What is the sender's city?");
        senderCity = console.nextLine();

        System.out.println("What is the sender's 5 digit zip code?");
        senderZipCode = console.next();

        /*
        //get recipient information
        System.out.println("What is the recipient's first name?");
        recipientFirstName = console.next();

        System.out.println("What is the recipient's last name?");
        recipientLastName = console.next();

        System.out.println("What is the recipient's address?");
        recipientAddress = console.nextLine();
        */
         
        recipientFirstName = "Bart";
        recipientLastName = "Simpson";
        recipientAddress = "687 E West Road Way SW";
        
        System.out.println("What is the recipient's city?");
        recipientCity = console.nextLine();

        System.out.println("What is the recipient's 5 digit zip code?");
        recipientZipCode = console.next();
        
        
        System.out.println("Are there any special notes for the delivery person?");
        specialNotes = console.nextLine();
     
        shippingStatus = "HasShipped";

        //set the current location of the package
        currentLocation = senderCity;

        //insert new package into the database
        Document doc = new Document("width", width)
                .append("length", length)
                .append("height", height)
                .append("weightInPounds", weight)
                .append("fromFirstName", senderFirstName)
                .append("fromLastName", senderLastName)
                .append("fromAddr", senderAddress)
                .append("fromCity", senderCity)
                .append("fromZipCode", senderZipCode)
                .append("toFirstName", recipientFirstName)
                .append("toLastName", recipientLastName)
                .append("toAddr", recipientAddress)
                .append("toCity", recipientCity)
                .append("toZipCode", recipientZipCode)
                .append("shippingStatus", shippingStatus)
                .append("shippingCost", shippingCost)
                .append("previousLocation", currentLocation)
                .append("currentLocation", currentLocation)
                .append("Notes", specialNotes);

        //insert the document into the database
        collectionPackage.insertOne(doc);

        //Since the tracking number is the ObjecId, have to generate the package and then retrieve it to get the "tracking number"
        //and then pull it back out.
         //This retrieves the entire document that matches the sender's last name 
        Document myDoc = (Document) collectionPackage.find(eq("fromLastName", senderLastName)).first();
        if (myDoc == null) {
            System.out.println("I'm sorry, but that package did not register in the system.");
        }
        
        //This extracts the ObjectId from the retrieved document
         ObjectId trackId = (ObjectId) (myDoc.get("_id"));
        
        
         //This retrieves the entire document that matches the tracking number  
         Document newDoc = (Document) collectionPackage.find(eq("_id", trackId)).first();
         
         //print out the this document to be used as a label

    }//end shipNewPackage



    //update the current location of a package that is enroute to its destination
    //Plus it updates previous location
    public void updateCurrentLocation() { //******This works except updating zipcodes
        
        try {
            // Search for a package by the ID number
            System.out.println("What is the tracking number of the package you wish to update?");
            packageToSearch = console.next();

            //cast tracking number as an ObjectId
            ObjectId trackId = new ObjectId(packageToSearch);

            //This retrieves the entire document that matches the tracking number (Using _id) 
            Document myDoc = (Document) collectionPackage.find(eq("_id", trackId)).first();

            //if there is a null pointer exception, the document does not exist
            if (myDoc == null) {
                System.out.println("I'm sorry, but we do not have that package in our system.");
            }
            //This extracts the current location from the retrieved document
            String currentCity = (String) (myDoc.get("currentLocation"));
            
            //This extracts the previous location of the package
            String previousCity = (String) (myDoc.get("previousLocation"));
            
            if (currentCity == null) { //if the current location is null
                System.out.println("Im sorry, but the current location of the package is not available.");
            }
            
            System.out.println("What is the new location of the package?");
            String newCity = console.next(); 
            
              //update the document for previous location
            collectionPackage.updateOne(eq("previousLocation", previousCity), new Document("$set", new Document("previousLocation", currentCity)));
           
            //update the document for new location
            collectionPackage.updateOne(eq("currentLocation", currentCity), new Document("$set", new Document("currentLocation", newCity)));
       
        } // end try
        catch (InputMismatchException e) {
            console.next();
        } catch (NullPointerException r) {
            System.out.println("/n" + r.toString());
        } catch (Exception e) {
            System.out.println("/n" + e.toString());

        } finally {
        } // end catch

    }//end 

    //add a new employee
    public void addNewEmployee() throws InvalidKeySpecException { // ****This works except doesn't put 
                                                                   //put address in database
        //The admin starts out by entering the employee information
        System.out.println("What is the employee's first name?");
        fName = console.next();
        System.out.println("What is the employee's last name?");
        lName = console.next();
        System.out.println("What is the employee's address?");
        String addr = console.nextLine();
        //consume remaining new line
        console.next();
        System.out.println("What city do they live in?");
        String cityEE = console.nextLine();
        //consume remaining new line
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
        String sDate = console.next(); //how to do a type Date??

        //The employee enters in this information
        System.out.println("Please have the employee enter a login and password for their account.\n");
        System.out.print("\nEnter the login for your account: ");
        String login = console.next();
        System.out.print("\nEnter the password for your account: ");
        String password = console.next();

        //generate a random salt to hash the password
        SecureRandom rand = new SecureRandom();
        byte[] salt = new byte[32];
        rand.nextBytes(salt);

        //create and insert a new employee document
        Document doc = new Document("login", login)
                .append("salt", salt)
                .append("hash", hash(password.toCharArray(), salt))
                .append("firstName", fName)
                .append("lastName", lName)
                .append("address", addr)
                .append("city", cityEE)
                .append("state", stateEE)
                .append("zipCode", zip)
                .append("phoneNum", phoneNo)
                .append("payRate", pRate)
                .append("startDate", sDate);

        //insert the document into the database
        collectionEE.insertOne(doc);

    }//end addNewEmployee


    //calculate the shipping cost based on zip to zip and weight
    public double calculateShippingCost() {
        System.out.println("What is the sending zipcode?"); 
        senderZipcode = console.nextInt();

        System.out.println("What is the recipient's zipcode?");
        recipientZipcode = console.nextInt();

        System.out.println("What is the weight of the package?");
        double weight = console.nextDouble();

        try {
            //This retrieves the entire document that matches the sender's zip code 
            Document sendZip = (Document) collectionZipCode.find(eq("Zipcode", senderZipcode)).first();
            if (sendZip == null) {
                System.out.println("I'm sorry, but we do not have that zip code in our system.");
            }
            double fromLatitude = (double) (sendZip.get("Lat"));
            double fromLongitude = (double) (sendZip.get("Long"));

            //This retrieves the entire document that matches the recipient's zip code 
            Document toZip = (Document) collectionZipCode.find(eq("Zipcode", recipientZipcode)).first();
            if (toZip == null) {
                System.out.println("I'm sorry, but we do not have that zip code in our system.");
            }
            double toLatitude = (double) (toZip.get("Lat"));
            double toLongitude = (double) (toZip.get("Long"));

            //code to determine distance between two zip codes
            double distanceBetweenZipcodes = distance2(fromLatitude, fromLongitude, toLatitude, toLongitude);

            System.out.println("The distance between zip codes " + senderZipcode + " and"
                    + recipientZipcode + "is " + distanceBetweenZipcodes + " miles.");

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

    // Returns Spherical distance in miles given the latitude 
    // and longitude of two points (depends on constant RADIUS)
    public static double distance(double lat1, double long1, double lat2, double long2) {
        lat1 = Math.toRadians(lat1);
        long1 = Math.toRadians(long1);
        lat2 = Math.toRadians(lat2);
        long2 = Math.toRadians(long2);
        double theCos = Math.sin(lat1) * Math.sin(lat2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.cos(long1 - long2);
        double arcLength = Math.acos(theCos);
        return arcLength * EARTH_RADIUS_MILES;
    }//end of distance method

    //attempt II at distance method
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
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(distancelongInRadians / 2), 2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // calculate the result 
        return (c * EARTH_RADIUS_MILES);
    }

    
     public static boolean login() throws Exception {
     Scanner console = new Scanner(System.in);
     String login = "";
     String password = "";

     System.out.print("\nPlease enter your username: ");
     EELogin = console.next();

     System.out.print("\nPlease enter your password: ");
     password = console.next();
     byte[] salt = getSalt(login);
     byte[] encodedPassword = getPassword(login);

     byte[] hashed = hash(password.toCharArray(), salt);

     for (int i = 0; i < encodedPassword.length; i++) {
     if (encodedPassword[i] != hashed[i]) {
     System.out.println("The username or password is incorrect. Please try again.");//shut off at 5 attempts
     return false;
     }
     }

     return true;
     }

     //converts password and salt to a hash value
     public static byte[] hash(char[] password, byte[] salt) throws InvalidKeySpecException {
     PBEKeySpec spec = new PBEKeySpec(password, salt, 10000, 256);
     Arrays.fill(password, Character.MIN_VALUE);
     try {
     SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
     return skf.generateSecret(spec).getEncoded();
     } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
     throw new AssertionError("Error while hashing a password: " + e.getMessage(), e);
     } finally {
     spec.clearPassword();
     }
     }//end hash method

 
    
     //need to retrieve the salt array from database
     public static byte[] getSalt(String adminLogin) throws Exception {

     //This retrieves the entire document that matches the Administrator's login 
     Document saltArray = (Document) collectionAdmin.find(eq("login", adminLogin)).first();
     if (saltArray == null) {
     System.out.println("I'm sorry, but we do not have that user login in our system.");
     }
       
     byte[] salted = (byte[]) (saltArray.get("salt"));

     return salted;
     }// end getSalt()

     //need to retrieve the hashed password from database
     public static byte[] getPassword(String login) throws Exception {
     byte[] password = new byte[32];

     return password;
     }// end getHash()

     }//end class

   
/*

    //track a package by its tracking number (id #)
    public void trackPackageByTrackingNumber() { //****this works ********

        try {
            // Search for a package by the ID number
            System.out.println("What is the tracking number of the package you wish to locate?");
            packageToSearch = console.next();

            //cast tracking number as an ObjectId
            ObjectId trackId = new ObjectId(packageToSearch);

            //This retrieves the entire document that matches the tracking number (Using _id) 
            Document myDoc = (Document) collectionPackage.find(eq("_id", trackId)).first();

            //if there is a null pointer exception, the document does not exist
            if (myDoc == null) {
                System.out.println("I'm sorry, but we do not have that package in our system.");
            }
            //This extracts the current location from the retrieved document
            String currentCity = (String) (myDoc.get("currentLocation"));
            if (currentCity == null) { //if the current location is null
                System.out.println("Im sorry, but the current location of the package is not available.");
            }
            //print out the current location of the package
            System.out.print("\n\nThe package is currently in " + currentCity);

        } // end try
        catch (InputMismatchException e) {
            console.next();
        } catch (NullPointerException r) {
            System.out.println("/n" + r.toString());
        } catch (Exception e) {
            System.out.println("/n" + e.toString());

        } finally {
        } // end catch

    }//end trackPackage
*/