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

class GroupBaseClass: UIViewController {
    
    var name : String = ""
    var mobileNumber : String = ""
    
    // Create Group Api Call
      func callCreateGroupApi() {
          self.showActivityIndicator()
          let groupUrl : URL = URL(string: Constants.ApiPath.UserApisUrl + Utils.shared.getUserId() + Constants.ApiPath.CreateGroupUrl )!
          let sessionParams : [String : Int64] = ["from" : Utils.shared.getFromEpochTime(), "to" : Utils.shared.getToEpochTime()]
          let groupParams : [String : Any] = ["name" : Constants.AddDeviceConstants.Individual+"+"+Constants.AddDeviceConstants.PeopleTracker, "session" : sessionParams, "type" : "one_to_one"]
          GroupService.shared.createGroup(createGroupUrl:  groupUrl, parameters: groupParams) { (result : Result<GroupModel, Error>) in
              switch result {
              case .success(let groupResponse):
                  let groupId = groupResponse.groupData?.groupId ?? ""
                  if groupId.count > 0 {
                      DispatchQueue.main.async {
                          self.addMemberToGroupApi(notificationName: Constants.NotificationName.GetGroupList,groupId: groupId)
                      }
                  } else {
                      DispatchQueue.main.async {
                          self.hideActivityIndicator()
                          self.ShowALert(title: Constants.ErrorMessage.Somethingwentwrong)
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
    func addMemberToGroupApi(notificationName : String, groupId : String) {
        self.showActivityIndicator()
        let addMemberUrl : URL = URL(string: Constants.ApiPath.UserApisUrl + Utils.shared.getUserId() + Constants.ApiPath.CreateGroupUrl + "/" + groupId + Constants.ApiPath.CreateMultiple )!
        let sessionParams : [String] = ["events"]
        let groupParams : [String : Any] = ["name": self.name,"phone" : self.mobileNumber ,"entities" : sessionParams]
        let params : [String : Any] = ["consents" : [groupParams]]
        GroupService.shared.addMemberToGroup(addMemberInGroupUrl:  addMemberUrl,parameters: params) { (result : Result<GroupMemberModel, Error>) in
            switch result {
            case .success(let groupResponse):
                print(groupResponse)
                DispatchQueue.main.async {
                    self.hideActivityIndicator()
                    NotificationCenter.default.post(name: Notification.Name(notificationName), object: nil)
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
