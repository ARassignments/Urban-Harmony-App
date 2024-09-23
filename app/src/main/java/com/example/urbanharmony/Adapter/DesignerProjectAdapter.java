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

import com.bumptech.glide.Glide;
import com.example.urbanharmony.Models.ProjectModel;
import com.example.urbanharmony.Models.UsersModel;
import com.example.urbanharmony.R;
import com.example.urbanharmony.Screens.DesignerDetailActivity;
import com.example.urbanharmony.Screens.ProjectDetailActivity;

import java.util.ArrayList;

public class DesignerProjectAdapter extends RecyclerView.Adapter<DesignerProjectAdapter.ViewHolder>{
    Context context;
    ArrayList<ProjectModel> data;

    public DesignerProjectAdapter(Context context, ArrayList<ProjectModel> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public DesignerProjectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.designer_projects_custom_listview, parent, false);
        // Passing view to ViewHolder
        DesignerProjectAdapter.ViewHolder viewHolder = new DesignerProjectAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DesignerProjectAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.pName.setText(data.get(position).getpName());
        holder.pDesc.setText(data.get(position).getpDesc());
        Glide.with(holder.itemView.getContext()).load(data.get(position).getpImage()).into(holder.pImage);
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ProjectDetailActivity.class);
                intent.putExtra("pid",data.get(position).getId());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView pName, pDesc;
        ImageView pImage;
        LinearLayout item;

        public ViewHolder(View view) {
            super(view);
            pName = view.findViewById(R.id.pName);
            item = view.findViewById(R.id.item);
            pImage = view.findViewById(R.id.pImage);
            pDesc = view.findViewById(R.id.pDesc);
        }
    }
}
