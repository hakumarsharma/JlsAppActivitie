//
//  DeviceModel.swift
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

@objcMembers class DeviceModel : Object, Decodable {
    
    dynamic var code          : Int = 0
    dynamic var message       : String = ""
    var devicedata  = RealmSwift.List<DeviceData>()
    
    enum CodingKeys : String, CodingKey {
        case code,message,devicedata = "data"
    }
    
    required init(from decoder: Decoder) throws
       {
           let container = try decoder.container(keyedBy: CodingKeys.self)
           
           code = try container.decode(Int.self, forKey: .code)
           message = try container.decode(String.self, forKey: .message)
           let deviceList = try container.decode([DeviceData].self, forKey: .devicedata)
           devicedata.append(objectsIn: deviceList)
           
           super.init()
       }
       
       required init()
       {
           super.init()
       }
    
}
@objcMembers class DeviceData : Object, Decodable{
    
    dynamic var  deviceId         : String = ""
    dynamic var  imei             : String = ""
    dynamic var  identifier       : String? = nil
    dynamic var  type             : String? = nil
    dynamic var  model            : String? = nil
    dynamic var  name             : String = ""
    dynamic var  phoneCountryCode : String? = nil
    dynamic var  phone            : String = ""
    dynamic var  userId           : String = ""

    enum CodingKeys : String, CodingKey {
        case deviceId = "_id",imei,identifier,type,model,name,phoneCountryCode,phone,userId = "user"
     }
    
    required init(from decoder: Decoder) throws
    {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        
        deviceId = try container.decode(String.self, forKey: .deviceId)
        imei = try container.decode(String.self, forKey: .imei)
        identifier = try container.decode(String.self, forKey: .identifier)
        type = try container.decode(String.self, forKey: .type)
        model = try container.decode(String.self, forKey: .model)
        name = try container.decode(String.self, forKey: .name)
        phoneCountryCode = try container.decode(String.self, forKey: .phoneCountryCode)
        phone = try container.decode(String.self, forKey: .phone)
        userId = try container.decode(String.self, forKey: .userId)
        
        super.init()
    }
    
    required init()
    {
        super.init()
    }
}

