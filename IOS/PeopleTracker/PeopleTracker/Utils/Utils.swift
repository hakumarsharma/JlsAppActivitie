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
        case isActive    = "active"
        case isClosed    = "closed"
        case isScheduled = "scheduled"
        case isCompleted = "completed"
        case isRemoved   = "removed"
        case isPending   = "pending"
        case isApproved  = "approved"
        case isExited    = "exited"
    }
    
    // Server error handling and displaying of error message
    func handleError(error : NetworkManager.ErrorType) -> String {
        if error == NetworkManager.ErrorType.Unauthorized {
            return Constants.ErrorMessage.Unauthorized
        } else if error == NetworkManager.ErrorType.SomethingWentWrong {
            return Constants.ErrorMessage.Somethingwentwrong
        } else if error == NetworkManager.ErrorType.MobileNumberAlreadyExists {
            return Constants.ErrorMessage.MobileNumberExists
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
    
    // creates a circular imageview
    func createCircularImage(imageView : UIImageView, borderColor : UIColor, borderWidth : CGFloat) -> UIImageView{
        imageView.layer.borderWidth = borderWidth
        imageView.layer.masksToBounds = false
        imageView.layer.borderColor = borderColor.cgColor
        imageView.layer.cornerRadius = imageView.frame.size.width / 2
        imageView.clipsToBounds = true
        return imageView
    }
    
    // creates a circular view
    func createCircularView(view : UIView, borderColor : UIColor, borderWidth : CGFloat) -> UIView{
        view.layer.borderWidth = borderWidth
        view.layer.masksToBounds = false
        view.layer.borderColor = borderColor.cgColor
        view.layer.cornerRadius = view.frame.size.width / 2
        view.clipsToBounds = true
        return view
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
    
    func getUgsTokenExpiryTime() -> Int64 {
        if RealmManager.sharedInstance.getUserDataFromDB().count > 0 {
            let loginData = RealmManager.sharedInstance.getUserDataFromDB().first
            return loginData?.ugsTokenexpiry ?? 0
        }
        return 0
    }
    // checking if ugs token is expired or not
    func isUgsTokenExpired() -> Bool {
        if self.getUgsTokenExpiryTime() != 0 && self.getUgsTokenExpiryTime() > self.getTokenEpochTime(val: 5){
            return false
        }
        return true
    }
    // To get current epoch time , passing to 5 minutes prior than current to call api back before it gets expired
    func getTokenEpochTime(val : Int) -> Int64 {
        let newData = NSCalendar.current.date(byAdding: .minute, value: val, to: NSDate() as Date)
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "MMM dd yyyy HH:mm"
        let dateStr = dateFormatter.string(from: newData!)
        let myTimeStamp = Int64(floor(dateFormatter.date(from: dateStr)!.timeIntervalSince1970))
        return myTimeStamp
    }
    
    func getFromEpochTime() -> Int64 {
        return self.getEpochTime(val: 1)
    }
    
    func getToEpochTime() -> Int64 {
        return self.getEpochTime(val: 5)
    }
    
    // get epoch time to pass during creation of groups
    func getEpochTime(val : Int) -> Int64 {
        let newData = NSCalendar.current.date(byAdding: .minute, value: val, to: NSDate() as Date)
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "MMM dd yyyy HH:mm:ss.SSS zzz"
        let dateStr = dateFormatter.string(from: newData!)
        let myTimeStamp = Int64(floor(dateFormatter.date(from: dateStr)!.timeIntervalSince1970 * 1000))
        return myTimeStamp
    }
    
    func getCurrentDate(val : Int) -> String {
        let newData = NSCalendar.current.date(byAdding: .minute, value: val, to: NSDate() as Date)
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ssZZZ"
        return dateFormatter.string(from: newData!)
    }
    // convert epoch to date time
    func getDateTimeFromEpochTime(epochTime : Int64) -> Date {
        let epochTime = TimeInterval(epochTime) / 1000
        let date = Date(timeIntervalSince1970: epochTime)
        return date
    }
    // To set duration and lapse time for tracking
    func getDuration(epochFromTime : Int64, epochToTime : Int64) -> (day: Int, hour: Int, minute : Int){
        let fromdate = Utils.shared.getDateTimeFromEpochTime(epochTime: epochFromTime)
        let todate = Utils.shared.getDateTimeFromEpochTime(epochTime: epochToTime)
        let components = Calendar.current.dateComponents([.day, .hour, .minute], from: fromdate, to: todate)
        return (components.day!, components.hour!, components.minute!)
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
