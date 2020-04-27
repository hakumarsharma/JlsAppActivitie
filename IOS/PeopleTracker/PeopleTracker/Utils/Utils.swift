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
    
    enum GroupStatus : String {
        case isActive = "active"
        case isClosed = "closed"
        case isScheduled = "scheduled"
    }
    
    func handleError(error : NetworkManager.ErrorType) -> String {
        if error == NetworkManager.ErrorType.Unauthorized {
            return Constants.ErrorMessage.Unauthorized
        } else if error == NetworkManager.ErrorType.SomethingWentWrong {
            return Constants.ErrorMessage.Somethingwentwrong
        } else if error == NetworkManager.ErrorType.ExceededGroupLimit {
            return Constants.ErrorMessage.ExceededGroupLimit
        }else {
            return Constants.ErrorMessage.Somethingwentwrong
        }
    }
    
    func currentDateToString() -> String {
        let date = Date()
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ssZ"
        return formatter.string(from: date)
    }
    
    func createCirculatImage(imageView : UIImageView) -> UIImageView{
        imageView.layer.borderWidth = 3.0
        imageView.layer.masksToBounds = false
        imageView.layer.borderColor = UIColor.clear.cgColor
        imageView.layer.cornerRadius = imageView.frame.size.width / 2
        imageView.clipsToBounds = true
        return imageView
    }
    
    func getUgsToken() -> String {
        if RealmManager.sharedInstance.getUserDataFromDB().count > 0 {
            let loginData = RealmManager.sharedInstance.getUserDataFromDB().first
            return loginData?.ugsToken ?? ""
        }
        return ""
    }
    
    func getUserId() -> String {
        
        if RealmManager.sharedInstance.getUserDataFromDB().count > 0 {
            let loginData = RealmManager.sharedInstance.getUserDataFromDB().first
            return loginData?.user?.userId ?? ""
        }
        return ""
    }
    
    func getUgsTokenExpiryTime() -> Double {
        if RealmManager.sharedInstance.getUserDataFromDB().count > 0 {
            let loginData = RealmManager.sharedInstance.getUserDataFromDB().first
            return loginData?.ugsTokenexpiry ?? 0
        }
        return 0
    }
}

extension UIViewController {
    func ShowALert(title: String){
        let alert = UIAlertController(title: Constants.AlertConstants.Alert, message: title, preferredStyle: UIAlertController.Style.alert)
        alert.addAction(UIAlertAction(title: Constants.AlertConstants.OkButton, style: UIAlertAction.Style.default, handler: nil))
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
