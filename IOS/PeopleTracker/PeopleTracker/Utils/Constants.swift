//
//  Constants.swift
//  PeopleTracker
//
//  Created by Apple on 18/03/20.
//  Copyright © 2020 Apple. All rights reserved.
//

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
