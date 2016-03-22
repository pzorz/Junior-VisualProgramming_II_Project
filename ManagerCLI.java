package UML;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Author:          Peter Zorzonello
 * File:            ManagerCLI.java
 * Last Updated:    5/1/15
 * Version:         2.0
 *
 * The IT manager's portal into the app.
 * From here they can add, remove, and list users.
 * They can add devices, remove, devices, list devices, and
 * search for devices. They can also check devices out.
 * The CLI will have all functionality of the GUI and more.
 */
public class ManagerCLI {

    //the connection to the DB
    static UserDBManager db;
    static DeviceDBManager deviceDB;


    /**
     * This is the main method for the class. When the application runs this method will execute.
     * This method continuously prompts the user for input and calls functions based off the user input.
     *
     * @param args
     */
    public static void main(String[] args){

        //Attempt to connect to the DB and create a table if there is is not already a table
        try {
            db = new UserDBManager();
            db.createUsersTable();

            deviceDB = new DeviceDBManager();
            deviceDB.createAssetTable();
        } catch (SQLException e) {
            System.err.println("There was a problem connecting to the User DB " + e);
        }

        //variables needed to authenticate a user and read commands in
        boolean isAuthed = false;
        String pass;
        String function;

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Welcome to Asset Manager");

        //as long as the Manager is not authenticated keep trying to log them in
        while(!isAuthed) {
            System.out.println("To log in as the manager please enter in your Manager password and press enter");
            try {
                pass = in.readLine();

                if(db.authUser("0001", pass)){
                    isAuthed = true;
                }
            } catch (Exception e) {
                System.out.println("I could not understand that");
            }
        }

        //after the Manager has successful logged in find out what they want to do
        function ="";
        try {
                while (!function.equals("exit")) {

                    //prompt the user for a command
                    System.out.println("Enter \"add employee\" and press enter to add a new employee to the DB. " +
                            "\n Enter \"remove employee\" to remove an employee. \n Enter \"list all\" to display " +
                            "a list of all employees\n Enter \"devices\" to switch to the devices menu \nEnter \"exit\" " +
                            "and press enter to quit.");
                    try {
                        function = in.readLine();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                    //if the string given by user is "add employee" call the addUser function
                    if (function.equals("add employee")) {

                        //try to get the user's name
                        System.out.println("Please enter the employee's name");
                        String name = null;
                        try {
                            name = in.readLine();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }

                        //try to get the employee id
                        System.out.println("Please enter the employee's ID");
                        String employeeId = null;
                        try {
                            employeeId = in.readLine();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }

                        //try to get the employee password
                        System.out.println("Please enter the employee's password");
                        String password = null;
                        try {
                            password = in.readLine();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }

                        //create a user to add
                        User user = new User(name, password, employeeId);

                        try {
                            //make sure the user does not already exist
                            if(!db.userExists(employeeId)){
                                //add the user
                                db.addUser(user);
                                System.out.println("The user was added");
                                System.out.println("");
                            }
                            else {
                                System.out.println("A user with that ID already exists");
                                System.out.println("");
                            }
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        } catch (NoSuchAlgorithmException e1) {
                            e1.printStackTrace();
                        } catch (UnsupportedEncodingException e1) {
                            e1.printStackTrace();
                        }

                    //if the user entered "remove employee" call the removeUser function
                    } else if (function.equals("remove employee")) {
                        System.out.println("Please enter Employee ID");
                        String employeeID = in.readLine();

                        if(!employeeID.equals("0001")){
                            db.removeUser(employeeID);
                            System.out.println("Employee " + employeeID + " was removed.");
                            System.out.println("");
                        } else {
                            System.out.println("You may not remove a manager");
                            System.out.println("");
                        }

                    //if the user entered "list all" call the printAllUsers function
                    }else if (function.equals("list all")) {
                        System.out.println("All Employees:");
                        db.printAllUsers();
                        System.out.println("");

                    //if the user entered "devices" switch over to the prompt for the device manager
                    }else if (function.equals("devices")) {
                        System.out.println("Welcome to the Device Manager Menu");
                        String devicefn = "";

                        //prompt the user for device commands until they want to exit
                        //then return to the employee menu
                        while (!devicefn.equals("exit")){
                            System.out.println("Enter \"add device\" and press enter to add a new device to the DB. " +
                                    "\n Enter \"remove device\" to remove a device. \n Enter \"list all\" to display " +
                                    "a list of all devices\n " +
                                    "Enter \"exit\" and press enter to return to the Employee Manager Menu.");
                            try {
                                devicefn = in.readLine();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }

                            //if the user enters "add device" try to get all information
                            //and then add the device to the db
                            if(devicefn.equals("add device")){
                                //try to get the serial number
                                String serialNum = null;
                                try {
                                    do{
                                        System.out.println("Please enter the serial number");
                                        serialNum = in.readLine();
                                    }while (deviceDB.deviceExists(serialNum));

                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }

                                //try to get the device type
                                String type = null;
                                while(type == null) {
                                    System.out.println("Please enter the device type: laptop, phone, desktop, AV");

                                    try {
                                        type = in.readLine();
                                        if(type.equals("phone")){
                                            type = "phone";
                                        }
                                        else if(type.equals("laptop")){
                                            type = "laptop";
                                        }
                                        else if(type.equals("phone")){
                                            type = "phone";
                                        }
                                        else if(type.equals("AV")){
                                            type = "AV";
                                        }
                                        else {
                                            type = null;
                                        }

                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                }

                                //try to get the os version
                                System.out.println("Please enter the OS version or na");
                                String os = "na";
                                try {
                                    os = in.readLine();
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }

                                //Decide if the device is going in or out
                                //if the device is going in then some fields don't need to be filed
                                String status = null;
                                do{
                                    System.out.println("Is the device going \"in\" or \"out\"?");
                                    try {
                                        status = in.readLine();
                                        if(status.equals("in")){
                                            status = "in";
                                            //create a device to add
                                            Device device = new Device(serialNum, type, os, "",
                                                    "", "", "", status );
                                            deviceDB.addDevice(device);

                                        }else if(status.equals("out")){
                                            status = "out";

                                            //try to get the employee email
                                            System.out.println("Please enter the email of the person getting the device");
                                            String remail = null;
                                            try {
                                                remail = in.readLine();
                                            } catch (IOException e1) {
                                                e1.printStackTrace();
                                            }

                                            //try to get the employee name
                                            System.out.println("Please enter the name of the person getting the device");
                                            String itname = null;
                                            try {
                                                itname = in.readLine();
                                            } catch (IOException e1) {
                                                e1.printStackTrace();
                                            }

                                            //create a device to add
                                            Device device = new Device(serialNum, type, os,
                                                    (LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).toString()),
                                                    "0001", remail, itname, status );

                                            deviceDB.addDevice(device);


                                        }else {
                                            System.out.println("That is not an option. Enter in or out.");
                                            status = null;
                                        }
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                }while(status == null);
                            }

                            //if the user entered "remove device" get the serial number and remove the device
                            else if(devicefn.equals("remove device")){
                                System.out.println("Please enter the serial number");
                                String serialNum = null;
                                try {
                                    serialNum = in.readLine();
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                                deviceDB.removeDevice(serialNum);
                            }

                            //if the user entered "list all" call the function to get the array list of device
                            //loop through the array list and print all the devices
                            else if(devicefn.equals("list all")) {
                                ArrayList<Device> devices;
                                devices = deviceDB.listDevices();

                                System.out.println();
                                for (Device d : devices) {
                                    System.out.println("SerialNum: " + d.serialNum + " | " +
                                            "Type: " + d.deviceType + " | " +
                                            "OS: " + d.osVersion + " | " +
                                            "Date Checked Out: " + d.dateOut + " | " +
                                            "Recipient Name: " + d.recipiantName + " | " +
                                            "Email of Recipient " + d.emailOfRecipiant + " | " +
                                            "IT Personal: " + d.itProfessional + " | "+
                                            "Device is: " + d.status);
                                }
                                System.out.println();
                            }

                            //if the user entered anything else tell them that was not an option
                            else {
                                if(!devicefn.equals("exit")){
                                    System.out.println("I'm sorry but I cannot understand that");
                                }

                            }
                        }

                    //if the user entered "exit" close program
                    }else if(function.equals("exit")) {
                        db.closeConnection();
                        System.out.println("Good By!");
                        System.exit(0);

                    //if the user entered something that was not an option nicely tell them we don't understand
                    } else {
                        System.out.println("I'm terribly sorry but I don't quite understand the request.");
                        System.out.println("");
                    }
                }
            }catch (Exception e){
            System.out.println("error");

        }
    }
}
