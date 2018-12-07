package com.example.samee.traveljournal;

import android.content.Intent;
import android.location.Location;
import android.support.constraint.solver.widgets.Snapshot;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewLocations extends AppCompatActivity {
ListView listView;
    ArrayList<String> locNames=new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_locations);
        listView=(ListView)findViewById(R.id.locList);
        final ArrayAdapter arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,locNames);
        listView.setAdapter(arrayAdapter);
        FirebaseDatabase.getInstance().getReference().child("locations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    locNames.add(snapshot.getKey());
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(ViewLocations.this, LocationRatings.class);
                intent.putExtra("location",locNames.get(position));
                startActivity(intent);

            }
        });



    }
}
