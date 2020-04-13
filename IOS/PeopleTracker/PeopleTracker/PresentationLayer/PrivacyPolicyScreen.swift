//
//  PrivacyPolicyScreen.swift
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

class PrivacyPolicyScreen: UIViewController {
    @IBOutlet weak var textView: UITextView!
    @IBOutlet weak var checkMarkButton: UIButton!
    var isPrivacySelected : Bool = false
    override func viewDidLoad() {
        super.viewDidLoad()
        self.intialiseData()
    }
    
    func intialiseData() {
        self.navigationController?.navigationBar.isHidden = false
        self.navigationItem.setHidesBackButton(true, animated: true)
        self.navigationController?.navigationBar.titleTextAttributes = [NSAttributedString.Key.foregroundColor : UIColor.white]
        self.title = "Privacy Policy"
        textView.attributedText = Constants.PrivacyScreen.PrivacyPolicy.htmlToAttributedString
    }
    
    @IBAction func checkMarkButtonAction(_ sender: Any) {
        if isPrivacySelected {
            isPrivacySelected = false
            self.checkMarkButton.setBackgroundImage(UIImage(named: "ic_checkempty"), for: .normal)
        } else {
            isPrivacySelected = true
            self.checkMarkButton.setBackgroundImage(UIImage(named: "ic_checkmark"), for: .normal)
        }
    }
    
    @IBAction func acceptButtonAction(_ sender: Any) {
        if isPrivacySelected {
        let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
        let loginViewController = storyBoard.instantiateViewController(withIdentifier: Constants.ScreenNames.LoginScreen) as! LoginScreen
        self.navigationController?.pushViewController(loginViewController, animated: true)
        } else {
            self.ShowALert(title: Constants.PrivacyScreen.DeclineAlert)
        }
    }
    
    @IBAction func DeclineButtonAction(_ sender: Any) {
        self.ShowALert(title: Constants.PrivacyScreen.DeclineAlert)
    }
}
