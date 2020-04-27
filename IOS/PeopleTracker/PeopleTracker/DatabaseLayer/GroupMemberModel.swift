//
//  GroupMemberModel.swift
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

@objcMembers class GroupMemberModel : Object, Decodable {
    
    dynamic var code            : Int = 0
    dynamic var message         : String = ""
    dynamic var tempId          : String? = nil
    let groupMemberData = RealmSwift.List<GroupMemberData>()
    
    enum CodingKeys : String, CodingKey {
        case code,message,groupMemberData = "data"
    }
    
    required init(from decoder: Decoder) throws
    {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        code = try container.decode(Int.self, forKey: .code)
        message = try container.decode(String.self, forKey: .message)
        let memeberList = try container.decode([GroupMemberData].self, forKey: .groupMemberData)
        groupMemberData.append(objectsIn: memeberList)
        tempId = groupMemberData.first?.groupMemberId
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

@objcMembers class GroupMemberData : Object, Decodable {
    
    dynamic var  status                : String  = ""
    dynamic var  groupMemberId         : String  = ""
    dynamic var  name                  : String  = ""
    dynamic var  phone                 : String  = ""
    dynamic var  user                  : String  = ""
    dynamic var  device                : String  = ""
    dynamic var  sessionGroup          : String  = ""
    
    enum CodingKeys : String, CodingKey {
        case status,groupMemberId = "_id",name,phone,user,device,sessionGroup
    }
    
    required init(from decoder: Decoder) throws
    {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        
        status = try container.decode(String.self, forKey: .status)
        groupMemberId = try container.decode(String.self, forKey: .groupMemberId)
        name = try container.decode(String.self, forKey: .name)
        phone = try container.decode(String.self, forKey: .phone)
        user = try container.decode(String.self, forKey: .user)
        device = try container.decode(String.self, forKey: .device)
        sessionGroup = try container.decode(String.self, forKey: .sessionGroup)
        
        super.init()
    }
    
    required init()
    {
        super.init()
    }
    override class func primaryKey() -> String? {
        return "groupMemberId"
    }
}
