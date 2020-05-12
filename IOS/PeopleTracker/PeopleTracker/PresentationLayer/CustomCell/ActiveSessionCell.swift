//
//  ActiveSessionCell.swift
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



import UIKit

protocol ActiveSessionDelegate {
    func optionsClicked(cell: ActiveSessionCell)
}
class ActiveSessionCell: UITableViewCell {
    @IBOutlet weak var avatar: UIImageView!
    @IBOutlet weak var nameLbl: UILabel!
    @IBOutlet weak var mobileNumberLbl: UILabel!
    @IBOutlet weak var optionsBtn: UIButton!
    @IBOutlet weak var customView: UIView!
    @IBOutlet weak var ImgBgView: UIView!
    @IBOutlet weak var durationLbl: UILabel!
    @IBOutlet weak var expiryTimeLbl: UILabel!
    
    var activeSessionDelegate   : ActiveSessionDelegate?
    
    func setGroupData(groupData : GroupListData){
        customView.layer.cornerRadius           = 10
        self.ImgBgView = Utils.shared.createCirculatView(view: ImgBgView, borderColor:  UIColor.lightGray, borderWidth: 3.0)
        self.optionsBtn.isHidden = true
        let groupName = groupData.name.components(separatedBy: "+")
        if groupName.count == 2 && groupName[0] == Constants.AddDeviceConstants.Individual {
            self.nameLbl.text = groupData.groupMember.first?.memberName
            self.mobileNumberLbl.text = groupData.groupMember.first?.memberPhone
            self.setUserIcon(val: groupName[1])
            self.optionsBtn.isHidden = false
        } else {
            self.nameLbl.text = groupData.name
            let memebersArr = groupData.groupMember.filter { $0.memberStatus == Utils.GroupStatus.isApproved.rawValue || $0.memberStatus == Utils.GroupStatus.isPending.rawValue}
            self.mobileNumberLbl.text = "Members : " + String(memebersArr.count)
            self.avatar.image = UIImage(named: "group")
        }
        self.durationLbl.text = "Tracking Duration : " + self.getTrackingDuration(epochFromTime: groupData.groupSession!.from!, epochToTime:  groupData.groupSession!.to!)
        self.expiryTimeLbl.text = "Tracking Expires In : " + self.getTrackingExpiresIn( epochToTime:  groupData.groupSession!.to!)
        
    }
    
    func getTrackingDuration(epochFromTime : Int64, epochToTime : Int64) -> String {
       let duration = Utils.shared.getDuration(epochFromTime: epochFromTime, epochToTime: epochToTime)
        if duration.day == 0 {
            if duration.hour == 0 {
                if duration.minute != 0 {
                    return String(duration.minute) + "min"
                }
            } else {
                return String(duration.hour) + "hr"
            }
        } else {
            return String(duration.day) + "days"
        }
        return ""
    }
    
    func getTrackingExpiresIn(epochToTime : Int64) -> String {
        let currentDate = Utils.shared.getEpochTime(val:0)
        let duration = Utils.shared.getDuration(epochFromTime: currentDate, epochToTime: epochToTime)
        if duration.day == 0 {
                   if duration.hour == 0 {
                       if duration.minute != 0 {
                           return String(duration.minute) + "min"
                       }
                   } else {
                       return String(duration.hour) + "hr"
                   }
               } else {
                   return String(duration.day) + "days"
               }
        return ""
        
    }
    // set icon based device type
    func setUserIcon(val : String){
        self.avatar = Utils.shared.createCirculatImage(imageView: self.avatar, borderColor: UIColor.clear, borderWidth: 3.0)
        switch val {
        case Constants.AddDeviceConstants.PeopleTracker:
            self.avatar.image = UIImage(named: "user4")
        case Constants.AddDeviceConstants.PetTracker:
            self.avatar.image = UIImage(named: "pet")
        case Constants.AddDeviceConstants.VehicleTracker:
            self.avatar.image = UIImage(named: "vehicle")
        case Constants.AddDeviceConstants.AdultTracker:
            self.avatar.image = UIImage(named: "adult")
        case Constants.AddDeviceConstants.KidTracker:
            self.avatar.image = UIImage(named: "kid")
        default:
            self.avatar.image = UIImage(named: "group")
        }
    }
    
    
    @IBAction func IBOutletweakvaroptionsButtonActionUIButton(_ sender: Any) {
        activeSessionDelegate?.optionsClicked(cell: self)
    }
    
}
