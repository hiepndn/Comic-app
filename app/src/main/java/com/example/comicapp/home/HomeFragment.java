package com.example.comicapp.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.comicapp.MainActivity;
import com.example.comicapp.R;
import com.example.comicapp.search.fragment.SearchFragment;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Tìm SearchView
        SearchView searchView = view.findViewById(R.id.searchView);

        // Khi SearchView được focus, sẽ chuyển sang SearchFragment
        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                navigateToSearchFragment();
            }
        });

        return view;
    }

    private void navigateToSearchFragment() {

        SearchView searchView = requireView().findViewById(R.id.searchView);
        searchView.clearFocus(); // Bỏ focus để tránh lỗi giữ bàn phím

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, new SearchFragment()); // Chuyển đến SearchFragment
        transaction.addToBackStack(null); // Cho phép quay lại màn trước
        transaction.commit();

        ((MainActivity) requireActivity()).receiveDataFromFragment(R.id.nav_search);
    }

}
