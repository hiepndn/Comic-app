package com.example.comicapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CategoriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoriesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CategoriesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CategoriesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CategoriesFragment newInstance(String param1, String param2) {
        CategoriesFragment fragment = new CategoriesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_categories, container, false);
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