package com.example.comicapp.reader.chapter.read;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.comicapp.R;
import com.google.firebase.database.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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

        // Cấu hình WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        webView.setWebViewClient(new WebViewClient());

        // Tải chương từ Firebase
        loadChapterFromFirebase();

        // Xử lý nút chuyển chương
        Button btnNextChapter = view.findViewById(R.id.btnNextChapter);
        btnNextChapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNextChapter();
            }
        });
        Button btnPrev = view.findViewById(R.id.btnPreviousChapter);
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPreviousChapter();
            }
        });
        Button btnBack = view.findViewById(R.id.btnBackToChapterList);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }

    private void loadPreviousChapter() {
        DatabaseReference chapterListRef = FirebaseDatabase.getInstance()
                .getReference("story")
                .child(storyId)
                .child("chapter");

        chapterListRef.orderByChild("index").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String prevKey = null;

                for (DataSnapshot child : snapshot.getChildren()) {
                    String key = child.getKey();
                    if (key.equals(chapterKey)) {
                        break;
                    }
                    prevKey = key;
                }

                if (prevKey != null) {
                    chapterKey = prevKey;
                    loadChapterFromFirebase();
                } else {
                    webView.loadData("Đây là chương đầu tiên.", "text/html", "UTF-8");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("READ_CHAPTER", "Lỗi khi tải danh sách chương: " + error.getMessage());
                webView.loadData("Lỗi khi chuyển chương.", "text/html", "UTF-8");
            }
        });
    }


    private void loadNextChapter() {
        DatabaseReference chapterListRef = FirebaseDatabase.getInstance()
                .getReference("story")
                .child(storyId)
                .child("chapter");

        chapterListRef.orderByChild("index").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String nextKey = null;
                boolean foundCurrent = false;

                for (DataSnapshot child : snapshot.getChildren()) {
                    if (foundCurrent) {
                        nextKey = child.getKey();
                        break;
                    }
                    if (child.getKey().equals(chapterKey)) {
                        foundCurrent = true;
                    }
                }

                if (nextKey != null) {
                    chapterKey = nextKey;
                    loadChapterFromFirebase();
                } else {
                    webView.loadData("Đây là chương cuối cùng.", "text/html", "UTF-8");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("READ_CHAPTER", "Lỗi khi tải danh sách chương: " + error.getMessage());
                webView.loadData("Lỗi khi chuyển chương.", "text/html", "UTF-8");
            }
        });
    }

    private void loadChapterFromFirebase() {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("story")
                .child(storyId)
                .child("chapter")
                .child(chapterKey)
                .child("url");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String url = snapshot.getValue(String.class);
                    Log.d("READ_CHAPTER", "URL chương: " + url);
                    if (url != null && !url.isEmpty()) {
                        try {
                            String googleDocsUrl = "https://docs.google.com/gview?embedded=true&url=" + URLEncoder.encode(url, "UTF-8");
                            webView.loadUrl(googleDocsUrl);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            webView.loadData("Lỗi khi xử lý URL chương.", "text/html", "UTF-8");
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
