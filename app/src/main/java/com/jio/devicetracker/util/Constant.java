package com.jio.devicetracker.util;

/**
 * Implementation of Constant class to maintain the constant of application .
 */
public class Constant {

    // TODO move IDAM details to properties file
    public static String REG_ID = "n7zz1h465305";
    public static String REG_KEY = "089640418g7f";
    public static String REG_SVC = "1329142c2120";
    public static String App_Name = "EDC";

    public static int INVALID_USER = 401;
    public static int ACCOUNT_LOCK = 403;

    // MQTT connection details
    public static final String MQTT_URL = "tcp://sit.tnt.cats.jvts.net:1883";
    public static final String MQTT_USER_NAME = "trackNT";
    public static final String MQTT_PASSWORD = "trackNT";

    //  Common Text

    public static final String ALERT_TITLE = "Alert";
    public static final String WAIT_LOADER = "Please wait...";

    // Registration Activity constants
    public static final String REGISTRATION_TITLE = "Registration";
    public static final String NAME_VALIDATION = "Name can't left empty";
    public static final String PHONE_VALIDATION = "Phone number cannot be left empty!";
    public static final String MOBILE_NETWORKCHECK = "Please use your mobile data";
    public static final String JIO_NUMBER = "Please use Jio number";
    public static final String SIM_VALIDATION = "Jio number should be in SIM slot 1";
    public static final String NUMBER_VALIDATION = "Entered phone number should be in SIM slot 1";
    public static final String DEVICE_JIONUMBER ="Enter the valid jio number present in device";

    // Dashboard Activity constants
    public static final String DASHBOARD_TITLE ="Home";
    public static final String DELETC_DEVICE = "Do you want to delete ?";
    public static final String CHOOSE_DEVICE = "Please select the number for tracking";
    public static final String CONSENT_NOTAPPROVED = "Consent is not apporoved for phone number ";
    public static final String CONSENT_APPROVED = "Consent status is already approved for this number";
    public static final String FMS_SERVERISSUE = "FMS server is down please call back after some time";
    public static final String TOKEN_NULL = "Token is null";
    public static final String LOADING_DATA = "Please wait loading data...";

    // Login Activity constants
    public static final String EMAILID_VALIDATION = "Email id cannot be left blank.";
    public static final String PASSWORD_VALIDATION = "Password cannot be left blank.";
    public static final String EMAIL_VALIDATION ="Please provide the correct Email Id!";
    public static final String LOGIN_VALIDATION = "Please enter correct email and password";
    public static final String EMAIL_LOCKED = "Account is locked";
    public static final String VALID_USER = "Please enter valid user";
    public static final String PASSWORD_VALIDATION2 = "Password must have a minimum of 8 characters and a maximum of 16 characters. Also, must contain atleast one lowercase alphabet, one uppercase alphabet, one numeric and one special character";

    // New Device Activity Constants
    public static final String IMEI_VALIDATION = "Enter the 15 digit IMEI number";
    public static final String MOBILENUMBER_VALIDATION = "Enter the valid mobile number";
    public static final String REGMOBILENUMBER_VALIDATION = "You can't add registered mobile number";
    public static final String CHECK_DETAILS ="Please Enter the details";
    public static final String DUPLICATE_NUMBER ="This number is already added";
    public static final String LOGOUT_CONFIRMATION_MESSAGE = "Are you sure you want to logout ?";

    // MAPs Activity constants
    public static final String LOCATION_UPDATE = "Location will be updated after every";

}
