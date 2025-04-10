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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private StoryAdapter storyAdapter;
    private List<Story> allStories;  // Danh sách đầy đủ
    private List<Story> filteredStories;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        SearchView searchView = view.findViewById(R.id.search);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewSearch);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        allStories = new ArrayList<>();
        filteredStories = new ArrayList<>();

        allStories.add(new Story("One Piece", R.drawable.one_piece));
        allStories.add(new Story("Naruto", R.drawable.one_piece));
        allStories.add(new Story("Dragon Ball", R.drawable.one_piece));
        allStories.add(new Story("Harry Potter", R.drawable.one_piece));
        allStories.add(new Story("Chiếc lá cuối cùng", R.drawable.one_piece));

        filteredStories.addAll(allStories);

        storyAdapter = new StoryAdapter(filteredStories);
        recyclerView.setAdapter(storyAdapter);

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

    private void filterStories(String keyword) {
        filteredStories.clear();
        if (keyword.isEmpty()) {
            filteredStories.addAll(allStories);
        } else {
            String lowerCaseKeyword = keyword.toLowerCase(Locale.getDefault());
            for (Story story : allStories) {
                if (story.getName().toLowerCase(Locale.getDefault()).contains(lowerCaseKeyword)) {
                    filteredStories.add(story);
                }
            }
        }
        storyAdapter.setQuery(keyword);
        storyAdapter.notifyDataSetChanged();
    }
}
