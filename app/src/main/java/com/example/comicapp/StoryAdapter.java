package com.example.comicapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {

    private List<String> storyList;

    public StoryAdapter(List<String> storyList) {
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
        holder.textView.setText(storyList.get(position));
    }

    @Override
    public int getItemCount() {
        return storyList.size();
    }

    // Cập nhật danh sách dữ liệu
    public void updateStories(List<String> newStories) {
        storyList.clear();
        storyList.addAll(newStories);
        notifyDataSetChanged();
    }

    public static class StoryViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textStoryTitle);
        }
    }
}
