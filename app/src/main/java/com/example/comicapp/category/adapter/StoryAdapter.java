package com.example.comicapp.category.adapter;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.comicapp.R;
import com.example.comicapp.page.pageComic;
import com.example.comicapp.Story;
import com.example.comicapp.chapter.fragment.ChaptersFragment;

import java.util.List;
import java.util.Locale;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {

    private List<Story> storyList;
    private String currentQuery = "";

    public StoryAdapter(List<Story> storyList) {
        this.storyList = storyList;
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_story, parent, false);
        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        Story story = storyList.get(position);
        String name = story.getName();

        // Highlight từ khóa tìm kiếm
        if (!currentQuery.isEmpty()) {
            String lowerName = name.toLowerCase(Locale.getDefault());
            String lowerQuery = currentQuery.toLowerCase(Locale.getDefault());
            int start = lowerName.indexOf(lowerQuery);
            if (start >= 0) {
                int end = start + currentQuery.length();
                SpannableString spannable = new SpannableString(name);
                spannable.setSpan(
                        new ForegroundColorSpan(Color.RED),
                        start, end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                );
                holder.textView.setText(spannable);
            } else {
                holder.textView.setText(name);
            }
        } else {
            holder.textView.setText(name);
        }

        // Load ảnh truyện bằng Glide
        Glide.with(holder.imageView.getContext())
                .load(story.getImg())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.imageView);

        // Sự kiện click: mở ChaptersFragment
        holder.itemView.setOnClickListener(v -> {
            FragmentManager fragmentManager = ((FragmentActivity) v.getContext()).getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, ChaptersFragment.newInstance(story.getId())); // ✅ Sử dụng newInstance
            transaction.addToBackStack(null);
            transaction.commit();
        });
    }

    @Override
    public int getItemCount() {
        return storyList.size();
    }

    public void updateStories(List<Story> newStories) {
        storyList.clear();
        storyList.addAll(newStories);
        notifyDataSetChanged();
    }

    public void setQuery(String query) {
        this.currentQuery = query != null ? query.toLowerCase() : "";
    }

    public static class StoryViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textStoryTitle);
            imageView = itemView.findViewById(R.id.storyImage);
        }
    }
}
