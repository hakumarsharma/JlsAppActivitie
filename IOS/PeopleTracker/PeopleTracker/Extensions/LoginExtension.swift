//
//  LoginExtension.swift
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


import Foundation
import CocoaMQTT

extension LoginScreen : CocoaMQTTDelegate {
    
    struct cocomqtt {
        static var mqtt: CocoaMQTT!
    }
    
    func setUpMqtt() {
        //v.dev.tnt.cats.jvts.net
        cocomqtt.mqtt = CocoaMQTT(clientID: "9019930384", host: Constants.MqttConstants.HostName, port: 1883)
        cocomqtt.mqtt.username = Constants.MqttConstants.UserName
        cocomqtt.mqtt.password = Constants.MqttConstants.Password
        cocomqtt.mqtt.keepAlive = 60
        cocomqtt.mqtt.delegate = self
       let isconnected = cocomqtt.mqtt.connect()
        if isconnected {
        let messageString : String =  "{\"imi\":\"" + "9019930384" + "\",\"evt\":\"GPS\",\"dvt\":\"JioDevice_g\",\"alc\":\"0\",\"lat\":\"" + "15.9488667" + "\",\"lon\":\"" + "78.7024609" + "\"," ;
        let messageStr2 : String = "\"ltd\":\"0\",\n" + "\"lnd\":\"0\",\"dir\":\"0\",\"pos\":\"A\",\"spd\":\"" + "12" + "\",\"tms\":\"" +  Utils.shared.getCurrentDate(val:2) + "\",\"odo\":\"0\",\"ios\":\"1\",\"bat\":\"" + "40" + "\",\"sig\":\"" + "-80" + "\"}";
        let myStr = messageString + messageStr2
        print(myStr)
        cocomqtt.mqtt.publish("jioiot/svcd/jiophone/9019930384/uc/fwd/locinfo", withString: myStr)
        }
    }
     
    
    // Optional ssl CocoaMQTTDelegate
    func mqtt(_ mqtt: CocoaMQTT, didReceive trust: SecTrust, completionHandler: @escaping (Bool) -> Void) {
        NSLog("trust: \(trust)")
        /// Validate the server certificate
        ///
        /// Some custom validation...
        ///
        /// if validatePassed {
        ///     completionHandler(true)
        /// } else {
        ///     completionHandler(false)
        /// }
        completionHandler(true)
    }
    
    func mqtt(_ mqtt: CocoaMQTT, didConnectAck ack: CocoaMQTTConnAck) {
        NSLog("ack: \(ack)")
        
    }
    
    func mqtt(_ mqtt: CocoaMQTT, didStateChangeTo state: CocoaMQTTConnState) {
        NSLog("new state: \(state)")
    }
    
    func mqtt(_ mqtt: CocoaMQTT, didPublishMessage message: CocoaMQTTMessage, id: UInt16) {
        NSLog("Publishmessage: \(message.string!.description), id: \(id)")
    }
    
    func mqtt(_ mqtt: CocoaMQTT, didPublishAck id: UInt16) {
        NSLog("id: \(id)")
    }
    
    func mqtt(_ mqtt: CocoaMQTT, didReceiveMessage message: CocoaMQTTMessage, id: UInt16 ) {
        NSLog("Receivemessage: \(message.string!.description), id: \(id)")
    }
    
    func mqtt(_ mqtt: CocoaMQTT, didSubscribeTopics success: NSDictionary, failed: [String]) {
        NSLog("subscribed: \(success), failed: \(failed)")
    }
    
    func mqtt(_ mqtt: CocoaMQTT, didSubscribeTopic topics: [String]) {
        NSLog("topic: \(topics)")
    }
    
    func mqttDidPing(_ mqtt: CocoaMQTT) {
        NSLog("ping")
    }
    
    func mqttDidReceivePong(_ mqtt: CocoaMQTT) {
        NSLog("receivepong")
    }
    
    func mqttDidDisconnect(_ mqtt: CocoaMQTT, withError err: Error?) {
        
    }
    
    func mqtt(_ mqtt: CocoaMQTT, didUnsubscribeTopic topic: String) {
        
    }
    
    
    
    
}
