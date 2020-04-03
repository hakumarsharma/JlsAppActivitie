//
//  DeviceModel.swift
//  PeopleTracker
//
//  Created by Apple on 01/04/20.
//  Copyright © 2020 Apple. All rights reserved.
//

import Foundation

public struct DeviceModel : Codable {
    
    let code    : Int
    let message : String
    let devicedata    : [DeviceData]?
    
    private enum CodingKeys : String, CodingKey {
        case code,message,devicedata = "data"
    }
    
}
public struct DeviceData : Codable{
    
    let  deviceId         : String
    let  imei             : String
    let  identifier       : String
    let  type             : String
    let  model            : String
    let  name             : String
    let  phoneCountryCode : String?
    let  phone            : String
    let  userid           : String
    let  deviceStatus     : [DeviceStatus]?
     private enum CodingKeys : String, CodingKey {
        case deviceId = "_id",imei,identifier,type,model,name,phoneCountryCode,phone,userid = "user",deviceStatus = "devicestatus"
     }
}

public struct DeviceStatus : Codable {
    
    let location : [location]?
     
    private enum CodingKeys : String, CodingKey {
        case location
    }
}

public struct location : Codable {
    
    let latitude : Double
    let longitude : Double
     
    private enum CodingKeys : String, CodingKey {
        case latitude = "lat",longitude = "lng"
    }
}
