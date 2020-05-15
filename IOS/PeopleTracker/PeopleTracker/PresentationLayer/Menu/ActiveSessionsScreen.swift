//
//  ActiveSessionsScreen.swift
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

class ActiveSessionsScreen: UIViewController,UITableViewDelegate, UITableViewDataSource, ActiveSessionDelegate {
    
    
    @IBOutlet weak var usersTableView: UITableView!
    @IBOutlet weak var instructionsLabel: UILabel!
    var groupList     : [GroupListData] = []
    var refreshControl = UIRefreshControl()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = "Active Sessions"
        self.initialiseData()
        self.getAllGroupsApi()
    }
    
    func initialiseData() {
        usersTableView.delegate = self
        usersTableView.dataSource = self
        usersTableView.tableFooterView = UIView()
        self.createNotification()
        
        refreshControl.attributedTitle = NSAttributedString(string: "Pull to refresh")
        refreshControl.addTarget(self, action: #selector(self.refresh(_:)), for: .valueChanged)
        usersTableView.addSubview(refreshControl)
    }
    @objc func refresh(_ sender: AnyObject) {
        self.getAllGroupsApi()
    }
    // MARK: UITableView Delegate and DataSource methods
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.groupList.count
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 140.0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "cellIdentifier") as! ActiveSessionCell
        cell.selectionStyle = .none
        let groupListDetails = self.groupList[indexPath.row]
        cell.setGroupData(groupData: groupListDetails)
        cell.activeSessionDelegate = self
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
    func optionsClicked(cell: ActiveSessionCell) {
        let indexpath = self.usersTableView.indexPath(for: cell)
        let groupData = self.groupList[indexpath!.row]
        self.showActionSheet(groupData: groupData)
    }
    
    // MARK : Navigatation screens
    func navigateToMapsScreen() {
        let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
        let mapsViewController = storyBoard.instantiateViewController(withIdentifier: Constants.ScreenNames.MapsScreen) as! MapsScreen
        self.navigationController?.pushViewController(mapsViewController, animated: true)
    }
    
    func navigateToGroupListScreen(title : String, groupData :  GroupListData) {
        let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
        let groupListViewController = storyBoard.instantiateViewController(withIdentifier: Constants.ScreenNames.GroupListScreen) as! GroupListScreen
        groupListViewController.navtitle = title
        groupListViewController.groupData = groupData
        groupListViewController.isActiveSession = true
        self.navigationController?.pushViewController(groupListViewController, animated: true)
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
        let isAdmin = (groupData.groupCreatedBy == Utils.shared.getUserId())
        
        let alert = UIAlertController(title: Constants.HomScreenConstants.Select, message: "", preferredStyle: .actionSheet)
        if isAdmin {
            alert.addAction(UIAlertAction(title:Constants.HomScreenConstants.Remove, style: .default , handler:{ (UIAlertAction)in
                self.callExitorRemoveMemberFromGroupApi(groupData: groupData, status: Utils.GroupStatus.isRemoved.rawValue)
            }))
        } else {
            alert.addAction(UIAlertAction(title:Constants.HomScreenConstants.Exit, style: .default , handler:{ (UIAlertAction)in
                self.callExitorRemoveMemberFromGroupApi(groupData: groupData, status: Utils.GroupStatus.isExited.rawValue)
            }))
        }
        alert.addAction(UIAlertAction(title:Constants.HomScreenConstants.Track, style: .default , handler:{ (UIAlertAction)in
            self.navigateToMapsScreen()
        }))
        alert.addAction(UIAlertAction(title: Constants.HomScreenConstants.Dismiss, style: .cancel, handler:{ (UIAlertAction)in
            self.dismiss(animated: true) {
                
            }
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
                for groupdata in groupResponse.groupListData {
                    if groupdata.status == Utils.GroupStatus.isActive.rawValue {
                        let groupName = groupdata.name.components(separatedBy: "+")
                        if groupName.count == 2 && groupName[0] == Constants.AddDeviceConstants.Individual && groupdata.groupMember.first?.memberStatus == Utils.GroupStatus.isApproved.rawValue {
                            self.groupList.append(groupdata)
                        } else if groupName.count != 2{
                            self.groupList.append(groupdata)
                        }
                    }
                }
                DispatchQueue.main.async {
                    self.hideActivityIndicator()
                    self.showHideTableView()
                    self.refreshControl.endRefreshing()
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
    
    // Exit from group APi call
    func callExitorRemoveMemberFromGroupApi(groupData : GroupListData,status : String) {
        self.showActivityIndicator()
        let exitGroupUrl : URL = URL(string: Constants.ApiPath.UserApisUrl + Utils.shared.getUserId() + Constants.ApiPath.CreateGroupUrl + "/" + groupData.groupId + Constants.ApiPath.CreateMultiple + Constants.ApiPath.ApproveConsent)!
        let consentNumber : [String : Any] = ["phone" : groupData.groupMember.first?.memberPhone ?? "","status": status]
        let parameters : [String : Any] = ["consent" : consentNumber]
        GroupService.shared.exitOrRemoveFromGroup(exitGroupUrl: exitGroupUrl, parameters: parameters) { (result : Result<UserModel, Error>) in
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
    
    
}

