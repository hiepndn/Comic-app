package com.example.comicapp.category.adapter;

<<<<<<< HEAD
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
=======
import android.content.Intent;
>>>>>>> devhiep
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

import com.example.comicapp.R;
<<<<<<< HEAD
import com.example.comicapp.Story;
import com.example.comicapp.category.fragmnet.ChaptersFragment;
=======
import com.example.comicapp.page.pageComic;
>>>>>>> devhiep

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

        // Highlight phần trùng khớp từ khóa
        if (!currentQuery.isEmpty()) {
            String lowerName = name.toLowerCase(Locale.getDefault());
            String lowerQuery = currentQuery.toLowerCase(Locale.getDefault());

            int start = lowerName.indexOf(lowerQuery);
            if (start >= 0) {
                int end = start + currentQuery.length();
                SpannableString spannable = new SpannableString(name);
                spannable.setSpan(
                        new ForegroundColorSpan(Color.RED),
                        start,
                        end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                );
                holder.textView.setText(spannable);
            } else {
                holder.textView.setText(name);
            }
        } else {
            holder.textView.setText(name);
        }

        holder.imageView.setImageResource(story.getImageResId());

        holder.itemView.setOnClickListener(v -> {
            FragmentManager fragmentManager = ((FragmentActivity) v.getContext()).getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, new ChaptersFragment(story.getName()));
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
<<<<<<< HEAD
            imageView = itemView.findViewById(R.id.storyImage);
=======
            textView.setOnClickListener(v -> {
                Intent switchPage = new Intent(itemView.getContext(), pageComic.class);
                itemView.getContext().startActivity(switchPage);
            });
>>>>>>> devhiep
        }
    }
}
