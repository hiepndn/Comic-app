package com.example.comicapp.authen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.comicapp.author.AuthorActivity;
import com.example.comicapp.MainActivity;
import com.example.comicapp.R;
import com.example.comicapp.account.AccountFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ManDangnhap extends AppCompatActivity {
    private EditText emailedit, passwordedit;
    private Button dnhap_button, dky_button;
    private ImageView back;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_man_dangnhap);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Bind UI components
        emailedit = findViewById(R.id.email);
        passwordedit = findViewById(R.id.password);
        dnhap_button = findViewById(R.id.dnhap_button);
        dky_button = findViewById(R.id.dky_button);
        back = findViewById(R.id.back);

        // Set button listeners
        dnhap_button.setOnClickListener(v -> login());
        dky_button.setOnClickListener(v -> register());
        back.setOnClickListener(v -> back());
    }

    private void back() {
        Intent intent = new Intent(ManDangnhap.this, MainActivity.class);
        startActivity(intent);
    }

    private void register() {
        Intent intent = new Intent(ManDangnhap.this, ManDangky.class);
        startActivity(intent);
    }

    private void login() {
        String email = emailedit.getText().toString().trim();
        String password = passwordedit.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Log.d("Login", "Đăng nhập thành công");
                            saveUserData(user.getEmail());
                            saveLoginState(true);
                            checkUserRole(user.getUid());
                        } else {
                            Log.e("Login", "Không thể lấy thông tin người dùng!");
                        }
                    } else {
                        Log.e("Login", "Đăng nhập thất bại", task.getException());
                        Toast.makeText(this, "Đăng nhập thất bại, vui lòng kiểm tra lại!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Lưu trạng thái đăng nhập
    private void saveLoginState(boolean isLoggedIn) {
        getSharedPreferences("UserPrefs", MODE_PRIVATE)
                .edit()
                .putBoolean("isLoggedIn", isLoggedIn)
                .apply();
    }

    private void saveUserData(String username) {
        if (username == null || username.isEmpty()) {
            Log.e("saveUserData", "Tên người dùng không hợp lệ!");
            return;
        }

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("username", username);
        editor.apply();
    }

    private void checkUserRole(String userId) {
        DocumentReference docRef = db.collection("users").document(userId);
        docRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("role")) {
                        String role = documentSnapshot.getString("role");
                        Log.d("CheckUserRole", "Vai trò của người dùng: " + role);

                        if ("author".equals(role)) {
                            Toast.makeText(this, "Đăng nhập thành công (Tác giả)!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ManDangnhap.this, AuthorActivity.class));
                        } else {
                            Toast.makeText(this, "Đăng nhập thành công (Người dùng)!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ManDangnhap.this, AccountFragment.class));
                        }
                        finish();
                    } else {
                        Log.e("CheckUserRole", "Tài khoản chưa có vai trò!");
                        Toast.makeText(this, "Không tìm thấy vai trò, vui lòng liên hệ Admin!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("CheckUserRole", "Lỗi khi truy xuất dữ liệu người dùng", e);
                    Toast.makeText(this, "Lỗi khi lấy dữ liệu, kiểm tra kết nối mạng!", Toast.LENGTH_SHORT).show();
                });
    }
}
