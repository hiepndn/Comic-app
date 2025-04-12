package com.example.comicapp;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.comicapp.account.AccountFragment;
import com.example.comicapp.category.CategoriesFragment;
import com.example.comicapp.home.HomeFragment;
import com.example.comicapp.search.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initBottomNavigationView(R.id.nav_home);
    }

    public void initBottomNavigationView(int data){
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (item.getItemId() == R.id.nav_search) {
                selectedFragment = new SearchFragment();
            } else if (item.getItemId() == R.id.nav_account) {
                selectedFragment = new AccountFragment();
            } else if (item.getItemId() == R.id.nav_category) {
                selectedFragment = new CategoriesFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });
        bottomNavigationView.setSelectedItemId(data);
    }

    public void receiveDataFromFragment(int data) {
        Log.d("MainActivity", "Dữ liệu nhận được từ Fragment: " + data);
        initBottomNavigationView(data);
    }
}
