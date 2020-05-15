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

class CreateGroupScreen: BaseViewController,UITextFieldDelegate {
    
    var navtitle : String = ""
    var groupData = GroupListData()
    @IBOutlet weak var createGroup: UIButton!
    
    @IBOutlet weak var groupName: UITextField!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = navtitle
        self.initialiseData()
    }
    
    func initialiseData() {
        self.groupName.delegate = self
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
        self.groupname = groupName.text!
        (groupData.groupId.count > 0) ? self.callCreateGroupApi(methodType: NetworkManager.Method.put.rawValue, isFromCreateGroup : true, groupId: groupData.groupId) : self.callCreateGroupApi(methodType: NetworkManager.Method.post.rawValue, isFromCreateGroup: true)
        
    }
    
    // MARK: UITextField Delegate
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        self.view.endEditing(true)
        return false
    }
    
}
