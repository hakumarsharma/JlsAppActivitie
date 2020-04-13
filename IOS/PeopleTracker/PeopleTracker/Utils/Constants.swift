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
       static let BaseUrl = "https://sit.boapi.cats.jvts.net/"
       static let UserApisUrl =  BaseUrl + "accounts/api/users/"
       static let DeviceApisUrl = BaseUrl + "accounts/api/devices/"
       static let LoginUrl = UserApisUrl + "login?isResponseDataAsUser=true&isPopulateGroup=true&isPopulateGroupUsers=true&isPopulateUserDevices=true&isPopulateUserDevicesAsWearableUsers=true"
       static let AddDeviceUrl = "/devices/verifyandassign?ugs_token="
       static let DeviceDetails = DeviceApisUrl + "v2/search?skip=0&limit=10&ugs_token="
        
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
    }
    
    
    struct  ScreenNames {
        static let LoginScreen = "LoginScreen"
        static let HomeScreen = "HomeScreen"
        static let AddDeviceScreen = "AddDeviceScreen"
        static let CreateGroupScreen = "CreateGroupScreen"
        static let GroupListScreen = "GroupListScreen"
        static let MapsScreen = "MapsScreen"
    }
    
    struct LoginScreenConstants {
        
        static let UserName = "Please enter user name"
        static let PhoneNumber = "Please enter valid phone number"
        static let Otp = "Please enter otp"
    }
    
    struct UserDefaultConstants {
        static let UgsToken = "ugsToken"
        static let UserId = "userId"
        static let UgsExpiryTime = "ugsExpiryTime"
    }
    
    struct HomScreenConstants {
        static let Select = "Select"
        static let Edit = "Edit"
        static let Delete = "Delete"
        static let Dismiss = "Dismiss"
        static let AddDevice = "Add Device"
        static let AddPerson = "Add Person"
        static let CreateGroup = "Create Group"
        static let DeleteDevice = "Are you sure do you want to delete ?"
        static let RequestConsent = "Request Consent"
        static let ConsentApproved = "Consent Approved"
        static let ConsentPending = "Consent Pending"
    }
    
    struct AddDeviceConstants {
          static let Name = "Please enter name"
          static let Imei = "Please enter valid IMEI number"
          static let DeviceAddedSuccessfully = "Device added successfully"
      }
    
    struct LocationConstants {
        static let NoLatLong = "Device doesnot have any latitude and longitude"
        static let LocationDetailsNotFound = "Selected Device location details are not available"
    }
    
    struct ErrorMessage {
        static let Unauthorized = "Please check your login credentails"
        static let DeviceCanotBeAssigned = "Device cannot be added, Please try again later"
        static let Somethingwentwrong = "Something went wrong"
    }
}
