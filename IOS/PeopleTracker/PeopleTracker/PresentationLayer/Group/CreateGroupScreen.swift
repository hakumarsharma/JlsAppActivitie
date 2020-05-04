//
//  CreateGroupScreen.swift
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

class CreateGroupScreen: UIViewController {
    
    var navtitle : String = ""
    var groupData = GroupListData()
    @IBOutlet weak var createGroup: UIButton!
    
    @IBOutlet weak var groupName: UITextField!
    
    override func viewDidLoad() {
             super.viewDidLoad()
             self.title = navtitle
        if groupData.groupId.count > 0 {
            self.groupName.text = groupData.name
            self.createGroup.setTitle("Update Group", for: .normal)
        } else {
             self.createGroup.setTitle("Create Group", for: .normal)
        }
    }
    
    @IBAction func createGroupButtonAction(_ sender: Any) {
        if groupName.text?.count == 0 {
            self.ShowALert(title: Constants.CreateGroupConstants.CreateGroup)
            return
        }
        
        (groupData.groupId.count > 0) ? self.callUpdateGroupApi() : self.callCreateGroupApi()
     
    }
    
    // Create Group Api Call
    func callCreateGroupApi() {
        self.showActivityIndicator()
        let groupUrl : URL = URL(string: Constants.ApiPath.UserApisUrl + Utils.shared.getUserId() + Constants.ApiPath.CreateGroupUrl )!
        let sessionParams : [String : Int64] = ["from" : Utils.shared.getFromEpochTime(), "to" : Utils.shared.getToEpochTime()]
        let groupParams : [String : Any] = ["name" : groupName.text!, "session" : sessionParams, "type" : "one_to_one"]
        GroupService.shared.createGroup(createGroupUrl:  groupUrl, parameters: groupParams) { (result : Result<GroupModel, Error>) in
            switch result {
            case .success(let groupResponse):
                  print(groupResponse)
                   DispatchQueue.main.async {
                  self.hideActivityIndicator()
                  NotificationCenter.default.post(name: Notification.Name(Constants.NotificationName.GetGroupList), object: nil)              
                  self.navigationController?.popViewController(animated: true)
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
    
    // Update Group Api Call
    func callUpdateGroupApi() {
        self.showActivityIndicator()
    
        let updateGroupUrl : URL = URL(string: Constants.ApiPath.UserApisUrl + Utils.shared.getUserId() + Constants.ApiPath.CreateGroupUrl + "/"  +  groupData.groupId )!
        let groupParams : [String : Any] = ["name" : groupName.text!]
        GroupService.shared.updateGroup(updateGroupUrl:  updateGroupUrl, parameters: groupParams) { (result : Result<GroupModel, Error>) in
            switch result {
            case .success(let groupResponse):
                  print(groupResponse)
                   DispatchQueue.main.async {
                  self.hideActivityIndicator()
                  NotificationCenter.default.post(name: Notification.Name(Constants.NotificationName.GetGroupList), object: nil)
                  self.navigationController?.popViewController(animated: true)
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
