package com.example.comicapp.author.manage.edit;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class EditChapter extends AppCompatActivity {
    private String storyId, chapterKey, chapterTitle;
    private EditText name;
    private Button btnSelectFile, btnEdit;
    private Uri selectedPdfUri = null;
    private SupaUploader supa;

    private TextView chapterName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_chapter);
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
            selectedPdfUri = data.getData();
            if (selectedPdfUri != null) {
                String fileName = getFileName(selectedPdfUri);
                chapterName = findViewById(R.id.textView3);
                chapterName.setText(fileName);
            } else {
                Log.d("FilePicker", "Không nhận được URI của file!");
            }

        }
    }

    private void loadIntent() {
        supa = new SupaUploader();
        Intent intent = getIntent();
        storyId = intent.getStringExtra("storyId");
        chapterKey = intent.getStringExtra("chapterKey");
        chapterTitle = intent.getStringExtra("chapterTitle");
    }

    private void onInit(){
        loadIntent();
        name = findViewById(R.id.name);
        btnSelectFile = findViewById(R.id.btnSelectFile);
        btnEdit = findViewById(R.id.btn_add);
        chapterName = findViewById(R.id.textView3);
        chapterName.setText("Bạn chưa chọn file truyện mới để cập nhật");
        name.setText(chapterTitle);
        btnSelectFile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            startActivityForResult(intent, 1);
        });
        btnEdit.setOnClickListener(v -> {
            uploadFile(name);
        });

    }

    private String getFileName(Uri uri) {
        String fileName = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    fileName = cursor.getString(index);
                }
            }
        } else {
            fileName = uri.getPath();
            int cut = fileName.lastIndexOf('/');
            if (cut != -1) {
                fileName = fileName.substring(cut + 1);
            }
        }
        return fileName;
    }

    private void uploadFile(EditText name) {
        String nameStr = name.getText().toString();
        if(nameStr.isEmpty()){
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }else if(selectedPdfUri == null){
            updateDataNonFile(nameStr);
        }else{
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
                        updateData(nameStr, pdfUrl);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateDataNonFile(String name){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("story").child(storyId).child("chapter").child(chapterKey);
        db.child("name").setValue(name);
        Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, AuthorActivity.class);
        startActivity(intent);
    }

    private void updateData(String name, String pdfUrl){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("story").child(storyId).child("chapter").child(chapterKey);
        db.child("name").setValue(name);
        db.child("url").setValue(pdfUrl);
        Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, AuthorActivity.class);
        startActivity(intent);

    }

}