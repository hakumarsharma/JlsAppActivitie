//
//  HelpScreen.swift
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
import SideMenu

class HelpScreen: UIViewController, UIScrollViewDelegate {
    
    @IBOutlet weak var scrollView: UIScrollView!
    @IBOutlet weak var pageControl: UIPageControl!
    @IBOutlet weak var descriptionLabel: UILabel!
    @IBOutlet weak var titleLbl: UILabel!
    
    var screens: [String] = ["screen1","screen2","screen3","screen4"]
    var screenTitle : [String] = [Constants.HelpScreen.LoginTitle, Constants.HelpScreen.HomeTitle, Constants.HelpScreen.HomeTitle, Constants.HelpScreen.LocationTitle]
    var screenDescription : [String] = [Constants.HelpScreen.LoginText, Constants.HelpScreen.HomeText, Constants.HelpScreen.HomeText2, Constants.HelpScreen.LocationText]
    var frame = CGRect.zero
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.navigationController?.navigationBar.isHidden = true
        self.initializeData()
        print(Utils.shared.getFromEpochTime())
        print(Utils.shared.getToEpochTime())
    }
    
    func initializeData() {
        pageControl.numberOfPages = screens.count
        setupScreens()
        scrollView.delegate = self
        titleLbl.sizeToFit()
        titleLbl.text = screenTitle[0]
        descriptionLabel.text =  screenDescription[0]
    }
    
    
    // set scrollview with images
    func setupScreens() {
        for index in 0..<screens.count {
            frame.origin.x = scrollView.frame.size.width * CGFloat(index)
            frame.size = scrollView.frame.size
            let imgView = UIImageView(frame: frame)
            imgView.image = UIImage(named: screens[index])
            imgView.contentMode = .scaleAspectFit
            self.scrollView.addSubview(imgView)
        }
        scrollView.contentSize = CGSize(width: (scrollView.frame.size.width * CGFloat(screens.count)), height: scrollView.frame.size.height)
        scrollView.delegate = self
    }
    
    @IBAction func skipButtonAction(_ sender: Any) {
        let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
        let privacyViewController = storyBoard.instantiateViewController(withIdentifier: "PrivacyPolicyScreen") as! PrivacyPolicyScreen
        self.navigationController?.pushViewController(privacyViewController, animated: true)
    }
    
    // MARK: UIScrollView Delegate methods
    func scrollViewDidEndDecelerating(_ scrollView: UIScrollView) {
        let pageNumber = scrollView.contentOffset.x / scrollView.frame.size.width
        pageControl.currentPage = Int(pageNumber)
        titleLbl.text = screenTitle[Int(pageNumber)]
        descriptionLabel.text =  screenDescription[Int(pageNumber)]
    }
    
}
