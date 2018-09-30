package com.ahmedelbossily.app.socialnetwork;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyPostsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView myPostsList;

    private FirebaseAuth auth;
    private DatabaseReference UsersReference, PostsReference, LikesReference;

    private String currentUserID;
    private Boolean likeChecker = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);

        auth = FirebaseAuth.getInstance();
        currentUserID = auth.getCurrentUser().getUid();
        UsersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        PostsReference = FirebaseDatabase.getInstance().getReference().child("Posts");
        LikesReference = FirebaseDatabase.getInstance().getReference().child("Likes");

        toolbar = findViewById(R.id.my_posts_bar_layout);
        myPostsList = findViewById(R.id.my_all_posts_list);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("My Posts");

        myPostsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myPostsList.setLayoutManager(linearLayoutManager);

        displayMyAllPosts();
    }

    private void displayMyAllPosts() {
        Query myPostsQuery = PostsReference.orderByChild("uid").startAt(currentUserID).endAt(currentUserID + "\uf8ff");

        FirebaseRecyclerOptions<Posts> options = new FirebaseRecyclerOptions.Builder<Posts>()
                .setQuery(myPostsQuery, Posts.class)
                .setLifecycleOwner(this)
                .build();

        FirebaseRecyclerAdapter<Posts, MyPostViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Posts, MyPostViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyPostViewHolder holder, int position, @NonNull Posts model) {
                final String postKey = getRef(position).getKey();

                holder.setFullname(model.getFullname());
                holder.setDescription(model.getDescription());
                holder.setProfileImage(model.getProfileImage());
                holder.setPostImage(model.getPostImage());
                holder.setDate(model.getDate());
                holder.setTime(model.getTime());

                holder.setLikeButtonStatus(postKey);

                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent clickPostIntent = new Intent(MyPostsActivity.this, ClickPostActivity.class);
                        clickPostIntent.putExtra("PostKey", postKey);
                        startActivity(clickPostIntent);
                    }
                });

                holder.likePostButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        likeChecker = true;
                        LikesReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (likeChecker.equals(true)) {
                                    if (dataSnapshot.child(postKey).hasChild(currentUserID)) {
                                        LikesReference.child(postKey).child(currentUserID).removeValue();
                                        likeChecker = false;
                                    } else {
                                        LikesReference.child(postKey).child(currentUserID).setValue(true);
                                        likeChecker = false;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });

                holder.commentPostButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent commentsIntent = new Intent(MyPostsActivity.this, CommentsActivity.class);
                        commentsIntent.putExtra("PostKey", postKey);
                        startActivity(commentsIntent);
                    }
                });
            }

            @NonNull
            @Override
            public MyPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_posts_layout, parent, false);
                return new MyPostViewHolder(view);
            }
        };
        myPostsList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class MyPostViewHolder extends RecyclerView.ViewHolder {
        View view;
        ImageButton likePostButton, commentPostButton;
        TextView displayNoOfLikes;
        int countLikes;
        String currentUserId;
        DatabaseReference LikesReference;

        public MyPostViewHolder(View itemView) {
            super(itemView);
            view = itemView;

            likePostButton = view.findViewById(R.id.like_button);
            commentPostButton = view.findViewById(R.id.comment_button);
            displayNoOfLikes = view.findViewById(R.id.display_no_of_likes);

            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            LikesReference = FirebaseDatabase.getInstance().getReference().child("Likes");
        }

        public void setLikeButtonStatus(final String postKey) {
            LikesReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(postKey).hasChild(currentUserId)) {
                        countLikes = (int) dataSnapshot.child(postKey).getChildrenCount();
                        likePostButton.setImageResource(R.drawable.like);
                        displayNoOfLikes.setText(Integer.toString(countLikes) + " Likes");
                    } else {
                        countLikes = (int) dataSnapshot.child(postKey).getChildrenCount();
                        likePostButton.setImageResource(R.drawable.dislike);
                        displayNoOfLikes.setText(Integer.toString(countLikes) + " Likes");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        public void setFullname(String fullname) {
            TextView username = view.findViewById(R.id.post_user_name);
            username.setText(fullname);
        }

        public void setDescription(String description) {
            TextView postDescription = view.findViewById(R.id.post_description);
            postDescription.setText(description);
        }

        public void setProfileImage(String profileImage) {
            CircleImageView image = view.findViewById(R.id.post_profile_image);
            Picasso.get().load(profileImage).into(image);
        }

        public void setPostImage(String postImage) {
            ImageView image = view.findViewById(R.id.post_image);
            Picasso.get().load(postImage).into(image);
        }

        public void setDate(String date) {
            TextView postDate = view.findViewById(R.id.post_date);
            postDate.setText("   " + date);
        }

        public void setTime(String time) {
            TextView postTime = view.findViewById(R.id.post_time);
            postTime.setText("   " + time);
        }
    }
}
