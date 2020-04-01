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
        if phoneNumberTxt.text?.count == 0 || !(phoneNumberTxt.text?.isValidPhone ?? false){
            self.ShowALert(title: Constants.LoginScreenConstants.phoneNumber)
            return
        }
        if imeiTxt.text?.count == 0 {
            self.ShowALert(title: Constants.AddDeviceConstants.imei)
            return
        }
        
    }
    
    func callAddDeviceApi() {
        let deviceURL = URL(string: Constants.ApiPath.BaseUrl + Constants.ApiPath.userApisUrl + userid + Constants.ApiPath.addDeviceUrl + ugstoken)!
        let deviceDetails : [String : String] = ["mac": imeiTxt.text ?? "","identifier": "imei","name": nameTxt.text ?? "","phone": phoneNumberTxt.text ?? ""]
        let flagDetails : [String : Bool] = ["isSkipAddDeviceToGroup" : false]
        let deviceParams :  [String : Any] = ["devices" : [{deviceDetails}], "flags": flagDetails]
        
//        {
//            "devices":
//                {
//                    "mac": "124644113",
//                    "identifier": "imei",
//                    "name": "eeedrrfty4023",
//                    "phone": "9090909090",
//
//                }
//            ],
//            "flags": {
//                "isSkipAddDeviceToGroup": false
//            }
//        }
        DeviceService.shared.addDevice(with: deviceURL, parameters: deviceParams) { (result : (Result<DeviceModel, Error>)) in
            
        }
    }
}
