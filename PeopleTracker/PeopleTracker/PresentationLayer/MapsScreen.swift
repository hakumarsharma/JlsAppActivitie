//
//  MapsScreen.swift
//  PeopleTracker
//
//  Created by Apple on 18/03/20.
//  Copyright © 2020 Apple. All rights reserved.
//

import UIKit
import MapKit
import GoogleMaps

class MapsScreen: UIViewController {

    @IBOutlet weak var mapView: MKMapView!
    
    let googleApiKey = "AIzaSyCL18AjsFlIRWkG5_BcHEZsOnFDE0aok2w"
    let regionRadius: CLLocationDistance = 1000
    var names : [String] = []
    var latitude : [Double] = [-33.86, -39.86]
    var longitude : [Double] = [151.20, 155.20]
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.title = "Location"
        
        // Maps Key
        GMSServices.provideAPIKey(googleApiKey)
        
        createmapView()
       
    }

    // create mapview to display maps
    func createmapView() {
        let camera = GMSCameraPosition.camera(withLatitude: -33.86, longitude: 151.20, zoom: 6.0)
        let mapView = GMSMapView.map(withFrame: CGRect.zero, camera: camera)
        mapView.settings.zoomGestures = true
        view = mapView
        self.createMapViewMarker(mapView: mapView)
    }
    
    // create marker to display pindrop over map
    func createMapViewMarker(mapView: GMSMapView) {
       
        
        for (index, element) in names.enumerated() {
            let marker = GMSMarker()
            marker.position = CLLocationCoordinate2D(latitude: latitude[index], longitude: longitude[index])
                   marker.map = mapView
                   marker.title = element
                   let img = UIImage.init(named: "avatar" + String(index+1))
                   let markerView = UIImageView(image: img)
                   markerView.tintColor = UIColor.red
                   marker.iconView = markerView
                   
//            let state_marker = GMSMarker()
//            state_marker.position = CLLocationCoordinate2D(latitude: -33.86 + 10, longitude: 151.20 + 20)
//            state_marker.title = val
//            state_marker.snippet = "Hey, this is \(val)"
//             let img = UIImage.init(named: "pindrop")
//             let markerView = UIImageView(image: img)
//             markerView.tintColor = UIColor.red
//            state_marker.iconView = markerView
//            state_marker.map = mapView
        }
    }
}
