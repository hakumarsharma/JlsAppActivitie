//
//  SideMenu.swift
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
import SideMenu

class SideMenuScreen: UITableViewController {
    
    let menuIcons: [String] = ["home","activesession", "profile", "settings", "about", "help","logout"]
    let menuItems: [String] = ["Home","Active Sessions", "Profile", "Settings", "About", "Help & Privacy","Logout"]
    var isFromHomevc : Bool = false
    @IBOutlet var sideMenuTable: UITableView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.navigationController?.navigationBar.tintColor = UIColor.init(red: 56.0/255.0, green: 86.0/255.0, blue: 133.0/255.0, alpha: 1.0)
        self.sideMenuTable.separatorColor = .clear
    }
    
    // MARK: UITableView Delegate and DataSource methods
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        return 2
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if section == 0 {
            return 1
        }
        return self.menuItems.count
    }
    
    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        if indexPath.section == 0 {
            return 250.0
        }
        return 80.0
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if indexPath.section == 0 {
            let profileCell = tableView.dequeueReusableCell(withIdentifier: "profileCell") as! ProfileCell
            profileCell.selectionStyle = .none
            profileCell.setProfileData()
            return profileCell
            
        } else {
            let menuCell = tableView.dequeueReusableCell(withIdentifier: "menuCell") as! MenuCell
            menuCell.selectionStyle = .none
            menuCell.setmenuData(menuItem: menuItems[indexPath.row], menuIcon: menuIcons[indexPath.row])
            return menuCell
        }
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if indexPath.section == 1 {
            switch indexPath.row {
                case 0:
                    if isFromHomevc {
                        dismiss(animated: true, completion: nil)
                    }else {
                        self.navigateToHomeScreen()
                    }
                case 1:
                self.navigateToActiveSession()
                case 2:
                self.navigateToProfileScreen()
                case 3:
                self.navigateToSettingsScreen()
                case 4:
                self.navigateToAboutScreen()
                case 5:
                self.navigateToHelpScreen()
                case 6:
                self.navigateToLoginScreen()
            default:
                dismiss(animated: true, completion: nil)
            }
        }
    }
    
    
    // MARK : Navigatation screens
    func navigateToHomeScreen() {
        let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
        let homeViewController = storyBoard.instantiateViewController(withIdentifier: Constants.ScreenNames.HomeScreen) as! HomeScreen
        self.navigationController?.pushViewController(homeViewController, animated: true)
    }
    func navigateToActiveSession() {
        let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
        let activeSessionViewController = storyBoard.instantiateViewController(withIdentifier: Constants.ScreenNames.ActiveSessions) as! ActiveSessionsScreen
        self.navigationController?.pushViewController(activeSessionViewController, animated: true)
    }
    
    func navigateToProfileScreen() {
         let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
         let profileViewController = storyBoard.instantiateViewController(withIdentifier: Constants.ScreenNames.Profile) as! ProfileScreen
         self.navigationController?.pushViewController(profileViewController, animated: true)
     }
    
    func navigateToSettingsScreen() {
        let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
        let settingsViewController = storyBoard.instantiateViewController(withIdentifier: Constants.ScreenNames.Settings) as! SettingScreen
        self.navigationController?.pushViewController(settingsViewController, animated: true)
    }
    
    func navigateToHelpScreen() {
        let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
        let helpViewController = storyBoard.instantiateViewController(withIdentifier: Constants.ScreenNames.HelpPrivacy) as! HelpPrivacyScreen
        self.navigationController?.pushViewController(helpViewController, animated: true)
    }
    
    func navigateToAboutScreen() {
        let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
        let aboutViewController = storyBoard.instantiateViewController(withIdentifier: Constants.ScreenNames.About) as! AboutScreen
        self.navigationController?.pushViewController(aboutViewController, animated: true)
    }
    
    func navigateToLoginScreen() {
           let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
           let loginViewController = storyBoard.instantiateViewController(withIdentifier: Constants.ScreenNames.LoginScreen) as! LoginScreen
           self.navigationController?.pushViewController(loginViewController, animated: true)
       }
    
}

