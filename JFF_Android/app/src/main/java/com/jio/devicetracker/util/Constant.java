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

    public static String AES_SECRET_KEY = "PBKDF2WithHmacSHA512";
    public static String CIPHER_KEY = "AES/CBC/PKCS5Padding";
    public static final String GOOGLE_RECAPCHA_KEY = "6Lf5VQAVAAAAAEoiNLih9hYqm-SPZ3Di8QZyFqbC";


    public static int INVALID_USER = 401;
    public static int ACCOUNT_LOCK = 403;

    // MQTT connection details
    public static final String MQTT_SIT_URL = "tcp://sit.tnt.cats.jvts.net:1883";
    public static final String MQTT_SIT_TOPIC = "jioiot/svcd/tracker/" + Util.imeiNumber + "/uc/fwd/locinfo";
    public static final String MQTT_STG_URL = "tcp://bocats.tnt.jiophone.net:1883";
    public static final String MQTT_STG_TOPIC = "jioiot/svcd/jiophone/" + Util.imeiNumber + "/uc/fwd/locinfo";
    public static final String MQTT_USER_NAME = "trackNT";
    public static final String MQTT_PASSWORD = "trackNT";
    public static final String MQTT_CIT_URL = "tcp://sit.tnt.cats.jvts.net:1883";
    public static final String MQTT_CIT_TOPIC = "tracker/svcd/jiophone/" + Util.imeiNumber + "/uc/fwd/locinfo";
    public static final String MQTT_CIT_USER_NAME = "borqs-sit";
    public static final String MQTT_CIT_PASSWORD = "borqs-sit@987";
    public static final int MQTT_TIME_INTERVAL = 10;

    //  Common Text
    public static final String ALERT_TITLE = "Alert";
    public static final String WAIT_LOADER = "Please wait...";
    public static final String JIO = "jio";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String FLAG = "flag";
    public static final String SUPERVISOR = "supervisor";
    public static final String TITLE = "Title...";
    public static final String EMPTY_STRING = "";
    public static final String GOOGLE_RECAPTCHA_ERROR = "Captcha error";
    public static final String DRAWABLE = "drawable";


    // Registration Activity constants
    public static final String REGISTRATION_TITLE = "Registration";
    public static final String REGISTRATION = "registration";
    public static final String NAME_VALIDATION = "Please enter your name";
    public static final String INVALID_OTP = "Invalid OTP Provided";
    public static final String OTP_SENT = "OTP sent.";
    public static final String SMS_SEND_FAILED = "SMS failed, please try again.";
    public static final String OTP_SMS = "Hi, please use this OTP :";
    public static final String PHONE_VALIDATION = "Phone number cannot be left empty!";
    public static final String MOBILE_NETWORKCHECK = "Please use your mobile data";
    public static final String JIO_NUMBER = "Please use Jio number";
    public static final String SIM_VALIDATION = "Jio number should be in SIM slot 1";
    public static final String NUMBER_VALIDATION = "Entered phone number should be in SIM slot 1";
    public static final String DEVICE_JIONUMBER = "Please use Jio number in SIM slot 1 to operate this application";
    public static final String REGISTRAION_ALERT_409 = "User is already registered";
    public static final String REGISTRAION_FAILED = "Register failed ,Please contact your admin";
    public static final String LOCATION_NOT_FOUND = "Location not found for the selected device.";
    public static final String COUNTRY_CODE = "91";

    // Dashboard Activity constants
    public static final String DASHBOARD_TITLE = "Home               ";
    public static final String DELETC_DEVICE = "Do you want to delete ?";
    public static final String CHOOSE_DEVICE = "Please select the number for tracking";
    public static final String CONSENT_NOT_APPROVED = "Consent is not apporoved for the selected phone number ";
    public static final String CONSENT_APPROVED = "Consent status is already approved for this number";
    public static final String FMS_SERVERISSUE = "FMS server is down please call back after some time";
    public static final String TOKEN_NULL = "Token is null";
    public static final String LOADING_DATA = "Please wait loading data...";
    public static final String GROUP_CREATION_FAILURE = "You have exceeded maximum allowed active groups, Please delete one of the group members to add any other group.";
    public static final String GROUP_UPDATION_FAILURE = "Group name updation failed";
    public static final String INDIVIDUAL_USER_EDIT_FAILURE = "User name updation failed!, Please try again";
    public static final String NO_GROUP_FOUND = "No Group found, Please try again.";
    public static final String REQUEST_CONSENT_FAILED = "Request consent failed, Please try again.";
    public static final String GROUP_DELETION_FAILURE = "Group Deletion failed";
    public static final String ACTIVE = "active";
    public static final String ACTIVE_CAPS = "Active";
    public static final String SCHEDULED = "scheduled";
    public static final String SCHEDULED_CAPS = "Scheduled";
    public static final String APPROVED = "approved";
    public static final String REJECTED = "rejected";
    public static final String COMPLETED = "completed";
    public static final String EXIT_FROM_GROUP_FAILURE = "Exit from group failed, Please try again!";
    public static final String REMOVE_FROM_GROUP_FAILURE = "Remove from group failed, Please try again!";
    public static final String EXIT_FROM_GROUP_SUCCESS = "Sucessfully exited from Group!";
    public static final String REMOVE_FROM_GROUP_SUCCESS = "Sucessfully removed from the Group!";
    public static final String ADDING_INDIVIDUAL_USER_FAILED = "Adding individual user failed, Please try again!";
    public static final String GROUP_MEMBER_CLASS_NAME = "com.jio.devicetracker.database.pojo.GroupMemberDataList";
    public static final String GET_GROUP_INFO_PER_USER_ERROR = "Unable to retrieve information from server, please try gain!";
    public static final String GROUP_NAME_CLASS_NAME = "com.jio.devicetracker.database.pojo.HomeActivityListData";
    public static final String GROUP = "group";
    public static final String INDIVIDUAL_MEMBER = "individualMember";
    public static final String IS_GROUP_MEMBER = "isGroupMember";
    public static final String LOCATION = "location";
    public static final String SOS = "sos";
    public static final String CONSENT_ID = "consentId";
    public static final String SELECTION_ERROR = "You cannot track individual user and group at the same time, Please select one of them to track.";
    public static final String NO_EVENTS_FOUND_RESPONSE = "No events found matching the search keys.";
    public static final String FETCH_LOCATION_ERROR = "Location details are not available, Please try again!";
    public static final String GROUP_NOT_ACTIVE = "Please wait till clock ticks scheduled time.";
    public static final String SESSION_COMPLETED = "Please request consent to track the group members.";
    public static final String CONSENT_PENDING_STATUS = "Consent is pending, please ask the user to accept consent for tracking";
    public static final String ADD_CONTACT_WARNING = "Please add contact first in the list to proceed further";
    public static final String GROUP_STATUS = "Group Status";
    public static final String GROUP_SELECTED = "groupSelected";
    public static final String TRACKEE_NAME = "TrackeeName";
    public static final String TRACKEE_NUMBER = "TrackeeNumber";


    // Login Activity constants
    public static final String LOGIN_TITLE = "People Tracker";
    public static final String EMAILID_VALIDATION = "Email id cannot be left blank.";
    public static final String PASSWORD_VALIDATION = "Password cannot be left blank.";
    public static final String EMAIL_VALIDATION = "Please provide the correct Email Id!";
    public static final String LOGIN_VALIDATION = "Please enter correct email and password";
    public static final String GENERATE_TOKEN_SUCCESS = "Token generated successfully and sent to user.";
    public static final String GENERATE_TOKEN_FAILURE = "Cannot generate token as your account is not yet activated. Please register the number first!";
    public static final String EMAIL_LOCKED = "Account is locked";
    public static final String VALID_USER = "Please enter valid user";
    public static final String PASSWORD_VALIDATION2 = "Password must have a minimum of 8 characters and a maximum of 16 characters. Also, must contain atleast one lowercase alphabet, one uppercase alphabet, one numeric and one special character";
    public static final String YESJFF_SMS = "Please click on below link to know the consent response https://peopletracker/YesPeopleTracker?data=";
    public static final String NOJFF_SMS = "Please click on below link to know the consent response https://peopletracker/NoPeopleTracker?data=";
    public static final String ADMIN_EMAIL_ID = "Shivakumar.jagalur@ril.com";
    public static final String ADMIN_PASSWORD = "Ril@12345";
    public static final String BORQS_TOKEN_ACTIVITY_TITLE = "Token Verification";
    public static final String RESEND_OTP = "Resend OTP in ";
    public static final String REQUEST_OTP = "Request OTP";
    public static final String GROUP_TAB = "Groups\n";
    public static final String GROUP_WITHOUT_NEXT_LINE = "Groups";
    public static final String PEOPLE_TAB = "People\n";
    public static final String PEOPLE_WITHOUT_NEXT_LINE= "People";
    public static final String DEVICES_TAB = "Devices\n";
    public static final String DEVICES_WITHOUT_NEXT_LINE = "Devices";
    public static final String WELCOME = "Welcome";
    public static final String MOBILE_NUMBER = "Mobile Number";
    public static final String OTP_TEXTVIEW = "Please enter the OTP sent to \n your mobile no. ";
    public static final String LOGIN_WITHOUT_DOT = "Login\n";
    public static final String SIGNUP_WITHOUT_DOT = "Sign up\n";
    public static final String SUCCESS = "success";



    // New Device Activity Constants
    public static final String ADD_DEVICE_TITLE = "Add";
    public static final String IMEI_VALIDATION = "Enter the 15 digit IMEI number";
    public static final String MOBILENUMBER_VALIDATION = "Enter the valid mobile number";
    public static final String PET_NUMBER_VALIDATION = "Please enter the valid device number of pet";
    public static final String PEOPLE_NUMBER_VALIDATION_PET_NUMBER_ENTERED = "Please enter the valid mobile number, Select Pet Tracker from drop down if you want to track any pet";
    public static final String PET_TRACKER_VALIDATION_PEOPLE_NUMBER_ENTERE = "Please enter the valid device number of pet, if you want to track people please select People Tracker from dropdown";
    public static final String REGMOBILENUMBER_VALIDATION = "You can't add registered mobile number";
    public static final String CHECK_DETAILS = "Please Enter the details";
    public static final String DUPLICATE_NUMBER = "This number is already added";
    public static final String LOGOUT_CONFIRMATION_MESSAGE = "Are you sure you want to logout ?";
    public static final String IMEI = "imei";
    public static final String PROGRESSBAR_MSG = "Please wait adding device";
    public static final String PET_TRACKER_DEVICE_TYPE = "Pet Tracker";
    public static final String PEOPLE_TRACKER_DEVICE_TYPE = "People Tracker";
    public static final String ADD_GROUP_MEMBER_INSTRUCTION1 = "* Click on + button to add member inside the group  ";
    public static final String ADD_GROUP_MEMBER_INSTRUCTION2 = "\n* Once added go to Home screen, select the group  from list and click on Track button to know the location of group members";

    // MAPs Activity constants
    public static final String MAP_TITLE = "Location";
    public static final String LOCATION_UPDATE = "Location will be updated after every";
    public static final String SUCCESSFULL_DEVICE_ADDITION_RESPONSE = "1 device(s) are assigned to one user.";
    public static final String SUCCESSFULL_DEVICE_ADDITION = "Device is successfully added";
    public static final String UNSUCCESSFULL_DEVICE_ADDITION = "Device can not be added, please try again later";
    public static final long FREQUENCY_FOR_LOCATION_UPDATE = 10;
    public static final long PRIORITY_BALANCED_POWER_ACCURACY = 10;
    public static final int EPOCH_TIME_DURATION = 15;
    public static final String MAP_DATA = "Map_Data";
    public static final String MAP_TAB = "Map\n";
    public static final String MAP_TAB_WITHOUT_NEXTLINE = "Map";
    public static final String LIST_TAB = "List\n";
    public static final String LIST_TAB_WITHOUT_NEXTLINE = "List";

    public static final String PEOPLE_DATA = "People_Data";


    // Forgot Activity
    public static final String FORGOT_TITLE = "Forgot password";
    public static final String RESET_TITLE = "Reset password";
    public static final String FORGOT_TOKEN_MSG = "Token is sent to entered email";
    public static final String FORGOT_TOKEN_FAIL_MSG = "Token api failed";
    public static final String FORGOT_EMAIL = "Email";
    public static final String CONTACT_DEVICE_TITLE = "Contact Detail";


    //Rest API URL
    public static final String LOGIN_URL = "/accounts/api/users/tokens/verify/login";
    public static final String REGISTRATION_TOKEN_URL = "/accounts/api/users/tokens";
    public static final String REGISTRATION_URL = "/accounts/api/users/v2/register";
    public static final String FORGOT_PASS_URL = "/accounts/api/users/resetpassword";
    public static final String FORGOTPASS_TOKEN_URL = "/ugs/api/user/forgotpassword";
    public static final String ADDDEVICE_URL_1 = "/accounts/api/users/";
    public static final String ADDDEVICE_URL_2 = "/devices/verifyandassign?ugs_token=";
    public static final String SEARCH_DEVICE_URL = "/accounts/api/devices/v2/search?skip=0&limit=10&ugs_token=";
    public static final String SEARCH_DEVICE_STATUS = "/accounts/api/devicestatuses/search?ugs_token=";
    public static final String SEARCH_EVENT_REQUEST = "/accounts/api/events/search?skip=0&limit=100&ugs_token=";
    public static final String TRACK_DEVICE_REQUEST = "/accounts/api/devicestatuses/search?skip=0&limit=20&tsp=1572443375692&ugs_token=";
    public static final String GET_DEVICE_LOCATION_URL_1 = "/accounts/api/devices/";
    public static final String GET_DEVICE_LOCATION_URL_2 = "?tsp=1585031229387&ugs_token=";
    public static final String REGISTRATION_URL_VERIFY = "/accounts/api/users/tokens/verify";
    public static final String GENERATE_TOKEN_REQUEST_URL = "/accounts/api/users/v2/tokens";
    public static final String GENERATE_LOGIN_TOKEN_REQUEST_URL = "/accounts/api/users/v2/tokens/login";
    public static final String ACCOUNTS_API_USER_URL = "/accounts/api/users/";
    public static final String SESSION_GROUPS_URL = "/sessiongroups/";
    public static final String SESSION_GROUPS_URL1 = "/sessiongroups";
    public static final String GET_ALL_GROUP_INFO_URL2 = "/sessiongroups?isPopulateConsents=true&isPopulateGroupOwner=true";
    public static final String SESSION_GROUP_CONSENTS_URL = "/sessiongroupconsents";
    public static final String SESSION_GROUP_CONSENTS_TOKEN_URL = "/sessiongroupconsents/tokens";
    public static final String APPROVE_REJECT_CONSENT_URL1 = "/accounts/api/users/sessiongroupconsents/";
    public static final String STATUS_URL = "/status";
    public static final String SESSION_GROUP_CONSENTS_URL1 = "/sessiongroupconsents/";
    public static final String GET_LOCATION_URL = "/events/search";

    //Privacy Policy
    public static final String TERM_AND_CONDITION_ALERT = "Please accept privacy policy to proceed with the application";
    public static final String TERM_AND_CONDITION_STATUS_MSG = "Please accept terms and conditions and login then again click on the link which is given in message";
    public static final String NOT_REGISTERED_MSG = "User is not registered";
    public static final String INVALID_TOKEN_MSG = "Invalid token please try again";
    public static final String PASSWORD_RESET_SUCCESS_MSG = "Password reset is successfull";
    public static final String VALID_EMAIL_ID = "Please enter the valid email id";
    public static final String ENTER_TOKEN = "Please enter the token which is sent to your email id";
    public static final String PASSWORD_EMPTY = "Password cannot be left blank.";
    public static final String PASSWORD_NOT_MATCHED = "Password did not match, please try again";
    public static final String RESET_PASSWORD_FAILED = "Reset password is failed";
    public static final String NAME_EMPTY = "Name can't be left blank.";
    public static final String EMAIL_EMPTY = "Email cannot be left blank.";
    public static final String MOBILE_NUMBER_EMPTY = "Mobile number cannot be left blank.";
    public static final String VALID_PHONE_NUMBER = "Please enter valid phone number";
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    public static final String SIT_URL = "https://sit.boapi.cats.jvts.net";
    //public static final String STG_URL = "https://stg.borqs.io";
    public static final String FMS_BASE_URL = "http://49.40.22.92:8080";
    public static final String CONSENT_APPROVED_STATUS = "Consent Approved";
    public static final String CONSENT_PENDING = "Consent Pending";
    public static final String REQUEST_CONSENT = "Request Consent";
    public static final String NUMBER_CARRIER = "number";
    public static final String GROUP_CHOOSE_CONDITION = "Please select on group";
    public static final String CONSENT_APPROVED_MESSAGE = "Thanks! you have approved the the group invitation request";
    public static final String CONSENT_REJECTED_MESSAGE = "You have rejected the consent.";
    public static final String CONSENT_NOT_APPROVED_MESSAGE = "Consent is not approved please try gain!";
    public static final String CONSENT_NOT_REJECTED_MESSAGE = "Consent rejection failed!";
    public static final String CONSENT_STATUS_MSG = "Yes JioTracker";
    public static final String CONSENT_MSG_SENT = "Consent sent";
    public static final String CONSENT_MSG_TO_TRACKEE = " wants to track your location. Click below link to reply. https://peopletracker/home?data=";
    public static final String START_TRACKING = "To start tracking ";
    public static final String REQUEST_CONSENT_USER = " please request for consent from the user by clicking “Request consent”";
    public static final String DIALOG_TITLE = "Title...";
    public static final String NO_JIO_TRACKER = "No Jiotracker";
    public static final String ACCESS_COARSE_PERMISSION_ALERT = "Location permission is not granted, please grant it first";
    public static final String IMEI_PERMISSION_NOT_GRANTED = "IMEI permission is not granted!";
    public static final String TERM_CONDITION_FLAG = "TermFlag";
    public static final String GROUP_ID_VALUE = "Group_ID_Value";
    public static String AUTO_LOGIN = "Autologin";
    public static final String AUTO_LOGIN_STATUS = "AutoLoginStatus";
    public static final String LOCATION_FLAG_STATUS = "LocationFlagStatus";
    public static final String CONSENT_NOT_SENT = "Consent not sent";
    public static final String BORQS_OTP_TITLE = "OTP Verification";
    public static final String EMPTY_OTP = "Please enter the otp";
    public static final String TOKEN_VERIFIED_SUCCESS = "Token successfully verified.";
    public static final String TOKEN_VERIFICATION_FAILED = "Token verification failed!";
    public static final String REGISTARTION_SUCCESS_MESSAGE = "Your registartion is successful";
    public static final String REGISTARTION_FAILED_MESSAGE = "Registration is failed";
    public static final String EDIT = "Edit";
    public static final String GROUP_NAME = "Group Name";
    public static final String GROUP_ID = "groupId";
    public static final String GROUPNAME = "groupName";
    public static final String QR_CODE_VALUE = "QRCodeValue";
    public static final String SCAN_QR_CODE_TITLE = "Scan QR Code";

    public static final String ACTIVE_SESSION_TITLE = "Group Management";
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
    public static final String PENDING = "pending";
    public static final String USER_LIMITATION = "You cannot create more than ten individual user and groups";
    public static final String GROUP_NAME_VALIDATION_ERROR = "Group name cannot be left empty";
    public static final String RELATION_WITH_GROUP_ERROR = "Please enter the relation with group member, it cannot be left empty";
    public static final String DEVICE_DETAIL_VALIDATION = "Entered device detail doesn't match with the server data, please enter the correct device detail";
    public static final String GROUP_MEMBER_ADDITION_FAILURE = "Unable to add member in a group, Please try again!";
    public static final String DEVICE_NOT_FOUND = "Device not found, Please try again!";
    public static final String REMOVE = "Remove";
    public static final String EXIT = "Exit";
    public static final String SESSION_GROUPS = "sessiongroups";
    public static final String TRACK = "Track";
    public static final String EXITED = "exited";
    public static final String BEARER = "bearer ";
    public static final String MEDIA_TYPE = "text/plain";
    public static final String APPLICATION_JSON = "application/json";
    public static final String REMOVED = "removed";
    public static final String USER_ID = "userId";
    public static final String OK = "OK";
    public static final String CANCEL = "Cancel";
    public static final String CREATED_BY = "createdBy";
    public static final String UPDATED_BY = "updatedBy";
    public static final String EVENTS = "events";
    public static final String ONE_TO_ONE = "one_to_one";
    public static final String INDIVIDUAL_USER_GROUP_NAME = "Individual+People Tracker";
    public static final String INDIVIDUAL_DEVICE_GROUP_NAME = "Individual+Device Tracker";
    public static final String IS_COMING_FROM_ADD_DEVICE = "isComingFromAddDevice";
    public static final String IS_COMING_FROM_ADD_CONTACT = "isComingFromAddContact";
    public static final String IS_COMING_FROM_GROUP_LIST = "isComingFromGroupList";
    public static final String IS_COMING_FROM_CONTACT_LIST = "isComingFromContactList";
    public static final String CLOSED = "closed";

    // Add Device constants
    public static final String Add_Device = "JioTrack";
    public static final String Add_People = "Add People";
    public static final String ENTER_DEVICE_NAME = "Please enter device name";

    // Add people constants
    public static final String Choose_Group = "Choose group";
    public static final String CREATE_GROUP = "Create group";
    public static final String ENTER_NAME = "Please enter contact name";
    public static final String ADD_GROUP = "Add";
    public static final String DEVICE_NOT_FOUND_QR = "Device not found";



    // Custom Alert Constants
    public static final String Custom_Alert_Title = "Invite";
    public static final String Custom_Alert_Message = "An invite message sent to selected contacts, you can view the status of them in invite section";

    // Profile Activity
    public static final String PROFILE_TITLE = "Profile";
    public static final String EDIT_PROFILE_TITLE = "Edit Profile";
    public static final String EDIT_MEMBER_PROFILE_TITLE = "Edit members";
    public static final String ATTACH_DEVICE_TITLE = "Attach device";


    // Customize device
    public static final String DEVICE_NAME_TITLE = "Customize device ";
    public static final String TITLE_NAME = "Title";
    public static final String REAL_ICON = "Real Icon";
    public static final String GROUP_ICON = "Group Icon";
    public static final String DEVICE_NUMBER = "DeviceNumber";
    public static final String MOM = "Woman";
    public static final String FATHER = "Man";
    public static final String KID = "Kid";
    public static final String HUSBAND = "Boy";
    public static final String WIFE = "Girl";
    public static final String OTHER = "Other";
    public static final String DOG = "Dog";
    public static final String CAT = "Cat";
    public static final String OTHER_PET = "Other Pet";


    //Status code
    public static final int STATUS_CODE_409 = 409;
    public static final int STATUS_CODE_404 = 404;
    public static final int STATUS_CODE_417 = 417;
    public static final int SUCCESS_CODE_200 = 200;
    public static final int STATUS_CODE_401 = 401;


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

    // Notifications
    public static final String NOTIFICATIONS_TITLE = "Notifications & Alerts";
    public static final String SILENT_MODE = "Silent mode";

    // HowToUse Activity
    public static final String HOW_TO_ADD_TITLE = "How to use device";

    // NavigateSupport Activity
    public static final String SUPPORT_TITLE = "Support";
    public static final String ABOUT_APP_TITLE = "About app";
    public static final String TERM_CONDITION_TITLE = "Terms & privacy policies";
    public static final String FAQ_TITLE = "FAQ";
    public static final String CONTACT_TITLE = "Contact us";

    //FAQ Activity
    public static final String QUES_1 = "First Question ?";
    public static final String QUES_2 = "Second Question ?";
    public static final String QUES_3 = "Third Question ?";

    public static final String ANS_1 = "Lorem ipsum dolor sit amet, consectetuer \nadipiscing elit, sed diam nonummy nibh \neuismod tincidunt ut laoreet dolore magna \naliquam erat volutpat.";
    public static final String ANS_2 = "Lorem ipsum dolor sit amet, consectetuer \nadipiscing elit, sed diam nonummy nibh \neuismod tincidunt ut laoreet dolore magna \naliquam erat volutpat.";
    public static final String ANS_3 = "Lorem ipsum dolor sit amet, consectetuer \nadipiscing elit, sed diam nonummy nibh \neuismod tincidunt ut laoreet dolore magna \naliquam erat volutpat.";



}
