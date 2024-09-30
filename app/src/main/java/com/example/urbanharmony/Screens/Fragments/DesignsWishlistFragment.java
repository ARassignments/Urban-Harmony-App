package com.example.urbanharmony.Screens.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.urbanharmony.Adapter.DesignWishlistAdapter;
import com.example.urbanharmony.Adapter.ProductWishlistAdapter;
import com.example.urbanharmony.MainActivity;
import com.example.urbanharmony.Models.WishlistDesignModel;
import com.example.urbanharmony.Models.WishlistModel;
import com.example.urbanharmony.R;
import com.example.urbanharmony.RecyclerDecoration.GridSpacingItemDecoration;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class DesignsWishlistFragment extends Fragment {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    static String UID = "";
    RecyclerView productView;
    LinearLayout notfoundContainer;
    ArrayList<WishlistDesignModel> datalist = new ArrayList<>();
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_designs_wishlist, container, false);
        sharedPreferences = getContext().getSharedPreferences("myData",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(!sharedPreferences.getString("UID","").equals("")){
            UID = sharedPreferences.getString("UID","").toString();
        }

        productView = view.findViewById(R.id.productView);
        notfoundContainer = view.findViewById(R.id.notfoundContainer);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1); // 2 columns
        productView.setLayoutManager(gridLayoutManager);

        // Add spacing of 10dp between items
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing); // Define this in dimens.xml or hardcode
        GridSpacingItemDecoration spacingItemDecoration = new GridSpacingItemDecoration(1, spacingInPixels, true);

        // Add ItemDecoration to the RecyclerView
        productView.addItemDecoration(spacingItemDecoration);

        fetchData("");
        return view;
    }

    public void fetchData(String data){
        MainActivity.db.child("WishlistDesigns").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    datalist.clear();
                    for (DataSnapshot ds: snapshot.getChildren()){
                        if(ds.child("UID").getValue().toString().equals(UID)){
                            WishlistDesignModel model = new WishlistDesignModel(ds.getKey(),
                                    ds.child("UID").getValue().toString(),
                                    ds.child("DID").getValue().toString()
                            );
                            datalist.add(model);
                        }
                    }
                    if(datalist.size() > 0){
                        productView.setVisibility(View.VISIBLE);
                        notfoundContainer.setVisibility(View.GONE);
                        DesignWishlistAdapter adapter = new DesignWishlistAdapter(getContext(),datalist);
                        productView.setAdapter(adapter);
                    } else {
                        productView.setVisibility(View.GONE);
                        notfoundContainer.setVisibility(View.VISIBLE);
                        if(!data.equals("")){
                            notfoundContainer.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    productView.setVisibility(View.GONE);
                    notfoundContainer.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}