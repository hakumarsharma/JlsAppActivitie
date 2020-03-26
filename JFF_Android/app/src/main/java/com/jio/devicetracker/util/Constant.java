/*************************************************************
 *
 * Reliance Digital Platform & Product Services Ltd.

 * CONFIDENTIAL
 * __________________
 *
 *  Copyright (C) 2020 Reliance Digital Platform & Product Services Ltd.–
 *
 *  ALL RIGHTS RESERVED.
 *
 * NOTICE:  All information including computer software along with source code and associated *documentation contained herein is, and
 * remains the property of Reliance Digital Platform & Product Services Ltd..  The
 * intellectual and technical concepts contained herein are
 * proprietary to Reliance Digital Platform & Product Services Ltd. and are protected by
 * copyright law or as trade secret under confidentiality obligations.

 * Dissemination, storage, transmission or reproduction of this information
 * in any part or full is strictly forbidden unless prior written
 * permission along with agreement for any usage right is obtained from Reliance Digital Platform & *Product Services Ltd.
 **************************************************************/

package com.jio.devicetracker.util;

/**
 * Implementation of Constant class to maintain the constant of application .
 */

@SuppressWarnings("PMD.ClassNamingConventions")
public class Constant {

    public static String REG_ID = "n7zz1h465305";
    public static String REG_KEY = "089640418g7f";
    public static String REG_SVC = "1329142c2120";
    public static String App_Name = "EDC";
    public static String AES_SECRET_KEY = "PBKDF2WithHmacSHA512";
    public static String CIPHER_KEY = "AES/CBC/PKCS5Padding";

    public static int INVALID_USER = 401;
    public static int ACCOUNT_LOCK = 403;

    // MQTT connection details
    public static final String MQTT_SIT_URL = "tcp://sit.tnt.cats.jvts.net:1883";
    public static final String MQTT_SIT_TOPIC = "jioiot/svcd/tracker/" + Util.imeiNumber + "/uc/fwd/locinfo";
    public static final String MQTT_STG_URL = "tcp://bocats.tnt.jiophone.net:1883";
    public static final String MQTT_STG_TOPIC = "jioiot/svcd/jiophone/" + Util.imeiNumber + "/uc/fwd/locinfo";
    public static final String MQTT_USER_NAME = "trackNT";
    public static final String MQTT_PASSWORD = "trackNT";
    public static final String MQTT_CIT_URL = "tcp://v.dev.tnt.cats.jvts.net:1883";
    public static final String MQTT_CIT_TOPIC = "jioiot/svcd/jiophone/" + Util.imeiNumber + "/uc/fwd/locinfo";
    public static final String MQTT_CIT_USER_NAME = "borqs-sit";
    public static final String MQTT_CIT_PASSWORD = "borqs-sit@987";
    public static final int MQTT_TIME_INTERVAL = 10;

    //  Common Text
    public static final String ALERT_TITLE = "Alert";
    public static final String WAIT_LOADER = "Please wait...";
    public static final String JIO = "jio";
    public static final String NAME = "name";
    public static final String FLAG = "flag";
    public static final String SUPERVISOR = "supervisor";
    public static final String TITLE = "Title...";


    // Registration Activity constants
    public static final String REGISTRATION_TITLE = "Registration";
    public static final String REGISTRATION = "registration";
    public static final String NAME_VALIDATION = "Please enter your name";
    public static final String PHONE_VALIDATION = "Phone number cannot be left empty!";
    public static final String MOBILE_NETWORKCHECK = "Please use your mobile data";
    public static final String JIO_NUMBER = "Please use Jio number";
    public static final String SIM_VALIDATION = "Jio number should be in SIM slot 1";
    public static final String NUMBER_VALIDATION = "Entered phone number should be in SIM slot 1";
    public static final String DEVICE_JIONUMBER = "Enter the valid jio number present in device";
    public static final String REGISTRAION_ALERT_409 = "User is already registered";
    public static final String REGISTRAION_FAILED = "Register failed ,Please contact your admin";


    // Dashboard Activity constants
    public static final String DASHBOARD_TITLE = "Home";
    public static final String DELETC_DEVICE = "Do you want to delete ?";
    public static final String CHOOSE_DEVICE = "Please select the number for tracking";
    public static final String CONSENT_NOTAPPROVED = "Consent is not apporoved for phone number ";
    public static final String CONSENT_APPROVED = "Consent status is already approved for this number";
    public static final String FMS_SERVERISSUE = "FMS server is down please call back after some time";
    public static final String TOKEN_NULL = "Token is null";
    public static final String LOADING_DATA = "Please wait loading data...";

    // Login Activity constants
    public static final String LOGIN_TITLE = "People Tracker";
    public static final String EMAILID_VALIDATION = "Email id cannot be left blank.";
    public static final String PASSWORD_VALIDATION = "Password cannot be left blank.";
    public static final String EMAIL_VALIDATION = "Please provide the correct Email Id!";
    public static final String LOGIN_VALIDATION = "Please enter correct email and password";
    public static final String EMAIL_LOCKED = "Account is locked";
    public static final String VALID_USER = "Please enter valid user";
    public static final String PASSWORD_VALIDATION2 = "Password must have a minimum of 8 characters and a maximum of 16 characters. Also, must contain atleast one lowercase alphabet, one uppercase alphabet, one numeric and one special character";
    public static final String YESJFF_SMS = "Please click on below link to know the consent response https://www.jff.com/YesJFF?data=";
    public static final String NOJFF_SMS = "Please click on below link to know the consent response https://www.jff.com/NoJFF?data=";


    // New Device Activity Constants
    public static final String ADD_DEVICE_TITLE = "Add";
    public static final String IMEI_VALIDATION = "Enter the 15 digit IMEI number";
    public static final String MOBILENUMBER_VALIDATION = "Enter the valid mobile number";
    public static final String REGMOBILENUMBER_VALIDATION = "You can't add registered mobile number";
    public static final String CHECK_DETAILS = "Please Enter the details";
    public static final String DUPLICATE_NUMBER = "This number is already added";
    public static final String LOGOUT_CONFIRMATION_MESSAGE = "Are you sure you want to logout ?";
    public static final String IMEI = "imei";
    public static final String PROGRESSBAR_MSG = "Please wait adding device";

    // MAPs Activity constants
    public static final String MAP_TITLE = "      Location";
    public static final String LOCATION_UPDATE = "Location will be updated after every";
    public static final String SUCCESSFULL_DEVICE_ADDITION_RESPONSE = "1 device(s) are assigned to one user.";
    public static final String SUCCESSFULL_DEVICE_ADDITION = "Device is successfully added";
    public static final String UNSUCCESSFULL_DEVICE_ADDITION = "Device can not be added, please try again later";
    public static final long FREQUENCY_FOR_LOCATION_UPDATE = 10;
    public static final long PRIORITY_BALANCED_POWER_ACCURACY = 10;
    public static final int EPOCH_TIME_DURATION = 15;
    // Forgot Activity
    public static final String FORGOT_TITLE = "Forgot password";
    public static final String RESET_TITLE = "Reset password";
    public static final String FORGOT_TOKEN_MSG = "Token is sent to entered email";
    public static final String FORGOT_TOKEN_FAIL_MSG = "Token api failed";
    public static final String FORGOT_EMAIL = "Email";
    public static final String CONTACT_DEVICE_TITLE = "Contact Detail";


    //Rest API URL
    public static final String LOGIN_URL = "/accounts/api/users/login?isResponseDataAsUser=true&isPopulateGroup=true&isPopulateGroupUsers=true&isPopulateUserDevices=true&isPopulateUserDevicesAsWearableUsers=true";
    public static final String REGISTRATION_TOKEN_URL = "/accounts/api/users/tokens";
    public static final String REGISTRATION_URL = "/accounts/api/users/tokens/verify";
    public static final String FORGOT_PASS_URL = "/accounts/api/users/resetpassword";
    public static final String FORGOTPASS_TOKEN_URL = "/ugs/api/user/forgotpassword";
    public static final String ADDDEVICE_URL_1 = "/accounts/api/users/";
    public static final String ADDDEVICE_URL_2 = "/devices/verifyandassign?ugs_token=";
    public static final String SEARCH_DEVICE_URL = "/accounts/api/devices/v2/search?skip=0&limit=10&ugs_token=";
    public static final String SEARCH_DEVICE_STATUS = "/accounts/api/devicestatuses/search?ugs_token=";
    public static final String SEARCH_EVENT_REQUEST = "/accounts/api/events/search?skip=0&limit=100&ugs_token=";
    public static final String TRACK_DEVICE_REQUEST = "/accounts/api/devicestatuses/search?skip=0&limit=20&tsp=1572443375692&ugs_token=";

    //Privacy Policy

    public static final String TERM_AND_CONDITION_ALERT = "Please accept terms and conditions to proceed with the application";
    public static final String TERM_AND_CONDITION_STATUS_MSG = "Please accept terms and conditions and login then again click on the link which is given in message";
    public static final String NOT_REGISTERED_MSG = "User is not registered";
    public static final String INVALID_TOKEN_MSG = "Invalid token please try again";
    public static final String PASSWORD_RESET_SUCCESS_MSG = "Password reset is successfull";
    public static final String VALID_EMAIL_ID = "Please enter the valid email id";
    public static final String ENTER_TOKEN = "Please enter the token which is sent to your email id";
    public static final String PASSWORD_EMPTY = "Password cannot be left blank.";
    public static final String PASSWORD_NOT_MATCHED = "Password did not match, please try again";
    public static final String RESET_PASSWORD_FAILED = "Reset password is failed";
    public static final String NAME_EMPTY = "Name cannot be left blank.";
    public static final String EMAIL_EMPTY = "Email cannot be left blank.";
    public static final String MOBILE_NUMBER_EMPTY = "Mobile number cannot be left blank.";
    public static final String VALID_PHONE_NUMBER = "Please enter valid phone number";
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    public static final String SIT_URL = "https://sit.boapi.cats.jvts.net";
    public static final String STG_URL = "https://stg.borqs.io";
    public static final String FMS_BASE_URL = "http://49.40.22.92:8080";
    public static final String CONSENT_APPROVED_STATUS = "Consent Approved";
    public static final String CONSENT_PENDING = "Consent Pending";
    public static final String REQUEST_CONSENT = "Request Consent";
    public static final String NUMBER_CARRIER = "number";
    public static final String CONSENT_STATUS_MSG = "Yes JioTracker";
    public static final String CONSENT_MSG_SENT = "Consent sent";
    public static final String CONSENT_MSG_TO_TRACKEE = " wants to track your location. Click below link to reply. https://www.jff.com/home?data=";
    public static final String START_TRACKING = "To start tracking ";
    public static final String REQUEST_CONSENT_USER = " please request for consent from the user by clicking “Request consent”";
    public static final String DIALOG_TITLE = "Title...";
    public static final String NO_JIO_TRACKER = "No Jiotracker";
    public static final String ACCESS_COARSE_PERMISSION_ALERT = "Location permission is not granted, please grant it first";
    public static final String IMEI_PERMISSION_NOT_GRANTED = "IMEI permission is not granted!";
    public static final String TERM_CONDITION_FLAG = "TermFlag";
    public static String AUTO_LOGIN = "Autologin";
    public static final String AUTO_LOGIN_STATUS = "AutoLoginStatus";
    public static final String LOCATION_FLAG_STATUS = "LocationFlagStatus";
    public static final String CONSENT_NOT_SENT = "Consent not sent";
    public static final String BORQS_OTP_TITLE = "OTP Verification";
    public static final String EMPTY_EMAIL_OTP = "Please enter the email otp";
    public static final String TOKEN_VERIFIED = "Token successfully verified.";
    public static final String TOKEN_VERIFICATION_FAILED = "Verification failed!";
    public static final String REGISTARTION_SUCCESS_MESSAGE = "Registration successfull!";
    public static final String REGISTARTION_FAILED_MESSAGE = "Registration is failed";
    public static final String EDIT = "Edit";
    public static final String GROUP_NAME = "Group Name";
    public static final String ACTIVE_SESSION_TITLE = "Active Session";
    public static final String TRACKER_TITLE = "Trackers";
    public static final String GROUP_TITLE = "Group List";
    public static final String CREATE_GROUP_LIST = "Track";
    public static final String CANT_ADD_REG_MOB_NUM = "You can't add registered mobile number";
    public static final String TEN_SECONDS = "10 Seconds";
    public static final String ONE_MINUTE = "1 Minute";
    public static final String TEN_MINUTES = "10 Minutes";
    public static final String FIFTEEN_MINUTES = "15 Minutes";
    public static final String ONE_HOUR = "1 Hour";
    public static final String REFRESH_INTERVAL_SETTING = "Settings";
    public static final String MAP_UPDATION_MSG = "Map will be updated after every ";
    public static final String PENDING = "Pending";
    public static final String GROUP_LIMITATION = "You can't create more than two groups, please add it as a individual trackee";
    public static final String USER_LIMITATION = "You can't add more than ten individual users";
    public static final String GROUP_NAME_VALIDATION_ERROR = "Group name cannot be left empty";
    public static final String RELATION_WITH_GROUP_ERROR = "Please enter the relation with group member, it cannot be left empty";
    // Profile Activity
    public static final String PROFILE_TITLE = "Profile";

    //Status code
    public static final int STATUS_CODE_409 = 409;
    public static final int STATUS_CODE_404 = 404;
    public static final int STATUS_CODE_417 = 417;
    public static final int SUCCESS_CODE_200 = 200;


    // Track device Activity
    public static final String PHONENUMBER_VALIDATION = "Phone Number cannot be left empty.";
    public static final String USER_TOKEN = "UserToken";
    //Time in min.
    public static final String MIN_15 = "15 min";
    public static final String MIN_25 = "25 min";
    public static final String MIN_30 = "30 min";
    public static final String MIN_40 = "40 min";
    public static final String NOTIFICATION_CHANNEL_ID = "channel";
    public static final int NOTIFICATION__ID = 1;
}
