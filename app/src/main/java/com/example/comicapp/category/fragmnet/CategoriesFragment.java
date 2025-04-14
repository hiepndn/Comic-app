package com.example.comicapp.category.fragmnet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CategoriesFragment extends Fragment {

    private RecyclerView recyclerView;
    private StoryAdapter storyAdapter;
    private List<Story> storyList;

    public CategoriesFragment() {
        super(R.layout.fragment_categories);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);


        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setNestedScrollingEnabled(true);

        storyList = new ArrayList<>();
        storyAdapter = new StoryAdapter(storyList);
        recyclerView.setAdapter(storyAdapter);


        Button btnComic = view.findViewById(R.id.btnComic);
        Button btnNovel = view.findViewById(R.id.btnNovel);
        Button btnStory = view.findViewById(R.id.btnStory);

        btnComic.setOnClickListener(v -> loadStories("comic"));
        btnNovel.setOnClickListener(v -> loadStories("novel"));
        btnStory.setOnClickListener(v -> loadStories("story"));


        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                navigateToSearchFragment();
            }
        });


        loadAllStories();

        return view;
    }


    private void loadStories(String category) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("story");


        storyList.clear();
        storyAdapter.notifyDataSetChanged();


        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot storySnap : snapshot.getChildren()) {
                    Story story = storySnap.getValue(Story.class);
                    if (story != null) {
                        story.setId(storySnap.getKey());


                        String storyCategory = story.getCategory().toLowerCase().trim();
                        if ((category.equals("comic") && storyCategory.equals("truyện tranh")) ||
                                (category.equals("novel") && storyCategory.equals("tiểu thuyết")) ||
                                (category.equals("story") && storyCategory.equals("truyện chữ"))) {
                            storyList.add(story);
                        }
                    }
                }
                storyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi tải dữ liệu!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadAllStories() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("story");


        storyList.clear();
        storyAdapter.notifyDataSetChanged();


        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot storySnap : snapshot.getChildren()) {
                    Story story = storySnap.getValue(Story.class);
                    if (story != null) {
                        story.setId(storySnap.getKey());
                        storyList.add(story);
                    }
                }
                storyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi tải dữ liệu!", Toast.LENGTH_SHORT).show();
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
