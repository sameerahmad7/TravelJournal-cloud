package com.example.samee.traveljournal;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.samee.models.Ratings;

import java.util.List;

public class MyRecyclerView extends RecyclerView.Adapter<MyRecyclerView.MyViewHolder> {
    private int itemLayout;
    private List<Ratings> data;

public MyRecyclerView(int itemLayout,List<Ratings> data)
{
    this.itemLayout=itemLayout;
    this.data=data;
}

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new MyViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
    holder.ratingBar.setRating(Float.parseFloat(data.get(position).getRating()));
    holder.userBox.setText(data.get(position).getUser());
    holder.reviewBox.setText(data.get(position).getReview());



    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder  {
       public TextView userBox;
       public RatingBar ratingBar;
       public TextView reviewBox;
public MyViewHolder(View viewGroup)
{
    super(viewGroup);
    userBox=(TextView)viewGroup.findViewById(R.id.userRating);
    ratingBar=(RatingBar)viewGroup.findViewById(R.id.ratingBar);
    ratingBar.setEnabled(false);
    reviewBox=(TextView)viewGroup.findViewById(R.id.review);

}

    }
}
