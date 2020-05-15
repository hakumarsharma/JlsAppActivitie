//
//  UserServiceAPI.swift
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
import UIKit
import RealmSwift

class UserService {
    public static let shared = UserService()
    private init() {}
    
    // Api to generate registration token
    func generateRegistartionTokenwith (generateTokenUrl: URL, parameters: [String : Any], completion: @escaping (Result<UserModel, Error>) -> Void) -> Void  {
        
        let networkManager = NetworkManager.init(url: generateTokenUrl)
        let request = networkManager.buildRequest(method: NetworkManager.Method.post, parameters: parameters)
        networkManager.sendRequest(request: request) { (result) in
            switch result {
            case .success(_, let data) :
                do {
                    if let resultdata = data {
                        if let json = try JSONSerialization.jsonObject(with: resultdata) as? [String : Any] {
                            let data = try JSONSerialization.data(withJSONObject: json, options: [])
                            if let string = String(data: data, encoding: String.Encoding.utf8) {
                                let jsonData = string.data(using: .utf8)!
                                let logindata = try! JSONDecoder().decode(UserModel.self, from: jsonData)
                                completion(.success(logindata))
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
    
    // Api to verify registration token
    func verifyRegistartionTokenwith(verifyTokenUrl: URL, parameters: [String : Any], completion: @escaping (Result<UserModel, Error>) -> Void) -> Void{
        
        let networkManager = NetworkManager.init(url: verifyTokenUrl)
        let request = networkManager.buildRequest(method: NetworkManager.Method.post, parameters: parameters)
        networkManager.sendRequest(request: request) { (result) in
            switch result {
            case .success(_, let data) :
                do {
                    if let resultdata = data {
                        if let json = try JSONSerialization.jsonObject(with: resultdata) as? [String : Any] {
                            let data = try JSONSerialization.data(withJSONObject: json, options: [])
                            if let string = String(data: data, encoding: String.Encoding.utf8) {
                                let jsonData = string.data(using: .utf8)!
                                let logindata = try! JSONDecoder().decode(UserModel.self, from: jsonData)
                                completion(.success(logindata))
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
    
    // Api to register
    func registerUser(registerTokenUrl: URL, parameters: [String : Any], completion: @escaping (Result<UserModel, Error>) -> Void) -> Void{
        
        let networkManager = NetworkManager.init(url: registerTokenUrl)
        let request = networkManager.buildRequest(method: NetworkManager.Method.post, parameters: parameters)
        networkManager.sendRequest(request: request) { (result) in
            switch result {
            case .success(_, let data) :
                do {
                    if let resultdata = data {
                        if let json = try JSONSerialization.jsonObject(with: resultdata) as? [String : Any] {
                            let data = try JSONSerialization.data(withJSONObject: json, options: [])
                            if let string = String(data: data, encoding: String.Encoding.utf8) {
                                let jsonData = string.data(using: .utf8)!
                                let userData = try! JSONDecoder().decode(UserModel.self, from: jsonData)
                                completion(.success(userData))
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
    
    // Api to check login data
    func loginRequest(with loginurl: URL, parameters: [String : Any], completion: @escaping (Result<LoginModel, Error>) -> Void) -> Void {
        
        let networkManager = NetworkManager.init(url: loginurl)
        let request = networkManager.buildRequest(method: NetworkManager.Method.post, parameters: parameters)
        networkManager.sendRequest(request: request) { (result) in
            switch result {
            case .success(_, let data) :
                do {
                    if let resultdata = data {
                        if let json = try JSONSerialization.jsonObject(with: resultdata) as? [String : Any] {
                            let data = try JSONSerialization.data(withJSONObject: json, options: [])
                            if let string = String(data: data, encoding: String.Encoding.utf8) {
                                let jsonData = string.data(using: .utf8)!
                                let logindata = try! JSONDecoder().decode(LoginModel.self, from: jsonData)
                                if var loginData = RealmManager.sharedInstance.getUserDataFromDB().first {
                                    let realm = try! Realm()
                                    try! realm.write {
                                         loginData = logindata
                                    }
                                    RealmManager.sharedInstance.addUserData(object: loginData)
                                }else {
                                RealmManager.sharedInstance.addUserData(object: logindata)
                                }
                                completion(.success(logindata))
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

