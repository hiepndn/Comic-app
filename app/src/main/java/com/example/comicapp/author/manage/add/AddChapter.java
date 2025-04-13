package com.example.comicapp.author.manage.add;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.comicapp.R;
import com.example.comicapp.author.AuthorActivity;
import com.example.comicapp.storage.SupaUploader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AddChapter extends AppCompatActivity {
    private String  storyId;
    private Long currentIndex;
    private Button btnAdd, btnSelectFile;
    private EditText name;
    private Uri selectedPdfUri = null;
    private SupaUploader supa;
    private DatabaseReference firebaseDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_chapter);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        loadIntent();
        initDatabase();
        onInit();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            selectedPdfUri = data.getData();
        }
    }

    private void loadIntent(){
        Intent intent = getIntent();
        storyId = intent.getStringExtra("storyId");
        currentIndex = intent.getLongExtra("currentIndex",0);
    }

    private void initDatabase() {
        supa = new SupaUploader();
        firebaseDb = FirebaseDatabase.getInstance().getReference("story").child(storyId).child("chapter");
    }

    private void onInit(){
        name = findViewById(R.id.name);
        btnAdd = findViewById(R.id.btn_add);
        btnSelectFile = findViewById(R.id.btnSelectFile);
        btnSelectFile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            startActivityForResult(intent, 1);

        });
        btnAdd.setOnClickListener(v -> {
            uploadFile(name);
        });
    }

    private void uploadFile(EditText name) {
        String nameStr = name.getText().toString();
        if (selectedPdfUri == null || nameStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            String pdfName = UUID.randomUUID() + ".pdf";
            supa.uploadFile(selectedPdfUri, "pdf", pdfName, new Callback() {
                @Override public void onFailure(Call call, IOException e) {
                    Log.e("SUPA_PDF", e.getMessage());
                }
                @Override public void onResponse(Call call, Response resp2) {
                    if (!resp2.isSuccessful()) {
                        Log.e("SUPA_PDF", "Code: " + resp2.code());
                        return;
                    }
                    String pdfUrl = supa.getPublicUrl("pdf", pdfName);
                    saveChapterToFirebase(pdfUrl, nameStr);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveChapterToFirebase(String pdfUrl, String name){
        String chapterKey = "chapter"+(currentIndex+2);
        Map<String, Object> chapterData = new HashMap<>();
        chapterData.put("name", name);
        chapterData.put("url", pdfUrl);
        chapterData.put("index", currentIndex + 1);
        firebaseDb.child(chapterKey).setValue(chapterData)
                .addOnSuccessListener(unused -> {
                    Log.d("CHAPTER_ADDED", "Chapter mới đã được thêm thành công với key: " + chapterKey);
                    Toast.makeText(this, "Thêm chap thành công", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, AuthorActivity.class);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Log.e("FIREBASE_ERROR", "Không thể thêm chapter: " + e.getMessage());
                });
    }
}