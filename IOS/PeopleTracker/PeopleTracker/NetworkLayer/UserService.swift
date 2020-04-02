//
//  UserServiceAPI.swift
//  PeopleTracker
//
//  Created by Apple on 24/03/20.
//  Copyright © 2020 Apple. All rights reserved.
//

import Foundation

class UserService {
    public static let shared = UserService()
    private init() {}
    
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

