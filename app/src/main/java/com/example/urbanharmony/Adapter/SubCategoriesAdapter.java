package com.example.urbanharmony.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.urbanharmony.Models.SubCategoryModel;
import com.example.urbanharmony.R;
import com.example.urbanharmony.Screens.FilteredProductsActivity;
import com.example.urbanharmony.Screens.SubcategoriesViewActivity;

import java.util.ArrayList;
import java.util.Random;

public class SubCategoriesAdapter extends RecyclerView.Adapter<SubCategoriesAdapter.ViewHolder>{
    Context context;
    ArrayList<SubCategoryModel> data;
    String categoryId, categoryName;

    public SubCategoriesAdapter(Context context, ArrayList<SubCategoryModel> data, String categoryId, String categoryName) {
        this.context = context;
        this.data = data;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    @NonNull
    @Override
    public SubCategoriesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subcategories_grid_custom_listview, parent, false);
        // Passing view to ViewHolder
        SubCategoriesAdapter.ViewHolder viewHolder = new SubCategoriesAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SubCategoriesAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(data.get(position).getName());
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), FilteredProductsActivity.class);
                intent.putExtra("categoryId",categoryId);
                intent.putExtra("categoryName",categoryName);
                v.getContext().startActivity(intent);
            }
        });

        // Set dynamic height
        Random random = new Random();
        int randomHeight = 200 + random.nextInt(301);
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        layoutParams.height = randomHeight;
        holder.itemView.setLayoutParams(layoutParams);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        LinearLayout item;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            item = view.findViewById(R.id.item);
        }
    }
}
