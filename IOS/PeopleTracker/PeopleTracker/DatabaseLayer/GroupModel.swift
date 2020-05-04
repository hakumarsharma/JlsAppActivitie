//
//  GroupModel.swift
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

@objcMembers class GroupModel : Object, Decodable {
    
    dynamic var code      : Int = 0
    dynamic var message   : String = ""
    dynamic var tempId    : String? = nil
    dynamic var groupData         : GroupData? = nil
    
    enum CodingKeys : String, CodingKey {
        case code,message,groupData = "data"
    }
    
    required init(from decoder: Decoder) throws
    {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        
        code = try container.decode(Int.self, forKey: .code)
        message = try container.decode(String.self, forKey: .message)
        groupData = try container.decode(GroupData.self, forKey: .groupData)
        tempId = groupData?.groupId
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
@objcMembers class GroupData : Object, Decodable{
    
    dynamic var  status     : String? = nil
    dynamic var  groupId    : String = ""
    dynamic var  name       : String = ""
    var session             : Session? = nil
    
    enum CodingKeys : String, CodingKey {
        case status,groupId = "_id",name,session
    }
    
    required init(from decoder: Decoder) throws
    {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        
        status = try container.decode(String.self, forKey: .status)
        groupId = try container.decode(String.self, forKey: .groupId)
        name = try container.decode(String.self, forKey: .name)
        session = try container.decode(Session.self, forKey: .session)
        
        super.init()
    }
    
    required init()
    {
        super.init()
    }
    
    override class func primaryKey() -> String? {
           return "groupId"
       }
}

@objcMembers class Session : Object, Decodable{
    
    dynamic var  from  : Int64 = 0
    dynamic var  to    : Int64 = 0
    
    enum CodingKeys : String, CodingKey {
        case from,to
    }
    
    required init(from decoder: Decoder) throws
    {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        
        from = try container.decode(Int64.self, forKey: .from)
        to = try container.decode(Int64.self, forKey: .to)
        
        super.init()
    }
    
    required init()
    {
        super.init()
    }
}
