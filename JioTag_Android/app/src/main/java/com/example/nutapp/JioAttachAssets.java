package com.example.nutapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class JioAttachAssets extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    int nut_position = 0;
    String m_deviceAddress = "";
    String m_deviceposName="JioTag";
    ImageButton attach_asset_camera;
    Uri m_cameraImageUri = Uri.EMPTY;
    //camera
    public static final int CAMERA_MENU_REQUEST_CODE = 200;
    private String mCurrentPhotoPath;
    private Bitmap mImageBitmap;
    File image;
    EditText asset_custom_name;
    Button attach_asset_btn_add;
    Button friendly_name_wallet;
    Button friendly_name_laptop;
    Button friendly_name_suitcase;
    Button friendly_name_purse;
    Button friendly_name_key;
    Button friendly_name_others;

    public void clearTicks() {
        friendly_name_wallet.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.wallet),
                null, null);
        friendly_name_laptop.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.laptop_home),
                null, null);
        friendly_name_suitcase.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.suitcase),
                null, null);
        friendly_name_purse.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.purse),
                null, null);
        friendly_name_key.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.key),
                null, null);
        friendly_name_others.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.others),
                null, null);
    }

    public void setHighlight(String resource) {

        clearTicks();
        switch (resource) {
            case "wallet":
                friendly_name_wallet.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.wallet),
                        getResources().getDrawable(R.drawable.tick), null);
                break;
            case "purse":
                friendly_name_purse.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.purse),
                        getResources().getDrawable(R.drawable.tick), null);
                break;

            case "key":
                friendly_name_key.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.key),
                        getResources().getDrawable(R.drawable.tick), null);
                break;
            case "suitcase":
                friendly_name_suitcase.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.suitcase),
                        getResources().getDrawable(R.drawable.tick), null);
                break;
            case "laptop":
                friendly_name_laptop.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.laptop_home),
                        getResources().getDrawable(R.drawable.tick), null);
                break;
            case "others":
                friendly_name_others.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.others),
                        getResources().getDrawable(R.drawable.tick), null);
                break;
            default:
                friendly_name_others.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.others),
                        getResources().getDrawable(R.drawable.tick), null);
                break;

        }
    }

    AlertDialog.Builder builder;
    AlertDialog m_disconnectAlert;
    public void showCustomTextAlert(String nutDevAddress) {
            //builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Dialog ));
            builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.Theme_Holo_Dialog_Alert));
            builder.setMessage("Please Enter the Custom Asset Name").setTitle("Alert")
                    .setCancelable(false)
                    .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setIcon(R.drawable.ic_action_settings_remote);

            m_disconnectAlert = builder.create();
            //m_disconnectAlert.setTitle(bleDevice.name + " Disconnected");
            m_disconnectAlert.setTitle("Alert");
            //m_disconnectAlert.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.asset_dialog_round_bkgd));
            m_disconnectAlert.show();
            Button bNegative = m_disconnectAlert.getButton(DialogInterface.BUTTON_NEGATIVE);
            bNegative.setBackgroundColor(0xFFCCF2FD);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attach_asset_view);

        Intent receiveIntent = this.getIntent();
        nut_position = receiveIntent.getIntExtra("POSITION_NUT", 0);
        m_deviceAddress = receiveIntent.getStringExtra("POSITION_ADDR");
        m_deviceposName = receiveIntent.getStringExtra("POSITION_NAME");

        final TextView filledNameValue = (TextView) findViewById(R.id.attach_asset_header);
        filledNameValue.setTypeface(JioUtils.mTypeface(this, 5));

        attach_asset_btn_add = (Button) findViewById(R.id.attach_asset_btn_add);
/*        attach_asset_btn_add.setEnabled(false);
        attach_asset_btn_add.setBackgroundResource(R.drawable.disabled_button);*/
        friendly_name_key = (Button) findViewById(R.id.attach_button_key);
        friendly_name_key.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendly_name_key.setPressed(true);
                attach_asset_btn_add.setEnabled(true);
                attach_asset_btn_add.setBackgroundResource(R.drawable.button_frame_blue);
                filledNameValue.setText(getResources().getString(R.string.friendly_key));
                setHighlight("key");
            }
        });
        friendly_name_wallet = (Button) findViewById(R.id.attach_button_wallet);
        friendly_name_wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendly_name_wallet.setPressed(true);
                attach_asset_btn_add.setEnabled(true);
                attach_asset_btn_add.setBackgroundResource(R.drawable.button_frame_blue);
                filledNameValue.setText(getResources().getString(R.string.friendly_wallet));
                setHighlight("wallet");
            }
        });
        friendly_name_laptop = (Button) findViewById(R.id.attach_button_laptops);
        friendly_name_laptop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendly_name_laptop.setPressed(true);
                attach_asset_btn_add.setEnabled(true);
                attach_asset_btn_add.setBackgroundResource(R.drawable.button_frame_blue);
                filledNameValue.setText(getResources().getString(R.string.friendly_laptops));
                setHighlight("laptop");
            }
        });
        friendly_name_suitcase = (Button) findViewById(R.id.attach_button_suitcase);
        friendly_name_suitcase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendly_name_suitcase.setPressed(true);
                attach_asset_btn_add.setEnabled(true);
                attach_asset_btn_add.setBackgroundResource(R.drawable.button_frame_blue);
                filledNameValue.setText(getResources().getString(R.string.friendly_suitcase));
                setHighlight("suitcase");
            }
        });
        friendly_name_others = (Button) findViewById(R.id.attach_button_others);
        friendly_name_others.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendly_name_others.setPressed(true);
                attach_asset_btn_add.setEnabled(true);
                attach_asset_btn_add.setBackgroundResource(R.drawable.button_frame_blue);
                filledNameValue.setText(getResources().getString(R.string.friendly_others));
                setHighlight("others");
            }
        });

        friendly_name_purse = (Button) findViewById(R.id.attach_button_purse);
        friendly_name_purse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attach_asset_btn_add.setEnabled(true);
                friendly_name_purse.setPressed(true);
                attach_asset_btn_add.setBackgroundResource(R.drawable.button_frame_blue);
                filledNameValue.setText(getResources().getString(R.string.friendly_purse));
                setHighlight("purse");
            }
        });
        ImageButton attachBack = (ImageButton) findViewById(R.id.attach_back);
        attachBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
        ImageButton attachSave = (ImageButton) findViewById(R.id.attach_tick);
        attachSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("FRNAME", filledNameValue.getText().toString());
                Intent intent = new Intent();
                intent.putExtra("POSITION_NUT", nut_position);
                intent.putExtra("POSITION_TYPE", filledNameValue.getText().toString());
                intent.putExtra("POSITION_ADDR", m_deviceAddress);
                intent.putExtra("IMAGEURI", m_cameraImageUri.toString());
                intent.putExtra("CUSTOMNAME", asset_custom_name.getText().toString());
                intent.putExtra("CUSTOMNAME_NEW", filledNameValue.getText().toString() + "." + asset_custom_name.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });


        attach_asset_btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("FRNAME", filledNameValue.getText().toString());
                if (asset_custom_name.getText().toString().isEmpty()) {
                    //Custom names will be prefixed with key.custom etc
                    showCustomTextAlert(m_deviceAddress);
                    //Toast.makeText(v.getContext(), "Please Enter the custom Name", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("POSITION_NUT", nut_position);
                    intent.putExtra("POSITION_TYPE", filledNameValue.getText().toString());
                    intent.putExtra("POSITION_ADDR", m_deviceAddress);
                    intent.putExtra("IMAGEURI", m_cameraImageUri.toString());
                    intent.putExtra("CUSTOMNAME", asset_custom_name.getText().toString());
                    intent.putExtra("CUSTOMNAME_NEW", filledNameValue.getText().toString() + "." + asset_custom_name.getText().toString());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        attach_asset_camera = (ImageButton) findViewById(R.id.attach_asset_camera);
        attach_asset_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCameraPopup(v);
            }
        });

        asset_custom_name = (EditText) findViewById(R.id.asset_custom_name);
        String resourceId=m_deviceposName.toLowerCase(Locale.ROOT).split("\\.")[0];
        setHighlight(resourceId);
        if(!"JioTag".equalsIgnoreCase(resourceId)){
            filledNameValue.setText(m_deviceposName.split("\\.")[0]);
        }
        attach_asset_btn_add.setEnabled(true);
    }

    public void showCameraPopup(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(view.getContext(), view, Gravity.BOTTOM);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.camera_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }

    public static final int RESULT_GALLERY = 133;

    public void launchGallery() {

        Intent galleryIntent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_GALLERY);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.camera_take_photo:
                Log.d("CAMMENU", "take photo");
                startCamera();
                break;
            case R.id.camera_open_gallery:
                Log.d("CAMMENU", "open gallery");
                launchGallery();
                break;
            default:
                break;
        }
        return false;
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


    public void startCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(this.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.i("CAMTAG", "IOException");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(cameraIntent, CAMERA_MENU_REQUEST_CODE);
            }
        }
    }

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_GALLERY && resultCode == RESULT_OK) {
            if (null != data) {
                Log.d("GALLERY PICK", requestCode + "");
                m_cameraImageUri = data.getData();
                attach_asset_camera.setImageURI(m_cameraImageUri);
            }
        }
        if (requestCode == CAMERA_MENU_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                Log.d("CAMTAG", "PICTAKEN");
                mImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
                Log.d("JioAttachAssets", "value of mImageBitmap"+mImageBitmap);
                m_cameraImageUri = Uri.parse(mCurrentPhotoPath);
                attach_asset_camera.setImageURI(m_cameraImageUri);
                //mImageView.setImageBitmap(mImageBitmap);
/*                Intent i = new Intent(Intent.ACTION_VIEW);

                i.setDataAndType(Uri.fromFile(image), "image/jpeg");
                startActivity(i);*/
                addToGallery();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
