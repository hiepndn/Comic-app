package com.example.comicapp.author.manage.add;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.comicapp.R;
import com.example.comicapp.author.AuthorActivity;
import com.example.comicapp.storage.SupaUploader;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.*;

import java.util.UUID;

public class AddStory extends AppCompatActivity {
    private Button btnAdd, btnSelectImg, btnSelectFile;
    private EditText name, des;
    private Spinner category;
    private Uri selectedImgUri = null;
    private Uri selectedPdfUri = null;
    private DatabaseReference firebaseDb;
    private SupaUploader supa;
    private String imgUrl, pdfUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_story);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initDatabase();
        onInit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            selectedImgUri = data.getData();

        }else if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            selectedPdfUri = data.getData();
        }
    }

    private void initDatabase() {
        supa = new SupaUploader();
        firebaseDb = FirebaseDatabase.getInstance().getReference("story");
    }

    private void onInit() {
        btnAdd = findViewById(R.id.btn_add);
        btnSelectImg = findViewById(R.id.btnSelectImg);
        btnSelectFile = findViewById(R.id.btnSelectFile);
        name = findViewById(R.id.name);
        des = findViewById(R.id.des);
        category = findViewById(R.id.category);

        btnAdd.setOnClickListener(v -> {
            uploadFile(name, des, category);
        });
        btnSelectImg.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 1);

        });
        btnSelectFile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            startActivityForResult(intent, 2);

        });
    }

    private void uploadFile(EditText name, EditText des, Spinner category) {
        String nameStr = name.getText().toString();
        String desStr = des.getText().toString();
        String categoryStr = category.getSelectedItem().toString();

        if (selectedImgUri == null || selectedPdfUri == null || nameStr.isEmpty() || desStr.isEmpty() || categoryStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }
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

                    String pdfName = UUID.randomUUID() + ".pdf";
                    try {
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
                                saveStoryToFirebase(imgUrl, pdfUrl, nameStr, desStr, categoryStr);
                            }
                        });
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void saveStoryToFirebase(String imgUrl, String pdfUrl, String name, String des, String category) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String userName = sharedPreferences.getString("userId", "");

        String storyId = firebaseDb.push().getKey();

        Map<String, Object> chapter1 = new HashMap<>();
        chapter1.put("inđex", 0);
        chapter1.put("name", "Chapter 1");
        chapter1.put("url", pdfUrl);

        Map<String, Object> chapterMap = new HashMap<>();
        chapterMap.put("chapter1", chapter1);

        Map<String, Object> storyData = new HashMap<>();
        storyData.put("name", name);
        storyData.put("des", des);
        storyData.put("category", category);
        storyData.put("img", imgUrl);
        storyData.put("author", userName);
        storyData.put("chapter", chapterMap);

        firebaseDb.child(storyId).setValue(storyData)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Thêm truyện thành công", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, AuthorActivity.class);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Log.e("FIREBASE_ERROR", e.getMessage());
                });
    }

}