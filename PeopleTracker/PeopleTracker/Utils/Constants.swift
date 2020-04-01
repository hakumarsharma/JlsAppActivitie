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
       static let loginUrl = BaseUrl + "accounts/api/users/login?isResponseDataAsUser=true&isPopulateGroup=true&isPopulateGroupUsers=true&isPopulateUserDevices=true&isPopulateUserDevicesAsWearableUsers=true"
        
    }
    
    struct LoginScreenConstants {
        
        static let userName = "Please enter user name"
        static let phoneNumber = "Please enter valid phone number"
        static let otp = "Please enter otp"
    }
    
    struct errorMessage {
        static let unauthorized = "Please check your login credentails"
        static let somethingwentwrong = "Something went wrong"
    }
}
