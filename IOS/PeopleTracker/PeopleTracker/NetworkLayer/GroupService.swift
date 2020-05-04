//
//  GroupService.swift
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

class GroupService {
    public static let shared = GroupService()
    private init() {}
    
    let accessKey = "bearer " + Utils.shared.getUgsToken()
    
    
    // Api to create a group
    func createGroup(createGroupUrl: URL, parameters: [String : Any], completion: @escaping (Result<GroupModel, Error>) -> Void) -> Void  {
        
        let networkManager = NetworkManager.init(url: createGroupUrl)
        let request = networkManager.buildRequest(method: NetworkManager.Method.post, accessKey: accessKey, parameters: parameters)
        networkManager.sendRequest(request: request) { (result) in
            switch result {
            case .success(_, let data) :
                do {
                    if let resultdata = data {
                        if let json = try JSONSerialization.jsonObject(with: resultdata) as? [String : Any] {
                            let data = try JSONSerialization.data(withJSONObject: json, options: [])
                            if let string = String(data: data, encoding: String.Encoding.utf8) {
                                let jsonData = string.data(using: .utf8)!
                                let groupdata = try! JSONDecoder().decode(GroupModel.self, from: jsonData)
                                RealmManager.sharedInstance.addGroupData(object: groupdata)
                                completion(.success(groupdata))
                            }
                        }
                    }
                }
                catch {
                    let msg = error.localizedDescription
                    completion(.failure(error))
                    print(msg)
                }
            case .failure(let error):
                completion(.failure(error))
                print(error)
            }
        }
        
    }
    
    // Api to get group list
    func getGroupListData(groupListUrl: URL, completion: @escaping (Result<GroupListModel, Error>) -> Void) -> Void  {
        
        let networkManager = NetworkManager.init(url: groupListUrl)
        let request = networkManager.buildRequest(method: NetworkManager.Method.get, accessKey: accessKey)
        networkManager.sendRequest(request: request) { (result) in
            switch result {
            case .success(_, let data) :
                do {
                    if let resultdata = data {
                        if let json = try JSONSerialization.jsonObject(with: resultdata) as? [String : Any] {
                            let data = try JSONSerialization.data(withJSONObject: json, options: [])
                            if let string = String(data: data, encoding: String.Encoding.utf8) {
                                let jsonData = string.data(using: .utf8)!
                                let groupListdata = try! JSONDecoder().decode(GroupListModel.self, from: jsonData)
                                completion(.success(groupListdata))
                            }
                        }
                    }
                }
                catch {
                    let msg = error.localizedDescription
                    completion(.failure(error))
                    print(msg)
                }
            case .failure(let error):
                completion(.failure(error))
                print(error)
            }
        }
        
    }
    
    // Api to update a group
    func updateGroup(updateGroupUrl: URL, parameters: [String : Any], completion: @escaping (Result<GroupModel, Error>) -> Void) -> Void  {
        
        let networkManager = NetworkManager.init(url: updateGroupUrl)
        let request = networkManager.buildRequest(method: NetworkManager.Method.put, accessKey: accessKey, parameters: parameters)
        networkManager.sendRequest(request: request) { (result) in
            switch result {
            case .success(_, let data) :
                do {
                    if let resultdata = data {
                        if let json = try JSONSerialization.jsonObject(with: resultdata) as? [String : Any] {
                            let data = try JSONSerialization.data(withJSONObject: json, options: [])
                            if let string = String(data: data, encoding: String.Encoding.utf8) {
                                let jsonData = string.data(using: .utf8)!
                                let groupdata = try! JSONDecoder().decode(GroupModel.self, from: jsonData)
                                completion(.success(groupdata))
                            }
                        }
                    }
                }
                catch {
                    let msg = error.localizedDescription
                    completion(.failure(error))
                    print(msg)
                }
            case .failure(let error):
                completion(.failure(error))
                print(error)
            }
        }
        
    }
    
    
    // Api to delete a group
    func deleteGroup(deleteGroupUrl: URL, completion: @escaping (Result<GroupModel, Error>) -> Void) -> Void  {
        
        let networkManager = NetworkManager.init(url: deleteGroupUrl)
        let request = networkManager.buildRequest(method: NetworkManager.Method.delete, accessKey: accessKey)
        networkManager.sendRequest(request: request) { (result) in
            switch result {
            case .success(_, let data) :
                do {
                    if let resultdata = data {
                        if let json = try JSONSerialization.jsonObject(with: resultdata) as? [String : Any] {
                            let data = try JSONSerialization.data(withJSONObject: json, options: [])
                            if let string = String(data: data, encoding: String.Encoding.utf8) {
                                let jsonData = string.data(using: .utf8)!
                                let groupdata = try! JSONDecoder().decode(GroupModel.self, from: jsonData)
                                completion(.success(groupdata))
                            }
                        }
                    }
                }
                catch {
                    let msg = error.localizedDescription
                    completion(.failure(error))
                    print(msg)
                }
            case .failure(let error):
                completion(.failure(error))
                print(error)
            }
        }
        
    }
    
    // Api to add member in group
    func addMemberToGroup(addMemberInGroupUrl: URL, parameters: [String : Any], completion: @escaping (Result<GroupMemberModel, Error>) -> Void) -> Void  {
        
        let networkManager = NetworkManager.init(url: addMemberInGroupUrl)
        let request = networkManager.buildRequest(method: NetworkManager.Method.post, accessKey: accessKey,parameters: parameters)
        networkManager.sendRequest(request: request) { (result) in
            switch result {
            case .success(_, let data) :
                do {
                    if let resultdata = data {
                        if let json = try JSONSerialization.jsonObject(with: resultdata) as? [String : Any] {
                            let data = try JSONSerialization.data(withJSONObject: json, options: [])
                            if let string = String(data: data, encoding: String.Encoding.utf8) {
                                let jsonData = string.data(using: .utf8)!
                                let memberData = try! JSONDecoder().decode(GroupMemberModel.self, from: jsonData)
                                RealmManager.sharedInstance.addGroupMemeberData(object: memberData)
                                completion(.success(memberData))
                            }
                        }
                    }
                }
                catch {
                    let msg = error.localizedDescription
                    completion(.failure(error))
                    print(msg)
                }
            case .failure(let error):
                completion(.failure(error))
                print(error)
            }
        }
        
    }
    
    // Api to get members in group
       func getMembersFromGroup(getMembersInGroupUrl: URL, completion: @escaping (Result<GroupMemberModel, Error>) -> Void) -> Void  {
           
           let networkManager = NetworkManager.init(url: getMembersInGroupUrl)
           let request = networkManager.buildRequest(method: NetworkManager.Method.get, accessKey: accessKey)
           networkManager.sendRequest(request: request) { (result) in
               switch result {
               case .success(_, let data) :
                   do {
                       if let resultdata = data {
                           if let json = try JSONSerialization.jsonObject(with: resultdata) as? [String : Any] {
                               let data = try JSONSerialization.data(withJSONObject: json, options: [])
                               if let string = String(data: data, encoding: String.Encoding.utf8) {
                                   let jsonData = string.data(using: .utf8)!
                                   let groupMemberData = try! JSONDecoder().decode(GroupMemberModel.self, from: jsonData)
                                   completion(.success(groupMemberData))
                               }
                           }
                       }
                   }
                   catch {
                       let msg = error.localizedDescription
                       completion(.failure(error))
                       print(msg)
                   }
               case .failure(let error):
                   completion(.failure(error))
                   print(error)
               }
           }
           
       }
    
    // Api to get single member data from group
    func getMemberDataFromGroup(getMemberDataUrl: URL, completion: @escaping (Result<GroupMemberModel, Error>) -> Void) -> Void  {
        
        let networkManager = NetworkManager.init(url: getMemberDataUrl)
        let request = networkManager.buildRequest(method: NetworkManager.Method.get, accessKey: accessKey)
        networkManager.sendRequest(request: request) { (result) in
            switch result {
            case .success(_, let data) :
                do {
                    if let resultdata = data {
                        if let json = try JSONSerialization.jsonObject(with: resultdata) as? [String : Any] {
                            let data = try JSONSerialization.data(withJSONObject: json, options: [])
                            if let string = String(data: data, encoding: String.Encoding.utf8) {
                                let jsonData = string.data(using: .utf8)!
                                let groupMemberData = try! JSONDecoder().decode(GroupMemberModel.self, from: jsonData)
                                completion(.success(groupMemberData))
                            }
                        }
                    }
                }
                catch {
                    let msg = error.localizedDescription
                    completion(.failure(error))
                    print(msg)
                }
            case .failure(let error):
                completion(.failure(error))
                print(error)
            }
        }
        
    }
}
