//
//  HomeScreen.swift
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
import SideMenu
import RealmSwift

class HomeScreen: UIViewController,UITableViewDelegate, UITableViewDataSource {
    
    @IBOutlet weak var usersTableView: UITableView!
    
    @IBOutlet weak var instructionsLabel: UILabel!
    let actionButton = JJFloatingActionButton()
    var selectedCell : [String] = []
    let names: [String] = ["Office Group","Sree", "Maruti", "Harish", "Ashish", "Satish"] // TODO: remove after api call is integrated
    //    var listOfDevices : [DeviceData]    = []
    var deviceDetails : [GroupListData] = []
    var groupList     : [GroupListData] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.initialiseData()
        floatingActionButton()
        self.getGroupListApi()
    }
    
    func initialiseData() {
        self.title = "Home"
        self.navigationItem.setHidesBackButton(true, animated: true)
        let menuBtn : UIBarButtonItem = UIBarButtonItem.init(image: UIImage(named: "Menu"), style: .plain, target: self, action: #selector(menuButton(sender:)))
        menuBtn.tintColor = .white
        self.navigationItem.setLeftBarButton(menuBtn, animated: true)
        let trackBtn : UIBarButtonItem = UIBarButtonItem.init(title: "Track", style: .plain, target: self, action: #selector(trackButton(sender:)))
        trackBtn.setTitleTextAttributes( [NSAttributedString.Key.foregroundColor : UIColor.white], for: .normal)
        self.navigationItem.setRightBarButton(trackBtn, animated: true)
        usersTableView.delegate = self
        usersTableView.dataSource = self
        usersTableView.tableFooterView = UIView()
        self.createNotification()
    }
    
    func createNotification() {
        NotificationCenter.default.addObserver(self, selector: #selector(callgroupListApi(notification:)), name: NSNotification.Name(rawValue: Constants.NotificationName.GetGroupList), object: nil)
    }
    
    @objc func callgroupListApi(notification: NSNotification) {
        self.getGroupListApi()
    }
    
    func showHideTableView() {
        
        if self.groupList.count > 0 {
            DispatchQueue.main.async {
                          self.instructionsLabel.isHidden = true
                          self.usersTableView.isHidden = false
            }
                      } else {
            DispatchQueue.main.async {
                          self.instructionsLabel.isHidden = false
                          self.usersTableView.isHidden = true
            }
                      }
    }
    
    // MARK: UITableView Delegate and DataSource methods
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.groupList.count
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 140.0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "cellIdentifier") as! UserCell
        cell.selectionStyle = .none
        let groupListDetails = self.groupList[indexPath.row]
        cell.setGroupData(groupData: groupListDetails)
        cell.optionsButton.addTarget(self, action: #selector(showActionSheet(sender:)), for: .touchUpInside)
        cell.checkBoxButton.addTarget(self, action: #selector(checkmarkSelection(sender:)), for: .touchUpInside)
        cell.checkBoxButton.tag = indexPath.row
        cell.checkBoxButton.isHidden = true
        // TODO: Remove after grouping and consent mechanisam is integrated
//        if indexPath.row == 0{
//            cell.checkBoxButton.isHidden = true
//            cell.consentstatusColor.backgroundColor = UIColor(red: 132/255.0, green: 222/255.0, blue: 2/255.0, alpha: 1.0)
//            cell.requestConsentButton.setTitle(Constants.HomScreenConstants.RequestConsent, for: .normal)
//        } else
//            if indexPath.row % 2 == 0{
//                cell.consentstatusColor.backgroundColor = UIColor(red: 132/255.0, green: 222/255.0, blue: 2/255.0, alpha: 1.0)
//                cell.requestConsentButton.setTitle(Constants.HomScreenConstants.ConsentApproved, for: .normal)
//            } else {
//                cell.consentstatusColor.backgroundColor = .orange
//                cell.requestConsentButton.setTitle(Constants.HomScreenConstants.ConsentPending, for: .normal)
//        }
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let groupListDetails = self.groupList[indexPath.row]
//        if groupListDetails.groupMember.count > 1 {
            navigateToGroupListScreen(title: groupListDetails.name,groupId: groupListDetails.groupId)
//        }
    }
    
    // MARK: Bar button action items
    @objc func checkmarkSelection(sender:UIButton) {
        let tempBtn : UIButton = sender
        if tempBtn.isSelected {
            selectedCell = selectedCell.filter({ (item) -> Bool in
                return item != self.groupList[tempBtn.tag].groupId
            })
            deviceDetails = deviceDetails.filter({ (item) -> Bool in
                return item.groupId != self.groupList[tempBtn.tag].groupId
            })
        } else {
            selectedCell.append(self.groupList[tempBtn.tag].groupId)
            
        }
        tempBtn.isSelected = !tempBtn.isSelected
    }
    
    @objc func menuButton(sender: UIBarButtonItem) {
        
        let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
        let menuViewController = storyBoard.instantiateViewController(withIdentifier: "SideMenuScreen") as! SideMenuScreen
        menuViewController.isFromHomevc = true
        let leftMenuNavigationController = SideMenuNavigationController(rootViewController: menuViewController)
        SideMenuManager.default.leftMenuNavigationController = leftMenuNavigationController
        leftMenuNavigationController.leftSide = true
        leftMenuNavigationController.navigationBar.isHidden = true
        leftMenuNavigationController.statusBarEndAlpha = 0
        leftMenuNavigationController.presentationStyle = SideMenuPresentationStyle.menuSlideIn
        leftMenuNavigationController.menuWidth = self.view.frame.size.width - 100
        present(leftMenuNavigationController, animated: true, completion: nil)
        
    }
    
    @objc func trackButton(sender: UIBarButtonItem) {
        if selectedCell.count > 0 {
        self.callgetDeviceLocationDetails { (success) in
            if success && self.deviceDetails.count > 0  {
                DispatchQueue.main.async {
                    self.navigateToMapsScreen()
                }
            }else {
                DispatchQueue.main.async {
                    self.ShowALert(title: Constants.LocationConstants.LocationDetailsNotFound)
                }
            }
        }
        } else {
            self.ShowALert(title: Constants.HomScreenConstants.SelectDevice)
        }
    }
    
    // MARK : Navigatation screens
    func navigateToMapsScreen() {
        let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
        let mapsViewController = storyBoard.instantiateViewController(withIdentifier: Constants.ScreenNames.MapsScreen) as! MapsScreen
        mapsViewController.deviceId = Utils.shared.getUserId()
        //        mapsViewController.deviceDetails = self.deviceDetails
        self.navigationController?.pushViewController(mapsViewController, animated: true)
    }
    
    func navigateToAddDeviceScreen(title : String) {
        let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
        let addDeviceViewController = storyBoard.instantiateViewController(withIdentifier: Constants.ScreenNames.AddDeviceScreen) as! AddDeviceScreen
        addDeviceViewController.navtitle = title
        addDeviceViewController.userid = Utils.shared.getUserId()
        addDeviceViewController.ugsToken = Utils.shared.getUgsToken()
        self.navigationController?.pushViewController(addDeviceViewController, animated: true)
    }
    
    func navigateToAddPersonScreen(title : String) {
        let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
        let addPersonViewController = storyBoard.instantiateViewController(withIdentifier: Constants.ScreenNames.AddPersonScreen) as! AddPersonScreen
        addPersonViewController.navtitle = title
        self.navigationController?.pushViewController(addPersonViewController, animated: true)
    }
    
    func navigateToCreateGroupScreen(title : String, isEdit : Bool) {
        let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
        let createGroupViewController = storyBoard.instantiateViewController(withIdentifier: Constants.ScreenNames.CreateGroupScreen) as! CreateGroupScreen
        createGroupViewController.navtitle = title
        if isEdit {
            createGroupViewController.name = self.groupList[0].name
        }
        self.navigationController?.pushViewController(createGroupViewController, animated: true)
    }
    
    func navigateToGroupListScreen(title : String, groupId :  String) {
        let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
        let groupListViewController = storyBoard.instantiateViewController(withIdentifier: Constants.ScreenNames.GroupListScreen) as! GroupListScreen
        groupListViewController.navtitle = title
        groupListViewController.groupId = groupId
        self.navigationController?.pushViewController(groupListViewController, animated: true)
    }
    
    
    // MARK: Show floating button
    func floatingActionButton () {
        actionButton.addItem(title: Constants.HomScreenConstants.AddDevice, image: UIImage(named: "device")?.withRenderingMode(.alwaysTemplate)) { item in
            // do something
            self.navigateToAddDeviceScreen(title: Constants.HomScreenConstants.AddDevice)
        }
        
        actionButton.addItem(title: Constants.HomScreenConstants.AddPerson, image: UIImage(named: "user4")?.withRenderingMode(.alwaysTemplate)) { item in
            // do something
            self.navigateToAddPersonScreen(title: Constants.HomScreenConstants.AddPerson)
        }
        
        actionButton.addItem(title: Constants.HomScreenConstants.CreateGroup, image: UIImage(named: "group")?.withRenderingMode(.alwaysTemplate)) { item in
            // do something
            self.navigateToCreateGroupScreen(title: Constants.HomScreenConstants.CreateGroup, isEdit: false)
        }
        
        view.addSubview(actionButton)
        actionButton.translatesAutoresizingMaskIntoConstraints = false
        actionButton.trailingAnchor.constraint(equalTo: view.safeAreaLayoutGuide.trailingAnchor, constant: -16).isActive = true
        actionButton.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor, constant: -16).isActive = true
    }
    
    // Alert with button action
    func ShowALertWithButtonAction(title: String){
        let alert = UIAlertController(title: Constants.AlertConstants.Alert, message: title, preferredStyle: UIAlertController.Style.alert)
        alert.addAction(UIAlertAction(title: Constants.AlertConstants.OkButton, style: UIAlertAction.Style.default, handler: {(_: UIAlertAction!) in
            DispatchQueue.main.async {
                
            }
        }))
        self.present(alert, animated: true, completion: nil)
    }
    
    // Alert with button action
    func ShowDeleteAlertWithButtonAction(title: String){
        let alert = UIAlertController(title: Constants.AlertConstants.Alert, message: title, preferredStyle: UIAlertController.Style.alert)
        alert.addAction(UIAlertAction(title: Constants.AlertConstants.Delete, style: UIAlertAction.Style.default, handler: {(_: UIAlertAction!) in
            DispatchQueue.main.async {
                self.callDeleteGroupApi()
            }
        }))
        alert.addAction(UIAlertAction(title: Constants.AlertConstants.CancelButton, style: UIAlertAction.Style.default, handler: {(_: UIAlertAction!) in
            DispatchQueue.main.async {
                
            }
        }))
        self.present(alert, animated: true, completion: nil)
    }
    
    // MARK: Display ActionSheet
    
    @objc func showActionSheet(sender : UIButton){
        
//        let buttonPosition = sender.convert(CGPoint.zero, to: self.usersTableView)
//        let indexPath = self.usersTableView.indexPathForRow(at:buttonPosition)
//        let cell = self.usersTableView.cellForRow(at: indexPath!) as! UserCell

        let alert = UIAlertController(title: Constants.HomScreenConstants.Select, message: "", preferredStyle: .actionSheet)
        
        alert.addAction(UIAlertAction(title: Constants.HomScreenConstants.Edit, style: .default , handler:{ (UIAlertAction)in
            self.navigateToCreateGroupScreen(title: Constants.HomScreenConstants.EditGroup, isEdit: true)
        }))
        
        alert.addAction(UIAlertAction(title:Constants.HomScreenConstants.Delete, style: .default , handler:{ (UIAlertAction)in
            self.ShowDeleteAlertWithButtonAction(title: Constants.HomScreenConstants.DeleteDevice)
        }))
        alert.addAction(UIAlertAction(title: Constants.HomScreenConstants.Dismiss, style: .cancel, handler:{ (UIAlertAction)in
            
        }))
        
        self.present(alert, animated: true, completion: {
            print("completion block")
        })
    }
    
    // MARK: Service Calls
    // API to add device details
    func callgetDeviceApi() {
        self.showActivityIndicator()
        let deviceURL = URL(string:  Constants.ApiPath.DeviceDetails + Utils.shared.getUgsToken())!
        let deviceParams :  [String : Any] = ["usersAssigned":Utils.shared.getUserId()]
        DeviceService.shared.addAndGetDeviceDetails(with: deviceURL, parameters: deviceParams) { (result : (Result<DeviceModel, Error>)) in
            switch result {
            case .success(let deviceResponse):
//                guard let deviceData = deviceResponse.devicedata else {
//                    return
//                }
                print(deviceResponse)
                //                for device in deviceData {
                //                    self.listOfDevices.append(device)
                //                }
                DispatchQueue.main.async {
                    self.hideActivityIndicator()
                    self.usersTableView.reloadData()
                }
            case .failure(let error):
                if type(of: error) == NetworkManager.ErrorType.self {
                    DispatchQueue.main.async {
                        self.hideActivityIndicator()
                        self.ShowALert(title: Utils.shared.handleError(error: error as! NetworkManager.ErrorType))
                    }
                } else {
                    DispatchQueue.main.async {
                        self.hideActivityIndicator()
                        self.ShowALert(title: error.localizedDescription)
                    }
                }
            }
        }
    }
    
    // API to get location details
    // TODO: check and change api call to track multiple devices
    func callgetDeviceLocationDetails(completion: @escaping(_ success: Bool) -> Void) {
        self.showActivityIndicator()
        for (index,element) in selectedCell.enumerated() {
            let deviceURL = URL(string:  Constants.ApiPath.DeviceApisUrl + element + "?tsp=1585031229387&ugs_token=" + Utils.shared.getUgsToken())! // TODO: Save tsp in user defaults
            DeviceService.shared.getDeviceLocationDetails(with: deviceURL) { (result : (Result<LocationModel, Error>)) in
                switch result {
                case .success(let deviceResponse):
                    print(index)
                    print(deviceResponse)
                    DispatchQueue.main.async {
                        self.hideActivityIndicator()
                    }
                    //                    if let device = deviceResponse.devicedata , let _ = deviceResponse.devicedata?.deviceStatus?.location {
                    ////                        self.deviceDetails.append(device)
                    //                    }
                    //                    if index == self.selectedCell.count - 1 {
                    //                        completion(true)
                    //                    }
                    
                case .failure(let error):
                    if type(of: error) == NetworkManager.ErrorType.self {
                        DispatchQueue.main.async {
                            self.hideActivityIndicator()
                            self.ShowALert(title: Utils.shared.handleError(error: error as! NetworkManager.ErrorType))
                        }
                    } else {
                        DispatchQueue.main.async {
                            self.hideActivityIndicator()
                            self.ShowALert(title: error.localizedDescription)
                        }
                    }
                }
            }
            
        }
    }
    
    
    // Create GroupList Api Call
    func getGroupListApi() {
       self.showActivityIndicator()
        let groupUrl : URL = URL(string: Constants.ApiPath.UserApisUrl + Utils.shared.getUserId() + Constants.ApiPath.GroupListUrl )!
        GroupService.shared.getGroupListData(groupListUrl:  groupUrl) { (result : Result<GroupListModel, Error>) in
            switch result {
            case .success(let groupResponse):
                self.groupList.removeAll()
                for groupdata in groupResponse.groupListData {
                    if groupdata.status == Utils.GroupStatus.isScheduled.rawValue {
                        UserDefaults.standard.set(groupdata.groupId, forKey: groupdata.name)
                        self.groupList.append(groupdata)
                    }
                   
                }
               DispatchQueue.main.async {
                    self.hideActivityIndicator()
                    self.showHideTableView()
                    self.usersTableView.reloadData()
                }
            case .failure(let error):
                if type(of: error) == NetworkManager.ErrorType.self {
                    DispatchQueue.main.async {
                        self.hideActivityIndicator()
                        self.ShowALert(title: Utils.shared.handleError(error: error as! NetworkManager.ErrorType))
                    }
                } else {
                    DispatchQueue.main.async {
                        self.hideActivityIndicator()
                        self.ShowALert(title: error.localizedDescription)
                    }
                }
                 self.showHideTableView()
            }
        }
    }
    
    // Delete Group Api Call
    func callDeleteGroupApi() {
        self.showActivityIndicator()
    
        let deleteGroupUrl : URL = URL(string: Constants.ApiPath.UserApisUrl + Utils.shared.getUserId() + Constants.ApiPath.CreateGroupUrl + "/"  +  UserDefaults.standard.string(forKey: self.groupList[0].name)! )!
        GroupService.shared.deleteGroup(deleteGroupUrl:  deleteGroupUrl) { (result : Result<GroupModel, Error>) in
            switch result {
            case .success(let groupResponse):
                  print(groupResponse)
                   DispatchQueue.main.async {
                    self.hideActivityIndicator()
                    self.getGroupListApi()
                  }
            case .failure(let error):
                if type(of: error) == NetworkManager.ErrorType.self {
                    DispatchQueue.main.async {
                        self.hideActivityIndicator()
                        self.ShowALert(title: Utils.shared.handleError(error: error as! NetworkManager.ErrorType))
                    }
                } else {
                    DispatchQueue.main.async {
                        self.hideActivityIndicator()
                        self.ShowALert(title: error.localizedDescription)
                    }
                }
            }
        }
    }
    
}
