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

class GroupListScreen: UIViewController,UITableViewDelegate, UITableViewDataSource, GroupCellDelegate {
    
    var navtitle : String = ""
    var groupData  = GroupListData()
    var isActiveSession : Bool = false
    @IBOutlet weak var groupListTableView: UITableView!
    @IBOutlet weak var instructionLbl: UILabel!
    var isMemeberAdded : Bool = false
    var refreshControl = UIRefreshControl()

    
    let actionButton = JJFloatingActionButton()
    var groupMemberData: [GroupMemberData] = []
    var dbMemberData: [GroupMemberData] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = navtitle
        self.navigationItem.setHidesBackButton(true, animated: true)
        self.initialiseData()
        if groupData.status == Utils.GroupStatus.isCompleted.rawValue {
            self.ShowALert(title: Constants.GroupConstants.SessionEnd)
        }
    }
    
    func initialiseData() {
        groupListTableView.delegate = self
        groupListTableView.dataSource = self
        groupListTableView.tableFooterView = UIView()
        self.createNotification()
        self.getMemberInGroupApi()
        self.createLeftBarButtonItem()
        let currentEpochTime = Utils.shared.getEpochTime(val:0)
        if !isActiveSession && groupData.groupMember.count < 10 && !(groupData.groupSession!.from! <= currentEpochTime && groupData.groupSession!.to! >= currentEpochTime) {
            floatingActionButton()
        }
        refreshControl.attributedTitle = NSAttributedString(string: "Pull to refresh")
        refreshControl.addTarget(self, action: #selector(self.refresh(_:)), for: .valueChanged)
        groupListTableView.addSubview(refreshControl)
    }
    @objc func refresh(_ sender: AnyObject) {
        self.getMemberInGroupApi()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        if isMemeberAdded {
            NotificationCenter.default.post(name: Notification.Name(Constants.NotificationName.GetGroupList), object: nil)
        }
    }
    
    func createLeftBarButtonItem(){
        let backBtn : UIBarButtonItem = UIBarButtonItem.init(image: UIImage(named: "back"), style: .plain, target: self, action: #selector(backButton(sender:)))
        backBtn.tintColor = .white
        self.navigationItem.setLeftBarButton(backBtn, animated: true)
        let trackBtn : UIBarButtonItem = UIBarButtonItem.init(title: "Track", style: .plain, target: self, action: #selector(trackButton(sender:)))
        trackBtn.tintColor = UIColor.white
        self.navigationItem.setRightBarButton(trackBtn, animated: true)
    }
    
    func createNotification() {
        NotificationCenter.default.addObserver(self, selector: #selector(callgetMembersIngroupApi(notification:)), name: NSNotification.Name(rawValue: Constants.NotificationName.GetMemebersInGroup), object: nil)
    }
    
    @objc func callgetMembersIngroupApi(notification: NSNotification) {
        self.isMemeberAdded = true
        self.getMemberInGroupApi()
    }
    
    func showHideTableView() {
        if self.groupMemberData.count > 0 {
            self.instructionLbl.isHidden = true
            self.groupListTableView.isHidden = false
        } else {
            self.instructionLbl.isHidden = false
            self.groupListTableView.isHidden = true
        }
    }
    
    // MARK: UITableView Delegate and DataSource methods
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.groupMemberData.count
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 135.0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "cellIdentifier") as! GroupCell
        cell.selectionStyle = .none
        let memberData = groupMemberData[indexPath.row]
        cell.groupcellDelegate = self
        var imgstr : String = ""
        if indexPath.row % 2 == 0{
            imgstr = "user0"
        }else if indexPath.row % 3 == 0 {
            imgstr = "user1"
        }else {
            imgstr = "user2"
        }
        cell.setUserData(memberData: memberData, groupData: self.groupData, imgStr : imgstr)
        cell.deleteButton.isHidden = true
        if isActiveSession {
            cell.deleteButton.isHidden = false
        }
        return cell
    }
    
    // MARK: Navigation methods
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
    }
    
    func deleteButtonClicked(cell: GroupCell) {
        if groupData.groupCreatedBy == Utils.shared.getUserId() {
            self.ShowALertWithButtonAction(title: Constants.HomScreenConstants.DeleteMemeber, status: Utils.GroupStatus.isRemoved.rawValue)
        } else {
            self.ShowALertWithButtonAction(title: Constants.HomScreenConstants.ExitMemeber, status: Utils.GroupStatus.isExited.rawValue)
        }
    }
    
    // Alert with button action
    func ShowALertWithButtonAction(title: String, status : String){
        let alert = UIAlertController(title: Constants.AlertConstants.Alert, message: title, preferredStyle: UIAlertController.Style.alert)
        alert.addAction(UIAlertAction(title: Constants.AlertConstants.Yes, style: UIAlertAction.Style.default, handler: {(_: UIAlertAction!) in
            DispatchQueue.main.async {
                self.callExitorRemoveMemberFromGroupApi(groupData: self.groupData, status: status)
            }
        }))
        alert.addAction(UIAlertAction(title: Constants.AlertConstants.No, style: UIAlertAction.Style.cancel, handler: {(_: UIAlertAction!) in
        }))
        self.present(alert, animated: true, completion: nil)
    }
    
    @objc func trackButton(sender: UIBarButtonItem) {
        if groupMemberData.count > 0 {
            let approvedArr = groupMemberData.filter({$0.status == Utils.GroupStatus.isApproved.rawValue})
            if approvedArr.count > 0 {
                let currentEpochTime = Utils.shared.getEpochTime(val:0)
                if(groupData.groupSession!.from! <= currentEpochTime && groupData.groupSession!.to! >= currentEpochTime) {
                    navigateToMapsScreen()
                }else if groupData.groupSession!.from! >= currentEpochTime {
                    self.ShowALert(title: Constants.GroupConstants.SessionStart)
                }else {
                    self.ShowALert(title: Constants.GroupConstants.SessionEnd)
                }
            }else {
                self.ShowALert(title:  Constants.GroupConstants.ConsentApprovalStatus)
            }
        } else {
            self.ShowALert(title:  Constants.GroupConstants.AddMemeber)
        }
    }
    @objc func backButton(sender: UIBarButtonItem) {
        self.navigationController?.popViewController(animated: true)
    }
    
    func navigateToMapsScreen() {
        let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
        let mapsViewController = storyBoard.instantiateViewController(withIdentifier: Constants.ScreenNames.MapsScreen) as! MapsScreen
        mapsViewController.groupData.append(groupData)
        self.navigationController?.pushViewController(mapsViewController, animated: true)
    }
    
    func navigateToAddDeviceScreen(title : String) {
        let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
        let addDeviceViewController = storyBoard.instantiateViewController(withIdentifier: Constants.ScreenNames.AddDeviceScreen) as! AddDeviceScreen
        addDeviceViewController.navtitle = title
        addDeviceViewController.groupId = self.groupData.groupId
        self.navigationController?.pushViewController(addDeviceViewController, animated: true)
    }
    func navigateToAddPersonScreen(title : String) {
        let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
        let addPersonViewController = storyBoard.instantiateViewController(withIdentifier: Constants.ScreenNames.AddPersonScreen) as! AddPersonScreen
        addPersonViewController.navtitle = title
        addPersonViewController.groupId = self.groupData.groupId
        self.navigationController?.pushViewController(addPersonViewController, animated: true)
    }
    
    func navigateToCreateGroupScreen() {
        let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
        let createGroupViewController = storyBoard.instantiateViewController(withIdentifier: Constants.ScreenNames.CreateGroupScreen) as! CreateGroupScreen
        self.navigationController?.pushViewController(createGroupViewController, animated: true)
    }
    
    // MARK: Show floating button
    func floatingActionButton () {
        actionButton.addItem(title: Constants.HomScreenConstants.AddDevice, image: UIImage(named: "device")?.withRenderingMode(.alwaysTemplate)) { item in
            // do something
            self.navigateToAddDeviceScreen(title: Constants.HomScreenConstants.AddDevice)
        }
        
        actionButton.addItem(title: Constants.HomScreenConstants.AddPerson, image: UIImage(named: "ic_user")?.withRenderingMode(.alwaysTemplate)) { item in
            // do something
            self.navigateToAddPersonScreen(title: Constants.HomScreenConstants.AddPerson)
        }
        
        view.addSubview(actionButton)
        actionButton.translatesAutoresizingMaskIntoConstraints = false
        actionButton.trailingAnchor.constraint(equalTo: view.safeAreaLayoutGuide.trailingAnchor, constant: -16).isActive = true
        actionButton.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor, constant: -40).isActive = true
    }
    
    
    // Get memebers in group Api Call
    func getMemberInGroupApi() {
        self.showActivityIndicator()
        let getListOfMembersUrl : URL = URL(string: Constants.ApiPath.UserApisUrl + Utils.shared.getUserId() + Constants.ApiPath.CreateGroupUrl + "/" + self.groupData.groupId + Constants.ApiPath.CreateMultiple )!
        GroupService.shared.getMembersFromGroup(getMembersInGroupUrl:  getListOfMembersUrl) { (result : Result<GroupMemberModel, Error>) in
            switch result {
            case .success(let groupResponse):
                self.groupMemberData.removeAll()
                //               let memberData = RealmManager.sharedInstance.getGroupMemeberDataFromDB()
                for response in groupResponse.groupMemberData {
                    if (response.status != Utils.GroupStatus.isRemoved.rawValue && response.status != Utils.GroupStatus.isExited.rawValue) {
                        //                    for member in memberData {
                        //                        let memberArr = member.groupMemberData.filter { $0.groupMemberId == response.groupMemberId }
                        //                        response.name = memberArr.first?.name
                        //                        print(memberArr)
                        //                    }
                        self.groupMemberData.append(response)
                    }
                }
                DispatchQueue.main.async {
                    self.hideActivityIndicator()
                    self.showHideTableView()
                    self.refreshControl.endRefreshing()
                    self.groupListTableView.reloadData()
                }
            case .failure(let error):
                if type(of: error) == NetworkManager.ErrorType.self {
                    DispatchQueue.main.async {
                        self.hideActivityIndicator()
                        if error as! NetworkManager.ErrorType == NetworkManager.ErrorType.MobileNumberAlreadyExists{
                            self.ShowALert(title: Constants.ErrorMessage.MobileNumberExists)
                        } else {
                            self.ShowALert(title: Utils.shared.handleError(error: error as! NetworkManager.ErrorType))
                        }
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
                    self.getMemberInGroupApi()
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
