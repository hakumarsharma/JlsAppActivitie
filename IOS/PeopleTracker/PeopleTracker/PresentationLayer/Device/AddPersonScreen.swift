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
import ContactsUI

class AddPersonScreen: BaseViewController,CNContactPickerDelegate,UITextFieldDelegate {
    
    @IBOutlet weak var nameTxt: UITextField!
    @IBOutlet weak var mobileNumberTxt: UITextField!
    
    var navtitle : String = ""
    var groupId : String = ""
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = navtitle
        self.navigationItem.setHidesBackButton(true, animated: true)
        self.initialiseData()
    }
    
    func initialiseData() {
        self.createBackBarButtonItem()
        self.createNavBarItems()
        self.nameTxt.delegate = self
        self.mobileNumberTxt.delegate = self
        self.nameTxt.inputAccessoryView = self.setToolbarWithDoneButton()
        self.mobileNumberTxt.inputAccessoryView = self.setToolbarWithDoneButton()
    }
    func setToolbarWithDoneButton() -> UIToolbar{
            let flexSpace = UIBarButtonItem(barButtonSystemItem: .flexibleSpace, target: nil, action: nil)
            let keyboardDoneButtonView = UIToolbar.init()
            keyboardDoneButtonView.sizeToFit()
            let doneButton = UIBarButtonItem.init(barButtonSystemItem: UIBarButtonItem.SystemItem.done,
                                                                      target: self,
                                                                      action: #selector(doneClicked(sender:)))

            keyboardDoneButtonView.items = [flexSpace,doneButton]
            return keyboardDoneButtonView
        }
       
        @objc func doneClicked(sender: AnyObject) {
          self.view.endEditing(true)
        }
    func createBackBarButtonItem() {
        let backBtn : UIBarButtonItem = UIBarButtonItem.init(image: UIImage(named: "back"), style: .plain, target: self, action: #selector(backButton(sender:)))
        backBtn.tintColor = .white
        self.navigationItem.setLeftBarButton(backBtn, animated: true)
    }
    
    @objc func backButton(sender: UIBarButtonItem) {
        self.navigationController?.popViewController(animated: true)
    }
    
    // creating navigation bar right item for adding contact
    func createNavBarItems(){
        let contactsBtn : UIBarButtonItem = UIBarButtonItem.init(image: UIImage.init(named: "contact"), style: .plain, target: self, action: #selector(contactsButtonAction(sender:)))
        contactsBtn.setTitleTextAttributes( [NSAttributedString.Key.foregroundColor : UIColor.white], for: .normal)
        self.navigationItem.setRightBarButton(contactsBtn, animated: true)
    }
    
    @objc func contactsButtonAction(sender: UIBarButtonItem) {
        let store = CNContactStore()
        store.requestAccess(for: .contacts) { granted, error in
            guard granted else {
                DispatchQueue.main.async {
                    self.ShowALert(title: "")
                }
                return
            }
            DispatchQueue.main.async {
                let contacVC = CNContactPickerViewController()
                contacVC.delegate = self
                self.present(contacVC, animated: true, completion: nil)
            }
        }
        
    }
    
    // MARK: Delegate method CNContectPickerDelegate
    func contactPicker(_ picker: CNContactPickerViewController, didSelect contact: CNContact) {
        let numbers = ((contact.phoneNumbers.first)?.value)?.stringValue ?? ""
        let phoneNumber = numbers.replacingOccurrences(of: "[() -]", with: "", options: .regularExpression, range: nil)
        self.nameTxt.text = contact.givenName + contact.familyName
        self.mobileNumberTxt.text = phoneNumber
    }
    
    func contactPickerDidCancel(_ picker: CNContactPickerViewController) {
        self.dismiss(animated: true, completion: nil)
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
        if groupId.count > 0 {
            self.memberName = nameTxt.text!
            self.memberNumber = mobileNumberTxt.text!
            self.addMemberToGroupApi(notificationName: Constants.NotificationName.GetMemebersInGroup, groupId: groupId)
        } else {
            self.groupname = Constants.AddDeviceConstants.Individual+"+"+Constants.AddDeviceConstants.PeopleTracker
            self.memberName = nameTxt.text!
            self.memberNumber = mobileNumberTxt.text!
            self.callCreateGroupApi(methodType: NetworkManager.Method.post.rawValue, isFromCreateGroup: false)
        }
    }
    
    // MARK: UITextField Delegate
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        self.view.endEditing(true)
        return false
    }
    
}
