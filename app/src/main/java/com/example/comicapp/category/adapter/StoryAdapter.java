package com.example.comicapp.category.adapter;

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
import com.example.comicapp.Story;
import com.example.comicapp.category.fragmnet.ChaptersFragment;

import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {

    private List<Story> storyList; // Update list to use Story objects

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
        holder.textView.setText(story.getName());
        holder.imageView.setImageResource(story.getImageResId()); // Set the image resource
        holder.itemView.setOnClickListener(v -> {
            FragmentManager fragmentManager = ((FragmentActivity) v.getContext()).getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, new ChaptersFragment(story.getName()));
            transaction.addToBackStack(null); // Cho phép quay lại màn hình trước
            transaction.commit();
        });
    }

    @Override
    public int getItemCount() {
        return storyList.size();
    }

    // Update stories
    public void updateStories(List<Story> newStories) {
        storyList.clear();
        storyList.addAll(newStories);
        notifyDataSetChanged();
    }

    public static class StoryViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView; // Add ImageView

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textStoryTitle);
            imageView = itemView.findViewById(R.id.storyImage); // Link ImageView
        }
    }


}
