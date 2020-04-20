package com.example.nutapp;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.MediaActionSound;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    SurfaceView m_surfaceview;
    SurfaceHolder m_surfaceHolder;
    private Camera mCamera;
    private String mCurrentPhotoPath;
    File image;


    public void addToGallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        //File f = new File(mCurrentPhotoPath);
        File f = new File(image.getAbsolutePath());
        Log.w("INFO", image.getAbsolutePath().toString());
        Log.w("INFO", mCurrentPhotoPath.toString());
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        //File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );
        image = new File(storageDir, imageFileName + ".jpg");
        Log.d("IMAGEPATH", storageDir.getAbsolutePath() + imageFileName);

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }


    Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d("CAMPIC", "Success");
            try {
                MediaActionSound sound = new MediaActionSound();
                sound.play(MediaActionSound.SHUTTER_CLICK);
                createImageFile();
                FileOutputStream fout = new FileOutputStream(image);
                fout.write(data);
                fout.close();
                addToGallery();
            } catch (Exception e) {
                Log.d("ONPIC", "EXCEPTION");
            }
            //attach_asset_camera.setImageBitmap(BitmapFactory.decodeByteArray(data,0,data.length));
        }

    };


    private void startCamera1() {
        mCamera = Camera.open();
        mCamera.setDisplayOrientation(90);
        mCamera.enableShutterSound(true);
        mCamera.getParameters().setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        try {
            mCamera.setPreviewDisplay(m_surfaceHolder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startProcessing() {
        startCamera1();
        Handler m_handler = new Handler();
        Runnable r = new Runnable() {
            public void run() {
                try {
                    mCamera.enableShutterSound(true);
                    mCamera.takePicture(null, null, mPicture);
                } catch (Exception e) {
                    Log.d("CAMERR", e.getMessage());
                }
            }
        };
        m_handler.postDelayed(r, 3000);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_layout);
        m_surfaceview = findViewById(R.id.surfaceView);
        m_surfaceHolder = m_surfaceview.getHolder();
        m_surfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("SURFACE", "surfaceCreated");
        startProcessing();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d("SURFACE", "surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("SURFACE", "surfaceDestroyed");
    }
}
