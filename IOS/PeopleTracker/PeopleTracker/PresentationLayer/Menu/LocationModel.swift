//
//  LocationModel.swift
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

public struct LocationModel : Codable {
    
    let code    : Int
    let message : String
    let devicedata    : DeviceDetails?
    
    private enum CodingKeys : String, CodingKey {
        case code,message,devicedata = "data"
    }
    
}

public struct DeviceDetails : Codable{
    
    let  deviceId         : String
    let  imei             : String
    let  identifier       : String?
    let  type             : String?
    let  model            : String?
    let  name             : String
    let  phoneCountryCode : String?
    let  phone            : String
    let  userid           : String
    let  deviceStatus     : DeviceStatus?
    private enum CodingKeys : String, CodingKey {
        case deviceId = "_id",imei,identifier,type,model,name,phoneCountryCode,phone,userid = "user",deviceStatus = "devicestatus"
     }
}

struct DeviceStatus : Codable {
    
    let location : location?
     
    private enum CodingKeys : String, CodingKey {
        case location
    }
}

struct location : Codable {
    
    let latitude : Double?
    let longitude : Double?
     
    private enum CodingKeys : String, CodingKey {
        case latitude = "lat",longitude = "lng"
    }
}
