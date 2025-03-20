package com.example.comicapp.category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comicapp.MainActivity;
import com.example.comicapp.R;
import com.example.comicapp.category.adapter.StoryAdapter;
import com.example.comicapp.search.SearchFragment;

import java.util.ArrayList;
import java.util.List;

public class CategoriesFragment extends Fragment {

    private RecyclerView recyclerView;
    private StoryAdapter storyAdapter;
    private List<String> storyList;

    public CategoriesFragment() {
        super(R.layout.fragment_categories);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext())); // Dùng LinearLayoutManager để hỗ trợ cuộn
        recyclerView.setNestedScrollingEnabled(true); // Cho phép cuộn mượt hơn

        storyList = new ArrayList<>();
        storyAdapter = new StoryAdapter(storyList);
        recyclerView.setAdapter(storyAdapter);

        // Xử lý sự kiện khi nhấn nút chọn thể loại
        Button btnComic = view.findViewById(R.id.btnComic);
        Button btnNovel = view.findViewById(R.id.btnNovel);
        Button btnStory = view.findViewById(R.id.btnStory);

        btnComic.setOnClickListener(v -> loadStories("comic"));
        btnNovel.setOnClickListener(v -> loadStories("novel"));
        btnStory.setOnClickListener(v -> loadStories("story"));

        // Xử lý tìm kiếm với SearchView
        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                navigateToSearchFragment();
            }
        });

        return view;
    }

    private void loadStories(String category) {
        storyList.clear();

        List<String> baseStories = new ArrayList<>();
        switch (category) {
            case "comic":
                baseStories.add("One Piece");
                baseStories.add("Naruto");
                baseStories.add("Dragon Ball");
                break;
            case "novel":
                baseStories.add("Harry Potter");
                baseStories.add("Sherlock Holmes");
                baseStories.add("The Lord of the Rings");
                break;
            case "story":
                baseStories.add("Chiếc lá cuối cùng");
                baseStories.add("Tắt đèn");
                baseStories.add("Lão Hạc");
                break;
        }


        for (int i = 0; i < 10; i++) {
            storyList.addAll(baseStories);
        }

        storyAdapter.notifyDataSetChanged();
    }

    private void navigateToSearchFragment() {
        SearchView searchView = requireView().findViewById(R.id.searchView);
        searchView.clearFocus(); // Bỏ focus để tránh lỗi giữ bàn phím

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, new SearchFragment()); // Chuyển đến SearchFragment
        transaction.addToBackStack(null); // Cho phép quay lại màn trước
        transaction.commit();

        ((MainActivity) requireActivity()).receiveDataFromFragment(R.id.nav_search);
    }
}
