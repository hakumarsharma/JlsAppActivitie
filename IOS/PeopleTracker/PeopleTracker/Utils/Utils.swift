//
//  Utils.swift
//  PeopleTracker
//
//  Created by Apple on 23/03/20.
//  Copyright © 2020 Apple. All rights reserved.
//

import UIKit

class Utils {
    static let shared = Utils()
    func handleError(error : NetworkManager.ErrorType) -> String {
        if error == NetworkManager.ErrorType.Unauthorized {
            return Constants.ErrorMessage.unauthorized
        } else if error == NetworkManager.ErrorType.SomethingWentWrong {
            return Constants.ErrorMessage.somethingwentwrong
        } else if error == NetworkManager.ErrorType.DeviceAlreadyAssigned {
            return Constants.ErrorMessage.deviceCanotBeAssigned
        }else {
            return Constants.ErrorMessage.somethingwentwrong
        }
    }
}

extension UIViewController {
    func ShowALert(title: String){
        let alert = UIAlertController(title: Constants.AlertConstants.alert, message: title, preferredStyle: UIAlertController.Style.alert)
        alert.addAction(UIAlertAction(title: Constants.AlertConstants.okButton, style: UIAlertAction.Style.default, handler: nil))
         self.present(alert, animated: true, completion: nil)
     }
    
}
