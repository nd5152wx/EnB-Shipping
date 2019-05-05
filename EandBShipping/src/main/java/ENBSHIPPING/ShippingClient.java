
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
import java.util.*;
import java.util.Scanner;
import java.util.logging.Level;

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
    	
    	//This removes console logging from mongo. Comment out this line for diagnostic purposes
        java.util.logging.Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);

        Methods EB = new Methods();

        //original login for Atlas
        //  MongoClientURI connectionString = new MongoClientURI("mongodb://localhost:27017");
        //  MongoClient mongoClient = new MongoClient("mongodb+srv://Jason:Pa33W7L@cluster0-68epl.mongodb.net/test?retryWrites=true");
        //use driver 3.4.3. Paste in connection string in quotes
        MongoClientURI uri = new MongoClientURI("mongodb://Joe:Parker1966@cluster0-shard-00-00-68epl.mongodb.net:27017,"
                + "cluster0-shard-00-01-68epl.mongodb.net:27017,cluster0-shard-00-02-68epl.mongodb.net:27017/test?ssl="
                + "true&replicaSet=Cluster0-shard-0&authSource=admin&retryWrites=true");
        MongoClient mongoClient = new MongoClient(uri);

        //access a database
        MongoDatabase database = mongoClient.getDatabase("EnBShipping");

        //access a collection
        MongoCollection<Document> collection = database.getCollection("Package");
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
                	console.nextLine();

                	System.out.println("Please enter your username: ");
                	String login = console.nextLine();
                	
                	System.out.println("Please enter your password: ");
                	String password = console.nextLine();
                	
                	/*
                	if(EB.checkPassword(login, password) == false) {
                		System.out.print("Password incorrect!");
                		break;
                	}
                	*/
                    System.out.println("Admin menu (password protected).");
                    
                //      boolean adminflag = EB.login();
                //       while (adminflag) {
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
                               // System.out.printf("The cost for this package is $%.2D", cost);
                                 System.out.println( cost);
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
                            case 5: //update current location of package
                                EB.updateCurrentLocation();
                                break;
                            case 0:// case 0 of Admin
                                System.out.println("Thank you for using the E & B Shipping Co.");
                                tempFlag = false;
                                //adminFlag = false;
                                break;
                            default:
                                System.out.println("Please enter one of the displayed numbers.");
                                break;
                        }// end admin switch
                    } // end admin while loop

                    break;// end of Permissions case 1 (Admin)

                case 2:// Start of Employee menu 
                	console.nextLine();
                	System.out.println("Please enter your username: ");
                	String login2 = console.nextLine();
                	
                	System.out.println("Please enter your password: ");
                	String password2 = console.nextLine();
                	
                	if(EB.checkPassword(login2, password2) == false) {
                		System.out.print("Password incorrect!");
                		break;
                	}

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
                                
                            case 23://user menu
                            	System.out.println("What is the tracking number of the package you wish to print a label for?");
                    			String searched = console.next();
                            	EB.printLabel(searched);
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
                + " 3 - Ship New Package\n"
                + " 0 - Exit\n\n Please enter a command:");
    }//end employeeMenu

    // print a user menu
    private static void showAdminMenu() {
        System.out.print("\n\n" + "1 - Calculate Shipping Cost\n" + "2 - Ship New Package\n"
                + "3 - Track Package by Tracking Number\n" + "4 - Add New Employee\n" 
                + "5 - Update Current Location\n"
                + "0 - Exit\n\n" + "Please enter a command: \n");
    }// end showAdminMenu

    public static void showUserMenu() {
        System.out.println("\n\n 21 - Calculate Shipping Cost.\n 22 - Track a Package by Tracking Number"+
    "\n 23 - Print a shipping label. \n 0 - Exit\n\n Please enter a command:\n");
    }//end showUserMenu
}//end class


    //*********BELOW IS STUFF FOR REFERENCE*********************
    /*
 
 
     //update a document
     //     collection.updateOne(eq("i", 10), new Document("$set", new Document("i", 110)));
     //delete one doc
     //    collection.deleteOne(eq("i", 110));
 //might need this 
 // Creating Credentials 
 MongoCredential credential;
 credential = MongoCredential.createCredential("sampleUser", "myDb",
 "password".toCharArray());
 System.out.println("Connected to the database successfully");
 */

  /*
     //update a document
     collectionEE.updateOne(eq(employeeField, currentFieldData), new Document("$set", new Document(employeeField, newFieldData)));
     System.out.println("The employee's information has been updated.");
      //update employee information
    public void updateEmployeeInformation() { //****this works ********
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
*/