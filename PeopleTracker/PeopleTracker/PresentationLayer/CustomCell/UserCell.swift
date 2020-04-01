//
//  UserCell.swift
//  PeopleTracker
//
//  Created by Apple on 23/03/20.
//  Copyright © 2020 Apple. All rights reserved.
//

import UIKit
class UserCell: UITableViewCell {
       
    @IBOutlet weak var userIcon: UIImageView!
    @IBOutlet weak var name: UILabel!
    @IBOutlet weak var phoneNumber: UILabel!
    @IBOutlet weak var optionsButton: UIButton!
    @IBOutlet weak var checkBoxButton: UIButton!
    @IBOutlet weak var consentstatusColor: UILabel!
    @IBOutlet weak var requestConsentButton: UIButton!
}
