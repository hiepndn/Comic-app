package com.example.comicapp.account;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.comicapp.Story;
import com.example.comicapp.authen.ManDangky;
import com.example.comicapp.authen.ManDangnhap;
import com.example.comicapp.R;
import com.example.comicapp.category.adapter.StoryAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AccountFragment extends Fragment {

    private TextView loginText, welcomeText, textView5;
    private Button registerButton, logoutButton;
    private RecyclerView recyclerView;
    private List<Story> favList = new ArrayList<>();
    private StoryAdapter favAdapter;

    public AccountFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        loginText = view.findViewById(R.id.textViewLogin);
        registerButton = view.findViewById(R.id.buttonRegister);
        welcomeText = view.findViewById(R.id.welcome_text);
        logoutButton = view.findViewById(R.id.buttonLogout);
        recyclerView = view.findViewById(R.id.recyclerViewFavourite);
        textView5 = view.findViewById(R.id.textView5);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("userId", null);
        int userRole = sharedPreferences.getInt("userRole", 0);
        String userKey = sharedPreferences.getString("userKey", null);
        initReaderUi(userRole, username);
        if (userKey != null) {
            fetchFavouriteData(userKey);
        }
        loginText.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ManDangnhap.class);
            startActivity(intent);
        });

        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ManDangky.class);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> logout());

        return view;
    }

    private void initReaderUi(int userRole, String username) {
        if (username == null) {
            loginText.setVisibility(View.VISIBLE);
            registerButton.setVisibility(View.VISIBLE);
            welcomeText.setVisibility(View.GONE);
            logoutButton.setVisibility(View.GONE);
        } else if (userRole == 1) {
            loginText.setVisibility(View.GONE);
            registerButton.setVisibility(View.GONE);
            welcomeText.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.VISIBLE);
            welcomeText.setText("Chào mừng " + username + " đã đăng nhập!");
        } else {
            loginText.setVisibility(View.GONE);
            registerButton.setVisibility(View.GONE);
            welcomeText.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.VISIBLE);
            welcomeText.setText("Chào mừng " + username + " đã đăng nhập!");
            textView5.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void logout() {
        // Xóa thông tin đăng nhập của người dùng
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();


        initReaderUi(0, null);

        Intent intent = new Intent(getActivity(), ManDangnhap.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Xóa các Activity trên stack nhưng không thoát app
        startActivity(intent);

        // Kết thúc Activity hiện tại để tránh quay lại bằng nút "Back"
        requireActivity().finish();
    }

    private void fetchFavouriteData(String userKey) {
        DatabaseReference favouriteRef = FirebaseDatabase.getInstance().getReference("user").child(userKey).child("favourite");
        favouriteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> favouriteId = new ArrayList<>();
                for (DataSnapshot ds: snapshot.getChildren()){
                    favouriteId.add(ds.getKey());
                }
                loadFavouriteFromId(favouriteId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("History", "Lỗi lấy danh sach yeue thicsh: " + error.getMessage());
            }
        });
    }

    private void loadFavouriteFromId(List<String> favouriteId){
        DatabaseReference storyRef = FirebaseDatabase.getInstance().getReference("story");
        favList.clear();

        final int[] loadCount = {0};
        for (String id: favouriteId){
            storyRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Story story = snapshot.getValue(Story.class);
                    if(story != null){
                        story.setId(id);
                        favList.add(story);
                    }
                    checkFinished(favouriteId.size());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

                private void checkFinished(int total){
                    loadCount[0]++;
                    if(loadCount[0] == total){
                        loadViewFav();
                    }
                }
            });
        }
    }

    private void loadViewFav(){
        favAdapter = new StoryAdapter(favList);
        recyclerView.setAdapter(favAdapter);
        favAdapter.notifyDataSetChanged();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }
}