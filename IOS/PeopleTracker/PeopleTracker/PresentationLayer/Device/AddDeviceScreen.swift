//
//  AddDeviceScreen.swift
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

class AddDeviceScreen: BaseViewController,UITextFieldDelegate {
    @IBOutlet weak var nameTxt: UITextField!
    @IBOutlet weak var ownerNumberTxt: UITextField!
    @IBOutlet weak var deviceNumberTxt: UITextField!
    @IBOutlet weak var deviceType: UIButton!
    
    var navtitle : String = ""
    var groupId : String = ""
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = navtitle
        self.navigationItem.setHidesBackButton(true, animated: true)
        self.intialiseData()
    }
    func intialiseData() {
        self.createBackBarButtonItem()
        deviceType.layer.borderWidth = 1.0
        deviceType.layer.borderColor = UIColor.Common.TextFieldBorderColor.cgColor
        deviceType.layer.cornerRadius = 4.0
        
        self.nameTxt.delegate = self
        self.ownerNumberTxt.delegate = self
        self.deviceNumberTxt.delegate = self
        
        self.nameTxt.inputAccessoryView = self.setToolbarWithDoneButton()
        self.ownerNumberTxt.inputAccessoryView = self.setToolbarWithDoneButton()
        self.deviceNumberTxt.inputAccessoryView = self.setToolbarWithDoneButton()
        
        self.createNavBarItems()
        self.createNotification()
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
    
    func createNavBarItems(){
        let qrscannerBtn : UIBarButtonItem = UIBarButtonItem.init(image: UIImage.init(named: "qr"), style: .plain, target: self, action: #selector(qrscannerBtnButtonAction(sender:)))
        qrscannerBtn.setTitleTextAttributes( [NSAttributedString.Key.foregroundColor : UIColor.white], for: .normal)
        self.navigationItem.setRightBarButton(qrscannerBtn, animated: true)
    }
    
    // MARK: Notification Methods
    
    func createNotification() {
        NotificationCenter.default.addObserver(self, selector: #selector(setData(notification:)), name: NSNotification.Name(rawValue: Constants.NotificationName.QRdata), object: nil)
    }
    
    @objc func setData(notification: NSNotification) {
        if let notificationData = notification.userInfo {
            if let dataArr = notificationData["qrData"] as? Array<Any> {
                if dataArr.count == 3 {
                    self.nameTxt.text = dataArr[0] as? String
                    self.ownerNumberTxt.text = dataArr[1] as? String
                    self.deviceNumberTxt.text = dataArr[2] as? String
                } else {
                    self.ShowALert(title: Constants.AddDeviceConstants.QrIncorrectData)
                }
            }
        }
    }
    
    
    @objc func qrscannerBtnButtonAction(sender: UIBarButtonItem) {
        
        let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
        let qrScannerViewController = storyBoard.instantiateViewController(withIdentifier: Constants.ScreenNames.QRCodeScanner) as! QRCodeScanner
        self.navigationController?.present(qrScannerViewController, animated: true, completion: nil)
        
    }
    func createBackBarButtonItem() {
        let backBtn : UIBarButtonItem = UIBarButtonItem.init(image: UIImage(named: "back"), style: .plain, target: self, action: #selector(backButton(sender:)))
        backBtn.tintColor = .white
        self.navigationItem.setLeftBarButton(backBtn, animated: true)
    }
    
    @objc func backButton(sender: UIBarButtonItem) {
        self.navigationController?.popViewController(animated: true)
    }
    
    @IBAction func deviceTypeButtonAction(_ sender: Any) {
        
        let alert = UIAlertController(title: Constants.AddDeviceConstants.SelectType, message: "", preferredStyle: .actionSheet)
        self.deviceType.setTitleColor(.black, for: .normal)
        alert.addAction(UIAlertAction(title: Constants.AddDeviceConstants.PetTracker, style: .default , handler:{ (UIAlertAction)in
            self.deviceType.setTitle( Constants.AddDeviceConstants.PetTracker, for: .normal)
        }))
        
        alert.addAction(UIAlertAction(title:Constants.AddDeviceConstants.VehicleTracker, style: .default , handler:{ (UIAlertAction)in
            self.deviceType.setTitle( Constants.AddDeviceConstants.VehicleTracker, for: .normal)
        }))
        alert.addAction(UIAlertAction(title: Constants.AddDeviceConstants.KidTracker, style: .default, handler:{ (UIAlertAction)in
            self.deviceType.setTitle( Constants.AddDeviceConstants.KidTracker, for: .normal)
        }))
        alert.addAction(UIAlertAction(title: Constants.AddDeviceConstants.AdultTracker, style: .default, handler:{ (UIAlertAction)in
            self.deviceType.setTitle( Constants.AddDeviceConstants.AdultTracker, for: .normal)
        }))
        
        self.present(alert, animated: true, completion: {
            print("completion block")
        })
        
    }
    
    @IBAction func addDeviceButtonAction(_ sender: Any) {
        if nameTxt.text?.count == 0 {
            self.ShowALert(title: Constants.AddDeviceConstants.Name)
            return
        }
        if ownerNumberTxt.text?.count == 0 || !(ownerNumberTxt.text?.isValidPhone ?? true){
            self.ShowALert(title: Constants.AddDeviceConstants.OwnerNumber)
            return
        }
        if deviceNumberTxt.text?.count == 0 || !(deviceNumberTxt.text?.isValidPhone ?? true){
            self.ShowALert(title: Constants.LoginScreenConstants.PhoneNumber)
            return
        }
        
        if deviceType.titleLabel?.text == "Device Type"{
            self.ShowALert(title: Constants.AddDeviceConstants.ChooseDeviceType)
            return
        }
        
        if groupId.count > 0 {
            self.memberName = nameTxt.text!
            self.memberNumber = deviceNumberTxt.text!
            self.addMemberToGroupApi(notificationName: Constants.NotificationName.GetMemebersInGroup, groupId: groupId)
        }else {
            self.groupname = Constants.AddDeviceConstants.Individual+"+"+(self.deviceType.titleLabel?.text ?? "")
            self.memberName = nameTxt.text!
            self.memberNumber = deviceNumberTxt.text!
            self.callCreateGroupApi(methodType: NetworkManager.Method.post.rawValue, isFromCreateGroup: false)
        }
    }
    
    // MARK: UITextField Delegate
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        self.view.endEditing(true)
        return false
    }
    
    // Alert with button action
    func ShowALertWithButtonAction(title: String){
        let alert = UIAlertController(title: Constants.AlertConstants.Alert, message: title, preferredStyle: UIAlertController.Style.alert)
        alert.addAction(UIAlertAction(title: Constants.AlertConstants.OkButton, style: UIAlertAction.Style.default, handler: {(_: UIAlertAction!) in
            DispatchQueue.main.async {
                self.navigationController?.popViewController(animated: true)
            }
        }))
        self.present(alert, animated: true, completion: nil)
    }
}
