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

public class ManDangnhap extends AppCompatActivity {
    private EditText emailedit, passwordedit;
    private Button dnhap_button, dky_button;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_man_dangnhap);
        mAuth = FirebaseAuth.getInstance();

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
        String email, password;
        email = emailedit.getText().toString();
        password = passwordedit.getText().toString();

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
                    Toast.makeText(getApplicationContext(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ManDangnhap.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Đóng màn hình đăng nhập sau khi đăng nhập thành công
                } else {

                    Toast.makeText(getApplicationContext(), "Đăng nhập không thành công: " , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}