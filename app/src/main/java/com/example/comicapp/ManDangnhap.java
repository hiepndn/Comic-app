package com.example.comicapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ManDangnhap extends AppCompatActivity {
    private EditText emailedit, passwordedit;
    private Button dnhap_button, dky_button;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_man_dangnhap);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        emailedit = findViewById(R.id.email);
        passwordedit = findViewById(R.id.password);
        dnhap_button = findViewById(R.id.dnhap_button);
        dky_button = findViewById(R.id.dky_button);

        dnhap_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        dky_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    private void register() {
        Intent i = new Intent(ManDangnhap.this, ManDangky.class);
        startActivity(i);
    }

    private void login() {
        String email = emailedit.getText().toString();
        String password = passwordedit.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng nhập pass", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        checkUserRole(user.getUid());
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Đăng nhập không thành công!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkUserRole(String userId) {
        DocumentReference docRef = db.collection("users").document(userId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String role = documentSnapshot.getString("role");

                if ("author".equals(role)) {
                    // Nếu là tác giả, chuyển đến màn hình dành cho tác giả
                    Toast.makeText(this, "Đăng nhập thành công (Tác giả)!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ManDangnhap.this, AuthorActivity.class));
                } else {
                    // Nếu là user bình thường, vào HomeFragment
                    Toast.makeText(this, "Đăng nhập thành công (Người dùng)!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ManDangnhap.this, HomeFragment.class));
                }
                finish();
            } else {
                Toast.makeText(this, "Không tìm thấy dữ liệu người dùng!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Lỗi khi lấy dữ liệu!", Toast.LENGTH_SHORT).show()
        );
    }
}
