package com.example.comicapp.reader.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comicapp.reader.MainActivity;
import com.example.comicapp.R;
import com.example.comicapp.model.Story;
import com.example.comicapp.reader.category.adapter.StoryAdapter;
import com.example.comicapp.reader.search.fragment.SearchFragment;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView, recyclerViewHistory;
    private StoryAdapter storyAdapter, historyAdapter;
    private final List<Story> storyList = new ArrayList<>();
    private final List<Story> historyList = new ArrayList<>();
    private TextView textViewHistory;
    private ImageView imageView2;
    private boolean isLoggedIn = false;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initViews(view);

        setupSearchView(view);

        setupStoryList();
        loadAllStories();

        String userKey = getUserKey();

        if (userKey != null) {
            isLoggedIn = true;
            setupHistoryList(view, userKey);
        } else {
            isLoggedIn = false;
            showNoHistoryMessage(); // Sẽ xử lý điều kiện trong hàm này
        }

        return view;
    }


    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewHome);
        textViewHistory = view.findViewById(R.id.textViewHistory);
        imageView2 = view.findViewById(R.id.imageView2);
        recyclerViewHistory = view.findViewById(R.id.historyList);
    }

    private String getUserKey() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        return sharedPreferences.getString("userKey", null);
    }

    private void setupSearchView(View view) {
        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) navigateToSearchFragment();
        });
    }

    private void setupStoryList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        storyAdapter = new StoryAdapter(storyList);
        recyclerView.setAdapter(storyAdapter);
    }

    private void setupHistoryList(View view, String userKey) {
        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        fetchUserHistory(userKey);
    }


    private void loadAllStories() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("story");
        storyList.clear();

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

    private void fetchUserHistory(String userKey) {
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference("user").child(userKey).child("history");

        historyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> storyIds = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    storyIds.add(ds.getKey());
                }

                if (storyIds.isEmpty()) {
                    showNoHistoryMessage();
                } else {
                    loadStoriesFromIds(storyIds);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("History", "Lỗi lấy lịch sử: " + error.getMessage());
            }
        });
    }

    private void loadStoriesFromIds(List<String> storyIds) {
        DatabaseReference storyRef = FirebaseDatabase.getInstance().getReference("story");
        historyList.clear();

        final int[] loadedCount = {0};
        for (String id : storyIds) {
            storyRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Story story = snapshot.getValue(Story.class);
                    if (story != null) {
                        story.setId(id);
                        historyList.add(story);
                    }
                    checkIfLoadingFinished(storyIds.size());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("History", "Lỗi khi load truyện " + id + ": " + error.getMessage());
                    checkIfLoadingFinished(storyIds.size());
                }

                private void checkIfLoadingFinished(int total) {
                    loadedCount[0]++;
                    if (loadedCount[0] == total) {
                        updateHistoryUI();
                    }
                }
            });
        }
    }


    private void showNoHistoryMessage() {
        if (!isLoggedIn) {
            textViewHistory.setText("Bạn cần đăng nhập để sử dụng tính năng này");
        } else {
            textViewHistory.setText("Bạn chưa đọc truyện nào");
        }

        textViewHistory.setVisibility(View.VISIBLE);
        imageView2.setVisibility(View.VISIBLE);
        recyclerViewHistory.setVisibility(View.GONE);
    }


    private void updateHistoryUI() {
        historyAdapter = new StoryAdapter(historyList);
        recyclerViewHistory.setAdapter(historyAdapter);
        historyAdapter.notifyDataSetChanged();

        if (historyList.isEmpty()) {
            showNoHistoryMessage();
        } else {
            textViewHistory.setVisibility(View.GONE);
            imageView2.setVisibility(View.GONE);
            recyclerViewHistory.setVisibility(View.VISIBLE);
        }
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

