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
    
    //Verification Api Call
    func verificationApiCall(){
        self.showActivityIndicator()
        UserService.shared.verifyRegistartionTokenwith(verifyTokenUrl: URL(string: Constants.ApiPath.VerifyTokenUrl)!, parameters: ["token": "62178","phone": "9019930385","phoneCountryCode": "91","type": "registration"]) { (result : Result<UserModel, Error>) in
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
    
    // Verification Api Call
    func registrationApiCall(){
        self.showActivityIndicator()
        UserService.shared.registerUser(registerTokenUrl: URL(string: Constants.ApiPath.VerifyTokenUrl)!, parameters: ["token": "62178","phone": "9019930385","phoneCountryCode": "91","type": "registration"]) { (result : Result<UserModel, Error>) in
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
        UserService.shared.loginRequest(with:  URL(string: Constants.ApiPath.LoginUrl)!, parameters: ["phone": "9019930384",
        "phoneCountryCode": "91","password":"Borqs@1234","type":"supervisor"]) { (result : Result<LoginModel, Error>) in
            switch result {
            case .success(let loginResponse):
                self.saveDataInUserDefaults(response: loginResponse)
                DispatchQueue.main.async {
                    self.hideActivityIndicator()
                    self.navigateToHomeScreen()
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
    
    func saveDataInUserDefaults(response : LoginModel) {
        UserDefaults.standard.set(response.ugsToken, forKey: Constants.UserDefaultConstants.UgsToken)
        UserDefaults.standard.set(response.user?.userId ?? "", forKey: Constants.UserDefaultConstants.UserId)
        UserDefaults.standard.set(response.ugsTokenexpiry, forKey: Constants.UserDefaultConstants.UgsExpiryTime)
    }
    
    // navigate to home screen upon succesful login
    func navigateToHomeScreen() {
        let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
        let nextViewController = storyBoard.instantiateViewController(withIdentifier: Constants.ScreenNames.HomeScreen) as! HomeScreen
        self.navigationController?.pushViewController(nextViewController, animated: true)
    }
    
    
}
