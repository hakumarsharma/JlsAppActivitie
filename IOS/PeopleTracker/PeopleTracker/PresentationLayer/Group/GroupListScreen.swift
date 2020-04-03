//
//  GroupListScreen.swift
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
import JJFloatingActionButton

class GroupListScreen: UIViewController,UITableViewDelegate, UITableViewDataSource {
    
     var navtitle : String = ""
     @IBOutlet weak var groupListTableView: UITableView!
       
       let actionButton = JJFloatingActionButton()
       let names: [String] = ["Sree", "Maruti", "Harish", "Ashish", "Satish"]
       let numbers: [String] = ["8088422893", "9019022684", "7200706845", "9949442884", "9848367485"]
       
       override func viewDidLoad() {
           super.viewDidLoad()
           self.title = navtitle
           groupListTableView.delegate = self
           groupListTableView.dataSource = self
           let trackBtn : UIBarButtonItem = UIBarButtonItem.init(title: "Track", style: .plain, target: self, action: #selector(trackButton(sender:)))
               self.navigationItem.setRightBarButton(trackBtn, animated: true)
           floatingActionButton()
           
       }
       
       // MARK: UITableView Delegate and DataSource methods
       
       func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
           return self.names.count
       }
       
       func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
           return 120.0
       }
       
       func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
           
           let cell = tableView.dequeueReusableCell(withIdentifier: "cellIdentifier") as! GroupCell
           cell.selectionStyle = .none
           cell.name.text = self.names[indexPath.row];
           cell.phoneNumber.text = self.numbers[indexPath.row];
           return cell
       }
       
       // MARK: Navigation methods
       func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
           
       }
    
    
        @objc func trackButton(sender: UIBarButtonItem) {
            navigateToMapsScreen()
        }
       
       func navigateToMapsScreen() {
           let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
           let mapsViewController = storyBoard.instantiateViewController(withIdentifier: "MapsScreen") as! MapsScreen
           self.navigationController?.pushViewController(mapsViewController, animated: true)
       }
       
       func navigateToAddDeviceScreen(title : String) {
             let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
             let addDeviceViewController = storyBoard.instantiateViewController(withIdentifier: "AddDeviceScreen") as! AddDeviceScreen
             addDeviceViewController.navtitle = title
             self.navigationController?.pushViewController(addDeviceViewController, animated: true)
         }
       
       func navigateToCreateGroupScreen() {
              let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
              let createGroupViewController = storyBoard.instantiateViewController(withIdentifier: "CreateGroupScreen") as! CreateGroupScreen
              self.navigationController?.pushViewController(createGroupViewController, animated: true)
          }
       
       // MARK: Show floating button
       func floatingActionButton () {
           actionButton.addItem(title: "Add Device", image: UIImage(named: "device")?.withRenderingMode(.alwaysTemplate)) { item in
               // do something
               self.navigateToAddDeviceScreen(title: "Add Device")
           }
           
           actionButton.addItem(title: "Add Person", image: UIImage(named: "user4")?.withRenderingMode(.alwaysTemplate)) { item in
               // do something
               self.navigateToAddDeviceScreen(title: "Add Person")
           }
         
           view.addSubview(actionButton)
           actionButton.translatesAutoresizingMaskIntoConstraints = false
           actionButton.trailingAnchor.constraint(equalTo: view.safeAreaLayoutGuide.trailingAnchor, constant: -16).isActive = true
           actionButton.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor, constant: -16).isActive = true
       }
       
    
}
