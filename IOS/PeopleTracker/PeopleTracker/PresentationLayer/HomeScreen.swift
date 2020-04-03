//
//  HomeScreen.swift
//  PeopleTracker
//
//  Created by Apple on 18/03/20.
//  Copyright © 2020 Apple. All rights reserved.
//

import UIKit
import JJFloatingActionButton

class HomeScreen: UIViewController,UITableViewDelegate, UITableViewDataSource {
    
    @IBOutlet weak var usersTableView: UITableView!
    
    let actionButton = JJFloatingActionButton()
    let names: [String] = ["Office Group","Sree", "Maruti", "Harish", "Ashish", "Satish"]
    let numbers: [String] = ["8188422893","8088422893", "9019022684", "7200706845", "9949442884", "9848367485"]
    let userIcons : [String] = ["group","user0","user1","user2","user3","user1"]
    var selectedCell : [Int] = []
    var userid : String = ""
    var ugsToken : String = ""
    var listOfDevices : [DeviceData] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = "Home"
        self.navigationItem.setHidesBackButton(true, animated: true)
        let trackBtn : UIBarButtonItem = UIBarButtonItem.init(title: "Track", style: .plain, target: self, action: #selector(trackButton(sender:)))
        self.navigationItem.setRightBarButton(trackBtn, animated: true)
        usersTableView.delegate = self
        usersTableView.dataSource = self
        floatingActionButton()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        self.callgetDeviceApi()
    }
    
    // MARK: UITableView Delegate and DataSource methods
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.listOfDevices.count
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 140.0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "cellIdentifier") as! UserCell
        cell.selectionStyle = .none
        let deviceDetails = self.listOfDevices[indexPath.row]
        cell.setData(deviceData: deviceDetails)
        cell.userIcon.image = UIImage(named: "user4")
        cell.optionsButton.addTarget(self, action: #selector(showActionSheet), for: .touchUpInside)
        cell.checkBoxButton.setBackgroundImage(UIImage(named: "ic_checkempty"), for: .normal)
        cell.checkBoxButton.setBackgroundImage(UIImage(named: "ic_checkmark"), for: .selected)
        cell.checkBoxButton.addTarget(self, action: #selector(checkmarkSelection(sender:)), for: .touchUpInside)
        cell.checkBoxButton.isHidden = false
        if indexPath.row == 0{
            cell.checkBoxButton.isHidden = true
            cell.consentstatusColor.backgroundColor = UIColor(red: 132/255.0, green: 222/255.0, blue: 2/255.0, alpha: 1.0)
            cell.requestConsentButton.setTitle("Request Consent", for: .normal)
        } else if indexPath.row % 2 == 0{
            cell.consentstatusColor.backgroundColor = UIColor(red: 132/255.0, green: 222/255.0, blue: 2/255.0, alpha: 1.0)
            cell.requestConsentButton.setTitle("Consent Approved", for: .normal)
        } else {
            cell.consentstatusColor.backgroundColor = .orange
            cell.requestConsentButton.setTitle("Consent pending", for: .normal)
        }
      
        return cell
    }
   
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        self.selectedCell.append(indexPath.row)
        tableView.reloadData()
           if indexPath.row > 0 {
               //navigateToMapsScreen()
           } else {
               let selectedName = self.names[indexPath.row]
               navigateToGroupListScreen(title: selectedName)
           }
       }
       
    @objc func checkmarkSelection(sender:UIButton) {
        let tempBtn : UIButton = sender
        tempBtn.isSelected = !tempBtn.isSelected
    }
    
    @objc func trackButton(sender: UIBarButtonItem) {
        navigateToMapsScreen()
    }
    
    // MARK : Navigatation screens
    func navigateToMapsScreen() {
        let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
        let mapsViewController = storyBoard.instantiateViewController(withIdentifier: "MapsScreen") as! MapsScreen
        mapsViewController.names = ["Maruti", "Ashish"]
        mapsViewController.deviceId = self.userid
        mapsViewController.ugsToken = self.ugsToken
        mapsViewController.userId = self.userid
        self.navigationController?.pushViewController(mapsViewController, animated: true)
    }
    
    func navigateToAddDeviceScreen(title : String) {
        let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
        let addDeviceViewController = storyBoard.instantiateViewController(withIdentifier: "AddDeviceScreen") as! AddDeviceScreen
        addDeviceViewController.navtitle = title
        addDeviceViewController.userid = self.userid
        addDeviceViewController.ugsToken = self.ugsToken
        self.navigationController?.pushViewController(addDeviceViewController, animated: true)
    }
    
    func navigateToCreateGroupScreen() {
           let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
           let createGroupViewController = storyBoard.instantiateViewController(withIdentifier: "CreateGroupScreen") as! CreateGroupScreen
           createGroupViewController.navtitle = "Create Group"
           self.navigationController?.pushViewController(createGroupViewController, animated: true)
       }

    func navigateToGroupListScreen(title : String) {
        let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
        let groupListViewController = storyBoard.instantiateViewController(withIdentifier: "GroupListScreen") as! GroupListScreen
        groupListViewController.navtitle = title
        self.navigationController?.pushViewController(groupListViewController, animated: true)
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
        
        actionButton.addItem(title: "Create Group", image: UIImage(named: "group")?.withRenderingMode(.alwaysTemplate)) { item in
            // do something
            self.navigateToCreateGroupScreen()
        }
        
        view.addSubview(actionButton)
        actionButton.translatesAutoresizingMaskIntoConstraints = false
        actionButton.trailingAnchor.constraint(equalTo: view.safeAreaLayoutGuide.trailingAnchor, constant: -16).isActive = true
        actionButton.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor, constant: -16).isActive = true
    }
    
    // MARK: Display ActionSheet
    
    @objc func showActionSheet(){
        
        let alert = UIAlertController(title: "Select", message: "", preferredStyle: .actionSheet)
        
        alert.addAction(UIAlertAction(title: "Edit", style: .default , handler:{ (UIAlertAction)in
            print("User click Approve button")
        }))
        
        alert.addAction(UIAlertAction(title: "Delete", style: .default , handler:{ (UIAlertAction)in
            print("User click Edit button")
        }))
        
        
        alert.addAction(UIAlertAction(title: "Dismiss", style: .cancel, handler:{ (UIAlertAction)in
            print("User click Dismiss button")
        }))
        
        self.present(alert, animated: true, completion: {
            print("completion block")
        })
    }
    
    // API to add device details
    func callgetDeviceApi() {
        let deviceURL = URL(string:  Constants.ApiPath.deviceDetails + ugsToken)!
        let deviceParams :  [String : Any] = ["usersAssigned":[self.userid]]
        DeviceService.shared.addAndGetDeviceDetails(with: deviceURL, parameters: deviceParams) { (result : (Result<DeviceModel, Error>)) in
            switch result {
                    case .success(let deviceResponse):
                        print(deviceResponse.devicedata!)
                        guard let deviceData = deviceResponse.devicedata else {
                            return
                        }
                        for device in deviceData {
                            self.listOfDevices.append(device)
                        }
                        DispatchQueue.main.async {
                          self.usersTableView.reloadData()
                        }
                    case .failure(let error):
                        if type(of: error) == NetworkManager.ErrorType.self {
                            
                        } else {
                            DispatchQueue.main.async {
                            self.ShowALert(title: error.localizedDescription)
                            }
                    }
                }
            }
    }
}
