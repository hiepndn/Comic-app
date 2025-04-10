package com.example.comicapp.authen;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.comicapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class ManDangky extends AppCompatActivity {
    private Button register, login;
    private EditText email, password;
    private List<User> users = new ArrayList<>();
    private CheckBox chkAuthor;
    private int checkRegister = 0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_man_dangky);
        onInit();
        load();
    }
    private void onInit(){
        register = findViewById(R.id.dky_button);
        login = findViewById(R.id.dnhap_button);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        chkAuthor = findViewById(R.id.chkAuthor);
        register.setOnClickListener(v -> {
            registerHandle(email, password, chkAuthor);
        });
        login.setOnClickListener(v -> {
            startActivity(new Intent(this, ManDangnhap.class));
        });
    }

    private void registerHandle(EditText emails, EditText passwords, CheckBox chkAuthor){
        String emailInput = emails.getText().toString().trim();
        String passwordInput = passwords.getText().toString();
        boolean isAuthorChecked = chkAuthor.isChecked();
        int role;
        if(isAuthorChecked == true) {
            role = 1;
        }else role = 1;
        emails.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkRegister = 0;
            }
        });
        if(emailInput.isEmpty() || passwordInput.isEmpty()){
            Toast.makeText(this, "Vui lòng nhập đủ user name và password!", Toast.LENGTH_SHORT).show();
        }
        else{
            for(User user: users){
                if(emailInput.equals(user.getUserName())){
                    checkRegister += 1;

                }
                Log.e("number: ", checkRegister + "");
            }
            if(checkRegister == 0){
                String hashPsw = BCrypt.withDefaults().hashToString(12, passwordInput.toCharArray());
                User newUser = new User(emailInput, hashPsw, role);
                DatabaseReference db = FirebaseDatabase.getInstance().getReference("user");
                db.push().setValue(newUser);
                Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, ManDangnhap.class));
            }
            else{
                Toast.makeText(this, "User name đã được sử dụng!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void load(){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("user");
        db.get().addOnCompleteListener(task -> {
            for(DataSnapshot ds : task.getResult().getChildren()){
                User user = ds.getValue(User.class);
                users.add(user);
            }
        });
    }
}
