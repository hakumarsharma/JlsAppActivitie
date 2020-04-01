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
       static let userApisUrl = "accounts/api/users/"
       static let loginUrl = BaseUrl + userApisUrl + "login?isResponseDataAsUser=true&isPopulateGroup=true&isPopulateGroupUsers=true&isPopulateUserDevices=true&isPopulateUserDevicesAsWearableUsers=true"
       static let addDeviceUrl = "/devices/verifyandassign?ugs_token="
    
        
    }
    
    struct LoginScreenConstants {
        
        static let userName = "Please enter user name"
        static let phoneNumber = "Please enter valid phone number"
        static let otp = "Please enter otp"
    }
    
    struct AddDeviceConstants {
          static let name = "Please enter name"
          static let imei = "Please enter valid IMEI number"
      }
    
    struct ErrorMessage {
        static let unauthorized = "Please check your login credentails"
        static let somethingwentwrong = "Something went wrong"
    }
}
