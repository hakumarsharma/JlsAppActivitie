//
//  LoginScreen.swift
//  PeopleTracker
//
//  Created by Apple on 18/03/20.
//  Copyright © 2020 Apple. All rights reserved.
//

import UIKit

class LoginScreen: UIViewController {
    
    @IBOutlet weak var userNameTxt: UITextField!
    @IBOutlet weak var mobileNumberTxt: UITextField!
    @IBOutlet weak var otpTxt: UITextField!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
    }
    
    @IBAction func continueBtnAction(_ sender: Any) {
        
        // validation check
        if userNameTxt.text?.count == 0{
            self.ShowALert(title: Constants.LoginScreenConstants.userName)
            return
        }

        if mobileNumberTxt.text?.count == 0 || !(mobileNumberTxt.text?.isValidPhone ?? true){
            self.ShowALert(title: Constants.LoginScreenConstants.phoneNumber);
            return
        }

        if otpTxt.text?.count == 0{
            self.ShowALert(title: Constants.LoginScreenConstants.otp);
            return
        }
     
       self.callLoginApi()
        
    }
    
    // login api call
    func callLoginApi() {
        UserService.shared.loginRequest(with:  URL(string: Constants.ApiPath.loginUrl)!, parameters: ["email":"shivakumar.jagalur@ril.com","password":"Ril@12345","type": "supervisor"]) { (result : Result<LoginModel, Error>) in
                switch result {
                    case .success(let loginResponse):
                        self.navigateToHomeScreen(usgToken: loginResponse.ugstoken)
                    case .failure(let error):
                        if type(of: error) == NetworkManager.ErrorType.self {
                            self.ShowALert(title: Utils.shared.handleError(error: error as! NetworkManager.ErrorType))
                        } else {
                            self.ShowALert(title: error.localizedDescription)
                    }
                }
            }
    }
    
    // navigate to home screen upon succesful login
    func navigateToHomeScreen(usgToken : String) {
        let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
        let nextViewController = storyBoard.instantiateViewController(withIdentifier: "HomeScreen") as! HomeScreen
        self.navigationController?.pushViewController(nextViewController, animated: true)
    }
    
    
    
}
