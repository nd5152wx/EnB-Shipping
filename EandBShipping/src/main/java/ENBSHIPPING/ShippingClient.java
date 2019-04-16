/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ENBSHIPPING;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.ServerAddress;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import org.bson.Document;
import java.util.Arrays;
import com.mongodb.Block;
import com.mongodb.MongoCredential;

import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.result.DeleteResult;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.UpdateResult;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Authors: Jason Richter/Joe Denzer Date: 7-May-2019 Course: CS485 Database
 * Systems Design Instructor: John Bartucz Program: Client Program Description:
 * Package Description:
 */
public class ShippingClient {

    //beginning of shipping variables
    private static String packageToSearch = ""; //This is the package ID and used to search for current locations
    private static String fName = "";//used to do searches for either a sender or recipient
    private static String lName = ""; //used to do searches for either a sender or recipient
    private static int senderZipcode = 0; //used to determine shipping costs
    private static int recipientZipcode = 0; //used to determine shipping costs

    static boolean permissionFlag = true;
    static int permission = Integer.MIN_VALUE;
    static boolean userFlag = true;
    static boolean tempFlag = true;
    static boolean employeeFlag = true;

    public static void main(String[] args) throws Exception {

        Methods EB = new Methods();

        //original login for Atlas
        //  MongoClientURI connectionString = new MongoClientURI("mongodb://localhost:27017");
        //  MongoClient mongoClient = new MongoClient("mongodb+srv://Jason:Pa33W7L@cluster0-68epl.mongodb.net/test?retryWrites=true");
        //use driver 3.4.3. Paste in connection string in quotes
        MongoClientURI uri = new MongoClientURI("mongodb://Joe:Parker1966@cluster0-shard-00-00-68epl.mongodb.net:27017,cluster0-shard-00-01-68epl.mongodb.net:27017,cluster0-shard-00-02-68epl.mongodb.net:27017/test?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin&retryWrites=true");
        MongoClient mongoClient = new MongoClient(uri);

        //access a database
        MongoDatabase database = mongoClient.getDatabase("EnBShipping");

        //access a collection
        MongoCollection<Document> collection = database.getCollection("Package");

        /*
        
        //create a document
        Document doc = new Document("name", "MongoDB")
                .append("type", "database")
                .append("count", 1)
                .append("versions", Arrays.asList("v3.2", "v3.0", "v2.6"))
                .append("info", new Document("x", 203).append("y", 102));
*/
        //insert one doc
      // collection.insertOne(doc);

        //update a document
   //     collection.updateOne(eq("i", 10), new Document("$set", new Document("i", 110)));

        //delete one doc
    //    collection.deleteOne(eq("i", 110));

        // Beginning of case/switch blocks
       Scanner console = new Scanner(System.in);

        while (permissionFlag) {

            showPermissionMenu();

            // try/catch to prevent input crash
            try {
                permission = console.nextInt();

            } // end try
            catch (InputMismatchException e) {
                console.next();

            } // end mismatch catch
            catch (Exception e) {
                System.out.println("/n" + e.toString());

            } // end catch

            //start of permissions for Admin, Employee, or User
            switch (permission) {

                case 1: // case 1 of permissions
                    System.out.println("Admin menu (password protected).");

                    //    boolean adminflag = login();
                     //   while (adminflag) {
                    while (tempFlag) {

                        int userCommand = Integer.MIN_VALUE;
                        showAdminMenu();

                        // try/catch to prevent input crash
                        try {
                            userCommand = console.nextInt();

                        } // end try
                        catch (InputMismatchException e) {
                            console.next();

                        } // end mismatch catch
                        catch (Exception e) {
                            System.out.println("/n" + e.toString());

                        } // end catch

                        switch (userCommand) {

                            case 1://Calculate shipping cost
                                double cost = EB.calculateShippingCost();
                                System.out.printf("The cost for this package is $%.2D", cost);
                                break;
                            case 2://change the last name of an employee
                                EB.shipNewPackage();
                                break;
                            case 3://Track package by tracking number
                                EB.trackPackageByTrackingNumber();
                                //Search for a package by its "tracking number" (document id)
                                //return shipping status and current location
                                break;
                            case 4://add a new employee
                                   EB.addNewEmployee();
                                System.out.println("The employee has been added to the database.");
                                break;

                            case 5://Delete an employee
                              EB.deleteEmployee();
                                break;

                            case 6://save for future use
                                  System.out.println("This selection is saved for future use.");
                                
                            case 7:// Update employee information
                              EB.updateEmployeeInformation();
                                break;
                                
                            case 8://show all employees
                                  EB.displayAllEmployees(); 
                                break;
         
                            case 9:
                                
                                break;
                      
                            case 0:// case 0 of Admin
                                System.out.println("Thank you for using the E & B Shipping Co.");
                                tempFlag = false;
                                break;
                            default:
                                System.out.println("Please enter one of the displayed numbers.");
                                break;
                        }// end admin switch
                    } // end admin while loop

                    break;// end of Permissions case 1 (Admin)

                case 2:// Start of Employee menu 

                    int employeeCommand = Integer.MIN_VALUE;

                    System.out.println("\n\nThis is the Employee menu.");

                    while (employeeFlag) {
                        // Display passenger menu
                        showEmployeeMenu();

                        // try/catch to prevent input crash
                        try {
                            employeeCommand = console.nextInt();

                        } // end try
                        catch (InputMismatchException e) {
                            console.next();

                        } // end mismatch catch
                        catch (Exception e) {
                            System.out.println("/n" + e.toString());

                        } // end catch

                        switch (employeeCommand) {

                            case 11://Employee menu/calculate shipping cost
                                double cost = EB.calculateShippingCost();
                                break;

                            case 12://Employee menu track package by tracking number
                                EB.trackPackageByTrackingNumber();
                                break;
                         
                            case 0:// exit the Employee menu
                                employeeFlag = false;
                                break;
                            default:
                                System.out.println("Please enter one of the displayed numbers.");
                                break;

                        }// end employee switch   
                    } // end employeeFlag

                case 3:// Start of User menu 

                    int userCommand = Integer.MIN_VALUE;

                    System.out.println("\n\nThis is the User menu.");

                    while (userFlag) {

                        // Display passenger menu
                        showUserMenu();

                        // try/catch to prevent input crash
                        try {
                            userCommand = console.nextInt();

                        } // end try
                        catch (InputMismatchException e) {
                            console.next();

                        } // end mismatch catch
                        catch (Exception e) {
                            System.out.println("/n" + e.toString());

                        } // end catch statements

                        switch (userCommand) {

                            case 21://user menu
                                double cost = EB.calculateShippingCost();
                                break;

                            case 22://user menu
                                EB.trackPackageByTrackingNumber();
                                break;

                            case 0:// exit the user menu
                                System.out.println("Exiting User Menu.");
                                userFlag = false;
                                break;
                            default:
                                System.out.println("Please enter one of the displayed numbers.");
                                break;

                        }// end user switch
                    } // end userFlag

                case 0:// exit the program
                    System.out.println("Thank you for using the E & B Shipping Co.");
                    permissionFlag = false;
                    break;

            }// End permission switch
        } // end permission flag

    }// end main

    public static void showPermissionMenu() {
        System.out.println("\n\n 1 - Admin Menu: Password protected.\n 2 - Employee Menu: Password Protected\n 3 - User\n 0 - Exit\n\n Please enter a command:\n");
    }//end showPermissionMenu

    public static void showEmployeeMenu() {
        System.out.println("\n 11 - Calculate Shipping Cost.\n 13 -Track a Package by Tracking Number\n "
                + " 0 - Exit\n\n Please enter a command:");
    }//end employeeMenu

    // print a user menu
    private static void showAdminMenu() {
        System.out.print("\n\n" + "1 - Calculate Shipping Cost\n" + "2 - Ship New Package\n"
                + "3 - Track Package by Tracking Number\n" + "4 - Add New Employee\n" + "5 - Remove Employee\n"
                + "6 - Add Admin\n" + "7 - Update Employee Information \n" + "8 - Display All Employees\n"
                + "0 - Exit\n\n" + "Please enter a command: \n");
    }// end showAdminMenu

    public static void showUserMenu() {
        System.out.println("\n\n 21 - Calculate Shipping Cost.\n 22 - Track a Package by Tracking Number\n "
                + " 0 - Exit\n\n Please enter a command:\n");
    }//end showUserMenu

    // MongoClient mongoClient = new MongoClient();
    //for local host
    //  MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
    //    MongoCollection<Document> collection = database.getCollection("Users");
    


    

    /*
     private static boolean login() throws Exception {

     Scanner console = new Scanner(System.in);
     String login;
     String password;

     System.out.print("\nPlease enter your username: ");
     login = console.next();
     System.out.print("\nPlease enter your password: ");
     password = console.next();
     return false;
     }

     */
}//end class

/*

 //might need this 
 // Creating Credentials 
 MongoCredential credential;
 credential = MongoCredential.createCredential("sampleUser", "myDb",
 "password".toCharArray());
 System.out.println("Connected to the database successfully");



 MySQLAccess dao = new MySQLAccess();
		
 byte[] salt = dao.getSalt(login);
 byte[] encPass = dao.getPassword(login);
		
 byte[] hashed = hash(password.toCharArray(), salt);
		
 for (int i = 0; i < encPass.length; i++) {
 if (encPass[i] != hashed[i]) {
 System.out.println("Incorrect username / password entered. Please try again.");
 return false;
 }
 }
		
 return true;
 }//end login

 

 public static byte[] hash(char[] password, byte[] salt) {
 PBEKeySpec spec = new PBEKeySpec(password, salt, 10000, 256);
 Arrays.fill(password, Character.MIN_VALUE);
 try {
 SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
 return skf.generateSecret(spec).getEncoded();
 } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
 throw new AssertionError("Error while hashing a password: " + e.getMessage(), e);
 } finally {
 spec.clearPassword();
 }//end finally
 }//end byte[]
	
 public static double milesBetweenTwoZipCodes(int sendingZipcode, int receivingZipcode) throws MalformedURLException  {
 // creates a URL with string representation. 
 URL url1 = new URL("https://www.melissa.com/v2/lookups/zipdistance/zipcode?zip1=" + sendingZipcode + "&zip2="+ receivingZipcode); 
             
 return 0.0; 
		
 }
 */
// end class

//The following example retrieves all documents from the inventory collection where status equals either "A" or "D":

//findPublisher = collection.find(in("status", "A", "D"));
