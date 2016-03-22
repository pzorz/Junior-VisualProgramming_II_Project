package UML;

/**
 * Author:          Peter Zorzonello
 * File:            User.java
 * Last Updated:    4/16/15
 * Version:         1.0
 *
 * This class represents a user. This class is used by other classes to
 * represent users so users can be verified, read from the database, and added
 * to the database.
 */

public class User {

    String name;
    String password;
    String user_ID;

    /**
     * default constructor
     */
    public User() {
    }

    /**
     * The constructor for a user
     *
     * @param name      the user's name
     * @param password  the user's password
     * @param user_ID   the user's unique ID
     */
    public User(String name, String password, String user_ID){
        this.name = name;
        this.password = password;
        this.user_ID = user_ID;
    }

    /**
     * Returns the User's name
     *
     * @return the user's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the user's name
     *
     * @param name the user's name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the user's password
     *
     * @return the user's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's password
     *
     * @param password the user's password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Get the users unique ID
     *
     * @return the user's ID
     */
    public String getUser_ID(){
        return user_ID;
    }

    /**
     * Assign the user ID
     * @param user_ID the user's ID
     */
    public void setUser_ID(String user_ID){
        this.user_ID = user_ID;
    }
}
