//
//  MapsScreen.swift
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
import MapKit
import GoogleMaps

class MapsScreen: UIViewController {
    
    @IBOutlet weak var mapView: MKMapView!
    
    let googleApiKey = Bundle.main.infoDictionary?["GoggleApiKey"]
    let regionRadius: CLLocationDistance = 1000
    var deviceDetails : [DeviceDetails] = []
    var groupData : [GroupListData] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = "Location"
        self.navigationItem.setHidesBackButton(true, animated: true)
        self.createBackBarButtonItem()
        self.callgetLocationDetails()
        
    }
    
    func createBackBarButtonItem() {
        let backBtn : UIBarButtonItem = UIBarButtonItem.init(image: UIImage(named: "back"), style: .plain, target: self, action: #selector(backButton(sender:)))
        backBtn.tintColor = .white
        self.navigationItem.setLeftBarButton(backBtn, animated: true)
    }
    
    @objc func backButton(sender: UIBarButtonItem) {
        self.navigationController?.popViewController(animated: true)
    }
    // Create marker to display pindrop over map
    func createMapViewMarker(deviceData :  DeviceDetails) {
        let camera = GMSCameraPosition.camera(withLatitude:  deviceData.location!.latitude , longitude: deviceData.location!.longitude, zoom: 6.0)
        let mapView = GMSMapView.map(withFrame: self.view.bounds, camera: camera)
        mapView.settings.zoomGestures = true
        self.view.addSubview(mapView)
        //        for (index, element) in self.deviceDetails.enumerated() {
        let marker = GMSMarker()
        marker.position = CLLocationCoordinate2D(latitude: deviceData.location!.latitude, longitude: deviceData.location!.longitude)
        marker.title = "Avatar"
        let img = UIImage.init(named: "avatar1")
        let markerView = UIImageView(image: img)
        markerView.tintColor = UIColor.red
        marker.iconView = markerView
        marker.map = mapView
        
        //        }
        
    }
    
    
    func callgetLocationDetails() {
        self.showActivityIndicator()
        let locationURL = URL(string:  Constants.ApiPath.UserApisUrl + Utils.shared.getUserId() + Constants.ApiPath.CreateGroupUrl + "/" +  self.groupData.first!.groupId + Constants.ApiPath.LocationUrl)!
        let types : [String] = ["location","sos"]
        let sessionTime : [String : Any] = ["from" : self.groupData.first!.groupSession!.from!, "to" : self.groupData.first!.groupSession!.to!]
        let parameters : [String : Any] = ["types" : types, "time" : sessionTime]
        
        LocationService.shared.getLocationDetails(locationUrl: locationURL, parameters: parameters) { (result : (Result<LocationModel, Error>)) in
            
            switch result {
            case .success(let deviceResponse):
                if deviceResponse.devicedata.count > 0 {
                    DispatchQueue.main.async {
                        self.hideActivityIndicator()
                        self.createMapViewMarker(deviceData: deviceResponse.devicedata.first!)
                    }
                } else {
                    self.ShowALert(title: Constants.LocationConstants.NoLatLong)
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
