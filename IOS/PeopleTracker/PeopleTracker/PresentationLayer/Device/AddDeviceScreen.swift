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

class AddDeviceScreen: UIViewController {
    @IBOutlet weak var nameTxt: UITextField!
    @IBOutlet weak var ownerNumberTxt: UITextField!
    @IBOutlet weak var deviceNumberTxt: UITextField!
    @IBOutlet weak var deviceType: UIButton!
    
    var navtitle : String = ""
    var userid : String = ""
    var ugsToken : String = ""
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = navtitle
        deviceType.layer.borderWidth = 1.0
        deviceType.layer.borderColor = UIColor(red: 206/255.0, green: 206/255.0, blue: 206/255.0, alpha: 1).cgColor
        deviceType.layer.cornerRadius = 4.0
        
    }
    
    @IBAction func deviceTypeButtonAction(_ sender: Any) {
        
        let alert = UIAlertController(title: Constants.AddDeviceConstants.SelectType, message: "", preferredStyle: .actionSheet)
         self.deviceType.titleLabel?.textColor = .black
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
        
        self.callAddDeviceApi()
    }
    
    // API to add device details
    func callAddDeviceApi() {
        let deviceURL = URL(string: Constants.ApiPath.UserApisUrl + userid + Constants.ApiPath.AddDeviceUrl + ugsToken)!
        let deviceDetails : [[String : String]] = [["mac": deviceNumberTxt.text ?? "","identifier": "imei","name": nameTxt.text ?? "","phone": ownerNumberTxt.text ?? ""]]
        let flagDetails : [String : Bool] = ["isSkipAddDeviceToGroup" : false]
        let deviceParams :  [String : Any] = ["devices" : deviceDetails, "flags": flagDetails]
        DeviceService.shared.addAndGetDeviceDetails(with: deviceURL, parameters: deviceParams) { (result : (Result<DeviceModel, Error>)) in
            switch result {
            case .success(let deviceResponse):
                print(deviceResponse)
                self.ShowALertWithButtonAction(title: Constants.AddDeviceConstants.DeviceAddedSuccessfully)
            case .failure(let error):
                if type(of: error) == NetworkManager.ErrorType.self {
                    DispatchQueue.main.async {
                        self.ShowALertWithButtonAction(title: Utils.shared.handleError(error: error as! NetworkManager.ErrorType))
                    }
                } else {
                    DispatchQueue.main.async {
                        self.ShowALertWithButtonAction(title: error.localizedDescription)
                    }
                }
            }
        }
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
