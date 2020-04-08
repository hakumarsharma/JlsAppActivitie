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
    var deviceId : String = ""
    var deviceDetails : [DeviceDetails] = []
    //    var deviceIdsArr : [String] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.title = "Location"
        
        self.createMapViewMarker()
        
        
    }
    
    // Create marker to display pindrop over map
    func createMapViewMarker() {
        let camera = GMSCameraPosition.camera(withLatitude: deviceDetails[0].deviceStatus?.location?.latitude ?? 12.3456, longitude: deviceDetails[0].deviceStatus?.location?.longitude ?? 27.5467, zoom: 6.0)
        let mapView = GMSMapView.map(withFrame: self.view.frame, camera: camera)
        mapView.settings.zoomGestures = true
        self.view.addSubview(mapView)
        for (index, element) in self.deviceDetails.enumerated() {
            let marker = GMSMarker()
            marker.position = CLLocationCoordinate2D(latitude: deviceDetails[index].deviceStatus?.location?.latitude ?? 12.3456, longitude:deviceDetails[index].deviceStatus?.location?.longitude ?? 27.5467)
            marker.title = element.name
            let img = UIImage.init(named: "avatar1")
            let markerView = UIImageView(image: img)
            markerView.tintColor = UIColor.red
            marker.iconView = markerView
            marker.map = mapView
            
        }
        
    }
    // API to get location details
    func callgetDeviceLocationDetails() {
        self.showActivityIndicator()
        let deviceURL = URL(string:  Constants.ApiPath.deviceApisUrl + "5e789ad0a789b5a7f632ff7e" + "?tsp=1585031229387&ugs_token=" + (UserDefaults.standard.string(forKey: Constants.userDefaultConstants.ugsToken) ?? ""))!
        DeviceService.shared.getDeviceLocationDetails(with: deviceURL) { (result : (Result<LocationModel, Error>)) in
            switch result {
            case .success(let deviceResponse):
                if let _ = deviceResponse.devicedata {
                    DispatchQueue.main.async {
                        self.hideActivityIndicator()
                        //self.createmapView()
                    }
                } else {
                    self.ShowALert(title: Constants.LocationConstants.noLatLong)
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
