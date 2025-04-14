package com.example.comicapp.authen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.comicapp.author.AuthorActivity;
import com.example.comicapp.model.User;
import com.example.comicapp.reader.MainActivity;
import com.example.comicapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class ManDangnhap extends AppCompatActivity {
    private EditText email;
    private EditText password;
    private Button login;
    private Button register;
    private List<User> users = new ArrayList<>();
    private boolean checkLogin = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_man_dangnhap);
        checkLogin();
        onInit();
        load();
    }
    private void checkLogin(){
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        int userRole = sharedPreferences.getInt("userRole", 2);
        Log.e("userRole: ", userRole + "");
        if(userRole != 2) {
            if (userRole == 1) {
                startActivity(new Intent(this, AuthorActivity.class));
            } else startActivity(new Intent(this, MainActivity.class));
            finish();
        }else {
            // Người dùng chưa đăng nhập, hiển thị màn hình đăng nhập
            setContentView(R.layout.activity_man_dangnhap);
        }
    }
    private void onInit() {
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.dnhap_button);
        register = findViewById(R.id.dky_button);
        login.setOnClickListener(v -> {
            login(email, password);
        });
        register.setOnClickListener(v -> {
            Intent intent = new Intent(ManDangnhap.this, ManDangky.class);
            startActivity(intent);
        });
    }

    private void login(TextView emails, TextView passwords){

        String emailInput = emails.getText().toString();
        String passwordInput = passwords.getText().toString();
        if(emailInput.isEmpty() || passwordInput.isEmpty()){
            Toast.makeText(this, "Vui lòng nhập đủ user name và password!", Toast.LENGTH_SHORT).show();
        }
        else{
            for(User user : users){
                Log.e("users: ", user + "");

                    if(user.getUserName().equals(emailInput) && checkkPsw(passwordInput, user.getPsw())){
                        checkLogin = true;
                        //
                        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("userId", user.getUserName());
                        editor.putInt("userRole", user.getRole());
                        editor.putString("userKey", user.getUserKey());
                        editor.apply();
                        //
                        Log.e("user: ", user.getRole() + "");
                        if(user.getRole() == 1){
                            startActivity(new Intent(this, AuthorActivity.class));
                        }
                        else startActivity(new Intent(this, MainActivity.class));
                    }

            }
            if(!checkLogin){
                Toast.makeText(this, "User name hoặc mật khẩu không chính xác!", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void load(){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("user");
        db.get().addOnCompleteListener(task -> {
            for(DataSnapshot ds : task.getResult().getChildren()){
                User user = ds.getValue(User.class);
                user.setUserKey(ds.getKey());
                users.add(user);
            }
        });

    }

    private boolean checkkPsw(String input, String hash){
        return BCrypt.verifyer().verify(input.toCharArray(), hash).verified;
    }
}
