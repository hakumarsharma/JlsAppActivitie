//
//  LoginModel.swift
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

@objcMembers class LoginModel : Object, Decodable {
    
    dynamic var ugsToken        : String = ""
    dynamic var ugsTokenexpiry  : Int64 = 0
    dynamic var user            : User? = nil
    
    enum CodingKeys : String, CodingKey {
        case ugsToken = "ugs_token", ugsTokenexpiry = "ugs_token_expiry",user
    }
    
    required init(from decoder: Decoder) throws
    {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        
        ugsToken = try container.decode(String.self, forKey: .ugsToken)
        ugsTokenexpiry = try container.decode(Int64.self, forKey: .ugsTokenexpiry)
        user = try container.decode(User.self, forKey: .user)
        super.init()
    }
    
    required init()
    {
        super.init()
    }
    
    override class func primaryKey() -> String? {
        return "ugsToken"
    }
    
}

@objcMembers class User : Object, Decodable {
    
    dynamic var userId        : String  = ""
    dynamic var usertype      : String  = ""
    dynamic var name          : String  = ""
    dynamic var phone         : String  = ""
    
    enum CodingKeys : String, CodingKey {
        case userId = "_id", usertype = "type", name, phone
    }
    
    required init(from decoder: Decoder) throws
    {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        
        userId = try container.decode(String.self, forKey: .userId)
        usertype = try container.decode(String.self, forKey: .usertype)
        name = try container.decode(String.self, forKey: .name)
        phone = try container.decode(String.self, forKey: .phone)
        
        super.init()
    }
    
    required init()
    {
        super.init()
    }
    
    override class func primaryKey() -> String? {
        return "userId"
    }
    
    
}

