package com.sidegigapps.chorematic.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.sidegigapps.chorematic.R;

import jonathanfinerty.once.Once;

/**
 * Created by ryand on 10/28/2016.
 */

public class SignInActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.forgotPasswordTextView).setOnClickListener(this);
        findViewById(R.id.signUpTextView).setOnClickListener(this);
        findViewById(R.id.emailSignIn).setOnClickListener(this);

        //testing
        findViewById(R.id.button2).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
           case R.id.emailSignIn:
               showComingSoonToast();
                break;
            case R.id.signUpTextView:
                showComingSoonToast();
                break;
            case R.id.forgotPasswordTextView:
                showComingSoonToast();
                break;
            case R.id.button2:
                Intent intent = new Intent(SignInActivity.this, ChooserActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void showComingSoonToast() {
        Toast.makeText(this, R.string.in_development_toast_string,Toast.LENGTH_SHORT).show();
    }


}
