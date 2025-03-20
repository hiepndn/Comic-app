package com.example.comicapp.author;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.comicapp.author.add.AuthorComicFragment;
import com.example.comicapp.author.del.AuthorDeleteFragment;
import com.example.comicapp.R;
import com.example.comicapp.account.AccountFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AuthorActivity extends AppCompatActivity {

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_author);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_author, new AuthorComicFragment())
                    .commit();
        }

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.nav_comic) {
                    selectedFragment = new AuthorComicFragment();
                } else if (itemId == R.id.nav_delete) {
                    selectedFragment = new AuthorDeleteFragment();
                } else if (itemId == R.id.nav_account_author) {
                    selectedFragment = new AccountFragment();
                }
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_author, selectedFragment)
                            .commit();
                }
                return true;
            }
        });
    }
}