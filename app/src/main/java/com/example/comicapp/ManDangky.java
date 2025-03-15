package com.example.comicapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ManDangky extends AppCompatActivity {
    private EditText edtEmail;
    private EditText edtPassword;
    private EditText edtUsername;
    private Button btnDangKy, btnTroVeDangNhap;
    private CheckBox chkAuthor;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_man_dangky);

        // Ánh xạ các thành phần
        edtEmail = findViewById(R.id.email);
        edtPassword = findViewById(R.id.password);
        edtUsername = findViewById(R.id.username);
        btnDangKy = findViewById(R.id.dky_button);
        btnTroVeDangNhap = findViewById(R.id.dnhap);
        chkAuthor = findViewById(R.id.chkAuthor);

        // Khởi tạo Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Xử lý đăng ký
        btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                String username = edtUsername.getText().toString().trim();
                boolean isAuthor = chkAuthor != null && chkAuthor.isChecked(); // Đảm bảo giá trị chính xác của checkbox

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(ManDangky.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(ManDangky.this, "Mật khẩu phải có ít nhất 6 ký tự!", Toast.LENGTH_SHORT).show();
                    return;
                }


                // Register the user
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    saveUserToDatabase(user.getUid(), email, username, isAuthor);
                                }
                            } else {
                                Toast.makeText(ManDangky.this, "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });


        btnTroVeDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ManDangky.this, ManDangnhap.class));
                finish();
            }
        });
    }

    private void saveUserToDatabase(String userId, String email, String username, boolean isAuthor) {
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("username", username);
        user.put("role", isAuthor ? "author" : "user"); // Gán vai trò tác giả hoặc người dùng bình thường

        db.collection("users").document(userId).set(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ManDangky.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

                    // Chuyển màn hình dựa trên vai trò
                    Intent intent;
                    if (isAuthor) {
                        intent = new Intent(ManDangky.this, AuthorActivity.class);
                    } else {
                        intent = new Intent(ManDangky.this, AccountFragment.class);
                    }

                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(ManDangky.this, "Lỗi khi lưu dữ liệu!", Toast.LENGTH_SHORT).show());
    }
}
