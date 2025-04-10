package com.example.comicapp.search.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comicapp.R;
import com.example.comicapp.Story;
import com.example.comicapp.category.adapter.StoryAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private StoryAdapter storyAdapter;
    private List<Story> allStories = new ArrayList<>();
    private List<Story> filteredStories = new ArrayList<>();
    private DatabaseReference databaseRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        SearchView searchView = view.findViewById(R.id.search);
        recyclerView = view.findViewById(R.id.recyclerViewSearch);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        storyAdapter = new StoryAdapter(filteredStories);
        recyclerView.setAdapter(storyAdapter);

        databaseRef = FirebaseDatabase.getInstance().getReference("story");
        loadStoriesFromFirebase();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterStories(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterStories(newText);
                return true;
            }
        });

        return view;
    }

    private void loadStoriesFromFirebase() {
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allStories.clear();
                for (DataSnapshot storySnapshot : snapshot.getChildren()) {
                    Story story = storySnapshot.getValue(Story.class);
                    if (story != null) {
                        story.setId(storySnapshot.getKey()); // Gán ID từ key Firebase
                        allStories.add(story);
                    }
                }
                filteredStories.clear();
                filteredStories.addAll(allStories);
                storyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error if needed
            }
        });
    }

    private void filterStories(String keyword) {
        filteredStories.clear();
        if (keyword == null || keyword.isEmpty()) {
            filteredStories.addAll(allStories);
        } else {
            String lowerKeyword = keyword.toLowerCase(Locale.getDefault());
            for (Story story : allStories) {
                if (story.getName().toLowerCase(Locale.getDefault()).contains(lowerKeyword)) {
                    filteredStories.add(story);
                }
            }
        }
        storyAdapter.setQuery(keyword);
        storyAdapter.notifyDataSetChanged();
    }
}
