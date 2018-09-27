package com.ahmedelbossily.app.socialnetwork;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonProfileActivity extends AppCompatActivity {

    private CircleImageView personProfilePic;
    private TextView personProfileFullName, personProfileUsername, personProfileStatus, personProfileCountry, personProfileDob, personProfileGender, personProfileRelationshipStatus;
    private Button sendFriendRequestButton, declineFriendRequestButton;

    private FirebaseAuth auth;
    private DatabaseReference UsersReference, FriendRequestsReference, FriendsReference;

    private String senderUserId, receiverUserId, CURRENT_STATE, saveCurrentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);

        receiverUserId = getIntent().getExtras().get("visitUserId").toString();

        auth = FirebaseAuth.getInstance();
        senderUserId = auth.getCurrentUser().getUid();
        UsersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        FriendsReference = FirebaseDatabase.getInstance().getReference().child("Friends");
        FriendRequestsReference = FirebaseDatabase.getInstance().getReference().child("FriendRequests");

        initFields();

        UsersReference.child(receiverUserId).addValueEventListener(new ValueEventListener() {
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

                    personProfileFullName.setText(fullname);
                    personProfileUsername.setText("@" + username);
                    personProfileStatus.setText(status);
                    personProfileCountry.setText("Country: " + country);
                    personProfileDob.setText("DOB: " + dob);
                    personProfileGender.setText("Gender: " + gender);
                    personProfileRelationshipStatus.setText("Relationship: " + relationshipStatus);
                    Picasso.get().load(profileImage).into(personProfilePic);

                    maintenanceOfButton();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        declineFriendRequestButton.setVisibility(View.GONE);
        declineFriendRequestButton.setEnabled(false);

        if (senderUserId.equals(receiverUserId)) {
            sendFriendRequestButton.setVisibility(View.GONE);
            declineFriendRequestButton.setVisibility(View.GONE);
        } else {
            sendFriendRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendFriendRequestButton.setEnabled(false);
                    if (CURRENT_STATE.equals("not_friends")) {
                        sendFriendRequest();
                    }
                    if (CURRENT_STATE.equals("request_sent")) {
                        cancelFriendRequest();
                    }
                    if (CURRENT_STATE.equals("request_received")) {
                        acceptFriendRequest();
                    }
                    if (CURRENT_STATE.equals("friends")) {
                        unFriend();
                    }
                }
            });
        }
    }

    private void initFields() {
        personProfilePic = findViewById(R.id.person_profile_pic);
        personProfileFullName = findViewById(R.id.person_profile_full_name);
        personProfileUsername = findViewById(R.id.person_profile_username);
        personProfileStatus = findViewById(R.id.person_profile_status);
        personProfileCountry = findViewById(R.id.person_profile_country);
        personProfileDob = findViewById(R.id.person_profile_dob);
        personProfileGender = findViewById(R.id.person_profile_gender);
        personProfileRelationshipStatus = findViewById(R.id.person_profile_relationship_status);
        sendFriendRequestButton = findViewById(R.id.person_send_friend_request_button);
        declineFriendRequestButton = findViewById(R.id.person_decline_friend_request_button);

        CURRENT_STATE = "not_friends";
    }

    private void maintenanceOfButton() {
        FriendRequestsReference.child(senderUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(receiverUserId)) {
                    String request_type = dataSnapshot.child(receiverUserId).child("request_type").getValue().toString();
                    if (request_type.equals("sent")) {
                        CURRENT_STATE = "request_sent";
                        sendFriendRequestButton.setText("Cancel Friend Request");
                        declineFriendRequestButton.setVisibility(View.GONE);
                        declineFriendRequestButton.setEnabled(false);
                    } else if (request_type.equals("received")) {
                        CURRENT_STATE = "request_received";
                        sendFriendRequestButton.setText("Accept Friend Request");
                        declineFriendRequestButton.setVisibility(View.VISIBLE);
                        declineFriendRequestButton.setEnabled(true);
                        declineFriendRequestButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cancelFriendRequest();
                            }
                        });
                    }
                } else {
                    FriendsReference.child(senderUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(receiverUserId)) {
                                CURRENT_STATE = "friends";
                                sendFriendRequestButton.setText("Unfriend");
                                declineFriendRequestButton.setVisibility(View.GONE);
                                declineFriendRequestButton.setEnabled(false);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendFriendRequest() {
        FriendRequestsReference.child(senderUserId).child(receiverUserId).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    FriendRequestsReference.child(receiverUserId).child(senderUserId).child("request_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                sendFriendRequestButton.setEnabled(true);
                                CURRENT_STATE = "request_sent";
                                sendFriendRequestButton.setText("Cancel Friend Request");
                                declineFriendRequestButton.setVisibility(View.GONE);
                                declineFriendRequestButton.setEnabled(false);
                            } else {
                                String message = task.getException().getMessage();
                                Toast.makeText(PersonProfileActivity.this, "Error occured: " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    String message = task.getException().getMessage();
                    Toast.makeText(PersonProfileActivity.this, "Error occured: " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void cancelFriendRequest() {
        FriendRequestsReference.child(senderUserId).child(receiverUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    FriendRequestsReference.child(receiverUserId).child(senderUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                sendFriendRequestButton.setEnabled(true);
                                CURRENT_STATE = "not_friends";
                                sendFriendRequestButton.setText("Send Friend Request");
                                declineFriendRequestButton.setVisibility(View.GONE);
                                declineFriendRequestButton.setEnabled(false);
                            } else {
                                String message = task.getException().getMessage();
                                Toast.makeText(PersonProfileActivity.this, "Error occured: " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    String message = task.getException().getMessage();
                    Toast.makeText(PersonProfileActivity.this, "Error occured: " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void acceptFriendRequest() {
        Calendar calendarForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calendarForDate.getTime());

        FriendsReference.child(senderUserId).child(receiverUserId).child("date").setValue(saveCurrentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    FriendsReference.child(receiverUserId).child(senderUserId).child("date").setValue(saveCurrentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                FriendRequestsReference.child(senderUserId).child(receiverUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            FriendRequestsReference.child(receiverUserId).child(senderUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        sendFriendRequestButton.setEnabled(true);
                                                        CURRENT_STATE = "friends";
                                                        sendFriendRequestButton.setText("Unfriend");
                                                        declineFriendRequestButton.setVisibility(View.GONE);
                                                        declineFriendRequestButton.setEnabled(false);
                                                    } else {
                                                        String message = task.getException().getMessage();
                                                        Toast.makeText(PersonProfileActivity.this, "Error occured: " + message, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        } else {
                                            String message = task.getException().getMessage();
                                            Toast.makeText(PersonProfileActivity.this, "Error occured: " + message, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                String message = task.getException().getMessage();
                                Toast.makeText(PersonProfileActivity.this, "Error occured: " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    String message = task.getException().getMessage();
                    Toast.makeText(PersonProfileActivity.this, "Error occured: " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void unFriend() {
        FriendsReference.child(senderUserId).child(receiverUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    FriendsReference.child(receiverUserId).child(senderUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                sendFriendRequestButton.setEnabled(true);
                                CURRENT_STATE = "not_friends";
                                sendFriendRequestButton.setText("Send Friend Request");
                                declineFriendRequestButton.setVisibility(View.GONE);
                                declineFriendRequestButton.setEnabled(false);
                            } else {
                                String message = task.getException().getMessage();
                                Toast.makeText(PersonProfileActivity.this, "Error occured: " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    String message = task.getException().getMessage();
                    Toast.makeText(PersonProfileActivity.this, "Error occured: " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
