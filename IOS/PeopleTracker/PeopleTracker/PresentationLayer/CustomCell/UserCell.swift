//
//  UserCell.swift
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

protocol UserCellDelegate {
    func checkMarkClicked(cell: UserCell)
    func editOptionsClicked(cell: UserCell)
    func requestConsentClicked(cell: UserCell)
}

class UserCell: UITableViewCell {
    @IBOutlet weak var userIcon             : UIImageView!
    @IBOutlet weak var name                 : UILabel!
    @IBOutlet weak var phoneNumber          : UILabel!
    @IBOutlet weak var optionsButton        : UIButton!
    @IBOutlet weak var checkBoxButton       : UIButton!
    @IBOutlet weak var consentstatusColor   : UILabel!
    @IBOutlet weak var requestConsentButton : UIButton!
    @IBOutlet weak var customView           : UIView!
    @IBOutlet weak var ImgBgView: UIView!
    
    var usercelldelegate                    : UserCellDelegate?
    
    func setGroupData(groupData : GroupListData){
        customView.layer.cornerRadius           = 10
        requestConsentButton.layer.cornerRadius = 10
        self.ImgBgView = Utils.shared.createCircularView(view: ImgBgView, borderColor:  UIColor.black, borderWidth: 2.0)
        self.userIcon = Utils.shared.createCircularImage(imageView: self.userIcon, borderColor:  UIColor.clear, borderWidth: 1.0)
        self.checkBoxButton.setBackgroundImage(UIImage(named: "ic_checkempty"), for: .normal)
        self.checkBoxButton.setBackgroundImage(UIImage(named: "ic_checkmark"), for: .selected)
        self.requestConsentButton.isHidden = false
        self.optionsButton.isHidden = false
        var isIndividual = false
        let groupName = groupData.name.components(separatedBy: "+")
        if groupName.count == 2 && groupName[0] == Constants.AddDeviceConstants.Individual {
            self.name.text = groupData.groupMember.first?.memberName
            self.phoneNumber.text = groupData.groupMember.first?.memberPhone
            self.checkBoxButton.isHidden = false
            self.setUserIcon(val: groupName[1])
            isIndividual = true
        } else {
            self.name.text = groupData.name
            self.checkBoxButton.isHidden = true
            let memebersArr = groupData.groupMember.filter { $0.memberStatus == Utils.GroupStatus.isApproved.rawValue || $0.memberStatus == Utils.GroupStatus.isPending.rawValue}
            self.phoneNumber.text = "Members : " + String(memebersArr.count)
            self.userIcon.image = UIImage(named: "group")
        }
        
        
        if groupData.groupCreatedBy != Utils.shared.getUserId() {
            self.requestConsentButton.isHidden = true
            self.optionsButton.isHidden = true
            self.consentstatusColor.backgroundColor = UIColor.Consent.ConsentApproved
        } else {
            if isIndividual {
//                self.name.text = groupData.groupOwner?.ownerName ?? ""
//                self.phoneNumber.text = groupData.groupOwner?.ownerPhone ?? ""
                self.setConsentStatusForIndividual(groupData : groupData)
            } else {
                let memebersArr = groupData.groupMember.filter { $0.memberStatus == Utils.GroupStatus.isApproved.rawValue || $0.memberStatus == Utils.GroupStatus.isPending.rawValue}
                if memebersArr.count > 0 {
                   self.setConsentStatusForGroup(groupData : groupData)
                } else {
                    self.requestConsentButton.setTitle(Constants.HomScreenConstants.RequestConsent, for: .normal)
                    self.consentstatusColor.backgroundColor = UIColor.Consent.RequestConsent
                }
            }
        }
    }
    
    // set icon based device type
    func setUserIcon(val : String){
        self.userIcon = Utils.shared.createCircularImage(imageView: self.userIcon, borderColor: UIColor.clear, borderWidth: 3.0)
        switch val {
        case Constants.AddDeviceConstants.PeopleTracker:
            self.userIcon.image = UIImage(named: "user4")
        case Constants.AddDeviceConstants.PetTracker:
            self.userIcon.image = UIImage(named: "pet")
        case Constants.AddDeviceConstants.VehicleTracker:
            self.userIcon.image = UIImage(named: "vehicle")
        case Constants.AddDeviceConstants.AdultTracker:
            self.userIcon.image = UIImage(named: "adult")
        case Constants.AddDeviceConstants.KidTracker:
            self.userIcon.image = UIImage(named: "kid")
        default:
            self.userIcon.image = UIImage(named: "group")
        }
    }
    
    // Consent for individual will be based on group status and member status
    func setConsentStatusForIndividual(groupData : GroupListData){
        if groupData.status == Utils.GroupStatus.isCompleted.rawValue {
            self.requestConsentButton.setTitle(Constants.HomScreenConstants.RequestConsent, for: .normal)
            self.consentstatusColor.backgroundColor = UIColor.Consent.RequestConsent
        } else {
            if (groupData.groupMember.first?.memberStatus == Utils.GroupStatus.isApproved.rawValue) {
                self.requestConsentButton.setTitle(Constants.HomScreenConstants.ConsentApproved, for: .normal)
                self.consentstatusColor.backgroundColor = UIColor.Consent.ConsentApproved
            } else {
                self.requestConsentButton.setTitle(Constants.HomScreenConstants.ConsentPending, for: .normal)
                self.consentstatusColor.backgroundColor = UIColor.Consent.ConsentPending
            }
        }
    }
    
    // Consent for group will be based group status
    func setConsentStatusForGroup(groupData : GroupListData){
        switch groupData.status {
        case Utils.GroupStatus.isActive.rawValue:
            self.requestConsentButton.setTitle(Constants.HomScreenConstants.ConsentSent, for: .normal)
            self.consentstatusColor.backgroundColor = UIColor.Consent.ConsentApproved
        case Utils.GroupStatus.isScheduled.rawValue:
            self.requestConsentButton.setTitle(Constants.HomScreenConstants.ConsentSent, for: .normal)
            self.consentstatusColor.backgroundColor = UIColor.Consent.ConsentSent
        case Utils.GroupStatus.isCompleted.rawValue:
            self.requestConsentButton.setTitle(Constants.HomScreenConstants.RequestConsent, for: .normal)
            self.consentstatusColor.backgroundColor = UIColor.Consent.RequestConsent
        default:
            self.requestConsentButton.setTitle(Constants.HomScreenConstants.RequestConsent, for: .normal)
            self.consentstatusColor.backgroundColor = UIColor.Consent.RequestConsent
        }
    }
    
    @IBAction func checkMarkButtonAction(_ sender: Any) {
        usercelldelegate?.checkMarkClicked(cell: self)
    }
    
    @IBAction func editButtonAction(_ sender: Any) {
        usercelldelegate?.editOptionsClicked(cell: self)
    }
    
    @IBAction func requestConsentButtonAction(_ sender: Any) {
        usercelldelegate?.requestConsentClicked(cell: self)
    }
}
