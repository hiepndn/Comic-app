package com.example.comicapp.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comicapp.MainActivity;
import com.example.comicapp.R;
import com.example.comicapp.Story;
import com.example.comicapp.category.adapter.StoryAdapter;
import com.example.comicapp.search.fragment.SearchFragment;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private StoryAdapter storyAdapter;
    private List<Story> storyList;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewHome);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        storyList = new ArrayList<>();
        storyAdapter = new StoryAdapter(storyList);
        recyclerView.setAdapter(storyAdapter);


        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                navigateToSearchFragment();
            }
        });


        loadAllStories();

        return view;
    }

    private void loadAllStories() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("story");

        storyList.clear();
        storyAdapter.notifyDataSetChanged();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    Story story = child.getValue(Story.class);
                    if (story != null) {
                        story.setId(child.getKey());
                        storyList.add(story);
                    }
                }
                storyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi tải danh sách truyện", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToSearchFragment() {
        SearchView searchView = requireView().findViewById(R.id.searchView);
        searchView.clearFocus();

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, new SearchFragment());
        transaction.addToBackStack(null);
        transaction.commit();

        ((MainActivity) requireActivity()).receiveDataFromFragment(R.id.nav_search);
    }
}
