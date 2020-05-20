//
//  DeviceService.swift
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

class DeviceService {
    public static let shared = DeviceService()
    private init() {}
    
    // Api to add device
    func addAndGetDeviceDetails(with deviceUrl: URL, parameters: [String : Any], completion: @escaping(Result<DeviceModel, Error>) -> Void) -> Void {
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
                                RealmManager.sharedInstance.addDeviceData(object: devicedata)
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
                if error == NetworkManager.ErrorType.MobileNumberAlreadyExists {
                let deviceData = DeviceModel()
                    deviceData.code = NetworkManager.ErrorType.MobileNumberAlreadyExists.hashValue
                    deviceData.message = ""
                RealmManager.sharedInstance.addDeviceData(object: deviceData)
                }
                print(error)
            }
        }
        
    }

    // Api to get location details
      func getDeviceLocationDetails(with deviceUrl: URL, completion: @escaping(Result<LocationModel, Error>) -> Void) -> Void {
          let networkManager = NetworkManager.init(url: deviceUrl)
          let request = networkManager.buildRequest(method: NetworkManager.Method.get)
          networkManager.sendRequest(request: request) { (result) in
              switch result {
              case .success(_, let data) :
                  do {
                      if let resultdata = data {
                          if let json = try JSONSerialization.jsonObject(with: resultdata) as? [String : Any] {
                              let data = try JSONSerialization.data(withJSONObject: json, options: [])
                              if let string = String(data: data, encoding: String.Encoding.utf8) {
                                  let jsonData = string.data(using: .utf8)!
                                  let locationdata = try! JSONDecoder().decode(LocationModel.self, from: jsonData)
                                  completion(.success(locationdata))
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

