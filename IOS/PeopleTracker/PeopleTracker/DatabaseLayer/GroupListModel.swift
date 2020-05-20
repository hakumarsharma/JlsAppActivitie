//
//  GroupListModel.swift
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

@objcMembers class GroupListModel : Object, Decodable {
    
    dynamic var code          : Int = 0
    dynamic var message       : String = ""
    let groupListData = RealmSwift.List<GroupListData>()
    
    enum CodingKeys : String, CodingKey {
        case code,message,groupListData = "data"
    }
    
    required init(from decoder: Decoder) throws
    {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        
        code = try container.decode(Int.self, forKey: .code)
        message = try container.decode(String.self, forKey: .message)
        let groupList = try container.decode([GroupListData].self, forKey: .groupListData)
        groupListData.append(objectsIn: groupList)
        
        super.init()
    }
    
    required init()
    {
        super.init()
    }
}

@objcMembers class  GroupListData : Object, Decodable {
    
    dynamic var  status          : String = ""
    dynamic var  groupId         : String = ""
    dynamic var  name            : String = ""
    dynamic var  groupSession    : GroupSession? = nil
    dynamic var  groupCreatedBy  : String? = nil
    let groupMember = RealmSwift.List<GroupMember>()
    let groupOwner = RealmSwift.List<GroupOwner>()
    
    enum CodingKeys : String, CodingKey {
        case status,groupId = "_id",name,groupSession = "session",groupMember = "consents",groupCreatedBy="createdBy",groupOwner
    }
    
    required init(from decoder: Decoder) throws
    {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        
        status = try container.decode(String.self, forKey: .status)
        groupId = try container.decode(String.self, forKey: .groupId)
        name = try container.decode(String.self, forKey: .name)
        groupSession = try container.decode(GroupSession.self, forKey: .groupSession)
        groupCreatedBy = try? container.decode(String.self, forKey: .groupCreatedBy)
        let groupMembersList = try container.decode([GroupMember].self, forKey: .groupMember)
        groupMember.append(objectsIn: groupMembersList)
        if let groupowner = try? container.decode([GroupOwner].self, forKey: .groupOwner) {
        groupOwner.append(objectsIn: groupowner)
        }
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

@objcMembers class  GroupOwner : Object, Decodable{
    
    dynamic var  ownerName    : String? = nil
    dynamic var  ownerPhone   : String? = nil
    dynamic var  ownerId      : String? = nil
    
    enum CodingKeys : String, CodingKey {
        case ownerName="name",ownerPhone="phone",ownerId="_id"
    }
    
    required init(from decoder: Decoder) throws
    {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        
        ownerName = try? container.decode(String.self, forKey: .ownerName)
        ownerPhone = try? container.decode(String.self, forKey: .ownerPhone)
        ownerId = try? container.decode(String.self, forKey: .ownerId)
        
        super.init()
    }
    
    required init()
    {
        super.init()
    }
    override class func primaryKey() -> String? {
        return "ownerId"
    }
}


@objcMembers class  GroupSession : Object, Decodable{
    
    dynamic var  from  : Int64? = nil
    dynamic var  to    : Int64? = nil
    
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

@objcMembers class GroupMember : Object, Decodable{
    
    dynamic var  memberStatus       : String? = nil
    dynamic var  memberPhone        : String? = nil
    dynamic var  memberName         : String? = nil
    dynamic var  memberDevice       : String? = nil
    dynamic var  isGroupAdmin       : Bool? = nil
    dynamic var  memberId           : String? = nil
    dynamic var  deviceType         : String? = nil
    
    enum CodingKeys : String, CodingKey {
        case memberStatus="status",memberPhone="phone",isGroupAdmin,memberId="_id",memberName = "name",deviceType,memberDevice="device"
    }
    
    required init(from decoder: Decoder) throws
    {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        
        memberStatus = try? container.decode(String.self, forKey: .memberStatus)
        memberPhone = try? container.decode(String.self, forKey: .memberPhone)
        isGroupAdmin = try? container.decode(Bool.self, forKey: .isGroupAdmin)
        memberName = try? container.decode(String.self, forKey: .memberName)
        memberDevice = try? container.decode(String.self, forKey: .memberDevice)
        memberId = try? container.decode(String.self, forKey: .memberId)
        deviceType = try? container.decode(String.self, forKey: .deviceType)
        super.init()
    }
    
    required init()
    {
        super.init()
    }
}
