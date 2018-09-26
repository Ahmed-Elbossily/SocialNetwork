package com.ahmedelbossily.app.socialnetwork;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText resetEmailInput;
    private Button resetPasswordEmailButton;

    private FirebaseAuth auth;

    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        auth = FirebaseAuth.getInstance();

        toolbar = findViewById(R.id.forget_password_toolbar);
        resetEmailInput = findViewById(R.id.reset_password_email);
        resetPasswordEmailButton = findViewById(R.id.reset_password_email_button);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Reset Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        resetPasswordEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userEmail = resetEmailInput.getText().toString();
                if (TextUtils.isEmpty(userEmail)) {
                    Toast.makeText(ResetPasswordActivity.this, "Please write your email to reset password...", Toast.LENGTH_SHORT).show();
                } else {
                    auth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ResetPasswordActivity.this, "Please check your email account...", Toast.LENGTH_SHORT).show();
                                sendUserToLoginActivity();
                            } else {
                                String message = task.getException().getMessage();
                                Toast.makeText(ResetPasswordActivity.this, "Error occured: " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void sendUserToLoginActivity() {
        Intent loginActivityIntent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
        startActivity(loginActivityIntent);
    }
}
