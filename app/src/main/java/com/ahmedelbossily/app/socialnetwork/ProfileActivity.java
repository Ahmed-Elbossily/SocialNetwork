package com.ahmedelbossily.app.socialnetwork;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference ProfileUserReference, PostsReference, FriendsReference;

    private CircleImageView myProfilePic;
    private TextView myProfileFullName, myProfileUsername, myProfileStatus, myProfileCountry, myProfileDob, myProfileGender, myProfileRelationshipStatus;
    private Button myPostButton, myFriendsButton;

    private String currentUserID;
    private int countFriends = 0, countPosts = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        currentUserID = auth.getCurrentUser().getUid();
        ProfileUserReference = FirebaseDatabase.getInstance().getReference().child("Users");
        PostsReference = FirebaseDatabase.getInstance().getReference().child("Posts");
        FriendsReference = FirebaseDatabase.getInstance().getReference().child("Friends");

        myProfilePic = findViewById(R.id.my_profile_pic);
        myProfileFullName = findViewById(R.id.my_profile_full_name);
        myProfileUsername = findViewById(R.id.my_profile_username);
        myProfileStatus = findViewById(R.id.my_profile_status);
        myProfileCountry = findViewById(R.id.my_profile_country);
        myProfileDob = findViewById(R.id.my_profile_dob);
        myProfileGender = findViewById(R.id.my_profile_gender);
        myProfileRelationshipStatus = findViewById(R.id.my_profile_relationship_status);
        myPostButton = findViewById(R.id.my_post_button);
        myFriendsButton = findViewById(R.id.my_friends_button);

        ProfileUserReference.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String fullname = dataSnapshot.child("fullname").getValue().toString();
                    String username = dataSnapshot.child("username").getValue().toString();
                    String status = dataSnapshot.child("status").getValue().toString();
                    String country = dataSnapshot.child("country").getValue().toString();
                    String dob = dataSnapshot.child("dob").getValue().toString();
                    String gender = dataSnapshot.child("gender").getValue().toString();
                    String relationshipStatus = dataSnapshot.child("relationshipStatus").getValue().toString();
                    String profileImage = dataSnapshot.child("profileImage").getValue().toString();

                    myProfileFullName.setText(fullname);
                    myProfileUsername.setText("@" + username);
                    myProfileStatus.setText(status);
                    myProfileCountry.setText("Country: " + country);
                    myProfileDob.setText("DOB: " + dob);
                    myProfileGender.setText("Gender: " + gender);
                    myProfileRelationshipStatus.setText("Relationship: " + relationshipStatus);
                    Picasso.get().load(profileImage).into(myProfilePic);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        PostsReference.orderByChild("uid").startAt(currentUserID).endAt(currentUserID + "\uf8ff").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    countPosts = (int) dataSnapshot.getChildrenCount();
                    myPostButton.setText(String.format("%s Posts", Integer.toString(countPosts)));
                } else {
                    myPostButton.setText("0 Posts");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FriendsReference.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    countFriends = (int) dataSnapshot.getChildrenCount();
                    myFriendsButton.setText(String.format("%s Friends", Integer.toString(countFriends)));
                } else {
                    myFriendsButton.setText("0 Friends");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        myPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToMyPostsActivity();
            }
        });

        myFriendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToFriendsActivity();
            }
        });
    }

    private void sendUserToMyPostsActivity() {
        Intent myPostsIntent = new Intent(ProfileActivity.this, MyPostsActivity.class);
        startActivity(myPostsIntent);
    }

    private void sendUserToFriendsActivity() {
        Intent friendsIntent = new Intent(ProfileActivity.this, FriendsActivity.class);
        startActivity(friendsIntent);
    }
}
