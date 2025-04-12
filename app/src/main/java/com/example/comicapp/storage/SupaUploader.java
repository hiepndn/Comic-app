package com.example.comicapp.storage;
import android.net.Uri;

import com.example.comicapp.MyApp;

import okhttp3.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
public class SupaUploader {
    private static final String SUPA_URL = "https://qttpsgmmrotwbvcrgute.supabase.co";
    private static final String SUPA_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InF0dHBzZ21tcm90d2J2Y3JndXRlIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDQzOTExODYsImV4cCI6MjA1OTk2NzE4Nn0.uukpcQ5lpe79rXOByPwqtxvSh6kN60b2eRvmyERPd8E";
    private static final String BUCKET  = "comicapp";

    private final OkHttpClient client = new OkHttpClient();

    /**
     * Upload 1 file lên Supabase Storage.
     * @param uri       Uri của file (ảnh hoặc PDF)
     * @param folder    Thư mục con trong bucket (ví dụ "covers" hoặc "chapters")
     * @param filename  Tên file muốn lưu (vd "1234.pdf" hoặc "abcd.jpg")
     * @param callback  Callback chạy trên background thread
     */
    public void uploadFile(Uri uri,
                           String folder,
                           String filename,
                           Callback callback) throws Exception {

        InputStream is = MyApp.context.getContentResolver().openInputStream(uri);
        byte[] data = readAllBytes(is);

        // Xác định media type
        String ext = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        String mime = ext.equals("pdf") ? "application/pdf" : "image/jpeg";

        // Tạo body multipart/form-data
        RequestBody requestBody = RequestBody.create(data, MediaType.get(mime));

        // Build request
        String path = folder + "/" + filename;
        HttpUrl url = HttpUrl.parse(SUPA_URL + "/storage/v1/object/" + BUCKET + "/" + path)
                .newBuilder()
                .addQueryParameter("cacheControl", "3600")
                .addQueryParameter("upsert", "false")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + SUPA_KEY)
                .addHeader("apikey", SUPA_KEY)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(callback);
    }

    /** Helper: đọc hết InputStream */
    private byte[] readAllBytes(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        int n;
        while ((n = is.read(buf)) > 0) {
            baos.write(buf, 0, n);
        }
        return baos.toByteArray();
    }

    /** Lấy public URL (nếu bucket public) */
    public String getPublicUrl(String folder, String filename) {
        return SUPA_URL
                + "/storage/v1/object/public/"
                + BUCKET + "/"
                + folder + "/"
                + filename;
    }
}
