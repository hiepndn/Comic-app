package com.example.comicapp.chapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.comicapp.chapter.ChaptersAdapter;
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
    private List<String> chapters = new ArrayList<>();

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
        adapter = new ChaptersAdapter(chapters);
        recyclerView.setAdapter(adapter);

        loadChaptersFromFirebase();
        return view;
    }

    private void loadChaptersFromFirebase() {
        FirebaseDatabase.getInstance().getReference("chapters").child(storyId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        chapters.clear();
                        for (DataSnapshot chapterSnap : snapshot.getChildren()) {
                            String chapterName = chapterSnap.getValue(String.class);
                            if (chapterName != null) {
                                chapters.add(chapterName);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Xử lý nếu cần
                    }
                });
    }
}
