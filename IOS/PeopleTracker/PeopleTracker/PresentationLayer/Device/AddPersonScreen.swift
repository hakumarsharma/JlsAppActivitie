//
//  AddPersonScreen.swift
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

class AddPersonScreen: UIViewController {
    
    @IBOutlet weak var nameTxt: UITextField!
    @IBOutlet weak var mobileNumberTxt: UITextField!
    
    var navtitle : String = ""
    var groupId : String = ""
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = navtitle
    }
    
    @IBAction func addPersonButtonAction(_ sender: Any) {
        
       if nameTxt.text?.count == 0 {
            self.ShowALert(title: Constants.AddDeviceConstants.Name)
            return
        }
        if mobileNumberTxt.text?.count == 0 || !(mobileNumberTxt.text?.isValidPhone ?? true){
            self.ShowALert(title: Constants.LoginScreenConstants.PhoneNumber)
            return
        }
        self.addMemberToGroupApi()
    }
    
    
    // Add member to group Api Call
    func addMemberToGroupApi() {
        self.showActivityIndicator()
        let addMemberUrl : URL = URL(string: Constants.ApiPath.UserApisUrl + Utils.shared.getUserId() + Constants.ApiPath.CreateGroupUrl + "/" + self.groupId + Constants.ApiPath.CreateMultiple )!
        let sessionParams : [String] = ["events"]
        let groupParams : [String : Any] = ["phone" : self.mobileNumberTxt.text! ,"entities" : sessionParams]
        let params : [String : Any] = ["consents" : [groupParams]]
        GroupService.shared.addMemberToGroup(addMemberInGroupUrl:  addMemberUrl, memebrName: self.nameTxt.text!,parameters: params) { (result : Result<GroupMemberModel, Error>) in
            switch result {
            case .success(let groupResponse):
                print(groupResponse)
                DispatchQueue.main.async {
                    self.hideActivityIndicator()
                    NotificationCenter.default.post(name: Notification.Name(Constants.NotificationName.GetMemebersInGroup), object: nil) 
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
