//
//  GroupBaseClass.swift
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

class BaseViewController: UIViewController {
    
    var groupname : String = ""
    var memberName : String = ""
    var memberNumber : String = ""
    
    
    //Regsiteration Api Call
    func generateTokenApiCall(tokenUrl : String, params : [String : Any]) {
        self.showActivityIndicator()
        UserService.shared.generateRegistartionTokenwith(generateTokenUrl: URL(string: tokenUrl)!, parameters: params) { (result : Result<UserModel, Error>) in
            switch result {
            case .success(_):
                DispatchQueue.main.async {
                    self.hideActivityIndicator()
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
    
    //  API to add device using verify and assign
    func callAddDeviceApi() {
        let deviceURL = URL(string: Constants.ApiPath.UserApisUrl + Utils.shared.getUserId() + Constants.ApiPath.AddDeviceUrl + Utils.shared.getUgsToken())!
        let deviceDetails : [[String : String]] = [["mac": self.memberNumber,"identifier": "imei","name": self.memberName ,"phone": self.memberNumber,"type": "watch","model": "watch",]]
        let flagDetails : [String : Bool] = ["isSkipAddDeviceToGroup" : false]
        let deviceParams :  [String : Any] = ["devices" : deviceDetails, "flags": flagDetails]
        DeviceService.shared.addAndGetDeviceDetails(with: deviceURL, parameters: deviceParams) { (result : (Result<DeviceModel, Error>)) in
            switch result {
            case .success( _):
                DispatchQueue.main.async {
                    self.hideActivityIndicator()
                    NotificationCenter.default.post(name: Notification.Name(Constants.NotificationName.NavigateToHome), object: nil)
                }
            case .failure(_):
                DispatchQueue.main.async {
                    self.hideActivityIndicator()
                    NotificationCenter.default.post(name: Notification.Name(Constants.NotificationName.NavigateToHome), object: nil)
                }
            }
        }
    }
    
    // Create Group Api Call
    func callCreateGroupApi(methodType : String, isFromCreateGroup : Bool, groupId : String? = nil,groupMemebers : [GroupMember]? = nil) {
        self.showActivityIndicator()
        var groupUrl: String = ""
        var groupParams : [String : Any] = [:]
        if groupId != nil{
            groupUrl = Constants.ApiPath.UserApisUrl + Utils.shared.getUserId() + Constants.ApiPath.CreateGroupUrl + "/" + groupId!
            groupParams = ["name" : self.groupname]
        } else {
            groupUrl = Constants.ApiPath.UserApisUrl + Utils.shared.getUserId() + Constants.ApiPath.CreateGroupUrl
            let sessionParams : [String : Int64] = ["from" : Utils.shared.getFromEpochTime(), "to" : Utils.shared.getToEpochTime()]
            groupParams  = ["name" : self.groupname, "session" : sessionParams, "type" : "one_to_one"]
            
        }
        GroupService.shared.createGroup(createGroupUrl:  URL(string: groupUrl)!, parameters: groupParams, methodType: methodType) { (result : Result<GroupModel, Error>) in
            switch result {
            case .success(let groupResponse):
                if isFromCreateGroup {
                    DispatchQueue.main.async {
                        self.hideActivityIndicator()
                        NotificationCenter.default.post(name: Notification.Name(Constants.NotificationName.GetGroupList), object: nil)
                        self.navigationController?.popViewController(animated: true)
                    }
                } else {
                    let groupId = groupResponse.groupData?.groupId ?? ""
                    if groupId.count > 0 {
                        DispatchQueue.main.async {
                            self.addMemberToGroupApi(notificationName: Constants.NotificationName.GetGroupList,groupId: groupId,groupMemebers : groupMemebers)
                        }
                    } else {
                        DispatchQueue.main.async {
                            self.hideActivityIndicator()
                            self.ShowALert(title: Constants.ErrorMessage.Somethingwentwrong)
                        }
                    }
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
    
    // Add member to group Api Call
    func addMemberToGroupApi(notificationName : String, groupId : String,groupMemebers : [GroupMember]? = nil) {
        self.showActivityIndicator()
        let addMemberUrl : URL = URL(string: Constants.ApiPath.UserApisUrl + Utils.shared.getUserId() + Constants.ApiPath.CreateGroupUrl + "/" + groupId + Constants.ApiPath.CreateMultiple )!
        let sessionParams : [String] = ["events"]
        var params : [String : Any] = [:]
        if groupMemebers != nil {
            var memebersArr : [[String : Any]] = []
            for memeber in groupMemebers! {
                memebersArr.append(["name": memeber.memberName ?? "" ,"phone" : memeber.memberPhone ?? "" ,"entities" : sessionParams])
            }
            params = ["consents" : memebersArr]
        } else {
            let groupParams  : [String : Any] = ["name": self.memberName,"phone" : self.memberNumber ,"entities" : sessionParams]
            params = ["consents" : [groupParams]]
        }
        
        GroupService.shared.addMemberToGroup(addMemberInGroupUrl:  addMemberUrl,parameters: params) { (result : Result<GroupMemberModel, Error>) in
            switch result {
            case .success(let groupResponse):
                print(groupResponse)
                DispatchQueue.main.async {
                    self.hideActivityIndicator()
                    NotificationCenter.default.post(name: Notification.Name(notificationName), object: nil)
                    if groupMemebers == nil {
                        self.navigationController?.popViewController(animated: true)
                    }
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
    
    // Approve consent
    func callApproveOrRejectConsent(consentId: String, token : String, status : String) {
        self.showActivityIndicator()
        let consentUrl : URL = URL(string: Constants.ApiPath.UserApisUrl + "sessiongroupconsents" + "/" + consentId + Constants.ApiPath.ApproveConsent)!
        let parameters : [String : Any] = ["consent" : ["token" : ["value" : token]],"status":status]
        ConsentService.shared.approveOrRejectConsentWithToken(consentUrl: consentUrl, parameters: parameters) { (result : Result<ConsentModel, Error>) in
            switch result {
            case .success(_):
                DispatchQueue.main.async {
                    self.hideActivityIndicator()
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
