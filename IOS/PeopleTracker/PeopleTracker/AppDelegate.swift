//
//  AppDelegate.swift
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
import CoreData
import GoogleMaps
import CoreLocation

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate,CLLocationManagerDelegate {
    
    var locationManager: CLLocationManager?
    var window : UIWindow?
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.
        GMSServices.provideAPIKey(Bundle.main.infoDictionary?["GoggleApiKey"] as! String)
        self.requestLocationDetails()
        self.setRootViewController()
        return true
    }
    
    func requestLocationDetails() {
        locationManager = CLLocationManager()
        locationManager?.delegate = self
        locationManager?.requestAlwaysAuthorization()
    }
    
    // changing rootview controller based on acceptence of privacy policy
    func setRootViewController () {
        self.window = UIWindow(frame: UIScreen.main.bounds)
        var rootVC = UIViewController()
        if UserDefaults.standard.object(forKey: Constants.PrivacyScreen.AcceptPrivacy) != nil {
            let isAcceptPrivacy = UserDefaults.standard.bool(forKey: Constants.PrivacyScreen.AcceptPrivacy)
            if isAcceptPrivacy {
                if Utils.shared.isUgsTokenExpired() {
                rootVC = UIStoryboard.init(name: "Main", bundle: nil).instantiateViewController(withIdentifier: Constants.ScreenNames.LoginScreen) as! LoginScreen
                } else {
                   rootVC = UIStoryboard.init(name: "Main", bundle: nil).instantiateViewController(withIdentifier: Constants.ScreenNames.HomeScreen) as! HomeScreen
                }
            } else {
                rootVC = UIStoryboard.init(name: "Main", bundle: nil).instantiateViewController(withIdentifier: Constants.ScreenNames.HelpScreen) as! HelpScreen
            }
            
        } else {
            rootVC = UIStoryboard.init(name: "Main", bundle: nil).instantiateViewController(withIdentifier: Constants.ScreenNames.HelpScreen) as! HelpScreen
        }
        let navVC = UINavigationController.init(rootViewController: rootVC)
        navVC.navigationBar.tintColor = UIColor.white
        navVC.navigationBar.barTintColor = UIColor.AppTheme.PrimaryColor
        navVC.navigationBar.isTranslucent = false
        navVC.navigationBar.titleTextAttributes = [NSAttributedString.Key.foregroundColor : UIColor.white]
        self.window?.rootViewController = navVC
        self.window?.makeKeyAndVisible()
    }
    
    func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        if status == .authorizedAlways {
            if CLLocationManager.isMonitoringAvailable(for: CLBeaconRegion.self) {
                if CLLocationManager.isRangingAvailable() {
                    // do stuff
                }
            }
        }
    }
    
    //    // MARK: UISceneSession Lifecycle
    //
    //    func application(_ application: UIApplication, configurationForConnecting connectingSceneSession: UISceneSession, options: UIScene.ConnectionOptions) -> UISceneConfiguration {
    //        // Called when a new scene session is being created.
    //        // Use this method to select a configuration to create the new scene with.
    //        return UISceneConfiguration(name: "Default Configuration", sessionRole: connectingSceneSession.role)
    //    }
    //
    //    func application(_ application: UIApplication, didDiscardSceneSessions sceneSessions: Set<UISceneSession>) {
    //        // Called when the user discards a scene session.
    //        // If any sessions were discarded while the application was not running, this will be called shortly after application:didFinishLaunchingWithOptions.
    //        // Use this method to release any resources that were specific to the discarded scenes, as they will not return.
    //    }
    
    func scene(_ scene: UIScene, openURLContexts URLContexts: Set<UIOpenURLContext>) {
        if let url = URLContexts.first?.url{
            print(url)
            let message = url.host?.removingPercentEncoding
            let alertController = UIAlertController(title: "Incoming Message", message: message, preferredStyle: .alert)
            let okAction = UIAlertAction(title: "OK", style: UIAlertAction.Style.default, handler: nil)
            alertController.addAction(okAction)
            let window : UIWindow = UIWindow(frame: UIScreen.main.bounds)
            window.rootViewController?.present(alertController, animated: true, completion: nil)
        }
    }
    func application(_ app: UIApplication, open url: URL, options: [UIApplication.OpenURLOptionsKey : Any] = [:]) -> Bool {
        print(url)
        let message = url.host?.removingPercentEncoding
        let alertController = UIAlertController(title: "Incoming Message", message: message, preferredStyle: .alert)
        let okAction = UIAlertAction(title: "OK", style: UIAlertAction.Style.default, handler: nil)
        alertController.addAction(okAction)
        let window : UIWindow = UIWindow(frame: UIScreen.main.bounds)
        window.rootViewController?.present(alertController, animated: true, completion: nil)
        
        //        var parameters: [String: String] = [:]
        //
        //                   URLComponents(url: url, resolvingAgainstBaseURL: false)?.queryItems?.forEach {
        //                       parameters[$0.name] = $0.value
        //                   }
        //                   let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
        //                   let loginVc = storyBoard.instantiateViewController(withIdentifier: "HelpScreen") as! HelpScreen
        //                   loginVc.params = parameters
        //            loginVc.url = url.path
        //                   let window : UIWindow = UIWindow(frame: UIScreen.main.bounds)
        //                   window.rootViewController = loginVc
        //                   window.makeKeyAndVisible()
        //        if let scheme = url.scheme,
        //            scheme.localizedCaseInsensitiveCompare("peopletracker") == .orderedSame,
        //            let _ = url.host {
        //
        //            var parameters: [String: String] = [:]
        //
        //            URLComponents(url: url, resolvingAgainstBaseURL: false)?.queryItems?.forEach {
        //                parameters[$0.name] = $0.value
        //            }
        //            let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
        //            let loginVc = storyBoard.instantiateViewController(withIdentifier: Constants.ScreenNames.LoginScreen) as! LoginScreen
        //            let window : UIWindow = UIWindow(frame: UIScreen.main.bounds)
        //            window.rootViewController = loginVc
        //            window.makeKeyAndVisible()
        //        }
        return true
    }
    
    private func application(_ application: UIApplication,
                             continue userActivity: NSUserActivity,
                             restorationHandler: @escaping ([Any]?) -> Void) -> Bool {
        if let url = userActivity.webpageURL {
            _ = url.lastPathComponent
            var parameters: [String: String] = [:]
            URLComponents(url: url, resolvingAgainstBaseURL: false)?.queryItems?.forEach {
                parameters[$0.name] = $0.value
            }
            let message = url.host?.removingPercentEncoding
            let alertController = UIAlertController(title: "Incoming Message", message: message, preferredStyle: .alert)
            let okAction = UIAlertAction(title: "OK", style: UIAlertAction.Style.default, handler: nil)
            alertController.addAction(okAction)
            let window : UIWindow = UIWindow(frame: UIScreen.main.bounds)
            window.rootViewController?.present(alertController, animated: true, completion: nil)
            //            redirect(to: view, with: parameters)
        }
        return true
    }
    
    
    // MARK: - Core Data stack
    
    lazy var persistentContainer: NSPersistentContainer = {
        /*
         The persistent container for the application. This implementation
         creates and returns a container, having loaded the store for the
         application to it. This property is optional since there are legitimate
         error conditions that could cause the creation of the store to fail.
         */
        let container = NSPersistentContainer(name: "PeopleTracker")
        container.loadPersistentStores(completionHandler: { (storeDescription, error) in
            if let error = error as NSError? {
                // Replace this implementation with code to handle the error appropriately.
                // fatalError() causes the application to generate a crash log and terminate. You should not use this function in a shipping application, although it may be useful during development.
                
                /*
                 Typical reasons for an error here include:
                 * The parent directory does not exist, cannot be created, or disallows writing.
                 * The persistent store is not accessible, due to permissions or data protection when the device is locked.
                 * The device is out of space.
                 * The store could not be migrated to the current model version.
                 Check the error message to determine what the actual problem was.
                 */
                fatalError("Unresolved error \(error), \(error.userInfo)")
            }
        })
        return container
    }()
    
    // MARK: - Core Data Saving support
    
    func saveContext () {
        let context = persistentContainer.viewContext
        if context.hasChanges {
            do {
                try context.save()
            } catch {
                // Replace this implementation with code to handle the error appropriately.
                // fatalError() causes the application to generate a crash log and terminate. You should not use this function in a shipping application, although it may be useful during development.
                let nserror = error as NSError
                fatalError("Unresolved error \(nserror), \(nserror.userInfo)")
            }
        }
    }
    
}

