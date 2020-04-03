//
//  StringExtensions.swift
//  PeopleTracker
//
//  Created by Apple on 01/04/20.
//  Copyright © 2020 Apple. All rights reserved.
//

import UIKit

extension String {
   // To check Email
   var isValidEmail: Bool {
      let regularExpressionForEmail = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}"
      let testEmail = NSPredicate(format:"SELF MATCHES %@", regularExpressionForEmail)
      return testEmail.evaluate(with: self)
   }
    // To validate Phone number
   var isValidPhone: Bool {
      let regularExpressionForPhone = "^[6-9][0-9]{9}$"
      let testPhone = NSPredicate(format:"SELF MATCHES %@", regularExpressionForPhone)
      return testPhone.evaluate(with: self)
   }
    
   // To validate IMEI number
     var isValidImei: Bool {
        let regularExpressionForPhone = "^\\d{15}$"
        let testPhone = NSPredicate(format:"SELF MATCHES %@", regularExpressionForPhone)
        return testPhone.evaluate(with: self)
     }
}
