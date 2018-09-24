package com.ahmedelbossily.app.socialnetwork;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CircleImageView settingsProfileImage;
    private EditText settingsStatus, settingsUsername, settingsProfileFullName, settingsCountry, settingsDob, settingsGender, settingsRelationshipStatus;
    private Button updateAccountSettingsButton;
    private SpotsDialog spotsDialog;

    private FirebaseAuth auth;
    private DatabaseReference SettingsUserReference;
    private StorageReference UserProfileImageReference;

    private String currentUserID;
    private static final int GALLERY_PICK_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        auth = FirebaseAuth.getInstance();
        currentUserID = auth.getCurrentUser().getUid();
        SettingsUserReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        UserProfileImageReference = FirebaseStorage.getInstance().getReference().child("Profile Images");

        toolbar = findViewById(R.id.settings_toolbar);
        settingsProfileImage = findViewById(R.id.settings_profile_image);
        settingsStatus = findViewById(R.id.settings_status);
        settingsUsername = findViewById(R.id.settings_username);
        settingsProfileFullName = findViewById(R.id.settings_profile_full_name);
        settingsCountry = findViewById(R.id.settings_country);
        settingsDob = findViewById(R.id.settings_dob);
        settingsGender = findViewById(R.id.settings_gender);
        settingsRelationshipStatus = findViewById(R.id.settings_relationship_status);
        updateAccountSettingsButton = findViewById(R.id.update_account_settings_button);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Account Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SettingsUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String status = dataSnapshot.child("status").getValue().toString();
                    String username = dataSnapshot.child("username").getValue().toString();
                    String fullname = dataSnapshot.child("fullname").getValue().toString();
                    String country = dataSnapshot.child("country").getValue().toString();
                    String dob = dataSnapshot.child("dob").getValue().toString();
                    String gender = dataSnapshot.child("gender").getValue().toString();
                    String relationshipStatus = dataSnapshot.child("relationshipStatus").getValue().toString();
                    String profileImage = dataSnapshot.child("profileImage").getValue().toString();

                    settingsStatus.setText(status);
                    settingsUsername.setText(username);
                    settingsProfileFullName.setText(fullname);
                    settingsCountry.setText(country);
                    settingsDob.setText(dob);
                    settingsGender.setText(gender);
                    settingsRelationshipStatus.setText(relationshipStatus);
                    Picasso.get().load(profileImage).into(settingsProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        updateAccountSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAccountInfo();
            }
        });

        settingsProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_PICK_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK_CODE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                LoadingBar();
                spotsDialog.show();

                Uri resultUri = result.getUri();
                final StorageReference filePath = UserProfileImageReference.child(currentUserID + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SettingsActivity.this, "Profile Image stored successfully to Firebase Storage...", Toast.LENGTH_SHORT).show();
                            final String downloadUrl = task.getResult().getDownloadUrl().toString();
                            Log.e("URL", "Download Url: " + downloadUrl);

                            SettingsUserReference.child("profileImage").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Intent selfIntent = new Intent(SettingsActivity.this, SettingsActivity.class);
                                        startActivity(selfIntent);
                                        Toast.makeText(SettingsActivity.this, "Profile Image stored successfully to Firebase Database...", Toast.LENGTH_SHORT).show();
                                        spotsDialog.dismiss();
                                    } else {
                                        String message = task.getException().getMessage();
                                        Toast.makeText(SettingsActivity.this, "Error occured: " + message, Toast.LENGTH_SHORT).show();
                                        spotsDialog.dismiss();
                                    }
                                }
                            });
                        } else {
                            String message = task.getException().getMessage();
                            Toast.makeText(SettingsActivity.this, "Error occured: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                spotsDialog.dismiss();
            }
        }
    }

    private void validateAccountInfo() {
        String status = settingsStatus.getText().toString();
        String username = settingsUsername.getText().toString();
        String fullname = settingsProfileFullName.getText().toString();
        String country = settingsCountry.getText().toString();
        String dob = settingsDob.getText().toString();
        String gender = settingsGender.getText().toString();
        String relationshipStatus = settingsRelationshipStatus.getText().toString();

        if (TextUtils.isEmpty(status)) {
            Toast.makeText(this, "Please write your status...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Please write your username...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(fullname)) {
            Toast.makeText(this, "Please write your fullname...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(country)) {
            Toast.makeText(this, "Please write your country...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(dob)) {
            Toast.makeText(this, "Please write your date of birth...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(gender)) {
            Toast.makeText(this, "Please write your gender...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(relationshipStatus)) {
            Toast.makeText(this, "Please write your relationship status...", Toast.LENGTH_SHORT).show();
        } else {
            LoadingBar();
            spotsDialog.show();
            updateAccountInfo(status, username, fullname, country, dob, gender, relationshipStatus);
        }
    }

    private void updateAccountInfo(String status, String username, String fullname, String country, String dob, String gender, String relationshipStatus) {
        HashMap userMap = new HashMap();
        userMap.put("status", status);
        userMap.put("username", username);
        userMap.put("fullname", fullname);
        userMap.put("country", country);
        userMap.put("dob", dob);
        userMap.put("gender", gender);
        userMap.put("relationshipStatus", relationshipStatus);

        SettingsUserReference.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    sendUserToMainActivity();
                    Toast.makeText(SettingsActivity.this, "Account settings updated successfully...", Toast.LENGTH_SHORT).show();
                    spotsDialog.dismiss();
                } else {
                    String message = task.getException().getMessage();
                    Toast.makeText(SettingsActivity.this, "Error occured: " + message, Toast.LENGTH_SHORT).show();
                    spotsDialog.dismiss();
                }
            }
        });
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void LoadingBar(){
        spotsDialog = (SpotsDialog) new SpotsDialog.Builder()
                .setContext(SettingsActivity.this)
                //.setMessage("Please wait, while we are creating your new account...")
                .setCancelable(false)
                .build();
    }
}
