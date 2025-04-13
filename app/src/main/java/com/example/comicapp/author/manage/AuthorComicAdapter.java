package com.example.comicapp.author.manage;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comicapp.R;
import com.example.comicapp.Story;
import com.example.comicapp.author.manage.add.AddChapter;
import com.example.comicapp.author.manage.edit.EditStory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AuthorComicAdapter extends  RecyclerView.Adapter<AuthorComicAdapter.AuthorComicViewHolder>{
    private List<Story> stories = new ArrayList<>();
    private List<String> chapterKeys = new ArrayList<>();

    public AuthorComicAdapter(List<Story> stories) {
        this.stories = stories;
    }

    @NonNull
    @Override
    public AuthorComicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manage, parent, false);
        return new AuthorComicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AuthorComicViewHolder holder, int position) {
        Story story = stories.get(position);
        loadIndexChapter(story.getId());
        holder.name.setText(story.getName());
        Picasso.get().load(story.getImg()).into(holder.image);
        holder.edit.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EditStory.class);
            intent.putExtra("storyId", story.getId());
            intent.putExtra("storyName", story.getName());
            intent.putExtra("storyImg", story.getImg());
            intent.putExtra("storyDes", story.getDes());
            intent.putExtra("storyCategory", story.getCategory());
            v.getContext().startActivity(intent);
        });
        holder.delete.setOnClickListener(v -> {
            if(stories.size() > position){
                DatabaseReference db = FirebaseDatabase.getInstance().getReference("story");
                String storyId = story.getId();
                db.child(storyId).removeValue();
                stories.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, stories.size() - position);
            }
        });
        holder.add.setOnClickListener(v -> {
            FirebaseDatabase.getInstance()
                    .getReference("story")
                    .child(story.getId())
                    .child("chapter")
                    .orderByChild("index")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            DataSnapshot lastChapterSnapshot = null;
                            for (DataSnapshot chapterSnap : snapshot.getChildren()){
                                lastChapterSnapshot = chapterSnap;
                            }
                            if (lastChapterSnapshot != null) {
                                String lastChapterKey = lastChapterSnapshot.getKey();
                                Long lastIndex = lastChapterSnapshot.child("index").getValue(Long.class);
                                Log.d("AddChapter", "lastChapterKey: " + lastChapterKey + ", lastIndex: " + lastIndex);
                                Intent intent = new Intent(v.getContext(), AddChapter.class);
                                intent.putExtra("storyId", story.getId());
                                intent.putExtra("chapterId", lastChapterKey);
                                if(lastIndex != null) {
                                    intent.putExtra("currentIndex", lastIndex);
                                }
                                v.getContext().startActivity(intent);
                            } else {
                                Log.e("AddChapter", "Không tìm thấy chapter nào cho truyện này!");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("Firebase", "Lỗi khi lấy chapter: " + error.getMessage());
                        }
                    });
        });
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    private void loadIndexChapter(String storyId){
        FirebaseDatabase.getInstance().getReference("story").child(storyId).child("chapter")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        chapterKeys.clear();
                        for (DataSnapshot chapterSnap : snapshot.getChildren()) {
                            String key = chapterSnap.getKey();
                            String name = chapterSnap.child("name").getValue(String.class);
                            if (key != null && name != null) {
                                chapterKeys.add(key);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }
    public class AuthorComicViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView image;
        Button delete, edit, add;
        public AuthorComicViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.image);
            delete = itemView.findViewById(R.id.btn_remove);
            edit = itemView.findViewById(R.id.btn_edit);
            add = itemView.findViewById(R.id.btn_addChapter);
        }
    }
}
