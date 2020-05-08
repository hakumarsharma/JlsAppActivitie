//
//  LoginScreen.swift
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

class LoginScreen: UIViewController {
    
    @IBOutlet weak var userNameTxt: UITextField!
    @IBOutlet weak var mobileNumberTxt: UITextField!
    @IBOutlet weak var otpTxt: UITextField!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.navigationController?.navigationBar.titleTextAttributes = [NSAttributedString.Key.foregroundColor : UIColor.white]
        self.navigationItem.setHidesBackButton(true, animated: true)
        // self.setUpMQTT()
        self.mobileNumberTxt.isUserInteractionEnabled = true
        self.checkIfUserExistsInDatabaseAndFetchDetails()
    }
    
    func checkIfUserExistsInDatabaseAndFetchDetails(){
        if RealmManager.sharedInstance.getUserDataFromDB().count > 0 {
            let loginData = RealmManager.sharedInstance.getUserDataFromDB().first
            userNameTxt.text = loginData?.user?.name ?? ""
            mobileNumberTxt.text = loginData?.user?.phone ?? ""
            self.mobileNumberTxt.isUserInteractionEnabled = false
        }
    }
    
    @IBAction func continueBtnAction(_ sender: Any) {
        
        // validation check
        if userNameTxt.text?.count == 0{
            self.ShowALert(title: Constants.LoginScreenConstants.UserName)
            return
        }
        
        if mobileNumberTxt.text?.count == 0 || !(mobileNumberTxt.text?.isValidPhone ?? true){
            self.ShowALert(title: Constants.LoginScreenConstants.PhoneNumber);
            return
        }
        
        if otpTxt.text?.count == 0{
            self.ShowALert(title: Constants.LoginScreenConstants.Otp);
            return
        }
        //self.registerationApiCall()
        self.callLoginApi()
        
    }
    
    //Regsiteration Api Call
    func registerationApiCall() {
        self.showActivityIndicator()
        UserService.shared.generateRegistartionTokenwith(generateTokenUrl: URL(string: Constants.ApiPath.GenerateTokenUrl)!, parameters: ["type": "registration","phone": "9019930385","phoneCountryCode": "91"]) { (result : Result<UserModel, Error>) in
            switch result {
            case .success(let userResponse):
                print(userResponse)
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

    
    // Login Api Call
    // TODO :  Change API call based on phone registration process
    func callLoginApi() {
        self.showActivityIndicator()
        UserService.shared.loginRequest(with:URL(string: Constants.ApiPath.LoginUrl)!,userName : self.userNameTxt.text!, parameters: ["phone": self.mobileNumberTxt.text!,"phoneCountryCode": "91","password":"Borqs@1234","type":"supervisor"]) { (result : Result<LoginModel, Error>) in
            switch result {
            case .success( _):
                    if RealmManager.sharedInstance.getDeviceDataFromDB().count > 0{
                         let deviceData = RealmManager.sharedInstance.getDeviceDataFromDB().first
                          if deviceData?.code == 200 {
                            DispatchQueue.main.async {
                                self.hideActivityIndicator()
                                self.navigateToHomeScreen()
                            }
                          } else {
                            DispatchQueue.main.async {
                               self.callAddDeviceApi()
                            }
                        }
                    } else {
                        DispatchQueue.main.async {
                           self.callAddDeviceApi()
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
    
    //  API to add device details
    func callAddDeviceApi() {
            let deviceURL = URL(string: Constants.ApiPath.UserApisUrl + Utils.shared.getUserId() + Constants.ApiPath.AddDeviceUrl + Utils.shared.getUgsToken())!
            let deviceDetails : [[String : String]] = [["mac": self.mobileNumberTxt.text!,"identifier": "imei","name": self.userNameTxt.text! ,"phone": self.mobileNumberTxt.text!]]
            let flagDetails : [String : Bool] = ["isSkipAddDeviceToGroup" : false]
            let deviceParams :  [String : Any] = ["devices" : deviceDetails, "flags": flagDetails]
            DeviceService.shared.addAndGetDeviceDetails(with: deviceURL, parameters: deviceParams) { (result : (Result<DeviceModel, Error>)) in
                switch result {
                case .success( _):
                    DispatchQueue.main.async {
                    self.hideActivityIndicator()
                    self.navigateToHomeScreen()
                    }
                case .failure(_):
                    DispatchQueue.main.async {
                     self.hideActivityIndicator()
                     self.navigateToHomeScreen()
                    }
                }
            }
        }
    
    // navigate to home screen upon succesful login
    func navigateToHomeScreen() {
        let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
        let nextViewController = storyBoard.instantiateViewController(withIdentifier: Constants.ScreenNames.HomeScreen) as! HomeScreen
        self.navigationController?.pushViewController(nextViewController, animated: true)
    }
    
    
}
