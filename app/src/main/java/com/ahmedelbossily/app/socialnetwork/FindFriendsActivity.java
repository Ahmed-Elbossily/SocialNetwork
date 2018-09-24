package com.ahmedelbossily.app.socialnetwork;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.ImageButton;

public class FindFriendsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText searchBoxInput;
    private ImageButton searchPeopleFriendsButton;
    private RecyclerView searchResultList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        toolbar = findViewById(R.id.find_friends_appbar_layout);
        searchBoxInput = findViewById(R.id.search_box_input);
        searchPeopleFriendsButton = findViewById(R.id.search_people_friends_button);
        searchResultList = findViewById(R.id.search_result_list);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Find Friends");

        searchResultList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        searchResultList.setLayoutManager(linearLayoutManager);
    }
}
