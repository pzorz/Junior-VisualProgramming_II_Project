package UML;

/**
 * Author:          Peter Zorzonello
 * File:            Device.java
 * Last Updated:    4/23/15
 * Version:         2.0
 *
 * This class represents a standard device.
 * not all of the fields for ech device will be needed.
 */
public class Device {

    String serialNum;
    String deviceType;
    String osVersion;
    String dateOut;
    String emailOfRecipiant;
    String recipiantName;
    String itProfessional;
    String status;


    /**
     * Constructor
     *
     * @param serialNum
     * @param deviceType
     * @param osVersion
     * @param dateOut
     * @param itProfessional
     * @param emailOfRecipiant
     * @param recipiantName
     * @param status
     */
    public Device(String serialNum, String deviceType, String osVersion, String dateOut,
                  String itProfessional, String emailOfRecipiant, String recipiantName, String status) {
        this.serialNum = serialNum;
        this.deviceType = deviceType;
        this.osVersion = osVersion;
        this.dateOut = dateOut;
        this.itProfessional = itProfessional;
        this.emailOfRecipiant = emailOfRecipiant;
        this.recipiantName = recipiantName;
        this.status = status;
    }



    /**
     * Default constructor
     */
    public Device() {
    }

    /**
     * Return's  the asset's serial number
     *
     * @return the Serial number of the device
     */
    public String getSerialNum() {
        return serialNum;
    }

    /**
     * Sets the asset's serial number
     * @param serialNum the serial number
     */
    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    /**
     * Returns the asset type
     * @return the type of the asset
     */
    public String getDeviceType() {
        return deviceType;
    }

    /**
     * sets the asset type
     *
     * @param deviceType the type of the device
     */
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    /**
     * returns the devices os version
     * @return the software version
     */
    public String getOsVersion() {
        return osVersion;
    }

    /**
     * sets the asset's OS version
     * @param osVersion the software version
     */
    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    /**
     * returns the email of the person who has the asset
     * @return an email address
     */
    public String getEmailOfRecipiant() {
        return emailOfRecipiant;
    }

    /**
     * Sets the email address of the person who has the asset
     *
     * @param emailOfRecipiant the email address of the person with the asset
     */
    public void setEmailOfRecipiant(String emailOfRecipiant) {
        this.emailOfRecipiant = emailOfRecipiant;
    }



    /**
     * Returns the name of the IT person who gave the device out
     * @return the name of a person
     */
    public String getItProfessional() {
        return itProfessional;
    }

    /**
     * Sets the name of the IT person who gave the device out
     *
     * @param itProfessional the name of a person
     */
    public void setItProfessional(String itProfessional) {
        this.itProfessional = itProfessional;
    }

    /**
     * Returns the name of the person with the asset
     *
     * @return name of a person
     */
    public String getRecipiantName() {
        return recipiantName;
    }

    /**
     * Sets the name of the person with the asset
     * @param recipiantName name of a person
     */
    public void setRecipiantName(String recipiantName) {
        this.recipiantName = recipiantName;
    }

    /**
     * Returns the date the device was signed out
     * @return dateOut the date string representing a date
     */
    public String getDateOut() {
        return dateOut;
    }

    /**
     * Sets the date the device was taken out
     *
     * @param dateOut the string representation of a date
     */
    public void setDateOut(String dateOut) {
        this.dateOut = dateOut;
    }

    /**
     * Returns the status the device is in. Is it checked in or out
     *
     * @return status the status of the device in or out
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the device
     *
     * @param status the status of the devices either in or out
     */
    public void setStatus(String status) {
        this.status = status;
    }

}
