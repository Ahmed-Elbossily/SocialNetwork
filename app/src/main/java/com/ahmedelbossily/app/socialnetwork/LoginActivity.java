package com.ahmedelbossily.app.socialnetwork;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    private EditText UserEmail, UserPassword;
    private Button LoginButton;
    private TextView NeedNewAccountLink;
    private ImageView FacebookSignInButton, TwitterSignInButton, GoogleSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        UserEmail = findViewById(R.id.login_email);
        UserPassword = findViewById(R.id.login_password);
        LoginButton = findViewById(R.id.login_button);
        NeedNewAccountLink = findViewById(R.id.register_account_link);
        FacebookSignInButton = findViewById(R.id.facebook_signin_button);
        TwitterSignInButton = findViewById(R.id.twitter_signin_button);
        GoogleSignInButton = findViewById(R.id.google_signin_button);

        NeedNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToRegisterActivity();
            }
        });
    }

    private void sendUserToRegisterActivity() {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
    }
}
