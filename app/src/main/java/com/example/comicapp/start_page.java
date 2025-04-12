package com.example.comicapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.comicapp.author.AuthorActivity;

public class start_page extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        checkLogin();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkLogin();
    }

    private void checkLogin(){
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        int userRole = sharedPreferences.getInt("userRole", 2);
        Log.e("userRole: ", userRole + "");
        if (userRole == 1) {
            new Handler().postDelayed(()-> {
                Intent switchPage = new Intent(this, AuthorActivity.class);
                startActivity(switchPage);
            }, 2000 );
        } else{
            new Handler().postDelayed(()-> {
                Intent switchPage = new Intent(this, MainActivity.class);
                startActivity(switchPage);
            }, 2000 );
        }
    }
}