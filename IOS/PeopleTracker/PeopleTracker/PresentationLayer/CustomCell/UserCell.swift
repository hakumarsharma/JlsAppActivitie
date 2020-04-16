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
class UserCell: UITableViewCell {
       
    @IBOutlet weak var userIcon             : UIImageView!
    @IBOutlet weak var name                 : UILabel!
    @IBOutlet weak var phoneNumber          : UILabel!
    @IBOutlet weak var optionsButton        : UIButton!
    @IBOutlet weak var checkBoxButton       : UIButton!
    @IBOutlet weak var consentstatusColor   : UILabel!
    @IBOutlet weak var requestConsentButton : UIButton!
    
    func setData(deviceData : DeviceData){
        self.name.text        = deviceData.name
        self.phoneNumber.text = deviceData.phone
        self.checkBoxButton.setBackgroundImage(UIImage(named: "ic_checkempty"), for: .normal)
        self.checkBoxButton.setBackgroundImage(UIImage(named: "ic_checkmark"), for: .selected)    
    }
   
}
