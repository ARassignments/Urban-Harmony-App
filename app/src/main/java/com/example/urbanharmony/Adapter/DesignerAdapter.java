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
import com.example.urbanharmony.Models.UsersModel;
import com.example.urbanharmony.R;
import com.example.urbanharmony.Screens.DesignerDetailActivity;
import com.example.urbanharmony.Screens.FilteredProductsActivity;
import com.example.urbanharmony.Screens.UserProfileActivity;

import java.util.ArrayList;
import java.util.Random;

public class DesignerAdapter extends RecyclerView.Adapter<DesignerAdapter.ViewHolder>{
    Context context;
    ArrayList<UsersModel> data;

    public DesignerAdapter(Context context, ArrayList<UsersModel> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public DesignerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.designer_custom_listview, parent, false);
        // Passing view to ViewHolder
        DesignerAdapter.ViewHolder viewHolder = new DesignerAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DesignerAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(data.get(position).getName());
        if(!data.get(position).getName().toString().equals("")){
            int imageResId = holder.itemView.getContext().getResources().getIdentifier(
                    data.get(position).getImage(),   // Get image string (e.g., "image_name")
                    "drawable",        // Resource type (drawable)
                    holder.itemView.getContext().getPackageName()  // Package name
            );
            holder.image.setImageResource(imageResId);
        }
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DesignerDetailActivity.class);
                intent.putExtra("userId",data.get(position).getId());
                v.getContext().startActivity(intent);
            }
        });
        holder.itemView.setAlpha(0f);
        holder.itemView.animate().alpha(1f).setDuration(400).setStartDelay(position * 20).start();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, designsCount;
        ImageView image;
        LinearLayout item;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            item = view.findViewById(R.id.item);
            image = view.findViewById(R.id.image);
            designsCount = view.findViewById(R.id.designsCount);
        }
    }
}
