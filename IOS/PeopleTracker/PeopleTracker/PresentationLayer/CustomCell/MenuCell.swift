//
//  MenuCell.swift
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
class MenuCell: UITableViewCell {
    
    
    @IBOutlet weak var menuIcon: UIImageView!
    @IBOutlet weak var menuTitle: UILabel!
    
    func setmenuData(menuItem : String, menuIcon : String)  {
        self.menuIcon.image = UIImage.init(named: menuIcon)
        self.menuTitle.text = menuItem
     }
}
