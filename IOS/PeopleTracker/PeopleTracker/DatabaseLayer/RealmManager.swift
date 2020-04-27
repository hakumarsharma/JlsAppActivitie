//
//  RealmManager.swift
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
import  RealmSwift

class RealmManager {
    private var   realm:Realm
    static let   sharedInstance = RealmManager()
    private init() {
//        let config = Realm.Configuration(
//            // Get the URL to the bundled file
//            fileURL: Bundle.main.url(forResource: "default", withExtension: "realm"),
//            // Open the file in read-only mode as application bundles are not writeable
//            readOnly: true)

        // Open the Realm with the configuration
//         realm = try! Realm(configuration: config)
//        let config = Realm.Configuration()
//        print(config.fileURL!)
//        // Open the Realm with the configuration
//        let fileUrl = Bundle.main.url(forResource: "default", withExtension: "realm")
//        if let realmurl = fileUrl {
//            let configuration = Realm.Configuration(fileURL: realmurl)
//            realm = try! Realm(configuration: configuration)
//        } else {
        realm = try! Realm(configuration: Realm.Configuration.defaultConfiguration)
//        }
        print(realm.configuration.fileURL?.absoluteString ?? "")
    }
    
    // LoginData
    func getUserDataFromDB() ->   Results<LoginModel> {
        let userrealm = try! Realm()
        let results: Results<LoginModel> = userrealm.objects(LoginModel.self)
        return results
    }
    func addUserData(object: LoginModel)   {
        if getUserDataFromDB().count > 0 {
            deleteAllUserDataFromDatabase()
        }
        try! realm.write {
            realm.add(object, update: .modified)
        }

    }
    func deleteAllUserDataFromDatabase()  {
        try!   realm.write {
            realm.deleteAll()
        }
    }
    func deleteUserFromDb(object: GroupModel)   {
        try!   realm.write {
            realm.delete(object)
        }
    }
    
    // Group Data
    func getGroupDataFromDB() ->   Results<GroupModel> {
          let results: Results<GroupModel> =   realm.objects(GroupModel.self)
          return results
      }
      func addGroupData(object: GroupModel)   {
          let grouprealm = try! Realm()
          try! grouprealm.write {
              grouprealm.add(object, update: .modified)
          }
      }
      func deleteAllGroupDataFromDatabase()  {
          try!   realm.write {
              realm.deleteAll()
          }
      }
      func deleteGroupFromDb(object: LoginModel)   {
          try!   realm.write {
              realm.delete(object)
          }
      }
    
    // Group MemberData
    func getGroupMemeberDataFromDB() ->   Results<GroupMemberModel> {
          let grouprealm = try! Realm()
          let results: Results<GroupMemberModel> =   grouprealm.objects(GroupMemberModel.self)
          return results
      }
      func addGroupMemeberData(object: GroupMemberModel)   {
          let grouprealm = try! Realm()
          try! grouprealm.write {
              grouprealm.add(object, update: .modified)
          }
      }
      func deleteAllMemebersFromDatabase()  {
          try!   realm.write {
              realm.deleteAll()
          }
      }
      func deleteMemeberFromDb(object: GroupMemberModel)   {
          try!   realm.write {
              realm.delete(object)
          }
      }
}
