package com.example.comicapp.page;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.comicapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class pageComic extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_page_comic);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        WebView webView = findViewById(R.id.webView1);
        webView.getSettings().setJavaScriptEnabled(true);
        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("page");
        Log.d("tag","ch vào");
        firebaseDatabase.child("url").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        String fileUrl = task.getResult().getValue(String.class);
                        Log.d("Firebase", "URL của file là: " + fileUrl);
                        webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + "https://cloud.appwrite.io/v1/storage/buckets/67dc2c7f000811ea979b/files/67dd0f710019f151cfa9/view?project=67dc2a3c0010a8f3b2d0&mode=admin");
                        // Sử dụng URL, ví dụ: Hiển thị trong WebView hoặc tải tệp
//                        viewFileFromUrl(fileUrl);
                    } else {
                        Log.e("Firebase", "Không tìm thấy URL hoặc lỗi xảy ra!", task.getException());
                    }
                });
        getFileUrlFromFirebase();


    }

    public void getFileUrlFromFirebase() {
        String fileUrl1;
        WebView webView = findViewById(R.id.webView1);
        webView.getSettings().setJavaScriptEnabled(true);
        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("page");
        firebaseDatabase.child("url").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        String fileUrl = task.getResult().getValue(String.class);
                        Log.d("Firebase", "URL của file là: " + fileUrl);
                        webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" +fileUrl);
                        // Sử dụng URL, ví dụ: Hiển thị trong WebView hoặc tải tệp
//                        viewFileFromUrl(fileUrl);
                    } else {
                        Log.e("Firebase", "Không tìm thấy URL hoặc lỗi xảy ra!", task.getException());
                    }
                });



    }

}