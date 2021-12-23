
package com.jio.rtlsappfull.internal;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.jio.rtlsappfull.BuildConfig;
import com.jio.rtlsappfull.R;
import com.jio.rtlsappfull.utils.JiotUtils;

public class AboutAppActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(R.string.about_app);
        TextView versionName = findViewById(R.id.app_version);
        versionName.setText("Version : v" + BuildConfig.VERSION_NAME);
        Button nextBtn = findViewById(R.id.next_btn);
        nextBtn.setOnClickListener(v -> {
            Intent i = new Intent(this, JiotUserName.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
            SharedPreferences.Editor editor = getSharedPreferences("shared_prefs", MODE_PRIVATE).edit();
            editor.putString("about_app", "yes");
            editor.commit();
            finish();
        });
    }
}
