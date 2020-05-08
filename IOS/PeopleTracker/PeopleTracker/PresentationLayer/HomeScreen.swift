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

class HomeScreen: UIViewController,UITableViewDelegate, UITableViewDataSource, UserCellDelegate {
    
    
    @IBOutlet weak var usersTableView: UITableView!
    
    @IBOutlet weak var instructionsLabel: UILabel!
    let actionButton  = JJFloatingActionButton()
    var selectedCell  : [GroupListData] = []
    var groupList     : [GroupListData] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.initialiseData()
        self.getAllGroupsApi()
    }
    
    func initialiseData() {
        self.title = "Home"
        self.navigationItem.setHidesBackButton(true, animated: true)
        usersTableView.delegate = self
        usersTableView.dataSource = self
        usersTableView.tableFooterView = UIView()
        self.createNavBarItems()
        self.createNotification()
        self.floatingActionButton()
    }
    
    // creating navigation bar left and right items for menu and track
    func createNavBarItems(){
        let menuBtn : UIBarButtonItem = UIBarButtonItem.init(image: UIImage(named: "Menu"), style: .plain, target: self, action: #selector(menuButton(sender:)))
        menuBtn.tintColor = .white
        self.navigationItem.setLeftBarButton(menuBtn, animated: true)
        let trackBtn : UIBarButtonItem = UIBarButtonItem.init(title: "Track", style: .plain, target: self, action: #selector(trackButton(sender:)))
        trackBtn.setTitleTextAttributes( [NSAttributedString.Key.foregroundColor : UIColor.white], for: .normal)
        self.navigationItem.setRightBarButton(trackBtn, animated: true)
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
        cell.usercelldelegate = self
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let groupListDetails = self.groupList[indexPath.row]
        let groupName = groupListDetails.name.components(separatedBy: "+")
        if groupName[0] == Constants.AddDeviceConstants.Individual  {
            // do nothing
        }else {
            navigateToGroupListScreen(title: groupListDetails.name,groupData: groupListDetails)
        }
    }
    
    // MARK: Customcell delegate methods
    func checkMarkClicked(cell: UserCell) {
        let indexpath = self.usersTableView.indexPath(for: cell)
        let groupData = self.groupList[indexpath!.row]
        if cell.checkBoxButton.isSelected {
            selectedCell = selectedCell.filter({ (item) -> Bool in
                return item.groupId != groupData.groupId
            })
        } else {
            selectedCell.append(groupData)
        }
        cell.checkBoxButton.isSelected = !cell.checkBoxButton.isSelected
    }
    
    func editOptionsClicked(cell: UserCell) {
        let indexpath = self.usersTableView.indexPath(for: cell)
        let groupData = self.groupList[indexpath!.row]
        self.showActionSheet(groupData: groupData)
    }
    
    func requestConsentClicked(cell: UserCell) {
        let indexpath = self.usersTableView.indexPath(for: cell)
        let groupData = self.groupList[indexpath!.row]
        let groupName = groupData.name.components(separatedBy: "+")
        let isIndividual = (groupName[0] == Constants.AddDeviceConstants.Individual) ? true : false
        if isIndividual && cell.requestConsentButton.titleLabel?.text == Constants.HomScreenConstants.RequestConsent {
            self.callRequestConsentApi(groupData: groupData)
        } else if !isIndividual && cell.requestConsentButton.titleLabel?.text == Constants.HomScreenConstants.RequestConsent {
           // call create group and add memeber api
        } else {
            self.ShowALertWithButtonAction(title: Constants.HomScreenConstants.ConsentAlredySent)
        }
    }
    
    // MARK: NavigationBar button item action methods
    
    // To display side menu
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
        let activeGroupsArr = selectedCell.filter({$0.status == Utils.GroupStatus.isActive.rawValue && $0.groupMember.first?.memberStatus == Utils.GroupStatus.isApproved.rawValue})
            if activeGroupsArr.count > 0 {
               self.navigateToMapsScreen()
            } else {
                self.ShowALert(title: Constants.HomScreenConstants.SelectDevice)
            }
        } else {
            self.ShowALert(title: Constants.HomScreenConstants.SelectDevice)
        }
    }
    
    // MARK : Navigatation screens
    func navigateToMapsScreen() {
        let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
        let mapsViewController = storyBoard.instantiateViewController(withIdentifier: Constants.ScreenNames.MapsScreen) as! MapsScreen
        mapsViewController.groupData = selectedCell
        self.navigationController?.pushViewController(mapsViewController, animated: true)
    }
    
    func navigateToAddDeviceScreen(title : String, groupData : GroupListData?) {
        let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
        let addDeviceViewController = storyBoard.instantiateViewController(withIdentifier: Constants.ScreenNames.AddDeviceScreen) as! AddDeviceScreen
        addDeviceViewController.navtitle = title
        addDeviceViewController.groupId = groupData?.groupId ?? ""
        self.navigationController?.pushViewController(addDeviceViewController, animated: true)
    }
    
    func navigateToAddPersonScreen(title : String, groupData : GroupListData?) {
        let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
        let addPersonViewController = storyBoard.instantiateViewController(withIdentifier: Constants.ScreenNames.AddPersonScreen) as! AddPersonScreen
        addPersonViewController.navtitle = title
        addPersonViewController.groupId = groupData?.groupId ?? ""
        self.navigationController?.pushViewController(addPersonViewController, animated: true)
    }
    
    func navigateToCreateGroupScreen(title : String, groupData : GroupListData?) {
        let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
        let createGroupViewController = storyBoard.instantiateViewController(withIdentifier: Constants.ScreenNames.CreateGroupScreen) as! CreateGroupScreen
        createGroupViewController.navtitle = title
        if let groupdata = groupData {
            createGroupViewController.groupData = groupdata
        }
        self.navigationController?.pushViewController(createGroupViewController, animated: true)
    }
    
    func navigateToGroupListScreen(title : String, groupData :  GroupListData) {
        let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
        let groupListViewController = storyBoard.instantiateViewController(withIdentifier: Constants.ScreenNames.GroupListScreen) as! GroupListScreen
        groupListViewController.navtitle = title
        groupListViewController.groupData = groupData
        groupListViewController.isActiveSession = false
        self.navigationController?.pushViewController(groupListViewController, animated: true)
    }
    
    
    // MARK: Show floating button
    func floatingActionButton () {
        actionButton.addItem(title: Constants.HomScreenConstants.AddDevice, image: UIImage(named: "device")?.withRenderingMode(.alwaysTemplate)) { item in
            self.navigateToAddDeviceScreen(title: Constants.HomScreenConstants.AddDevice, groupData: nil)
        }
        
        actionButton.addItem(title: Constants.HomScreenConstants.AddPerson, image: UIImage(named: "ic_user")?.withRenderingMode(.alwaysTemplate)) { item in
            self.navigateToAddPersonScreen(title: Constants.HomScreenConstants.AddPerson, groupData: nil)
        }
        
        actionButton.addItem(title: Constants.HomScreenConstants.CreateGroup, image: UIImage(named: "ic_group")?.withRenderingMode(.alwaysTemplate)) { item in
            self.navigateToCreateGroupScreen(title: Constants.HomScreenConstants.CreateGroup, groupData: nil)
        }
        actionButton.tintColor = .black
        view.addSubview(actionButton)
        actionButton.translatesAutoresizingMaskIntoConstraints = false
        actionButton.trailingAnchor.constraint(equalTo: view.safeAreaLayoutGuide.trailingAnchor, constant: -16).isActive = true
        actionButton.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor, constant: -16).isActive = true
    }
    
    // MARK: Notification Methods
    
    func createNotification() {
        NotificationCenter.default.addObserver(self, selector: #selector(callgroupListApi(notification:)), name: NSNotification.Name(rawValue: Constants.NotificationName.GetGroupList), object: nil)
    }
    
    @objc func callgroupListApi(notification: NSNotification) {
        self.getAllGroupsApi()
    }
    
    // MARK: Display ActionSheet
    
    @objc func showActionSheet(groupData : GroupListData){
        let groupName = groupData.name.components(separatedBy: "+")
        
        let isGroup = (groupName[0] == Constants.AddDeviceConstants.Individual) ? false : true
        let alert = UIAlertController(title: Constants.HomScreenConstants.Select, message: "", preferredStyle: .actionSheet)
        
        alert.addAction(UIAlertAction(title: Constants.HomScreenConstants.Edit, style: .default , handler:{ (UIAlertAction)in
            if isGroup {
                self.navigateToCreateGroupScreen(title: Constants.HomScreenConstants.EditGroup, groupData: groupData)
            } else {
                self.navigateToAddPersonScreen(title: Constants.HomScreenConstants.EditPerson, groupData: groupData)
            }
        }))
        
        alert.addAction(UIAlertAction(title:Constants.HomScreenConstants.Delete, style: .default , handler:{ (UIAlertAction)in
            if isGroup {
                self.ShowDeleteAlertWithButtonAction(title: Constants.HomScreenConstants.DeleteGroup,groupData : groupData)
            } else {
                self.ShowDeleteAlertWithButtonAction(title: Constants.HomScreenConstants.DeleteContact,groupData : groupData)
            }
        }))
        alert.addAction(UIAlertAction(title: Constants.HomScreenConstants.Dismiss, style: .cancel, handler:{ (UIAlertAction)in
            
        }))
        
        self.present(alert, animated: true, completion: {
            print("completion block")
        })
    }
    
    // MARK: Common Methods
    
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
    func ShowDeleteAlertWithButtonAction(title: String,groupData : GroupListData){
        let alert = UIAlertController(title: Constants.AlertConstants.Alert, message: title, preferredStyle: UIAlertController.Style.alert)
        alert.addAction(UIAlertAction(title: Constants.AlertConstants.Delete, style: UIAlertAction.Style.default, handler: {(_: UIAlertAction!) in
            DispatchQueue.main.async {
                self.callDeleteGroupApi(groupData : groupData)
            }
        }))
        alert.addAction(UIAlertAction(title: Constants.AlertConstants.CancelButton, style: UIAlertAction.Style.default, handler: {(_: UIAlertAction!) in
            DispatchQueue.main.async {
                
            }
        }))
        self.present(alert, animated: true, completion: nil)
    }
    
    // show or hide tablew view to display instructiona whene there is no data
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
    
    // MARK: Service Calls
    
    
    // Create GroupList Api Call
    func getAllGroupsApi() {
        self.showActivityIndicator()
        let groupUrl : URL = URL(string: Constants.ApiPath.UserApisUrl + Utils.shared.getUserId() + Constants.ApiPath.GroupListUrl )!
        GroupService.shared.getGroupListData(groupListUrl:  groupUrl) { (result : Result<GroupListModel, Error>) in
            switch result {
            case .success(let groupResponse):
                self.groupList.removeAll()
//                let groupMembers : [GroupMemberModel]  = Array(RealmManager.sharedInstance.getGroupMemeberDataFromDB())
                for groupdata in groupResponse.groupListData {
                    if ((groupdata.status == Utils.GroupStatus.isActive.rawValue || groupdata.status == Utils.GroupStatus.isCompleted.rawValue || groupdata.status == Utils.GroupStatus.isScheduled.rawValue) && groupdata.groupCreatedBy == Utils.shared.getUserId()) {
//                        for member in groupdata.groupMember {
//                            let memberArr = groupMembers.filter { $0.groupMemberData.first?.groupMemberId == member.memberId }
//                            member.deviceType = memberArr.first?.groupMemberData.first?.deviceType
//                        }
                     let groupName = groupdata.name.components(separatedBy: "+")
                        if groupName.count == 2 && groupName[0] == Constants.AddDeviceConstants.Individual && (groupdata.groupMember.first?.memberStatus == Utils.GroupStatus.isApproved.rawValue || groupdata.groupMember.first?.memberStatus == Utils.GroupStatus.isPending.rawValue ) {
                             self.groupList.append(groupdata)
                        } else if groupName.count != 2{
                            self.groupList.append(groupdata)
                        }
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
    func callDeleteGroupApi(groupData : GroupListData) {
        self.showActivityIndicator()
        
        let deleteGroupUrl : URL = URL(string: Constants.ApiPath.UserApisUrl + Utils.shared.getUserId() + Constants.ApiPath.CreateGroupUrl + "/"  +  groupData.groupId )!
        GroupService.shared.deleteGroup(deleteGroupUrl:  deleteGroupUrl) { (result : Result<GroupModel, Error>) in
            switch result {
            case .success(let groupResponse):
                print(groupResponse)
                DispatchQueue.main.async {
                    self.hideActivityIndicator()
                    self.getAllGroupsApi()
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
    
    
    // Request Consent APi call
    func callRequestConsentApi(groupData : GroupListData) {
         self.showActivityIndicator()
        let requestConsentUrl : URL = URL(string: Constants.ApiPath.UserApisUrl + Utils.shared.getUserId() + Constants.ApiPath.CreateGroupUrl + "/" + groupData.groupId + Constants.ApiPath.CreateMultiple + Constants.ApiPath.GenerateConsent)!
        let consentNumber : [String : Any] = ["phone" : groupData.groupMember.first?.memberPhone ?? ""]
        let parameters : [String : Any] = ["consent" : consentNumber]
        ConsentService.shared.generateConsentToken(generateConsentUrl: requestConsentUrl, parameters: parameters) { (result : Result<ConsentModel, Error>) in
            switch result {
            case .success(let groupResponse):
                print(groupResponse)
                DispatchQueue.main.async {
                    self.hideActivityIndicator()
                    self.ShowALert(title: Constants.HomScreenConstants.ConsentAlert)
                    self.getAllGroupsApi()
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
