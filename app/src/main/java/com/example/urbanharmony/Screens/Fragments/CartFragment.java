package com.example.urbanharmony.Screens.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.example.urbanharmony.Models.WishlistModel;
import com.example.urbanharmony.R;

import java.util.ArrayList;

public class CartFragment extends Fragment {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    static String UID = "";
    GridView gridView;
    LinearLayout notfoundContainer;
    ArrayList<WishlistModel> datalist = new ArrayList<>();
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_cart, container, false);
        sharedPreferences = getContext().getSharedPreferences("myData",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(!sharedPreferences.getString("UID","").equals("")){
            UID = sharedPreferences.getString("UID","").toString();
        }

        gridView = view.findViewById(R.id.gridView);
        notfoundContainer = view.findViewById(R.id.notfoundContainer);
//        fetchData("");
        return view;
    }
}