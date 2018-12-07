package com.example.samee.traveljournal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.samee.models.Posts;

import java.util.ArrayList;
import java.util.List;

public class TripDetails extends AppCompatActivity {
    RecyclerView recyclerView;
    public List<String> data=new ArrayList<String>();
    public   TripAdapter myRecyclerViewAdapter;
    TextView tripName;
    TextView locName;
    public List<String> videoData=new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);
        tripName=(TextView)findViewById(R.id.tripName);
        locName=(TextView)findViewById(R.id.locName);
        Posts posts=(Posts)getIntent().getSerializableExtra("post");
        tripName.setText(posts.getTripName());
        locName.setText(posts.getLocation());
        for(String uri:posts.getUris())
        {
            if(!uri.contains("mp4"))
            data.add(uri);
            else if(uri.contains("mp4"))
                videoData.add(uri);

        }
        recyclerView=(RecyclerView)findViewById(R.id.imgRecycler);
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.details_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.playVideo)
        {
            if(videoData.size()>0) {
                Intent intent = new Intent(TripDetails.this, VideoActivity.class);
                intent.putExtra("uri", videoData.get(0));
                startActivity(intent);
            }
            else
                Toast.makeText(getApplicationContext(),"No Video Data!",Toast.LENGTH_SHORT).show();

        }

        return super.onOptionsItemSelected(item);
    }
}
