package com.ahmedelbossily.app.socialnetwork;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText searchInputText;
    private ImageButton searchButton;
    private RecyclerView searchResultList;

    private FirebaseAuth auth;
    private DatabaseReference AllUsersDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        auth = FirebaseAuth.getInstance();
        AllUsersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        toolbar = findViewById(R.id.find_friends_appbar_layout);
        searchInputText = findViewById(R.id.search_box_input);
        searchButton = findViewById(R.id.search_people_friends_button);
        searchResultList = findViewById(R.id.search_result_list);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Find Friends");

        searchResultList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        searchResultList.setLayoutManager(linearLayoutManager);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchBoxInput = searchInputText.getText().toString();
                searchPeopleAndFriends(searchBoxInput);
            }
        });
    }

    private void searchPeopleAndFriends(String searchBoxInput) {
        Toast.makeText(this, "Searching...", Toast.LENGTH_SHORT).show();

        Query searchPeopleAndFriendsQuery = AllUsersDatabaseReference.orderByChild("fullname").startAt(searchBoxInput).endAt(searchBoxInput + "\uf8ff");

        FirebaseRecyclerOptions<FindFriends> options = new FirebaseRecyclerOptions.Builder<FindFriends>()
                .setQuery(searchPeopleAndFriendsQuery, FindFriends.class)
                .setLifecycleOwner(this)
                .build();

        FirebaseRecyclerAdapter<FindFriends, FindFriendsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<FindFriends, FindFriendsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FindFriendsViewHolder holder, final int position, @NonNull FindFriends model) {
                        holder.setProfileImage(model.getProfileImage());
                        holder.setFullname(model.getFullname());
                        holder.setStatus(model.getStatus());

                        holder.view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String visitUserId = getRef(position).getKey();
                                Intent personProfileIntent = new Intent(FindFriendsActivity.this, PersonProfileActivity.class);
                                personProfileIntent.putExtra("visitUserId", visitUserId);
                                startActivity(personProfileIntent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_users_display_layout, parent, false);
                        return new FindFriendsViewHolder(view);
                    }
                };
        searchResultList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder {
        View view;

        public FindFriendsViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setProfileImage(String profileImage) {
            CircleImageView myImage = view.findViewById(R.id.all_users_profile_image);
            Picasso.get().load(profileImage).into(myImage);
        }

        public void setFullname(String fullname) {
            TextView myFullName = view.findViewById(R.id.all_users_profile_full_name);
            myFullName.setText(fullname);
        }

        public void setStatus(String status) {
            TextView myStatus = view.findViewById(R.id.all_users_status);
            myStatus.setText(status);
        }
    }
}
