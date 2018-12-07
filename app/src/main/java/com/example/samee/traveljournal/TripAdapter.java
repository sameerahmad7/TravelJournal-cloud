package com.example.samee.traveljournal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.samee.models.Posts;

import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripHolder> {
    private int itemLayout;
    private List<String> data;

    Context c;
    public TripAdapter(int itemLayout, List<String> data, Context c)
    {
        this.itemLayout=itemLayout;
        this.data=data;
        this.c=c;
    }

    @NonNull
    @Override
    public TripAdapter.TripHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new TripAdapter.TripHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull TripAdapter.TripHolder holder, int position) {


        Glide.with(c)
                .load(data.get(position))
                .into(holder.locView);




    }

    @Override
    public int getItemCount() {
        return data.size();
    }




    public class TripHolder extends RecyclerView.ViewHolder
    {
        ImageView locView;
        public TripHolder(View itemView)
        {
            super(itemView);
            locView=(ImageView)itemView.findViewById(R.id.locImages);

        }
    }
}
