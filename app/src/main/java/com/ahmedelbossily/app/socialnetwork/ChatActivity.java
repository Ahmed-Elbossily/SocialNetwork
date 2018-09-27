package com.ahmedelbossily.app.socialnetwork;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView receiverName;
    private CircleImageView receiverProfileImage;
    private RecyclerView userMessageList;
    private ImageButton sendImageFileButton, sendMessageButton;
    private EditText userMessageInput;

    //private FirebaseAuth auth;
    private DatabaseReference RootReference, UsersReference;

    private String messageReceiverId, messageReceiverName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageReceiverId = getIntent().getExtras().get("visitUserId").toString();
        messageReceiverName = getIntent().getExtras().get("fullname").toString();

        //auth = FirebaseAuth.getInstance();
        RootReference = FirebaseDatabase.getInstance().getReference();

        initFields();

        displayReceiverInfo();

        userMessageList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        userMessageList.setLayoutManager(linearLayoutManager);
    }

    private void initFields() {
        toolbar = findViewById(R.id.chat_bar_layout);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.chat_custom_bar, null);
        actionBar.setCustomView(actionBarView);

        receiverName = findViewById(R.id.custom_profile_name);
        receiverProfileImage = findViewById(R.id.custom_profile_image);

        userMessageList = findViewById(R.id.messages_list_users);
        sendImageFileButton = findViewById(R.id.send_image_file_button);
        sendMessageButton = findViewById(R.id.send_message_button);
        userMessageInput = findViewById(R.id.input_message);
    }

    private void displayReceiverInfo() {
        receiverName.setText(messageReceiverName);
        RootReference.child("Users").child(messageReceiverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String image = dataSnapshot.child("profileImage").getValue().toString();
                    Picasso.get().load(image).into(receiverProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
