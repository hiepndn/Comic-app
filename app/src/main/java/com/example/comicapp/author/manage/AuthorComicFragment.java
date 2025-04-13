package com.example.comicapp.author.manage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.comicapp.R;
import com.example.comicapp.Story;
import com.example.comicapp.author.manage.add.AddStory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class AuthorComicFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public AuthorComicFragment() {}
    private Button btnAdd;
    private RecyclerView recyclerView;
    private AuthorComicAdapter adapter;
    private List<Story> stories = new ArrayList<>();

    public static AuthorComicFragment newInstance(String param1, String param2) {
        AuthorComicFragment fragment = new AuthorComicFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_author_comic, container, false);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString("userId", null);
        load(userName, view);
        init(view);
        return view;
    }

    private void init(View view ) {
        btnAdd = view.findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddStory.class);
            startActivity(intent);
        });

    }

    private void load(String userName, View view) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("story");
        Query query = db.orderByChild("author").equalTo(userName);
        query.get().addOnCompleteListener(task -> {
            stories.clear();
            for(DataSnapshot ds : task.getResult().getChildren()){
                Story story = ds.getValue(Story.class);
                story.setId(ds.getKey());
                stories.add(story);
            }
            adapter.notifyDataSetChanged();
        });
        Log.e("stories: ", stories.size() + "");
        Log.e("stories: ", userName);
        recyclerView = view.findViewById(R.id.recyclerView);
        adapter = new AuthorComicAdapter(stories);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }
}