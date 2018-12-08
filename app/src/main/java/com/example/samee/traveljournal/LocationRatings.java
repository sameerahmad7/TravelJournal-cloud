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

import com.example.samee.models.Ratings;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LocationRatings extends AppCompatActivity {
    RecyclerView recyclerView;
    public List<Ratings> data=new ArrayList<Ratings>();
    public   MyRecyclerView myRecyclerViewAdapter;
    String location;
    List<String> uris=new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_ratings);
        recyclerView=(RecyclerView)findViewById(R.id.ratingsRecycler);
        myRecyclerViewAdapter=new MyRecyclerView(R.layout.reviews_view,data);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(myRecyclerViewAdapter);
        recyclerView.setHasFixedSize(true);
        location=getIntent().getStringExtra("location");
        setTitle(location);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);


        FirebaseDatabase.getInstance().getReference("locations").child(location).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


           if(dataSnapshot.getValue()!=null)
           {

               Ratings rating=dataSnapshot.getValue(Ratings.class);
               data.add(rating);
               for(String uri:rating.getUris())
               {
                   uris.add(uri);
               }

               myRecyclerViewAdapter.notifyDataSetChanged();

           }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {



            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.location_details_menu,menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.gallery)
        {
            Intent intent=new Intent(LocationRatings.this,GalleryActivity.class);
            intent.putExtra("location",location);
            intent.putExtra("uris", (Serializable) uris);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
