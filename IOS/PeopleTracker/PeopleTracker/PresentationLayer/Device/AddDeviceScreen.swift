//
//  AddDeviceScreen.swift
//  PeopleTracker
//
//  Created by Apple on 18/03/20.
//  Copyright © 2020 Apple. All rights reserved.
//

import UIKit

class AddDeviceScreen: UIViewController {
    @IBOutlet weak var nameTxt: UITextField!
    @IBOutlet weak var phoneNumberTxt: UITextField!
    @IBOutlet weak var imeiTxt: UITextField!
    
    var navtitle : String = ""
    var userid : String = ""
    var ugstoken : String = ""
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = navtitle
        // Do any additional setup after loading the view.
    }
    
    @IBAction func addDeviceButtonAction(_ sender: Any) {
        if nameTxt.text?.count == 0 {
            self.ShowALert(title: Constants.AddDeviceConstants.name)
            return
        }
        if phoneNumberTxt.text?.count == 0 || !(phoneNumberTxt.text?.isValidPhone ?? true){
            self.ShowALert(title: Constants.LoginScreenConstants.phoneNumber)
            return
        }
        if imeiTxt.text?.count == 0 {
            self.ShowALert(title: Constants.AddDeviceConstants.imei)
            return
        }
        
        self.callAddDeviceApi()
    }
    
    // API to add device details
    func callAddDeviceApi() {
        let deviceURL = URL(string: Constants.ApiPath.BaseUrl + Constants.ApiPath.userApisUrl + userid + Constants.ApiPath.addDeviceUrl + ugstoken)!
        let deviceDetails : [[String : String]] = [["mac": imeiTxt.text ?? "","identifier": "imei","name": nameTxt.text ?? "","phone": phoneNumberTxt.text ?? ""]]
        let flagDetails : [String : Bool] = ["isSkipAddDeviceToGroup" : false]
        let deviceParams :  [String : Any] = ["devices" : deviceDetails, "flags": flagDetails]
        DeviceService.shared.addDevice(with: deviceURL, parameters: deviceParams) { (result : (Result<DeviceModel, Error>)) in
            switch result {
                    case .success(let deviceResponse):
                          print(deviceResponse)
                          self.navigationController?.popViewController(animated: true)
                    case .failure(let error):
                        if type(of: error) == NetworkManager.ErrorType.self {
                            self.ShowALert(title: Utils.shared.handleError(error: error as! NetworkManager.ErrorType))
                        } else {
                            self.ShowALert(title: error.localizedDescription)
                    }
                }
            }
    }
}
