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
import CocoaMQTT

class LoginScreen: BaseViewController, UITextFieldDelegate {

    @IBOutlet weak var userNameTxt: UITextField!
    @IBOutlet weak var mobileNumberTxt: UITextField!
    @IBOutlet weak var otpTxt: UITextField!
    @IBOutlet weak var resendOtpBtn: UIButton!
    @IBOutlet weak var scrollView: UIScrollView!
    var mqtt: CocoaMQTT?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.navigationController?.navigationBar.titleTextAttributes = [NSAttributedString.Key.foregroundColor : UIColor.white]
        self.navigationItem.setHidesBackButton(true, animated: true)
        self.initialiseData()
    }
 
    func initialiseData() {
       // self.setUpMqtt()
        self.userNameTxt.delegate = self
        self.mobileNumberTxt.delegate = self
        self.otpTxt.delegate = self
        
        userNameTxt.inputAccessoryView = self.setToolbarWithDoneButton()
        mobileNumberTxt.inputAccessoryView = self.setToolbarWithDoneButton()
        otpTxt.inputAccessoryView = self.setToolbarWithDoneButton()
        
        self.createNotification()
        self.mobileNumberTxt.isUserInteractionEnabled = true
        self.checkIfUserExistsInDatabaseAndFetchDetails()
        
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
    // MARK: Notification Methods
    
    func createNotification() {
        NotificationCenter.default.addObserver(self, selector: #selector(navigateToHome(notification:)), name: NSNotification.Name(rawValue: Constants.NotificationName.NavigateToHome), object: nil)
    }
    
    @objc func navigateToHome(notification: NSNotification) {
        self.navigateToHomeScreen()
    }
    
    // checking if user exists blocking user to enter new number to avoid db issues
    func checkIfUserExistsInDatabaseAndFetchDetails(){
        if RealmManager.sharedInstance.getUserDataFromDB().count > 0 {
            let loginData = RealmManager.sharedInstance.getUserDataFromDB().first
            userNameTxt.text = loginData?.user?.name ?? ""
            mobileNumberTxt.text = loginData?.user?.phone ?? ""
            self.mobileNumberTxt.isUserInteractionEnabled = false
        }
    }
    
    @IBAction func resendOtpBtnAction(_ sender: Any) {
        if mobileNumberTxt.text?.count == 0 || !(mobileNumberTxt.text?.isValidPhone ?? true){
            self.ShowALert(title: Constants.LoginScreenConstants.PhoneNumber);
            return
        }
        self.resendOtpBtn.setTitle("Resend OTP", for: .normal)
        self.generateTokenApiCall(tokenUrl: Constants.ApiPath.GenerateLoginTokenUrl,params: ["phone": self.mobileNumberTxt.text!,"role": [
            "code": "supervisor"]])
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
        
        self.callLoginApi()
        
    }
    
    // MARK: Service Calls
    
    // Login Api Call
    // TODO :  Change API call based on phone registration process
    func callLoginApi() {
        self.showActivityIndicator()
        UserService.shared.loginRequest(with:URL(string: Constants.ApiPath.LoginUrl)!, parameters: ["phone": "9019930385","phoneCountryCode": "91","password":"Borqs@1234","type":"supervisor"]) { (result : Result<LoginModel, Error>) in
            switch result {
            case .success( _):
                // checking if device is whitelisted using verify and assign or not
                if let deviceData = RealmManager.sharedInstance.getDeviceDataFromDB().first{
                    // status code is device added successfully and 409 is if we are trying to add same device again
                    if deviceData.code == 200  {
                        DispatchQueue.main.async {
                            self.hideActivityIndicator()
                            self.navigateToHomeScreen()
                        }
                    } else {
                        DispatchQueue.main.async {
                            self.memberName = self.userNameTxt.text!
                            self.memberNumber = self.mobileNumberTxt.text!
                            self.callAddDeviceApi()
                        }
                    }
                } else {
                    DispatchQueue.main.async {
                        self.memberName = self.userNameTxt.text!
                        self.memberNumber = self.mobileNumberTxt.text!
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
    
    // navigate to home screen upon succesful login
    func navigateToHomeScreen() {
        let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
        let nextViewController = storyBoard.instantiateViewController(withIdentifier: Constants.ScreenNames.HomeScreen) as! HomeScreen
        self.navigationController?.pushViewController(nextViewController, animated: true)
    }
    
    // MARK: UITextField Delegate
    
    func textFieldDidBeginEditing(_ textField: UITextField) {
        scrollView.setContentOffset(CGPoint(x: 0, y: (textField.superview?.frame.origin.y)! + 50), animated: true)
    }

    func textFieldDidEndEditing(_ textField: UITextField) {
        scrollView.setContentOffset(CGPoint(x: 0, y: 0), animated: true)
    }
}


