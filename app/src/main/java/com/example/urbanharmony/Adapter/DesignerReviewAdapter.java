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

import com.bumptech.glide.Glide;
import com.example.urbanharmony.MainActivity;
import com.example.urbanharmony.Models.DesignModel;
import com.example.urbanharmony.Models.FeedbackModel;
import com.example.urbanharmony.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DesignerReviewAdapter extends RecyclerView.Adapter<DesignerReviewAdapter.ViewHolder>{
    Context context;
    List<FeedbackModel> data;

    public DesignerReviewAdapter(Context context, List<FeedbackModel> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public DesignerReviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.designer_reviews_custom_listview, parent, false);
        // Passing view to ViewHolder
        DesignerReviewAdapter.ViewHolder viewHolder = new DesignerReviewAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DesignerReviewAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
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
        MainActivity.db.child("Users").child(data.get(position).getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    holder.username.setText(snapshot.child("name").getValue().toString());
                    if(!snapshot.child("image").getValue().toString().equals("")){
                        Glide.with(context).load(snapshot.child("image").getValue().toString()).into(holder.image);
//                        holder.image.setImageResource(Integer.parseInt(snapshot.child("image").getValue().toString()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(!data.get(position).getReplyReview().equals("")){
            holder.replyContainer.setVisibility(View.VISIBLE);
        }

        holder.replyReview.setText(data.get(position).getReplyReview());
        holder.replyDate.setText(data.get(position).getRepltReviewDate());
        String replyStar = data.get(position).getReplyRating();
        if(replyStar.equals("0.0")){
            holder.replyStarOne.setImageResource(R.drawable.star_outline);
            holder.replyStarTwo.setImageResource(R.drawable.star_outline);
            holder.replyStarThree.setImageResource(R.drawable.star_outline);
            holder.replyStarFour.setImageResource(R.drawable.star_outline);
            holder.replyStarFive.setImageResource(R.drawable.star_outline);
        } else if(replyStar.equals("1.0")){
            holder.replyStarOne.setImageResource(R.drawable.star);
            holder.replyStarTwo.setImageResource(R.drawable.star_outline);
            holder.replyStarThree.setImageResource(R.drawable.star_outline);
            holder.replyStarFour.setImageResource(R.drawable.star_outline);
            holder.replyStarFive.setImageResource(R.drawable.star_outline);
        } else if(replyStar.equals("2.0")){
            holder.replyStarOne.setImageResource(R.drawable.star);
            holder.replyStarTwo.setImageResource(R.drawable.star);
            holder.replyStarThree.setImageResource(R.drawable.star_outline);
            holder.replyStarFour.setImageResource(R.drawable.star_outline);
            holder.replyStarFive.setImageResource(R.drawable.star_outline);
        } else if(replyStar.equals("3.0")){
            holder.replyStarOne.setImageResource(R.drawable.star);
            holder.replyStarTwo.setImageResource(R.drawable.star);
            holder.replyStarThree.setImageResource(R.drawable.star);
            holder.replyStarFour.setImageResource(R.drawable.star_outline);
            holder.replyStarFive.setImageResource(R.drawable.star_outline);
        } else if(replyStar.equals("4.0")){
            holder.replyStarOne.setImageResource(R.drawable.star);
            holder.replyStarTwo.setImageResource(R.drawable.star);
            holder.replyStarThree.setImageResource(R.drawable.star);
            holder.replyStarFour.setImageResource(R.drawable.star);
            holder.replyStarFive.setImageResource(R.drawable.star_outline);
        } else if(replyStar.equals("5.0")){
            holder.replyStarOne.setImageResource(R.drawable.star);
            holder.replyStarTwo.setImageResource(R.drawable.star);
            holder.replyStarThree.setImageResource(R.drawable.star);
            holder.replyStarFour.setImageResource(R.drawable.star);
            holder.replyStarFive.setImageResource(R.drawable.star);
        }
        MainActivity.db.child("Users").child(data.get(position).getDesignerId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    holder.replyUsername.setText(snapshot.child("name").getValue().toString());
                    if(!snapshot.child("image").getValue().toString().equals("")){
                        Glide.with(context).load(snapshot.child("image").getValue().toString()).into(holder.replyImage);
//                        holder.replyImage.setImageResource(Integer.parseInt(snapshot.child("image").getValue().toString()));
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
        TextView username, date, review, replyUsername, replyDate, replyReview;
        ImageView image, starOne, starTwo, starThree, starFour, starFive;
        LinearLayout item, replyContainer;
        ImageView replyImage, replyStarOne, replyStarTwo, replyStarThree, replyStarFour, replyStarFive;

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
            replyContainer = view.findViewById(R.id.replyContainer);
            replyUsername = view.findViewById(R.id.replyUsername);
            replyDate = view.findViewById(R.id.replyDate);
            replyReview = view.findViewById(R.id.replyReview);
            replyImage = view.findViewById(R.id.replyImage);
            replyStarOne = view.findViewById(R.id.replyStarOne);
            replyStarTwo = view.findViewById(R.id.replyStarTwo);
            replyStarThree = view.findViewById(R.id.replyStarThree);
            replyStarFour = view.findViewById(R.id.replyStarFour);
            replyStarFive = view.findViewById(R.id.replyStarFive);
        }
    }
}
