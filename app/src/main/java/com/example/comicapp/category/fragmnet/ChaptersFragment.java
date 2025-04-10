package com.example.comicapp.category.fragmnet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comicapp.chapter.ChaptersAdapter;
import com.example.comicapp.R;

import java.util.ArrayList;
import java.util.List;

public class ChaptersFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChaptersAdapter chaptersAdapter;
    private List<String> chaptersList;
    private String storyTitle;

    public ChaptersFragment(String storyTitle) {
        this.storyTitle = storyTitle; // Nhận tên truyện để hiển thị danh sách chương
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chapters, container, false);

        TextView titleView = view.findViewById(R.id.textStoryTitle);
        titleView.setText(storyTitle); // Hiển thị tên truyện ở trên cùng

        recyclerView = view.findViewById(R.id.recyclerViewChapters);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        chaptersList = new ArrayList<>();
        loadChapters(); // Tải danh sách chương cho truyện

        chaptersAdapter = new ChaptersAdapter(chaptersList);
        recyclerView.setAdapter(chaptersAdapter);

        return view;
    }

    private void loadChapters() {
        // Tạo danh sách chương giả lập dựa trên tên truyện
        for (int i = 1; i <= 10; i++) {
            chaptersList.add("Chapter " + i + " of " + storyTitle);
        }
    }
}
