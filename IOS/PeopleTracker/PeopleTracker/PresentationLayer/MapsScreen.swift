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
    var count : Int = 0
    let googleApiKey = Bundle.main.infoDictionary?["GoggleApiKey"]
    let regionRadius: CLLocationDistance = 1000
    var deviceDetails : [DeviceDetails] = []
    var groupData : [GroupListData] = []
    var lat  : [Double] = [12.9487662,13.2558273,19.2558273,25.2558273,13.2558273,12.9487662]
    var long : [Double] = [77.7016317,77.9816146,72.9816146,72.9816146,77.7016317,77.7016317]
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = "Location"
        self.navigationItem.setHidesBackButton(true, animated: true)
        self.createBackBarButtonItem()
        
        for group in groupData {
            self.callgetLocationDetails(groupDetails: group) { (val) in
            }
        }
        
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
    func createMapViewMarker(deviceData :  [DeviceDetails]) {
        let camera = `GMSCameraPosition`.camera(withLatitude:  deviceData.first?.location?.latitude ?? 0 , longitude: deviceData.first?.location?.longitude ?? 0, zoom: 8.0)
        let mapView = GMSMapView.map(withFrame: self.view.bounds, camera: camera)
        mapView.settings.zoomGestures = true
        self.view.addSubview(mapView)
        var marakers:[Any] = []
        for (index,device) in deviceData.enumerated() {
            let marker = GMSMarker()
            marker.position = CLLocationCoordinate2D(latitude: device.location?.latitude ?? 0 , longitude:device.location?.longitude ?? 0)
            marker.title = (device.name ?? "") + " : " + (device.phone ?? "")
            marker.isTappable = true
            marker.zIndex = Int32(index)
            var img : UIImage? = nil
            if index % 2 == 0 {
                img = UIImage.init(named: "pindropr")
            } else if index % 3 == 0{
                img = UIImage.init(named: "pindropb")
            } else {
                img = UIImage.init(named: "pindropg")
            }
            let markerView = UIImageView(image: img)
            markerView.tintColor = UIColor.red
            marker.iconView = markerView
            marker.map = mapView
            marakers.append(marker)
            
        }
        
        self.focusMapToShowAllMarkers(markers: marakers, mapView: mapView)
        
    }
    //    // Create marker to display pindrop over map
    //    func createMapViewMarker(deviceData :  [DeviceDetails]) {
    //        let camera = `GMSCameraPosition`.camera(withLatitude:  self.lat.first! , longitude: self.long.first!, zoom: 8.0)
    //        let mapView = GMSMapView.map(withFrame: self.view.bounds, camera: camera)
    //        mapView.settings.zoomGestures = true
    //        self.view.addSubview(mapView)
    //        var marakers:[Any] = []
    //        for (index,_) in lat.enumerated() {
    //            let marker = GMSMarker()
    //            marker.position = CLLocationCoordinate2D(latitude: lat[index] , longitude: long[index] )
    //            marker.title = "Avatar:" + "8088422893"
    //            marker.isTappable = true
    //            marker.zIndex = Int32(index)
    //            var img : UIImage? = nil
    //            if index % 2 == 0 {
    //              img = UIImage.init(named: "pindropr")
    //            } else if index % 3 == 0{
    //              img = UIImage.init(named: "pindropb")
    //            } else {
    //               img = UIImage.init(named: "pindropg")
    //            }
    //            let markerView = UIImageView(image: img)
    //            markerView.tintColor = UIColor.red
    //            marker.iconView = markerView
    //            marker.map = mapView
    //            marakers.append(marker)
    //
    //        }
    //
    //        self.focusMapToShowAllMarkers(markers: marakers, mapView: mapView)
    //
    //    }
    
    func focusMapToShowAllMarkers(markers : [Any], mapView : GMSMapView) {
        let bounds = markers.reduce(GMSCoordinateBounds()) {
            $0.includingCoordinate(($1 as AnyObject).position)
        }
        
        mapView.animate(with: .fit(bounds, withPadding: 30.0))
        
    }
    
    func callgetLocationDetails(groupDetails : GroupListData, completionHandler: @escaping (Bool) -> Void) {
        self.showActivityIndicator()
        let locationURL = URL(string:  Constants.ApiPath.UserApisUrl + Utils.shared.getUserId() + Constants.ApiPath.CreateGroupUrl + "/" +  groupDetails.groupId + Constants.ApiPath.LocationUrl)!
        let types : [String] = ["location","sos"]
        let sessionTime : [String : Any] = ["from" : groupDetails.groupSession!.from!, "to" : groupDetails.groupSession!.to!]
        let parameters : [String : Any] = ["types" : types, "time" : sessionTime]
        LocationService.shared.getLocationDetails(locationUrl: locationURL, parameters: parameters) { (result : (Result<    LocationModel, Error>)) in
            
            switch result {
            case .success(let deviceResponse):
                
                DispatchQueue.main.async {
                    self.hideActivityIndicator()
                    if deviceResponse.devicedata.count > 0 {
                        for member in Array(groupDetails.groupMember) {
                            self.count = self.count + 1
                            for device in Array(deviceResponse.devicedata) {
                                if member.memberDevice == device.deviceId {
                                    device.name = member.memberName
                                    device.phone = member.memberPhone
                                    self.deviceDetails.append(device)
                                    
                                }
                            }
                        }
                        if self.groupData.count > 1 {
                            self.checkGroupData(dataCount: self.groupData.count)
                        }else{
                            self.checkGroupData(dataCount: self.groupData.first?.groupMember.count ?? 0)
                        }
                    }else {
                        self.ShowALert(title: Constants.LocationConstants.NoLatLong)
                    }
                    
                    completionHandler(true)
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
                completionHandler(true)
            }
        }
    }
    
    func checkGroupData(dataCount : Int){
        if self.count == dataCount{
            print(self.deviceDetails)
            self.createMapViewMarker(deviceData: self.deviceDetails)
        } else {
            self.ShowALert(title: Constants.LocationConstants.NoLatLong)
        }
    }
    
}
