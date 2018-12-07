package com.example.samee.traveljournal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.samee.models.Ratings;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class LocationRatings extends AppCompatActivity {
    RecyclerView recyclerView;
    public List<Ratings> data=new ArrayList<Ratings>();
    public   MyRecyclerView myRecyclerViewAdapter;
    String location;

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

}
