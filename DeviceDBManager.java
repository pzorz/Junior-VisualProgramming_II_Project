package UML;

import java.sql.*;
import java.util.ArrayList;

/**
 * Author:      Peter Zorzonello
 * File:        DeviceDBManager.java
 * Last Update: 4/23/15
 * Version:     2.0
 *
 * This class is responsible for the functionality of the DeviceDB.
 * This class contains the methods to add a device, remove a device,
 * update parts of the device, list all the devices, and show devices by categories.
 */
public class DeviceDBManager {

    //class code that sets the database name and type of database as well as the DB URL
    private final static String DB_NAME = "devices.db";
    private final static String JDBC = "jdbc:sqlite";
    private final static String DB_URL = JDBC + ":" + DB_NAME;

    private Connection dbConnection;

    /**
     * the constructor for this class
     */
    public DeviceDBManager() throws SQLException {

        // Try connecting to the database.
        try {
            dbConnection = DriverManager.getConnection(DB_URL);
        } catch(SQLException e) {
            System.err.println("Couldn't connect to the DB in DeviceDB.");
            throw e;
        }
    }

    /**
     * Creates the asset table if one does not already exist
     */
    public void createAssetTable() throws SQLException {

        Statement statement = dbConnection.createStatement();
        String createTableStatement = "create table if not exists assets("+
                "serialNum text, deviceType text, osVersion text, dateOut text, " +
                "itProfessional text, emailOfRecipiant text,recipiantName text, status text, primary key(serialNum))";

        try {
            statement.executeUpdate( createTableStatement );
        } catch(SQLException e){
            System.err.println("Trouble creating assets table "+
                    "DeviceDB.createAssetTable.");
            throw e;
        } finally {
            statement.close();
        }
    }


    /**
     * Adds an asset to the DB
     *
     * @param device a device object representing an asset of the company
     */
    public void addDevice(Device device) throws SQLException {

        String insertStatement = "insert into assets values(?,?,?,?,?,?,?,?)";
        PreparedStatement statement = dbConnection.prepareStatement(insertStatement);

        try {
            statement.setString(1, device.serialNum);
            statement.setString(2, device.deviceType);
            statement.setString(3, device.osVersion);
            statement.setString(4, device.dateOut);
            statement.setString(5, device.itProfessional);
            statement.setString(6, device.emailOfRecipiant);
            statement.setString(7, device.recipiantName);
            statement.setString(8, device.status);


            statement.executeUpdate();

        } catch(SQLException e){

            System.err.println("Trouble inserting device into table in "+
                    "DeviceDBManager.addDevice; Device: "+ device);
            throw e;
        } finally {
            statement.close();
        }
    }

    /**
     * Removes the asset from the database
     *
     * @param serialNum the serial number of a device/asset
     */
    public void removeDevice(String serialNum) throws SQLException {

        String removeStatement = "DELETE FROM assets WHERE serialNum=?";
        PreparedStatement statement = dbConnection.prepareStatement(removeStatement);

        try{

            statement.setString(1, serialNum);
            statement.executeUpdate();

        }catch(SQLException e){

            System.err.println("Trouble removing device from table in "+
                    "DeviceDBManager.removeDevice: " + serialNum);
            throw e;
        }finally {
            statement.close();
        }

    }

    /**
     * This function returns an array list of devices representing all the devices in the
     * Device DB.
     *
     * @return an array list of devices
     * @throws SQLException
     */
    public ArrayList<Device> listDevices() throws SQLException {
        ArrayList<Device> devices = new ArrayList<Device>();

        //query the DB for all of the users and print out names and ID's
        String query = "select * from assets";
        PreparedStatement statement = dbConnection.prepareStatement(query);

        ResultSet results;

        try {
            results = statement.executeQuery();

            while (results.next()){

                Device device = new Device(
                        results.getString("serialNum"),
                        results.getString("deviceType"),
                        results.getString("osVersion"),
                        results.getString("dateOut"),
                        results.getString("itProfessional"),
                        results.getString("emailOfRecipiant"),
                        results.getString("recipiantName"),
                        results.getString("status")
                        );
                devices.add(device);
            }
            return devices;

        }catch (Exception e){
            System.err.println("Error " + e);
        }

        return devices;
    }

    /**
     * A function to make sure devices do not get entered twice
     *
     * @param serialNum the serial number of the device
     * @return  True if the device has already been entered
     * false if not.
     */
    public boolean deviceExists(String serialNum) throws SQLException {
        String query = "select * from assets where serialNum = ?";
        PreparedStatement statement = dbConnection.prepareStatement(query);

        ResultSet results;

        try{
            //query the database for the serialNum
            statement.setString(1, serialNum);
            results = statement.executeQuery();
            try{
                results.next();
                if(results.isFirst()){
                    return true;
                } else {
                    return false;
                }

            }catch (SQLException e){
                throw e;
            }finally {
                results.close();
            }
        }catch (SQLException e){
            System.err.println("Trouble finding user from table in " +
                    "UserDBManager.userExists: " + serialNum);
            throw e;

        }finally {
            statement.close();
        }

    }

    /**
     * Lists all devices that have the value val in the column col.
     * Used to: see all devices checked out, all phone device, all devices checked out by Joe, etc.
     * @param col the column to check in
     * @param val the value to look for
     * @return devices an array list of devices that represents all devices with the value val in the column col
     */
    public ArrayList<Device> listDeviceByColVal(String col, String val) throws SQLException {
        ArrayList<Device> devices = new ArrayList<Device>();
        String query = null;
        if (col.equals("Device Type")){
           query = "select * from assets where deviceType=?";
        }
        else if(col.equals("OS")){
            query = "select * from assets where osVersion=?";
        }
        else if(col.equals("Status")){
            query = "select * from assets where status=?";
        }
        else if(col.equals("Employee")){
            query = "select * from assets where recipiantName=?";
        }

        PreparedStatement statement = dbConnection.prepareStatement(query);

        ResultSet results;
        try {
            statement.setString(1, val);

            results = statement.executeQuery();
            while (results.next()){

                Device device = new Device(
                        results.getString("serialNum"),
                        results.getString("deviceType"),
                        results.getString("osVersion"),
                        results.getString("dateOut"),
                        results.getString("itProfessional"),
                        results.getString("emailOfRecipiant"),
                        results.getString("recipiantName"),
                        results.getString("status")
                );
                devices.add(device);
            }
            return devices;


        }catch (Exception e){
            System.err.println();

        }
        return devices;
    }

    /**
     * When a device is being checked out or updated it should be edited to reflect the
     * new status of the device. This function allows IT personal to change properties of the device
     *
     * @param device the serial number of the device being updated
     */
    public void editDevice(Device device) throws SQLException {

        String updateStatement = "UPDATE assets SET deviceType=?, osVersion=?, dateOut=?, itProfessional=?, " +
                "emailOfRecipiant=?, recipiantName=?, status=? WHERE serialNum=?";
        PreparedStatement statement = dbConnection.prepareStatement(updateStatement);

        try{

            statement.setString(1, device.deviceType);
            statement.setString(2, device.osVersion);
            statement.setString(3, device.dateOut);
            statement.setString(4, device.itProfessional);
            statement.setString(5, device.emailOfRecipiant);
            statement.setString(6, device.recipiantName);
            statement.setString(7, device.status);
            statement.setString(8, device.serialNum);

            statement.executeUpdate();

        }catch(SQLException e){
            System.err.println("Trouble updating device from table in "+
                    "DeviceDBManager.editDevice: " );
            throw e;

        }

    }

    /**
     * New Function added in Version 2.0 to work better with the GUI.
     * This function gets a specific device based off the unique serial number.
     * It is used with the GUI to fill in information needed to change a device but not
     * supplied by the user.
     *
     * @param serialNum the serial number for the device
     * @return  a Device object representing the device with all the device information
     * @throws SQLException
     */
    public Device getDevice(String serialNum) throws SQLException {
        String query = "select * from assets where serialNum=?";
        PreparedStatement statement = dbConnection.prepareStatement(query);

        ResultSet results;

        try{
            statement.setString(1, serialNum);

            results = statement.executeQuery();

            while (results.next()){
                Device d = new Device(
                        results.getString("serialNum"),
                        results.getString("deviceType"),
                        results.getString("osVersion"),
                        results.getString("dateOut"),
                        results.getString("itProfessional"),
                        results.getString("emailOfRecipiant"),
                        results.getString("recipiantName"),
                        results.getString("status")
                );
                return d;
            }


        }catch (Exception e){
            System.err.println(e);
        }

        return null;
    }
}
