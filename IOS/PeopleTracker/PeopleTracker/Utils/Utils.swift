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
    
    func showActivityIndicator(){
        let container: UIView = UIView()
        container.frame = CGRect(x: 0, y: 0, width: 100, height: 100)
        container.backgroundColor = UIColor(red: 128.0/255.0, green: 128.0/255.0, blue: 128.0/255.0, alpha: 0.5)
        container.center = self.view.center
        container.layer.cornerRadius = 15
        container.tag = 101
        let activityView = UIActivityIndicatorView(style: .large)
        activityView.center = CGPoint(x: container.frame.size.width / 2, y: container.frame.size.height / 2)
        container.addSubview(activityView)
        self.view.addSubview(container)
        activityView.startAnimating()
    }
    
    func hideActivityIndicator () {
        for activityview in self.view.subviews {
            if activityview.tag == 101 {
                activityview.removeFromSuperview()
            }
        }
    }
    
}
