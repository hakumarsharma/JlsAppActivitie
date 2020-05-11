//
//  QRCodeScanner.swift
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
import AVFoundation

class QRCodeScanner: UIViewController , AVCaptureMetadataOutputObjectsDelegate {
    var captureSession: AVCaptureSession!
    var previewLayer: AVCaptureVideoPreviewLayer!

    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = "QRCode Scanner"
        view.backgroundColor = UIColor.black
       self.navigationItem.setHidesBackButton(true, animated: true)
       self.createBackBarButtonItem()
        self.intialiseData()
    }
    
    func intialiseData() {
        captureSession = AVCaptureSession()

               guard let videoCaptureDevice = AVCaptureDevice.default(for: .video) else { return }
               let videoInput: AVCaptureDeviceInput

               do {
                   videoInput = try AVCaptureDeviceInput(device: videoCaptureDevice)
               } catch {
                   return
               }

               if (captureSession.canAddInput(videoInput)) {
                   captureSession.addInput(videoInput)
               } else {
                   failed()
                   return
               }

               let metadataOutput = AVCaptureMetadataOutput()

               if (captureSession.canAddOutput(metadataOutput)) {
                   captureSession.addOutput(metadataOutput)

                   metadataOutput.setMetadataObjectsDelegate(self, queue: DispatchQueue.main)
                   metadataOutput.metadataObjectTypes = [.qr]
               } else {
                   failed()
                   return
               }

               previewLayer = AVCaptureVideoPreviewLayer(session: captureSession)
               previewLayer.frame = view.layer.bounds
               previewLayer.videoGravity = .resizeAspectFill
               view.layer.addSublayer(previewLayer)

               captureSession.startRunning()
    }

    func createBackBarButtonItem() {
          let backBtn : UIBarButtonItem = UIBarButtonItem.init(image: UIImage(named: "back"), style: .plain, target: self, action: #selector(backButton(sender:)))
          backBtn.tintColor = .white
          self.navigationItem.setLeftBarButton(backBtn, animated: true)
      }
      
      @objc func backButton(sender: UIBarButtonItem) {
          self.navigationController?.popViewController(animated: true)
      }
   
    
    func failed() {
        let ac = UIAlertController(title: "Scanning not supported", message: "Your device does not support scanning a code from an item. Please use a device with a camera.", preferredStyle: .alert)
        ac.addAction(UIAlertAction(title: "OK", style: .default))
        present(ac, animated: true)
        captureSession = nil
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        if (captureSession?.isRunning == false) {
            captureSession.startRunning()
        }
    }

    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)

        if (captureSession?.isRunning == true) {
            captureSession.stopRunning()
        }
    }

    func metadataOutput(_ output: AVCaptureMetadataOutput, didOutput metadataObjects: [AVMetadataObject], from connection: AVCaptureConnection) {
        captureSession.stopRunning()

        if let metadataObject = metadataObjects.first {
            guard let readableObject = metadataObject as? AVMetadataMachineReadableCodeObject else { return }
            guard let stringValue = readableObject.stringValue else { return }
            AudioServicesPlaySystemSound(SystemSoundID(kSystemSoundID_Vibrate))
            found(code: stringValue)
        }

        dismiss(animated: true)
    }

    func found(code: String) {
        print(code)
    }

    override var prefersStatusBarHidden: Bool {
        return true
    }

    override var supportedInterfaceOrientations: UIInterfaceOrientationMask {
        return .portrait
    }
}
