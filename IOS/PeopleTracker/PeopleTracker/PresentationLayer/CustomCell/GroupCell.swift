//
//  GroupCell.swift
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
class GroupCell: UITableViewCell {
    @IBOutlet weak var name        : UILabel!
    @IBOutlet weak var phoneNumber : UILabel!
    @IBOutlet weak var userImg     : UIImageView!
    @IBOutlet weak var status: UILabel!
    @IBOutlet weak var bgView: UIView!
    @IBOutlet weak var customView: UIView!
    
    func setUserData(memberData : GroupMemberData) {
        customView.layer.cornerRadius           = 10
        bgView.layer.borderWidth = 4.0
        bgView.layer.masksToBounds = false
        bgView.layer.cornerRadius = bgView.frame.size.width / 2
        bgView.clipsToBounds = true
        if memberData.status == "approved" {
        bgView.layer.borderColor = UIColor(red: 52/255.0, green: 199/255.0, blue: 89/255.0, alpha: 1.0).cgColor
        } else if memberData.status == "pending" {
        bgView.layer.borderColor = UIColor(red: 255/255.0, green: 165/255.0, blue: 0/255.0, alpha: 1.0).cgColor
        } else {
            bgView.layer.borderColor = UIColor.lightGray.cgColor
        }
        
        self.userImg = Utils.shared.createCirculatImage(imageView: userImg)
        self.name.text = memberData.name
        self.phoneNumber.text = memberData.phone
        self.status.text = "Status : " + memberData.status.capitalizingFirstLetter()
    }
}
