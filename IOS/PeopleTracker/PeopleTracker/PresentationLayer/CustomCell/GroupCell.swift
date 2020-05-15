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
protocol GroupCellDelegate {
    func deleteButtonClicked(cell: GroupCell)
}
class GroupCell: UITableViewCell  {
    
    @IBOutlet weak var name        : UILabel!
    @IBOutlet weak var phoneNumber : UILabel!
    @IBOutlet weak var userImg     : UIImageView!
    @IBOutlet weak var status: UILabel!
    @IBOutlet weak var bgView: UIView!
    @IBOutlet weak var customView: UIView!
    @IBOutlet weak var deleteButton: UIButton!
    var groupcellDelegate : GroupCellDelegate?
    
    func setUserData(memberData : GroupMemberData, groupData : GroupListData, imgStr : String) {
        customView.layer.cornerRadius = 10
        self.bgView  = Utils.shared.createCircularView(view: bgView, borderColor:  UIColor.clear, borderWidth: 3.0)
        self.userImg = Utils.shared.createCircularImage(imageView: self.userImg, borderColor: UIColor.clear, borderWidth: 3.0)
        self.userImg.image = UIImage.init(named: imgStr)
        self.name.text = memberData.name
        self.phoneNumber.text = memberData.phone
        
      if groupData.status == Utils.GroupStatus.isCompleted.rawValue {
        bgView.layer.borderColor = UIColor.lightGray.cgColor
        self.status.text = "Status : " + "Request Consent"
      } else {
        if memberData.status ==  Utils.GroupStatus.isApproved.rawValue {
                  bgView.layer.borderColor = UIColor.Consent.ConsentApproved.cgColor
              } else if memberData.status == Utils.GroupStatus.isPending.rawValue {
                  bgView.layer.borderColor = UIColor.Consent.ConsentPending.cgColor
              } else {
                  bgView.layer.borderColor = UIColor.lightGray.cgColor
              }
              self.status.text = "Status : " + memberData.status.capitalizingFirstLetter()
        }
        
    }
    
    @IBAction func deleteButtonAction(_ sender: Any) {
        groupcellDelegate?.deleteButtonClicked(cell: self)
    }
    
}
