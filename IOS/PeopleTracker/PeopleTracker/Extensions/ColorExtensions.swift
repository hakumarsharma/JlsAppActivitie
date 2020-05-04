//
//  ColorExtensions.swift
//  PeopleTracker
//
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

extension UIColor {
    
    struct AppTheme {
        static var PrimaryColor: UIColor  { return UIColor(red: 56/255.0, green: 86/255.0, blue: 133/255.0, alpha: 1) }
        
    }
    struct Common {
        static var TextFieldBorderColor : UIColor { return UIColor(red: 206/255.0, green: 206/255.0, blue: 206/255.0, alpha: 1)}
    }
    struct Consent {
        static var ConsentApproved : UIColor {return UIColor(red: 132/255.0, green: 222/255.0, blue: 2/255.0, alpha: 1.0)}
        static var ConsentPending  : UIColor {return UIColor(red: 255/255.0, green: 102/255.0, blue: 0/255.0, alpha: 1.0)}
        static var RequestConsent  : UIColor {return UIColor(red: 72/255.0, green: 180/255.0, blue: 224/255.0, alpha: 1.0)}
        static var ConsentSent     : UIColor {return UIColor(red: 135/255.0, green: 100/255.0, blue: 62/255.0, alpha: 1.0) }
    }
    
}
