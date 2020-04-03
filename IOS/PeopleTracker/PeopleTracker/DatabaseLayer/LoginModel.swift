//
//  LoginModel.swift
//  PeopleTracker
//
//  Created by Apple on 24/03/20.
//  Copyright © 2020 Apple. All rights reserved.
//

import Foundation

public struct LoginModel : Codable {
    
    let ugsToken        : String
    let ugsTokenexpiry  : Double
    let user            : User?
    
    private enum CodingKeys : String, CodingKey {
        case ugsToken = "ugs_token", ugsTokenexpiry = "ugs_token_expiry",user
    }
    
}

struct User : Codable {
    
    let userId        : String
    let email         : String
    let usertype      : String
    let name          : String
    let phone         : String
    let wearableUsers : [WearableUsers]?
    
    private enum CodingKeys : String, CodingKey {
        case userId = "_id", email, usertype = "type", name, phone,wearableUsers
    }
}
struct WearableUsers : Codable {
    
    let wearableDeviceId : String
    let deviceId         : String
    
    private enum CodingKeys: String, CodingKey {
        case wearableDeviceId = "_id", deviceId = "deviceId"
    }
    
}
