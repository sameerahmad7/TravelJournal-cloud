package com.example.samee.traveljournal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    public List<String> data=new ArrayList<String>();
    public   TripAdapter myRecyclerViewAdapter;
    String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        Intent intent=getIntent();
        location=intent.getStringExtra("location");
        setTitle(location);
        data=(ArrayList<String>)intent.getSerializableExtra("uris");
        for(String uri:data)
        {
            if(uri.contains("mp4"))
            {
                data.remove(uri);
            }
        }
        recyclerView=(RecyclerView)findViewById(R.id.galleryView);
        myRecyclerViewAdapter=new TripAdapter(R.layout.images_view,data,this);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(myRecyclerViewAdapter);
        recyclerView.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);


    }
}
