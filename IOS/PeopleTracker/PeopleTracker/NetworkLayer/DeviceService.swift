//
//  DeviceService.swift
//  PeopleTracker
//
//  Created by Apple on 01/04/20.
//  Copyright © 2020 Apple. All rights reserved.
//


import Foundation

class DeviceService {
    public static let shared = DeviceService()
    private init() {}
    
    // Api to add device
    func addDevice(with deviceUrl: URL, parameters: [String : Any], completion: @escaping(Result<DeviceModel, Error>) -> Void) -> Void {
        let networkManager = NetworkManager.init(url: deviceUrl)
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
                                let devicedata = try! JSONDecoder().decode(DeviceModel.self, from: jsonData)
                                completion(.success(devicedata))
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

