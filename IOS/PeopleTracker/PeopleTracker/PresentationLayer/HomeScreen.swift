//
//  HomeScreen.swift
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
import JJFloatingActionButton

class HomeScreen: UIViewController,UITableViewDelegate, UITableViewDataSource {
    
    @IBOutlet weak var usersTableView: UITableView!
    
    let actionButton = JJFloatingActionButton()
    var selectedCell : [String] = []
    let names: [String] = ["Office Group","Sree", "Maruti", "Harish", "Ashish", "Satish"] // TODO: remove after api call is integrated
    var listOfDevices : [DeviceData] = []
    var deviceDetails : [DeviceDetails] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = "Home"
        self.navigationItem.setHidesBackButton(true, animated: true)
        let trackBtn : UIBarButtonItem = UIBarButtonItem.init(title: "Track", style: .plain, target: self, action: #selector(trackButton(sender:)))
        trackBtn.setTitleTextAttributes( [NSAttributedString.Key.foregroundColor : UIColor.white], for: .normal)
        self.navigationItem.setRightBarButton(trackBtn, animated: true)
        usersTableView.delegate = self
        usersTableView.dataSource = self
        floatingActionButton()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        self.callgetDeviceApi()
    }
    
    // MARK: UITableView Delegate and DataSource methods
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.listOfDevices.count
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 140.0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "cellIdentifier") as! UserCell
        cell.selectionStyle = .none
        let deviceDetails = self.listOfDevices[indexPath.row]
        cell.setData(deviceData: deviceDetails)
        cell.userIcon.image = UIImage(named: "user4")
        cell.optionsButton.addTarget(self, action: #selector(showActionSheet), for: .touchUpInside)
        cell.checkBoxButton.addTarget(self, action: #selector(checkmarkSelection(sender:)), for: .touchUpInside)
        cell.checkBoxButton.tag = indexPath.row
        cell.checkBoxButton.isHidden = false
        // TODO: Remove after grouping and consent mechanisam is integrated
        if indexPath.row == 0{
            cell.checkBoxButton.isHidden = true
            cell.consentstatusColor.backgroundColor = UIColor(red: 132/255.0, green: 222/255.0, blue: 2/255.0, alpha: 1.0)
            cell.requestConsentButton.setTitle(Constants.HomScreenConstants.requestConsent, for: .normal)
        } else
            if indexPath.row % 2 == 0{
                cell.consentstatusColor.backgroundColor = UIColor(red: 132/255.0, green: 222/255.0, blue: 2/255.0, alpha: 1.0)
                cell.requestConsentButton.setTitle(Constants.HomScreenConstants.consentApproved, for: .normal)
            } else {
                cell.consentstatusColor.backgroundColor = .orange
                cell.requestConsentButton.setTitle(Constants.HomScreenConstants.consentPending, for: .normal)
        }
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if indexPath.row == 0 {
            let selectedName = self.names[indexPath.row]
            navigateToGroupListScreen(title: selectedName)
        }
    }
    
    @objc func checkmarkSelection(sender:UIButton) {
        let tempBtn : UIButton = sender
        if tempBtn.isSelected {
            selectedCell = selectedCell.filter({ (item) -> Bool in
                return item != self.listOfDevices[tempBtn.tag].deviceId
            })
            deviceDetails = deviceDetails.filter({ (item) -> Bool in
                return item.deviceId != self.listOfDevices[tempBtn.tag].deviceId
            })
        } else {
            selectedCell.append(self.listOfDevices[tempBtn.tag].deviceId)
            
        }
        tempBtn.isSelected = !tempBtn.isSelected
    }
    
    @objc func trackButton(sender: UIBarButtonItem) {
        self.callgetDeviceLocationDetails { (success) in
            if success && self.deviceDetails.count > 0  {
                DispatchQueue.main.async {
                    self.navigateToMapsScreen()
                }
            }else {
                DispatchQueue.main.async {
                    self.ShowALert(title: Constants.LocationConstants.locationDetailsNotFound)
                }
            }
        }
    }
    
    // MARK : Navigatation screens
    func navigateToMapsScreen() {
        let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
        let mapsViewController = storyBoard.instantiateViewController(withIdentifier: Constants.screenNames.mapsScreen) as! MapsScreen
        mapsViewController.deviceId = UserDefaults.standard.string(forKey: Constants.userDefaultConstants.userId) ?? ""
        mapsViewController.deviceDetails = self.deviceDetails
        self.navigationController?.pushViewController(mapsViewController, animated: true)
    }
    
    func navigateToAddDeviceScreen(title : String) {
        let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
        let addDeviceViewController = storyBoard.instantiateViewController(withIdentifier: Constants.screenNames.addDeviceScreen) as! AddDeviceScreen
        addDeviceViewController.navtitle = title
        addDeviceViewController.userid = UserDefaults.standard.string(forKey: Constants.userDefaultConstants.userId) ?? ""
        addDeviceViewController.ugsToken = UserDefaults.standard.string(forKey: Constants.userDefaultConstants.ugsToken) ?? ""
        self.navigationController?.pushViewController(addDeviceViewController, animated: true)
    }
    
    func navigateToCreateGroupScreen() {
        let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
        let createGroupViewController = storyBoard.instantiateViewController(withIdentifier: Constants.screenNames.createGroupScreen) as! CreateGroupScreen
        createGroupViewController.navtitle = Constants.HomScreenConstants.createGroup
        self.navigationController?.pushViewController(createGroupViewController, animated: true)
    }
    
    func navigateToGroupListScreen(title : String) {
        let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
        let groupListViewController = storyBoard.instantiateViewController(withIdentifier: Constants.screenNames.groupListScreen) as! GroupListScreen
        groupListViewController.navtitle = title
        self.navigationController?.pushViewController(groupListViewController, animated: true)
    }
    
    
    // MARK: Show floating button
    func floatingActionButton () {
        actionButton.addItem(title: Constants.HomScreenConstants.addDevice, image: UIImage(named: "device")?.withRenderingMode(.alwaysTemplate)) { item in
            // do something
            self.navigateToAddDeviceScreen(title: Constants.HomScreenConstants.addDevice)
        }
        
        actionButton.addItem(title: Constants.HomScreenConstants.addPerson, image: UIImage(named: "user4")?.withRenderingMode(.alwaysTemplate)) { item in
            // do something
            self.navigateToAddDeviceScreen(title: Constants.HomScreenConstants.addPerson)
        }
        
        actionButton.addItem(title: Constants.HomScreenConstants.createGroup, image: UIImage(named: "group")?.withRenderingMode(.alwaysTemplate)) { item in
            // do something
            self.navigateToCreateGroupScreen()
        }
        
        view.addSubview(actionButton)
        actionButton.translatesAutoresizingMaskIntoConstraints = false
        actionButton.trailingAnchor.constraint(equalTo: view.safeAreaLayoutGuide.trailingAnchor, constant: -16).isActive = true
        actionButton.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor, constant: -16).isActive = true
    }
    
    // Alert with button action
    func ShowALertWithButtonAction(title: String){
        let alert = UIAlertController(title: Constants.AlertConstants.alert, message: title, preferredStyle: UIAlertController.Style.alert)
        alert.addAction(UIAlertAction(title: Constants.AlertConstants.okButton, style: UIAlertAction.Style.default, handler: {(_: UIAlertAction!) in
            DispatchQueue.main.async {
                
            }
        }))
        self.present(alert, animated: true, completion: nil)
    }
    
    // MARK: Display ActionSheet
    
    @objc func showActionSheet(){
        
        let alert = UIAlertController(title: Constants.HomScreenConstants.select, message: "", preferredStyle: .actionSheet)
        
        alert.addAction(UIAlertAction(title: Constants.HomScreenConstants.edit, style: .default , handler:{ (UIAlertAction)in
            self.navigateToAddDeviceScreen(title : Constants.HomScreenConstants.edit)
        }))
        
        alert.addAction(UIAlertAction(title:Constants.HomScreenConstants.delete, style: .default , handler:{ (UIAlertAction)in
            self.ShowALert(title: Constants.HomScreenConstants.deleteDevice)
        }))
        alert.addAction(UIAlertAction(title: Constants.HomScreenConstants.dismiss, style: .cancel, handler:{ (UIAlertAction)in
            
        }))
        
        self.present(alert, animated: true, completion: {
            print("completion block")
        })
    }
    
    // MARK: Service Calls
    // API to add device details
    func callgetDeviceApi() {
        self.showActivityIndicator()
        let deviceURL = URL(string:  Constants.ApiPath.deviceDetails + (UserDefaults.standard.string(forKey: Constants.userDefaultConstants.ugsToken) ?? ""))!
        let deviceParams :  [String : Any] = ["usersAssigned":[UserDefaults.standard.string(forKey: Constants.userDefaultConstants.userId) ?? ""]]
        DeviceService.shared.addAndGetDeviceDetails(with: deviceURL, parameters: deviceParams) { (result : (Result<DeviceModel, Error>)) in
            switch result {
            case .success(let deviceResponse):
                guard let deviceData = deviceResponse.devicedata else {
                    return
                }
                for device in deviceData {
                    self.listOfDevices.append(device)
                }
                DispatchQueue.main.async {
                    self.hideActivityIndicator()
                    self.usersTableView.reloadData()
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
    
    // API to get location details
    // TODO: check and change api call to track multiple devices
    func callgetDeviceLocationDetails(completion: @escaping(_ success: Bool) -> Void) {
        self.showActivityIndicator()
        for (index,element) in selectedCell.enumerated() {
            let deviceURL = URL(string:  Constants.ApiPath.deviceApisUrl + element + "?tsp=1585031229387&ugs_token=" + (UserDefaults.standard.string(forKey: Constants.userDefaultConstants.ugsToken) ?? ""))! // TODO: Save tsp in user defaults
            DeviceService.shared.getDeviceLocationDetails(with: deviceURL) { (result : (Result<LocationModel, Error>)) in
                switch result {
                case .success(let deviceResponse):
                     DispatchQueue.main.async {
                        self.hideActivityIndicator()
                     }
                    if let device = deviceResponse.devicedata , let _ = deviceResponse.devicedata?.deviceStatus?.location {
                        self.deviceDetails.append(device)
                    }
                    if index == self.selectedCell.count - 1 {
                        completion(true)
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
    
}
