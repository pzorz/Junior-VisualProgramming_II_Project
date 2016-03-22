package UML;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;

/**
 * Author:          Peter Zorzonello
 * File:            UserDBManager.java
 * Contributors:    https://www.owasp.org/index.php/Hashing_Java and Class Code
 * Last Updated:    4/16/15
 * Version:         1.0
 *
 * This class is responsible for accessing the Users database. It
 * Connects to the database, adds users, removes users, verifies users
 * exist and verifies the password that the person logging-in gave is correct.
 */
public class UserDBManager {

    //class code that sets the database name and type of database as well as the DB URL
    private final static String DB_NAME = "users.db";
    private final static String JDBC = "jdbc:sqlite";
    private final static String DB_URL = JDBC + ":" + DB_NAME;

    private Connection dbConnection;

    //not my code
    private final static int ITERATION_NUMBER = 1000;



    /*
     * This function creates the connection to the database
     * This is code we used in class.
     *
     * @throws SQLException
     */
    public UserDBManager() throws SQLException {
        // Try connecting to the database.
        try {
            dbConnection = DriverManager.getConnection(DB_URL);
        } catch(SQLException e) {
            System.err.println("Couldn't connect to the DB in UsersDB.");
            throw e;
        }
    }

    /*
     * This function closes the connection to the the DB.
     * This is code we used in class.
     *
     * @throws SQLException
     */
    public void closeConnection() throws SQLException {
        try {
            dbConnection.close();
        } catch(SQLException e ){
            System.err.println("Couldn't close connection to DB in UsersDB.");
            throw e;
        }
    };

    /**
     * Creates the users table in the database if it doesn't already exist.
     * This is code we used in class.
     *
     * @throws SQLException
     */
    public void createUsersTable() throws SQLException {
        Statement statement = dbConnection.createStatement();
        String createTableStatement = "create table if not exists users("+
                "name text, password_hash text, salt text, user_ID text , primary key(user_ID))";

        try {
            statement.executeUpdate( createTableStatement );
        } catch(SQLException e){
            System.err.println("Trouble creating users table "+
                    "UsersDB.createUsersTable.");
            throw e;
        } finally {
            statement.close();
        }
    }

    /**
     * This function adds a user to the DB.
     * This function also hashes the password given before storing it.
     * This is a combination of class code and
     * code from: https://www.owasp.org/index.php/Hashing_Java
     *
     * @param user  An instance of the User class representing the user to add to the DB
     * @throws SQLException
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    void addUser(User user) throws SQLException, NoSuchAlgorithmException, UnsupportedEncodingException {
        String insertStatement = "insert into users values(?,?,?,?)";
        PreparedStatement statement = dbConnection.prepareStatement(insertStatement);

        try {
            statement.setString(1, user.name);

            //code from: https://www.owasp.org/index.php/Hashing_Java
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            // Salt generation 64 bits long
            byte[] bSalt = new byte[8];
            random.nextBytes(bSalt);
            // Digest computation
            byte[] bDigest = getHash(ITERATION_NUMBER,user.password,bSalt);
            String hashed_password = byteToBase64(bDigest);
            String salt = byteToBase64(bSalt);

            //my code
            statement.setString(2, hashed_password);
            //assign the salt
            statement.setString(3, salt);
            statement.setString(4, user.user_ID);
            statement.executeUpdate();

        } catch(SQLException e){

            System.err.println("Trouble inserting user into table in "+
                    "UserDBManager.addUser; User: "+ user);
            throw e;
        } finally {

            statement.close();

        }
    }

    /**
     * This function is responsible for removing a user form the DB.
     * Before the user is removed, the program must check to see if the user exists.
     *
     * @param user_ID   Unique user ID representing the user to remove
     * @throws SQLException
     */
    void removeUser(String user_ID) throws SQLException{

        String removeStatement = "DELETE FROM users WHERE user_ID=?";
        PreparedStatement statement = dbConnection.prepareStatement(removeStatement);

        try{

            statement.setString(1, user_ID);
            statement.executeUpdate();

        }catch(SQLException e){

            System.err.println("Trouble removing user from table in "+
                    "UserDBManager.removeUser: " + user_ID);
            throw e;
        }finally {
            statement.close();
        }

    }

    /**
     * This function checks to see if a user is in the DB.
     *
     * @param user_ID   Unique user id representing the user
     * @return  a boolean value, true if the user exists, false if not
     * @throws SQLException
     */
    boolean userExists(String user_ID) throws SQLException {

        //User user = null;
        String query = "select * from users where user_ID = ?";
        PreparedStatement statement = dbConnection.prepareStatement(query);

        ResultSet results;

        try{
            //query the database for the user_ID
            statement.setString(1, user_ID);
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
                    "UserDBManager.userExists: " + user_ID);
            throw e;

        }finally {
            statement.close();
        }
    }

    /**
     * This function is responsible for authenticating users trying to login.
     * Before the function checks if the password matching the ID it first makes sure
     * the user is in the DB. Then it hashes the text password and compares the hashes.
     *
     * @param user_ID   Unique user ID used to represent the user
     * @param password  User's clear text password
     * @return  A boolean value. True if the passwords match, false if not.
     */
    public boolean authUser(String user_ID, String password) throws SQLException {

        String passHash;
        String salt;
        try {
            //check if the user exits
            if(userExists(user_ID)){

                String query = "select * from users where user_ID = ?";
                PreparedStatement statement = dbConnection.prepareStatement(query);

                ResultSet results;

                try {
                    statement.setString(1, user_ID);
                    results = statement.executeQuery();
                    try{
                        while(results.next()) {

                            passHash=results.getString("password_hash");
                            salt = results.getString("salt");


                            byte[] bSalt = base64ToByte(salt);
                            byte[] bDigest = getHash(ITERATION_NUMBER,password,bSalt);
                            String hashed_password = byteToBase64(bDigest);


                            if(hashed_password.equals(passHash)){
                                return true;
                            }
                        }
                    }catch (Exception e){

                    }

                }catch (Exception e){
                    System.err.println(e);
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //else return false
        return false;
    }

    /**
     * This function is responsible for hashing the password
     * This code came from: https://www.owasp.org/index.php/Hashing_Java
     *
     * @param iterationNb
     * @param password  the clear text password
     * @param salt  the password salt
     * @return  a byte array representing the hashed password
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public byte[] getHash(int iterationNb, String password, byte[] salt) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        digest.reset();
        digest.update(salt);
        byte[] input = digest.digest(password.getBytes("UTF-8"));
        for (int i = 0; i < iterationNb; i++) {
            digest.reset();
            input = digest.digest(input);
        }
        return input;
    }

    /**
     * Converts the String representation of the password into the byte array format
     * This code came from: https://www.owasp.org/index.php/Hashing_Java
     *
     * @param data the String representation of the password hash
     * @return the byte array of the password hash
     * @throws IOException
     */
    public static byte[] base64ToByte(String data) throws IOException {
        BASE64Decoder decoder = new BASE64Decoder();
        return decoder.decodeBuffer(data);
    }

    /**
     * Returns the password hash as a String
     * This code came from: https://www.owasp.org/index.php/Hashing_Java
     *
     * @param data byte[] the password hash in byte array form
     * @return String   the String representation of the hashed password
     */
    public static String byteToBase64(byte[] data){
        BASE64Encoder endecoder = new BASE64Encoder();
        return endecoder.encode(data);
    }

    /**
     * This function will print out every user in the DB
     * This is good if the manager needs to look and see who's in the DB
     */
    public void printAllUsers() throws SQLException {
        ArrayList<User> users = new ArrayList<User>();

        //query the DB for all of the users and print out names and ID's
        String query = "select * from users";
        PreparedStatement statement = dbConnection.prepareStatement(query);

        ResultSet results;

        try {
            results = statement.executeQuery();

            while (results.next()){
                User user = new User(
                        results.getString("name"),
                        results.getString("password_hash"),
                        results.getString("user_ID"));
                users.add(user);
            }
            //print all the users except manager
            for(User u : users) {
                if(!u.getName().equals("Manager"))
                    System.out.println("Name: " + u.getName() + " Employee ID:" + u.getUser_ID());
            }

        }catch (Exception e){
            System.err.println("Error " + e);
        }


    }
}