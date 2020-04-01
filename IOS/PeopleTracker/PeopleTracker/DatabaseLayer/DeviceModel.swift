//
//  DeviceModel.swift
//  PeopleTracker
//
//  Created by Apple on 01/04/20.
//  Copyright © 2020 Apple. All rights reserved.
//

import Foundation

public struct DeviceModel : Codable {
    
    let code : Int
    let message : String
    
    private enum CodingKeys : String, CodingKey {
        case code,message
    }
    
}
