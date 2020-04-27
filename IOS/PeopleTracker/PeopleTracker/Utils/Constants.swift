//
//  Constants.swift
//  PeopleTracker
//
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


import Foundation

class Constants {
    
    struct ApiPath {
       static let BaseUrl = "https://stg.borqs.io/"//"https://sit.boapi.cats.jvts.net/"
       static let UserApisUrl =  BaseUrl + "accounts/api/users/"
       static let DeviceApisUrl = BaseUrl + "accounts/api/devices/"
       static let LoginUrl = UserApisUrl + "login?isResponseDataAsUser=true&isPopulateGroup=true&isPopulateGroupUsers=true&isPopulateUserDevices=true&isPopulateUserDevicesAsWearableUsers=true"
       static let AddDeviceUrl = "/devices/verifyandassign?ugs_token="
       static let DeviceDetails = DeviceApisUrl + "v2/search?skip=0&limit=10&ugs_token="
       static let GenerateTokenUrl = UserApisUrl + "tokens"
       static let VerifyTokenUrl = UserApisUrl + "tokens/verify"
       static let RegisterationUrl = UserApisUrl + "register"
       static let CreateGroupUrl = "/sessiongroups"
       static let GroupListUrl = "/sessiongroups?isPopulateConsents=true&status=scheduled"
       static let CreateMultiple = "/sessiongroupconsents"
       static let GenerateConsent = "/tokens"
       static let ApproveConsent = "/status"
    }
    
    struct HelpScreen {
       static let LoginTitle = "Login :"
       static let HomeTitle = "Home :"
       static let LocationTitle = "Location :"
       static let LoginText = "It is mandatory for the user to login by using the credentials used during registration. Without login, the user will not be able to use the application."
       static let HomeText = "Logged-in user can add a device to be tracked by clicking (+) button provided on the bottom-right corner of the screen.\nDevice\'s phone number and IMEI number are required for device addition. \nAfter the device is added successfully, it will be displayed on the screen as shown above."
       static let HomeText2 = "To track the device, please click on the \"Request Consent\" and get an approval from the owner of the device to be tracked.\nTracking can be initiated after consent is approved by the device owner."
       static let LocationText = "Location of the device being tracked will be displayed on the map as a pin-drop. Tapping on the pin-drop will display address of the location.\nLocation of multiple devices will be displayed as multiple pin-drops on the map."
    }
    
    struct PrivacyScreen {
        static let PrivacyPolicy = "<font size='4'><p><strong>This document is an electronic record in terms of Information Technology&nbsp;Act, 2000 and rules there under as applicable and the amended provisions pertaining to electronic records in various statutes as amended by the</strong>&nbsp;<strong>Information Technology Act, 2000. This electronic record is generated</strong>&nbsp;<strong>by a computer system and does not require any physical or digital</strong>&nbsp;<strong>signatures.</strong></p><p>For the purpose of these Privacy policies, wherever the context so requires&nbsp;<strong>\"You\"</strong>&nbsp;or&nbsp;<strong>\"User\"</strong>&nbsp;shall mean any natural or legal person who uses our <strong>People Tracker app</strong> to track location information of other users of the app with a mutual consent. The term&nbsp;<strong>\"We\"</strong>,&nbsp;<strong>\"Us\"</strong>,&nbsp;<strong>\"Our\"</strong>&nbsp;shall mean Reliance Jio Infocomm Limited.</p><p>&nbsp;</p><p><strong><u>Definitions</u></strong></p><p>&ldquo;<strong>App(s)</strong>&rdquo; shall mean applications for mobile and includes People Tracker App.</p><p>&ldquo;<strong>Device(s)</strong>&rdquo; shall mean a desktop computer, laptop computer, tablet, mobile phone, smart watch or such other electronic equipment.</p><p>&ldquo;<strong>Force Majeure Event</strong>&rdquo; shall mean any event that is beyond our reasonable control and shall include, without limitation, sabotage, fire, flood, explosion, acts of God, civil commotion, strikes or industrial action or any kind, riots, insurrection, war, acts of government, computer hacking, unauthorized access to your mobile or storage device, crashes, breach of security and encryption.</p><p>&ldquo;<strong>Personal Information</strong>&rdquo; refers to any information that identifies or can be used to identify, contact or locate the person, to whom such information pertains including, but not limited to, name, address, phone number and email address disclosed by you in relation to the services available on the Platform.</p><p>&ldquo;<strong>Sensitive personal data or information</strong>&rdquo; consists of information relating to the following:</p><ol><li>passwords;</li><li>financial information such as bank account or credit card or debit card or other payment instrument details;</li><li>physical, physiological and mental health condition;</li><li>sexual orientation;</li><li>medical records and history;</li><li>Biometric information;</li><li>any detail relating to the above clauses as provided to body corporate for providing service; and</li><li>any of the information received under above clauses by body corporate for processing, stored or processed under lawful contract or otherwise.</li></ol><p><strong>&ldquo;Third Party</strong>&rdquo; refers to any person or entity other than you or us.</p><p><strong>SECTION 1&nbsp;&ndash; WHAT INFORMATION DO WE COLLECT</strong></p><p><strong>Personal Information provided by you in relation to the registration process:&nbsp;</strong>We collect personal data from you when you voluntarily provide such information, such as e-mail and phone number.&nbsp; Wherever possible, we indicate which fields are required and which fields are optional. You always have the option to not provide information for the fields that are optional.</p><p>&nbsp;</p><p><strong>Personal Information provided by you in relation to the use of the app:&nbsp;</strong>In order for us to enable location tracking by your family and friends, we collect your location only after you allow the app to use location in the settings and when you provide your consent to track.</p><p>In connection with facilitating this service, we access SMS messages, the parties&rsquo; phone numbers, and the content of the SMS message such as OTP, etc.</p><p><strong>Collection of Sensitive personal data or information:</strong>&nbsp;We do not collect, store or process Sensitive personal data or information as part of our services on our Platform.</p><p><strong>Cookies:</strong>&nbsp;We do not collect any information from cookies</p><p><strong>SECTION 2:&nbsp;WHAT DO WE DO WITH YOUR INFORMATION</strong></p><p>2.1 We use your information for security purpose to allow you to login into the app, and to take your consent before you track location of others or allow others to track your location. We also use content of SMS to take your consent for tracking and also to read OTP. Once consent is given, your location information will be sent to our server so that your relatives and friends can track your location provided you have given your consent to them.</p><p><strong>SECTION&nbsp;3 &ndash;&nbsp;CONSENT</strong></p><p>3.1 When You&nbsp;provide&nbsp;Us&nbsp;with personal information for registration and provide consent through SMS for others to track your location, it is deemed that You have consented to the use of your personal information by Us to track your location.</p><p>3.2 In the course of business, if your personal information for any secondary reason, We shall specify the reason for requiring such information. Upon such request, You shall have the option to refrain from revealing such information to Us.</p><p>3.3 Each consent request to track your location is tied with a time duration. After that time duration, others will be not be allowed track you. For others to continue tracking you, they have to take your consent again for the time duration mutually agreed by you and the person who is tracking your location. You may decide not to approve your consent by selecting &ldquo;Decline&rdquo;.</p><p><strong>SECTION&nbsp;4 &ndash;&nbsp;SECURITY</strong></p><p>4.1&nbsp;To protect your personal information, we take reasonable precautions and follow industry best practices to make sure it is not inappropriately lost, misused, accessed, disclosed, altered or destroyed.</p><p>4.2 If You&nbsp;provide&nbsp;Us&nbsp;with your sensitive information, the information is encrypted using secure socket layer technology (SSL) and stored with a AES-256 encryption. Although,&nbsp;no method of transmission over the Internet or electronic storage is 100% secure, we follow all PCI-DSS requirements and implement additional generally accepted industry standards.</p><p><strong>SECTION 5 - AGE OF CONSENT</strong></p><p>5.1&nbsp;By using this site,&nbsp;You&nbsp;represent that&nbsp;You&nbsp;are at least the age of majority in your state or province of residence&nbsp;and you have given us your consent to allow any of your minor dependents to use this app.</p><p><strong>SECTION 6 - CHANGES TO THIS PRIVACY POLICY</strong></p><p>6.1&nbsp;We reserve the right to modify this privacy policy at any time, so please review it frequently. Changes and clarifications will take effect immediately upon their posting on the&nbsp;App/Website. If&nbsp;We&nbsp;make material changes to this policy,&nbsp;We&nbsp;will notify&nbsp;You&nbsp;here that it has been updated, so that&nbsp;You&nbsp;are aware of what information&nbsp;We&nbsp;collect, how we use it, and under what circumstances, if any,&nbsp;We&nbsp;use and/or disclose it.</p><p>6.2&nbsp;If our&nbsp;App/Website&nbsp;is acquired or merged with another company, your information may be&nbsp;transferred to the new owners so that we may continue to sell products to&nbsp;You.</p><p>CONTACT INFORMATION :</p><p>For any query, grievance, complaint or any other information that may be required by You, You may contact Our Privacy Compliance Officer at&nbsp;<a href=\"mailto:info@ril.com\">info@ril.com</a>&nbsp;and We shall ensure that We help resolve the same within a period of 48 (forty eight) hours.</p></font>"
        static let DeclineAlert = "Please accept privacy policy to continue."
    }
    
    struct MqttConstants {
        static let HostName = "tcp://v.dev.tnt.cats.jvts.net"
        static let UserName = "borqs-sit"
        static let Password = "borqs-sit@987"
    }
    
    struct AlertConstants {
        static let Alert = "Alert"
        static let OkButton = "Ok"
        static let CancelButton = "Cancel"
        static let Delete = "Delete"
    }
    
    
    struct  ScreenNames {
        static let PrivacyScreen = "PrivacyPolicyScreen"
        static let LoginScreen = "LoginScreen"
        static let HomeScreen = "HomeScreen"
        static let AddDeviceScreen = "AddDeviceScreen"
        static let AddPersonScreen = "AddPersonScreen"
        static let CreateGroupScreen = "CreateGroupScreen"
        static let GroupListScreen = "GroupListScreen"
        static let MapsScreen = "MapsScreen"
        static let ActiveSessions = "ActiveSessionsScreen"
        static let Profile = "ProfileScreen"
        static let Settings = "SettingsScreen"
        static let HelpPrivacy = "HelpPrivacyScreen"
        static let About = "AboutScreen"
    }
    
    struct LoginScreenConstants {
        
        static let UserName = "Please enter user name"
        static let PhoneNumber = "Please enter valid phone number"
        static let Otp = "Please enter otp"
    }
    
    
    struct HomScreenConstants {
        static let Select = "Select"
        static let Edit = "Edit"
        static let Delete = "Delete"
        static let Dismiss = "Dismiss"
        static let AddDevice = "Add Device"
        static let AddPerson = "Add Person"
        static let CreateGroup = "Create Group"
        static let EditGroup = "Edit Group"
        static let DeleteDevice = "Are you sure do you want to delete ?"
        static let RequestConsent = "Request Consent"
        static let ConsentApproved = "Consent Approved"
        static let ConsentPending = "Consent Pending"
        static let SelectDevice = "Select any added device to track"
    }
    
    struct AddDeviceConstants {
          static let Name = "Please enter name"
          static let Imei = "Please enter valid IMEI number"
          static let DeviceAddedSuccessfully = "Device added successfully"
          static let SelectType = "Select device type"
          static let AdultTracker = "Adult Tracker"
          static let KidTracker = "Kid Tracker"
          static let PetTracker = "Pet Tracker"
          static let VehicleTracker = "Vehicle Tracker"
          static let OwnerNumber = "Please enter valid owner number"
      }
    
    struct CreateGroupConstantts {
        static let CreateGroup = "Please enter group name"
    }
    
    struct LocationConstants {
        static let NoLatLong = "Device doesnot have any latitude and longitude"
        static let LocationDetailsNotFound = "Selected Device location details are not available"
    }
    
    struct ErrorMessage {
        static let Unauthorized = "Please check your login credentails"
        static let DeviceCanotBeAssigned = "Device cannot be added, Please try again later"
        static let Somethingwentwrong = "Something went wrong"
        static let ExceededGroupLimit = "Limit has been exceeded to create a group"
        static let MobileNumberExists = "Mobile number already exists"
    }
    
    struct NotificationName {
        static let GetGroupList = "GetGroupList"
        static let GetMemebersInGroup = "GetMemebersInGroup"
    }
}
