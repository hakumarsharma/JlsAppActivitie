//
//  LoginExtension.swift
//  PeopleTracker
//
//  Created by Apple on 13/04/20.
//  Copyright © 2020 Apple. All rights reserved.
//

import Foundation
import CocoaMQTT

extension LoginScreen : CocoaMQTTDelegate {
    
    struct cocomqtt {
        static var mqtt: CocoaMQTT!
    }
    
    // MQTT connection establishment
    func setUpMQTT() {
        cocomqtt.mqtt = CocoaMQTT(clientID: "866027030031773", host: Constants.MqttConstants.HostName, port: 1883)
        cocomqtt.mqtt.delegate = self
        cocomqtt.mqtt.username = Constants.MqttConstants.UserName
        cocomqtt.mqtt.password = Constants.MqttConstants.Password
        cocomqtt.mqtt.enableSSL = true
        cocomqtt.mqtt.keepAlive = 60
        
        let checkConncetion =  cocomqtt.mqtt.connect()
        if checkConncetion { // if connection is established publish a message
            // TODO : Change message formate to dynamic data
            let messageStr1 : String =  "{\"imi\":\"" + "866027030031773" + "\",\"evt\":\"GPS\",\"dvt\":\"JioDevice_g\",\"alc\":\"0\",\"lat\":\"" + "13.9488667" + "\",\"lon\":\"" + "78.7024609" + "\"," ;
            let messageStr2 : String = "\"ltd\":\"0\",\n" + "\"lnd\":\"0\",\"dir\":\"0\",\"pos\":\"A\",\"spd\":\"" + "12" + "\",\"tms\":\"" +  Utils.shared.currentDateToString() + "\",\"odo\":\"0\",\"ios\":\"0\",\"bat\":\"" + "40" + "\",\"sig\":\"" + "-80" + "\"}";
            let myStr = messageStr1 + messageStr2
            // TODO : Move to constants and pass dynamic IMEI
            cocomqtt.mqtt.publish("jioiot/svcd/jiophone/866027030031773/uc/fwd/locinfo", withString: myStr)
        }
        
    }
    
    
    // MARK: COCOAMQTT methods
    func mqtt(_ mqtt: CocoaMQTT, didConnect host: String, port: Int) {
        NSLog("connected")
    }
    
    func mqtt(_ mqtt: CocoaMQTT, didReceiveMessage message: CocoaMQTTMessage, id: UInt16 ) {
        NSLog("did receive message")
    }
    
    func mqtt(_ mqtt: CocoaMQTT, didReceive trust: SecTrust, completionHandler: @escaping (Bool) -> Void) {
        completionHandler(true)
        
    }
    
    func mqtt(_ mqtt: CocoaMQTT, didConnectAck ack: CocoaMQTTConnAck) {
    }
    
    func mqtt(_ mqtt: CocoaMQTT, didPublishMessage message: CocoaMQTTMessage, id: UInt16) {
        NSLog("published message")
        cocomqtt.mqtt.disconnect()
    }
    
    func mqtt(_ mqtt: CocoaMQTT, didPublishAck id: UInt16) {
    }
    
    func mqtt(_ mqtt: CocoaMQTT, didSubscribeTopic topic: String) {
    }
    
    func mqtt(_ mqtt: CocoaMQTT, didUnsubscribeTopic topic: String) {
    }
    
    func mqttDidPing(_ mqtt: CocoaMQTT) {
        NSLog("did ping")
    }
    
    func mqttDidReceivePong(_ mqtt: CocoaMQTT) {
    }
    
    func mqttDidDisconnect(_ mqtt: CocoaMQTT, withError err: Error?) {
        NSLog("disconnected")
    }
    
    func _console(_ info: String) {
    }
    func mqtt(_ mqtt: CocoaMQTT, didSubscribeTopic topics: [String]) {
        
    }
}
