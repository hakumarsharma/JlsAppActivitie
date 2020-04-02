//
//  NetowrkManager.swift
//  PeopleTracker
//
//  Created by Apple on 31/03/20.
//  Copyright © 2020 Apple. All rights reserved.
//

import Foundation

struct NetworkManager {

   enum  Method : String {
        case get    = "GET"
        case post   = "POST"
        case put    = "PUT"
        case delete = "DELETE"
        
        var acceptsBody : Bool {
            switch self {
            case .post, .put:
                return true
            default:
                return false
            }
        }
    }
    
    enum QueryResult<Element> {
        case success(Element)
        case failure(ErrorType)
    }

    enum ErrorType: Int, Error {
        case NoInternet                     = -1
        case SomethingWentWrong             = 400
        case Unauthorized                   = 401
        case Forbidden                      = 403
        case NotFound                       = 404
        case DeviceAlreadyAssigned          = 409
    }


    typealias NetworkResult = QueryResult<(URLResponse, Data?)>
    
    let baseUrl : URL
    let session: URLSession
    typealias JSONDictionary = [String: Any]
    
    init(url : URL) {
        self.baseUrl = url
        self.session = URLSession.shared
    }
    
    func buildRequest(method: Method , path: String? = nil, accessKey : String? = nil ,queryItems: [URLQueryItem] = [], parameters: JSONDictionary? = nil) -> URLRequest {
        
        var url = self.baseUrl
        if let appendpath = path {
            url = self.baseUrl.appendingPathComponent(appendpath)
        }
        // Apply query parameters to url
        if !queryItems.isEmpty, var components = URLComponents(url: url, resolvingAgainstBaseURL: true) {
            components.queryItems = queryItems
            
            if let modifiedURL = components.url {
                url = modifiedURL
            }
        }
        // Set request url
        var request = URLRequest(url: url)
        request.httpMethod = method.rawValue
        if  let key = accessKey{
            request.addValue(key, forHTTPHeaderField: "Authorization")
        }
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("application/json", forHTTPHeaderField: "Accept")
        // set http body
        if let param = parameters, let body = try? JSONSerialization.data(withJSONObject: param, options: []) {
            request.httpBody = body
        }
        return request
    }
    
    func sendRequest(request : URLRequest, completion:  ((NetworkResult) -> Void)? = nil) {
        // Send request
        session.dataTask(with: request as URLRequest) { (data, response, error) in
            // Success
            if let httpResponse = response as? HTTPURLResponse {
                // success response code
                if(httpResponse.statusCode == 200) {
                    if let response = response {
                       completion?(.success((response, data)))
                        return
                    }
                } else if (httpResponse.statusCode == 401) {
                    completion?(.failure(ErrorType.Unauthorized))
                    return
                }  else {
                    completion?(.failure(ErrorType.SomethingWentWrong))
                    return
                }
            }
            completion?(.failure(ErrorType.SomethingWentWrong))
        }.resume()
    }
    
}

