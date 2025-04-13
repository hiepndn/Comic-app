package com.example.comicapp.author.manage.edit.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comicapp.R;
import com.example.comicapp.author.manage.edit.EditChapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ChapterAdapter extends  RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder>{
    private String storyId;
    private List<String> chapterKeys;
    private List<String> chapterTitles;
    public ChapterAdapter(String storyId, List<String> chapterKeys, List<String> chapterTitles){
        this.storyId = storyId;
        this.chapterKeys = chapterKeys;
        this.chapterTitles = chapterTitles;
    }

    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manage_chapter, parent, false);
        return new ChapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        holder.name.setText(chapterTitles.get(position));
        holder.delete.setOnClickListener(v -> {
            if(chapterKeys.size() > position){
                DatabaseReference db = FirebaseDatabase.getInstance().getReference("story").child(storyId).child("chapter");
                String chapterKey = chapterKeys.get(position);
                db.child(chapterKey).removeValue();
                chapterKeys.remove(position);
                chapterTitles.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, chapterKeys.size() - position);
            }
        });
        holder.edit.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EditChapter.class);
            intent.putExtra("storyId", storyId);
            intent.putExtra("chapterKey", chapterKeys.get(position));
            intent.putExtra("chapterTitle", chapterTitles.get(position));
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return chapterKeys.size();
    }

    public class ChapterViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        Button delete, edit;
        public ChapterViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.menu_name);
            delete = itemView.findViewById(R.id.btn_remove);
            edit = itemView.findViewById(R.id.btn_edit);
        }
    }
}
