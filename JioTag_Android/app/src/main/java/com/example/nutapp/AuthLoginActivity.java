package com.example.nutapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;



public class AuthLoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText emailEdittext;
    private EditText passEdittext;
    private TextView emailTitleText;
    private TextView passTitleText;
    private  CallbackManager callbackManager;
    private LoginButton loginButton;
    private Button signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN =0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_auth_login);
        TextView title = findViewById(R.id.toolbar_title);
        TextView pass_condition_text = findViewById(R.id.password_condition);
        title.setText(JioUtils.AUTH_LOGIN_TITLE);
        emailEdittext = findViewById(R.id.login_edit_email);
        passEdittext = findViewById(R.id.login_edit_password);
        emailTitleText = findViewById(R.id.login_text_email);
        passTitleText = findViewById(R.id.login_text_password);
        pass_condition_text.setTypeface(JioUtils.mTypeface(this, 3));

        callbackManager = CallbackManager.Factory.create();

        loginButton = findViewById(R.id.fb_login);
        signInButton = findViewById(R.id.google_login);
        signInButton.setOnClickListener(this);

        emailEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(emailEdittext.getText().toString().isEmpty())
                {
                    emailTitleText.setVisibility(View.INVISIBLE);
                } else {
                    emailTitleText.setVisibility(View.VISIBLE);
                }
            }
        });

        passEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(passEdittext.getText().toString().isEmpty())
                {
                    passTitleText.setVisibility(View.INVISIBLE);
                } else {
                    passTitleText.setVisibility(View.VISIBLE);
                }
            }
        });

        googleSignIn();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                Toast.makeText(AuthLoginActivity.this,"FB login successfully done",Toast.LENGTH_SHORT).show();
                Intent startMain = new Intent(getApplicationContext(), JioAddFinder.class);
                startActivity(startMain);

            }

            @Override
            public void onCancel() {
                Toast.makeText(AuthLoginActivity.this,"FB login cancel done",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(AuthLoginActivity.this,"FB login fail done",Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        signOut();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    public void googleSignIn(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_login:
                signIn();
                break;
            // ...
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            Toast.makeText(this,"Google login successful now",Toast.LENGTH_SHORT).show();
            // Signed in successfully, show authenticated UI.
           // updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("AuthLoginActivity", "signInResult:failed code=" + e.getStatusCode());
           // updateUI(null);
        }
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }
}
