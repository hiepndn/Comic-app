package com.example.comicapp.author.manage.edit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comicapp.R;
import com.example.comicapp.author.AuthorActivity;
import com.example.comicapp.author.manage.edit.adapter.ChapterAdapter;
import com.example.comicapp.storage.SupaUploader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class EditStory extends AppCompatActivity {
    private String storyId, storyName, storyImg, storyDes, storyCategory;
    private EditText name, des;
    private Spinner category;
    private ImageView imageStory;

    private RecyclerView recyclerView;
    private ChapterAdapter adapter;
    private List<String> chapterKeys = new ArrayList<>();
    private List<String> chapterTitles = new ArrayList<>();
    private Button btnEdit, btnSelectImg;
    private SupaUploader supa;
    private Uri selectedImgUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_story);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        onInit();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            selectedImgUri = data.getData();
            if (selectedImgUri != null) {
                imageStory.setImageURI(selectedImgUri);
            } else {
                Log.d("ImagePicker", "Không nhận được URI của hình ảnh!");
            }
        }
    }

    private void loadIntent() {
        Intent intent = getIntent();
        storyId = intent.getStringExtra("storyId");
        storyName = intent.getStringExtra("storyName");
        storyImg = intent.getStringExtra("storyImg");
        storyDes = intent.getStringExtra("storyDes");
        storyCategory = intent.getStringExtra("storyCategory");
    }

    private void onInit(){
        loadIntent();
        loadChapter();
        name = findViewById(R.id.name);
        des = findViewById(R.id.des);
        category = findViewById(R.id.category);
        imageStory = findViewById(R.id.imageStory);
        recyclerView = findViewById(R.id.recyclerViewChapters);
        btnEdit = findViewById(R.id.btn_add);
        btnSelectImg = findViewById(R.id.btnSelectImg);

        btnEdit.setOnClickListener(v -> {
            uploadFile(name, des, category);
        });
        btnSelectImg.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        });
        name.setText(storyName);
        des.setText(storyDes);
        Picasso.get().load(storyImg).into(imageStory);

        int count = category.getCount();
        int position = -1;
        for (int i = 0; i < count; i++) {
            if (category.getItemAtPosition(i).equals(storyCategory)) {
                position = i;
                break;
            }
        }
        if (position >= 0) {
            category.setSelection(position);
        } else {
            Log.d("Spinner", "Giá trị không tồn tại trong Spinner!");
        }

        adapter= new ChapterAdapter(storyId, chapterKeys, chapterTitles);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadChapter(){
        supa = new SupaUploader();
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
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void uploadFile(EditText name, EditText des, Spinner category) {
        String nameStr = name.getText().toString();
        String desStr = des.getText().toString();
        String categoryStr = category.getSelectedItem().toString();
        if(nameStr.isEmpty() || desStr.isEmpty() || categoryStr.isEmpty()){
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }else if (selectedImgUri == null){
            updateDate(nameStr, desStr, categoryStr, storyId, storyImg);
        }else {
            try {
                String imgName = UUID.randomUUID() + ".jpg";
                supa.uploadFile(selectedImgUri, "image", imgName, new Callback() {
                    @Override public void onFailure(Call call, IOException e) {
                        Log.e("SUPA_IMG", e.getMessage());
                    }
                    @Override public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            Log.e("SUPA_IMG", "Code: " + response.code());
                            Log.e("SUPA_IMG", response.body().string());
                            return;
                        }
                        String imgUrl = supa.getPublicUrl("image", imgName);
                        updateDate(nameStr, desStr, categoryStr, storyId, imgUrl);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateDate(String name, String des, String category, String storyId, String img){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("story").child(storyId);;
        HashMap<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("des", des);
        updates.put("category", category);
        updates.put("img", img);

        db.updateChildren(updates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, AuthorActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                Log.e("Firebase", "Cập nhật lỗi: " + task.getException());
            }
        });
    }
}