package com.example.comicapp.chapter.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.comicapp.R;
import com.example.comicapp.chapter.adapter.ChaptersAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
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
    private ImageView imageCover;
    private TextView textAuthor;
    private TextView textDescription;
    private TextView textName;
    private ImageView favourite;

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

        // Nút Back
        Button buttonBack = view.findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        recyclerView = view.findViewById(R.id.recyclerViewChapters);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChaptersAdapter(getContext(), storyId, chapterKeys, chapterTitles);
        recyclerView.setAdapter(adapter);

        imageCover = view.findViewById(R.id.imageCover);
        textAuthor = view.findViewById(R.id.textAuthor);
        textDescription = view.findViewById(R.id.textDescriptionLabel);
        textName = view.findViewById(R.id.textName);

        loadStoryInfo();
        loadChaptersFromFirebase();
        updateHistory();
        checkFavourite(view);

        return view;
    }

    private void loadStoryInfo() {
        FirebaseDatabase.getInstance().getReference("story").child(storyId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String author = snapshot.child("author").getValue(String.class);
                        String description = snapshot.child("des").getValue(String.class);
                        String imageUrl = snapshot.child("img").getValue(String.class);
                        String name = snapshot.child("name").getValue(String.class);

                        if (author != null) textAuthor.setText("Tác giả: " + author);
                        if (description != null) textDescription.setText("Cốt truyện: " + description);
                        if (name != null) textName.setText("Tên truyện: " + name);
                        if (imageUrl != null && getContext() != null) {
                            Glide.with(getContext()).load(imageUrl).into(imageCover);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error if needed
                    }
                });
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

    private void updateHistory() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String userKey = sharedPreferences.getString("userKey", null);
        if (userKey != null) {
            FirebaseDatabase.getInstance().getReference("user").child(userKey).child("history").child(storyId).setValue(true);
        }
    }

    private void checkFavourite(View view) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String userKey = sharedPreferences.getString("userKey", null);
        if (userKey == null || storyId == null) {
            Log.e("ChaptersFragment", "userKey hoặc storyId bị null");
            return;
        }

        DatabaseReference favouritesRef = FirebaseDatabase.getInstance()
                .getReference("user").child(userKey).child("favourite");

        favouritesRef.child(storyId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isFavourite = dataSnapshot.exists();
                initFavourite(view, isFavourite);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Lỗi khi kiểm tra danh sách yêu thích: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initFavourite(View view, Boolean isFavourite) {
        favourite = view.findViewById(R.id.favourite);
        favourite.setImageResource(isFavourite ? R.drawable.heart_2 : R.drawable.heart_1);

        favourite.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
            String userKey = sharedPreferences.getString("userKey", null);

            if (userKey == null) {
                Toast.makeText(getContext(), "Vui lòng đăng nhập để sử dụng tính năng này", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isFavourite) {
                FirebaseDatabase.getInstance().getReference("user").child(userKey)
                        .child("favourite").child(storyId).removeValue();
                favourite.setImageResource(R.drawable.heart_1);
                Toast.makeText(getContext(), "Đã xóa khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
            } else {
                FirebaseDatabase.getInstance().getReference("user").child(userKey)
                        .child("favourite").child(storyId).setValue(true);
                favourite.setImageResource(R.drawable.heart_2);
                Toast.makeText(getContext(), "Đã thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
            }

            // Cập nhật lại trạng thái yêu thích sau khi click
            checkFavourite(view);
        });
    }
}
