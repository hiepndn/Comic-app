package com.example.comicapp.read;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.comicapp.R;
import com.google.firebase.database.*;

public class ReadChapterFragment extends Fragment {

    private static final String ARG_STORY_ID = "story_id";
    private static final String ARG_CHAPTER_KEY = "chapter_key";

    private String storyId;
    private String chapterKey;
    private WebView webView;

    public static ReadChapterFragment newInstance(String storyId, String chapterKey) {
        ReadChapterFragment fragment = new ReadChapterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STORY_ID, storyId);
        args.putString(ARG_CHAPTER_KEY, chapterKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            storyId = getArguments().getString(ARG_STORY_ID);
            chapterKey = getArguments().getString(ARG_CHAPTER_KEY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_read_chapter, container, false);
        webView = view.findViewById(R.id.webViewChapterContent);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        loadChapterFromFirebase();

        return view;
    }

    private void loadChapterFromFirebase() {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("story")
                .child(storyId)
                .child("chapter")
                .child(chapterKey) //
                .child("url");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String url = snapshot.getValue(String.class);
                    Log.d("READ_CHAPTER", "URL chương: " + url);
                    if (url != null && !url.isEmpty()) {
                        if (url.endsWith(".pdf")) {
                            String embeddedUrl = "https://docs.google.com/gview?embedded=true&url=" + url;
                            webView.loadUrl(embeddedUrl);
                        } else {
                            webView.loadUrl(url);
                        }
                    } else {
                        webView.loadData("Không tìm thấy nội dung chương.", "text/html", "UTF-8");
                    }
                } else {
                    webView.loadData("Không có dữ liệu chương.", "text/html", "UTF-8");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("READ_CHAPTER", "Lỗi Firebase: " + error.getMessage());
                webView.loadData("Lỗi khi tải chương.", "text/html", "UTF-8");
            }
        });
    }
}
