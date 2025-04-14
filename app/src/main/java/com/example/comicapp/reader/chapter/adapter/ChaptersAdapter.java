package com.example.comicapp.reader.chapter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comicapp.R;
import com.example.comicapp.reader.chapter.read.ReadChapterFragment;

import java.util.List;

public class ChaptersAdapter extends RecyclerView.Adapter<ChaptersAdapter.ChapterViewHolder> {

    private List<String> chapterKeys;
    private List<String> chapterTitles;
    private Context context;
    private String storyId;

    public ChaptersAdapter(Context context, String storyId, List<String> chapterKeys, List<String> chapterTitles) {
        this.context = context;
        this.storyId = storyId;
        this.chapterKeys = chapterKeys;
        this.chapterTitles = chapterTitles;
    }

    public void setChapters(List<String> keys, List<String> titles) {
        this.chapterKeys = keys;
        this.chapterTitles = titles;
    }

    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chapter, parent, false);
        return new ChapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        holder.chapterTitle.setText(chapterTitles.get(position));
        holder.itemView.setOnClickListener(v -> {
            String chapterKey = chapterKeys.get(position);
            Fragment fragment = ReadChapterFragment.newInstance(storyId, chapterKey);
            ((AppCompatActivity) context).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return chapterTitles.size();
    }

    public static class ChapterViewHolder extends RecyclerView.ViewHolder {
        TextView chapterTitle;

        public ChapterViewHolder(@NonNull View itemView) {
            super(itemView);
            chapterTitle = itemView.findViewById(R.id.textChapterTitle);
        }
    }
}
