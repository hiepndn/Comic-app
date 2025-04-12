package com.example.comicapp.account;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.comicapp.authen.ManDangky;
import com.example.comicapp.authen.ManDangnhap;
import com.example.comicapp.R;

public class AccountFragment extends Fragment {

    private TextView loginText, welcomeText;
    private Button registerButton, logoutButton;

    public AccountFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        loginText = view.findViewById(R.id.textViewLogin);
        registerButton = view.findViewById(R.id.buttonRegister);
        welcomeText = view.findViewById(R.id.welcome_text);
        logoutButton = view.findViewById(R.id.buttonLogout);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("userId", null);

        updateUI(username);

        loginText.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ManDangnhap.class);
            startActivity(intent);
        });

        registerButton.setOnClickListener( v -> {
            Intent intent = new Intent(getActivity(), ManDangky.class);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> logout());

        return view;
    }

    private void updateUI(String username) {
        if (username != null) {
            loginText.setVisibility(View.GONE);
            registerButton.setVisibility(View.GONE);
            welcomeText.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.VISIBLE);
            welcomeText.setText("Chào mừng " + username + " đã đăng nhập!");
        } else {
            loginText.setVisibility(View.VISIBLE);
            registerButton.setVisibility(View.VISIBLE);
            welcomeText.setVisibility(View.GONE);
            logoutButton.setVisibility(View.GONE);
        }
    }

    private void logout() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        updateUI(null);
        Intent intent = new Intent(getActivity(), ManDangnhap.class);
        startActivity(intent);
    }
}
