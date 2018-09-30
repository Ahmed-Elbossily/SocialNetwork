package com.ahmedelbossily.app.socialnetwork;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder> {

    private FirebaseAuth auth;
    private DatabaseReference UsersReference;

    private List<Messages> userMessagesList;

    public MessagesAdapter(List<Messages> userMessagesList) {
        this.userMessagesList = userMessagesList;
    }

    @NonNull
    @Override
    public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout_of_users, parent, false);
        auth = FirebaseAuth.getInstance();
        return new MessagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessagesViewHolder holder, int position) {
        String messageSenderID = auth.getCurrentUser().getUid();
        Messages messages = userMessagesList.get(position);
        String fromUserID = messages.getFrom();
        String fromMessageType = messages.getType();
        UsersReference = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserID);
        UsersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String image = dataSnapshot.child("profileImage").getValue().toString();
                    Picasso.get().load(image).into(holder.messageProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (fromMessageType.equals("text")) {
            holder.messageProfileImage.setVisibility(View.INVISIBLE);
            holder.receiverMessageText.setVisibility(View.INVISIBLE);
            if (fromUserID.equals(messageSenderID)) {
                //holder.senderMessageText.setBackgroundResource(R.drawable.sender_message_text_background);
                holder.senderMessageText.setText(messages.getMessage());
            } else {
                holder.senderMessageText.setVisibility(View.INVISIBLE);
                holder.messageProfileImage.setVisibility(View.VISIBLE);
                holder.receiverMessageText.setVisibility(View.VISIBLE);
                holder.receiverMessageText.setText(messages.getMessage());
            }
        }
    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }

    public class MessagesViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView messageProfileImage;
        public TextView receiverMessageText;
        public TextView senderMessageText;

        public MessagesViewHolder(View itemView) {
            super(itemView);
            messageProfileImage = itemView.findViewById(R.id.message_profile_image);
            receiverMessageText = itemView.findViewById(R.id.receiver_message_text);
            senderMessageText = itemView.findViewById(R.id.sender_message_text);
        }
    }
}
