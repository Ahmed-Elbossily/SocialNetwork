package com.ahmedelbossily.app.socialnetwork;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsActivity extends AppCompatActivity {

    private RecyclerView myFriendsList;

    private FirebaseAuth auth;
    private DatabaseReference UsersReference, FriendsReference;

    private String onlineUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        auth = FirebaseAuth.getInstance();
        onlineUserID = auth.getCurrentUser().getUid();
        UsersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        FriendsReference = FirebaseDatabase.getInstance().getReference().child("Friends").child(onlineUserID);

        myFriendsList = findViewById(R.id.friends_list);

        myFriendsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myFriendsList.setLayoutManager(linearLayoutManager);

        displayAllFriends();
    }

    private void updateUserStatus(String state) {
        String saveCurrentDate, saveCurrentTime;
        Calendar calendarForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendarForDate.getTime());

        Calendar calendarForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendarForTime.getTime());

        Map currentStateMap = new HashMap();
        currentStateMap.put("time", saveCurrentTime);
        currentStateMap.put("date", saveCurrentDate);
        currentStateMap.put("type", state);

        UsersReference.child(onlineUserID).child("userState").updateChildren(currentStateMap);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUserStatus("online");
    }

    @Override
    protected void onStop() {
        super.onStop();
        updateUserStatus("offline");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateUserStatus("offline");
    }

    private void displayAllFriends() {
        FirebaseRecyclerOptions<Friends> options = new FirebaseRecyclerOptions.Builder<Friends>()
                .setQuery(FriendsReference, Friends.class)
                .setLifecycleOwner(this)
                .build();

        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FriendsViewHolder holder, final int position, @NonNull final Friends model) {
                final String usersId = getRef(position).getKey();
                UsersReference.child(usersId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final String profileImage = dataSnapshot.child("profileImage").getValue().toString();
                            final String fullname = dataSnapshot.child("fullname").getValue().toString();
                            final String type;

                            if (dataSnapshot.hasChild("userState")) {
                                type = dataSnapshot.child("userState").child("type").getValue().toString();
                                if (type.equals("online")) {
                                    holder.onlineStatusView.setVisibility(View.VISIBLE);
                                } else {
                                    holder.onlineStatusView.setVisibility(View.INVISIBLE);
                                }
                            }

                            holder.setProfileImage(profileImage);
                            holder.setFullname(fullname);
                            holder.setDate(model.getDate());

                            holder.view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CharSequence options[] = new CharSequence[] {
                                            fullname + "'s Profile",
                                            "Send Message"
                                    };
                                    AlertDialog.Builder builder = new AlertDialog.Builder(FriendsActivity.this);
                                    builder.setTitle("Select Options");
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case 0:
                                                    Intent personProfileIntent = new Intent(FriendsActivity.this, PersonProfileActivity.class);
                                                    personProfileIntent.putExtra("visitUserId", usersId);
                                                    startActivity(personProfileIntent);
                                                    break;
                                                case 1:
                                                    Intent chatIntent = new Intent(FriendsActivity.this, ChatActivity.class);
                                                    chatIntent.putExtra("visitUserId", usersId);
                                                    chatIntent.putExtra("fullname", fullname);
                                                    startActivity(chatIntent);
                                                    break;
                                            }
                                        }
                                    });
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_users_display_layout, parent, false);
                return new FriendsViewHolder(view);
            }
        };
        myFriendsList.setAdapter(firebaseRecyclerAdapter);
        updateUserStatus("online");
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {
        View view;
        ImageView onlineStatusView;

        public FriendsViewHolder(View itemView) {
            super(itemView);
            view = itemView;

            onlineStatusView = itemView.findViewById(R.id.all_users_online_icon);
        }

        public void setProfileImage(String profileImage) {
            CircleImageView myImage = view.findViewById(R.id.all_users_profile_image);
            Picasso.get().load(profileImage).into(myImage);
        }

        public void setFullname(String fullname) {
            TextView myFullName = view.findViewById(R.id.all_users_profile_full_name);
            myFullName.setText(fullname);
        }

        public void setDate(String date) {
            TextView friendsDate = view.findViewById(R.id.all_users_status);
            friendsDate.setText("Friends since: " + date);
        }
    }
}
