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
       static let userApisUrl =  BaseUrl + "accounts/api/users/"
       static let deviceApisUrl = BaseUrl + "accounts/api/devices/"
       static let loginUrl = userApisUrl + "login?isResponseDataAsUser=true&isPopulateGroup=true&isPopulateGroupUsers=true&isPopulateUserDevices=true&isPopulateUserDevicesAsWearableUsers=true"
       static let addDeviceUrl = "/devices/verifyandassign?ugs_token="
       static let deviceDetails = deviceApisUrl + "v2/search?skip=0&limit=10&ugs_token="
        
    }
    
    struct AlertConstants {
        static let alert = "Alert"
        static let okButton = "Ok"
        static let cancelButton = "Cancel"
    }
    
    struct LoginScreenConstants {
        
        static let userName = "Please enter user name"
        static let phoneNumber = "Please enter valid phone number"
        static let otp = "Please enter otp"
    }
    
    struct AddDeviceConstants {
          static let name = "Please enter name"
          static let imei = "Please enter valid IMEI number"
          static let deviceAddedSuccessfully = "Device added successfully"
      }
    
    struct ErrorMessage {
        static let unauthorized = "Please check your login credentails"
        static let deviceCanotBeAssigned = "Device cannot be added, Please try again later"
        static let somethingwentwrong = "Something went wrong"
    }
}
