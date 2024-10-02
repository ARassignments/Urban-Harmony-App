package com.example.urbanharmony.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.urbanharmony.MainActivity;
import com.example.urbanharmony.Models.FeedbackModel;
import com.example.urbanharmony.Models.ProductFeedbackModel;
import com.example.urbanharmony.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ProductReviewAdapter extends RecyclerView.Adapter<ProductReviewAdapter.ViewHolder>{
    Context context;
    List<ProductFeedbackModel> data;

    public ProductReviewAdapter(Context context, List<ProductFeedbackModel> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public ProductReviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.designer_reviews_custom_listview, parent, false);
        // Passing view to ViewHolder
        ProductReviewAdapter.ViewHolder viewHolder = new ProductReviewAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductReviewAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.review.setText(data.get(position).getReview());
        holder.date.setText(data.get(position).getReviewDate());
        String star = data.get(position).getRating();
        if(star.equals("0.0")){
            holder.starOne.setImageResource(R.drawable.star_outline);
            holder.starTwo.setImageResource(R.drawable.star_outline);
            holder.starThree.setImageResource(R.drawable.star_outline);
            holder.starFour.setImageResource(R.drawable.star_outline);
            holder.starFive.setImageResource(R.drawable.star_outline);
        } else if(star.equals("1.0")){
            holder.starOne.setImageResource(R.drawable.star);
            holder.starTwo.setImageResource(R.drawable.star_outline);
            holder.starThree.setImageResource(R.drawable.star_outline);
            holder.starFour.setImageResource(R.drawable.star_outline);
            holder.starFive.setImageResource(R.drawable.star_outline);
        } else if(star.equals("2.0")){
            holder.starOne.setImageResource(R.drawable.star);
            holder.starTwo.setImageResource(R.drawable.star);
            holder.starThree.setImageResource(R.drawable.star_outline);
            holder.starFour.setImageResource(R.drawable.star_outline);
            holder.starFive.setImageResource(R.drawable.star_outline);
        } else if(star.equals("3.0")){
            holder.starOne.setImageResource(R.drawable.star);
            holder.starTwo.setImageResource(R.drawable.star);
            holder.starThree.setImageResource(R.drawable.star);
            holder.starFour.setImageResource(R.drawable.star_outline);
            holder.starFive.setImageResource(R.drawable.star_outline);
        } else if(star.equals("4.0")){
            holder.starOne.setImageResource(R.drawable.star);
            holder.starTwo.setImageResource(R.drawable.star);
            holder.starThree.setImageResource(R.drawable.star);
            holder.starFour.setImageResource(R.drawable.star);
            holder.starFive.setImageResource(R.drawable.star_outline);
        } else if(star.equals("5.0")){
            holder.starOne.setImageResource(R.drawable.star);
            holder.starTwo.setImageResource(R.drawable.star);
            holder.starThree.setImageResource(R.drawable.star);
            holder.starFour.setImageResource(R.drawable.star);
            holder.starFive.setImageResource(R.drawable.star);
        }
        MainActivity.db.child("Users").child(data.get(position).getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    holder.username.setText(snapshot.child("name").getValue().toString());
                    if(!snapshot.child("image").getValue().toString().equals("")){
                        holder.image.setImageResource(Integer.parseInt(snapshot.child("image").getValue().toString()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        Glide.with(holder.itemView.getContext()).load(data.get(position).getdImage()).into(holder.dImage);
//        holder.item.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(v.getContext(), ProjectDetailActivity.class);
//                intent.putExtra("pid",data.get(position).getId());
//                v.getContext().startActivity(intent);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView username, date, review;
        ImageView image, starOne, starTwo, starThree, starFour, starFive;
        LinearLayout item;

        public ViewHolder(View view) {
            super(view);
            item = view.findViewById(R.id.item);
            username = view.findViewById(R.id.username);
            date = view.findViewById(R.id.date);
            review = view.findViewById(R.id.review);
            image = view.findViewById(R.id.image);
            starOne = view.findViewById(R.id.starOne);
            starTwo = view.findViewById(R.id.starTwo);
            starThree = view.findViewById(R.id.starThree);
            starFour = view.findViewById(R.id.starFour);
            starFive = view.findViewById(R.id.starFive);
        }
    }
}
