/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ENBSHIPPING;

import com.mongodb.Block;
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
import static java.lang.System.console;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.bson.Document;

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
    private String login = "";
    private String password = "";

    static Scanner console = new Scanner(System.in);

    //use driver 3.4.3. Paste in connection string in quotes
    MongoClientURI uri = new MongoClientURI("mongodb://Joe:Parker1966@cluster0-shard-"
            + "00-00-68epl.mongodb.net:27017,cluster0-shard-00-01-68epl.mongodb.net:27017,"
            + "cluster0-shard-00-02-68epl.mongodb.net:27017/test?ssl=true&replicaSet="
            + "Cluster0-shard-0&authSource=admin&retryWrites=true");
    MongoClient mongoClient = new MongoClient(uri);

    //access a database
    MongoDatabase database = mongoClient.getDatabase("EnBShipping");

    //access a collection
    MongoCollection<Document> collectionEE = database.getCollection("Employee");

    //access a collection
    MongoCollection<Document> collectionPackage = database.getCollection("Package");

    //access a collection
    MongoCollection<Document> collectionAdmin = database.getCollection("Administrator");

    //ship a new package
    public void shipNewPackage() {
        double width = 0;
        double length = 0;
        double height = 0;
        double weight = 0;
        double shippingCost = 0;
        String senderFirstName = "";
        String senderLastName = "";
        String senderAddress = "";
        String senderCity = "";
        String senderState = "";
        String senderZipCode = "";
        String recipientFirstName = "";
        String recipientLastName = "";
        String recipientAddress = "";
        String recipientCity = "";
        String recipientState = "";
        String recipientZipCode = "";
        String shippingStatus = ""; //keeps track of whether package has been shipped/delivered
        String currentLocation = ""; //the current city the package is in
        String specialNotes = ""; //note such as "place in garage"/who signed for package

        //get the package specifics
        System.out.println("What is the width of the package?");
        width = console.nextDouble();
        System.out.println("What is the length of the package?");
        length = console.nextDouble();
        System.out.println("What is the heigth of the package?");
        height = console.nextDouble();
        System.out.println("What is the weight of the package in pounds?");
        weight = console.nextDouble();

        //get the sender information
        System.out.println("What is the sender's first name?");
        senderFirstName = console.next();

        System.out.println("What is the sender's last name?");
        senderLastName = console.next();

        System.out.println("What is the sender's address?");
        senderAddress = console.nextLine();
        console.next();
        System.out.println("What is the sender's city?");
        senderAddress = console.next();

        System.out.println("What is the sender's state?");
        senderState = console.next();

        System.out.println("What is the sender's zip code?");
        senderZipCode = console.next();

        //get recipient information
        System.out.println("What is the recipient's first name?");
        recipientFirstName = console.next();

        System.out.println("What is the recipient's last name?");
        recipientLastName = console.next();

        System.out.println("What is the recipient's address?");
        recipientAddress = console.nextLine();
        console.next();
        System.out.println("What is the recipient's city?");
        recipientCity = console.next();
        console.next();
        System.out.println("What is the recipient's state?");
        recipientState = console.next();

        System.out.println("What is the recipient's zip code?");
        recipientZipCode = console.next();
        System.out.println("Are there any special notes for the delivery person?");
        specialNotes = console.nextLine();
        console.next();

        //Need a better way to set this later if have time
        shippingStatus = "HasShipped";

        //set the current location of the package
        currentLocation = senderCity;

        //call method to calculate shipping cost
        shippingCost = calculateShippingCost(senderZipCode, recipientZipCode, weight);
        System.out.println("The package will cost " + shippingCost + " to ship.");

        //insert new package into the database
        //create and insert a new employee document
        Document doc = new Document("width", width)
                .append("length", height)
                .append("height", height)
                .append("weightInPounds", weight)
                .append("fromFirstName", senderFirstName)
                .append("fromLastName", senderLastName)
                .append("fromAddr", senderAddress)
                .append("fromCity", senderCity)
                .append("fromState", senderState)
                .append("fromZipCode", senderZipCode)
                .append("toFirstName", recipientFirstName)
                .append("toLastName", recipientLastName)
                .append("toAddr", recipientAddress)
                .append("toCity", recipientCity)
                .append("toState", recipientState)
                .append("toZipCode", recipientZipCode)
                .append("shippingStatus", shippingStatus)
                .append("shippingCost", shippingCost)
                .append("currentLocation", currentLocation)
                .append("Notes", specialNotes);

        //insert the document into the database
        collectionPackage.insertOne(doc);
        //print out the shipping label

    }//end shipNewPackage

    public double calculateShippingCost(String senderZipcode, String recipientZipcode, double weight) {
        return weight * 2.00;
    }

    //add a new employee
    public void addNewEmployee() throws InvalidKeySpecException {

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
        login = console.next();
        System.out.print("\nEnter the password for your account: ");
        password = console.next();

        //generate a random salt to hash the password
        SecureRandom rand = new SecureRandom();
        byte[] salt = new byte[32];
        rand.nextBytes(salt);
        // byte[] hash = hash(password.toCharArray(),salt);
        byte[] hash;

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
                .append("phoneNum", phoneNo) //will the employee be able to alter there pay rate if logging in this way
                .append("payRate", pRate) //or should it be on a different account?
                .append("startDate", sDate);

        //insert the document into the database
        collectionEE.insertOne(doc);

    //   Document myDoc = (Document) collectionEE.find(eq("login",login));
        //    System.out.println(myDoc.toJson());
    }//end addNewEmployee

    //remove an employee from the database **This works but only based on last name***
    public void deleteEmployee() {
        System.out.println("What is the employee's first name?");
        fName = console.next();

        System.out.println("What is the employee's last name?");
        lName = console.next();

        //first retrieve the employee document to ensure that it is the correct one
        collectionEE.find(and(eq("firstName", fName), eq("lastName", lName)));

        collectionEE.deleteOne(eq("lastName", lName));

        //state that the employee has been removed from the database
        System.out.println("The employee, " + fName + " " + lName + " has been removed from the database.");

    }//end deleteEmployee

    //update employee information. ***This method works**
    public void updateEmployeeInformation() {

        System.out.println("Here is a list of employee fields:\n"
                + "firstName, lastName, address, city, state, zipcode\n"
                + "phoneNum, payRate, startDate\n\n");
        System.out.println("Which field of employee do you wish to update?");
        String employeeField = console.next();

        //get the current data from the employee document that needs to be updated
        System.out.println("What is the current " + employeeField + " of the employee that you wish to update?");
        String currentFieldData = console.next();

        //get the current data from the employee document that needs to be updated
        System.out.println("What is the new " + employeeField + " of the employee??");
        String newFieldData = console.next();

        //update a document
        collectionEE.updateOne(eq(employeeField, currentFieldData), new Document("$set", new Document(employeeField, newFieldData)));
        System.out.println("The employee's information has been updated.");
    }

    //display all empployees
    public void displayAllEmployees() {

        FindIterable<Document> findIterable = collectionEE.find(new Document());
        //print this collection
        collectionEE = database.getCollection("Employee");

        MongoCursor<Document> cursor = collectionEE.find().iterator();
        try {
            while (cursor.hasNext()) {
                System.out.println(cursor.next().toJson());
                System.out.println();
            }
        } finally {
            cursor.close();
        }

        
        
        try (MongoCursor<Document> cur = collectionEE.find().iterator()) {
     while (cur.hasNext()) {

        Document doc = cur.next();
        
        List list = new ArrayList(doc.values());
        System.out.print(list.get(1));
        System.out.print(": ");
        System.out.println(list.get(2));
    }
}
    
        
      
     

     
        //this skips the first 3 items and only prints the next 7
        //This will skip confidential information
        FindIterable it = collectionEE.find().skip(3).limit(7);
        
     //   it.forEach((Block<Document>) System.out::println);
  
        
    }

    //track a package by its tracking number (id #)
    //****This should return a result of current location and shipping status or print here
    public void trackPackageByTrackingNumber() {

        try {
            // Search for a package by the ID number
            System.out.println("What is the tracking number of the package you wish to locate?");
            packageToSearch = console.next();// 

            //retrieve currentLocation based off of package id (tracking number)
            FindIterable<Document> location = collectionPackage.
                    find(eq("_id", packageToSearch)).projection(fields(include("currentLocation", "_id")));
            if (location == null) {
                System.out.println("I'm sorry, but we do not have that package in our system.");
            } else {
                for (Document doc : location) {
                    System.out.println("The current location for that package is " + location);

                }
            }

        } // end try
        catch (InputMismatchException e) {
            console.next();

        } // end mismatch catch
        catch (Exception e) {
            System.out.println("/n" + e.toString());

        } finally {
        } // end catch

    }//end trackPackage

    //calculate the shipping cost based on zip to zip
    public double calculateShippingCost() {
        System.out.println("What is the sending zipcode?"); //this should probably be done in the client and send in
        senderZipcode = console.nextInt();

        System.out.println("What is the recipient's zipcode?");
        recipientZipcode = console.nextInt();

        System.out.println("What is the weight of the package?");
        double weight = console.nextDouble();

        //calculate the distance between the 2 zipcodes
        //application key for EandBShipping  6f3omKcSjKiBVZCRvlzkM65TPLKysKvHCJfKh2gyZxRDAYpOOIXgraWwdq6jS2Qx
        //https://www.zipcodeapi.com/Register
        return 0;
    }


    /*
     private static boolean login() throws Exception {
     Scanner console = new Scanner(System.in);
     String login;
     String password;

     System.out.print("\nPlease enter your username: ");
     login = console.next();
     System.out.print("\nPlease enter your password: ");
     password = console.next();
		
          
		
     //query database to get the user's password and salt based on login 
      
     byte[] salt = getSalt(login);
     byte[] encPass = getPassword(login);
		
     byte[] hashed = hash(password.toCharArray(), salt);
		
     for (int i = 0; i < encPass.length; i++) {
     if (encPass[i] != hashed[i]) {
     System.out.println("Incorrect username / password entered. Please try again.");
     return false;
     }
     }
     return true;
     }//end login
     
     */
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
    }//end has method

    public byte[] getSalt(String login) throws Exception {
        byte[] salt = new byte[32];
        Class.forName("com.mysql.jdbc.Driver");

        String sql = "SELECT salt FROM TicketAgent WHERE login = ?";

        //	ResultSet set = pst.executeQuery();
        //	while (set.next()) {
        //		salt = set.getBytes(1);
        //	}
        return salt;
    }// end getSalt()

    /*

     public byte[] getPassword(String login) throws Exception {
     byte[] password = new byte[32];
     Class.forName("com.mysql.jdbc.Driver");


     //String sql = "SELECT encPass FROM TicketAgent WHERE login = ?";

     //loop thru the returned array and get the bytes
     ResultSet set = pst.executeQuery();
     while (set.next()) {
     password = set.getBytes(1);
     }
     return password;
     }// end getHash()
   
        
     } 
    
     */
}//end class

