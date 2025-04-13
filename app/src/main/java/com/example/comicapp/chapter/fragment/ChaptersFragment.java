package com.example.comicapp.chapter.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.comicapp.chapter.adapter.ChaptersAdapter;
import com.example.comicapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class ChaptersFragment extends Fragment {

    private static final String ARG_STORY_ID = "story_id";
    private String storyId;
    private RecyclerView recyclerView;
    private ChaptersAdapter adapter;
    private List<String> chapterKeys = new ArrayList<>();
    private List<String> chapterTitles = new ArrayList<>();

    public static ChaptersFragment newInstance(String storyId) {
        ChaptersFragment fragment = new ChaptersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STORY_ID, storyId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            storyId = getArguments().getString(ARG_STORY_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chapters, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewChapters);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChaptersAdapter(getContext(), storyId, chapterKeys, chapterTitles);
        recyclerView.setAdapter(adapter);
        updateHistory();
        loadChaptersFromFirebase();
        return view;
    }

    private void loadChaptersFromFirebase() {
        FirebaseDatabase.getInstance().getReference("story").child(storyId).child("chapter")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        chapterKeys.clear();
                        chapterTitles.clear();
                        for (DataSnapshot chapterSnap : snapshot.getChildren()) {
                            String key = chapterSnap.getKey();
                            String name = chapterSnap.child("name").getValue(String.class);
                            if (key != null && name != null) {
                                chapterKeys.add(key);
                                chapterTitles.add(name);
                            }
                        }
                        adapter.setChapters(chapterKeys, chapterTitles);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error if needed
                    }
                });
    }

    private void updateHistory(){
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String userKey = sharedPreferences.getString("userKey", null);
        if(userKey == null){
            return;
        }
        else{
            FirebaseDatabase.getInstance().getReference("user").child(userKey).child("history").child(storyId).setValue(true);
        }
    }
}
