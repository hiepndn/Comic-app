package com.example.comicapp;

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

public class AccountFragment extends Fragment {

    private TextView loginText, welcomeText;
    private Button registerButton, logoutButton;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        // Ánh xạ các thành phần giao diện
        loginText = view.findViewById(R.id.textViewLogin);
        registerButton = view.findViewById(R.id.buttonRegister);
        welcomeText = view.findViewById(R.id.welcome_text);
        logoutButton = view.findViewById(R.id.buttonLogout); // Đảm bảo ID đúng

        // Lấy trạng thái đăng nhập từ SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        String username = prefs.getString("username", "Người dùng");

        // Cập nhật giao diện
        updateUI(isLoggedIn, username);

        // Xử lý khi nhấn Đăng nhập
        loginText.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ManDangnhap.class);
            startActivity(intent);
        });

        // Xử lý khi nhấn Đăng xuất
        logoutButton.setOnClickListener(v -> logout());

        return view;
    }

    private void updateUI(boolean isLoggedIn, String username) {
        if (isLoggedIn) {
            // Hiển thị lời chào và nút Đăng xuất
            loginText.setVisibility(View.GONE);
            registerButton.setVisibility(View.GONE);
            welcomeText.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.VISIBLE);
            welcomeText.setText("Chào mừng " + username + " đã đăng nhập!");
        } else {
            // Hiển thị nút Đăng nhập và Đăng ký
            loginText.setVisibility(View.VISIBLE);
            registerButton.setVisibility(View.VISIBLE);
            welcomeText.setVisibility(View.GONE);
            logoutButton.setVisibility(View.GONE);
        }
    }

    private void logout() {
        // Xóa trạng thái đăng nhập
        SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear(); // Xóa toàn bộ dữ liệu
        editor.apply();

        // Cập nhật giao diện về trạng thái chưa đăng nhập
        updateUI(false, "");
    }
}
