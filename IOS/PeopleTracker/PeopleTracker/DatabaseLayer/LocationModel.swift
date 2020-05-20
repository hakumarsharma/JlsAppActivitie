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
import RealmSwift

@objcMembers class LocationModel : Object, Decodable {
    
    dynamic var code    : Int = 0
    dynamic var message : String = ""
    dynamic var devicedata    = RealmSwift.List<DeviceDetails>()
    dynamic var tempId    : String? = nil
    
    enum CodingKeys : String, CodingKey {
        case code,message,devicedata = "data"
    }
    
    required init(from decoder: Decoder) throws
       {
           let container = try decoder.container(keyedBy: CodingKeys.self)
           
           code = try container.decode(Int.self, forKey: .code)
           message = try container.decode(String.self, forKey: .message)
           let devicesList = try container.decode([DeviceDetails].self, forKey: .devicedata)
           devicedata.append(objectsIn: devicesList)
           tempId = devicedata.first?.deviceId
           super.init()
       }
       
       required init()
       {
           super.init()
       }
       
       override class func primaryKey() -> String? {
           return "tempId"
       }
}

@objcMembers class DeviceDetails : Object, Decodable{
    
    dynamic var  locationId       : String = ""
    dynamic var  deviceId         : String = ""
    dynamic var  type             : String = ""
    dynamic var  location         : Location? = nil
    dynamic var  name             : String? = nil
    dynamic var  phone            : String? = nil
    
    enum CodingKeys : String, CodingKey {
        case locationId="_id",deviceId = "device",identifier,type,location = "location",name,phone
     }
    
    required init(from decoder: Decoder) throws
    {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        
        locationId = try container.decode(String.self, forKey: .locationId)
        deviceId = try container.decode(String.self, forKey: .deviceId)
        type = try container.decode(String.self, forKey: .type)
        location = try container.decode(Location.self, forKey: .location)
        name = try? container.decode(String.self, forKey: .name)
        phone = try? container.decode(String.self, forKey: .phone)
               
        super.init()
    }
    
    required init()
    {
        super.init()
    }
}

@objcMembers class Location :  Object, Decodable {
    
    dynamic var latitude : Double = 0
    dynamic var longitude : Double = 0
     
    enum CodingKeys : String, CodingKey {
        case latitude = "lat",longitude = "lng"
    }
    
    required init(from decoder: Decoder) throws
       {
           let container = try decoder.container(keyedBy: CodingKeys.self)
           
           latitude = try container.decode(Double.self, forKey: .latitude)
           longitude = try container.decode(Double.self, forKey: .longitude)
           super.init()
       }
       
       required init()
       {
           super.init()
       }
}
