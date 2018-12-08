package com.example.samee.traveljournal;

import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.constraint.solver.widgets.Snapshot;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;

import com.bumptech.glide.Glide;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewLocations extends AppCompatActivity {
ListView listView;
    ArrayList<String> locNames=new ArrayList<String>();
    private DrawerLayout dl;
    FirebaseAuth mAuth;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    ImageView profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_locations);
        setTitle("Locations");
        mAuth=FirebaseAuth.getInstance();
        dl = (DrawerLayout)findViewById(R.id.activity_main);

        t = new ActionBarDrawerToggle(
                this, dl, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        dl.addDrawerListener(t);
        t.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nv = (NavigationView)findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id==R.id.logout)
                {
                    if(null!=mAuth.getCurrentUser()) {
                        mAuth.signOut();
                        if(null!= Profile.getCurrentProfile())
                        {
                            LoginManager.getInstance().logOut();
                        }

                    }
                }
                else if(id==R.id.profile)
                {
                    startActivity(new Intent(ViewLocations.this,ProfileActivity.class));


                }
                else if(id==R.id.cityTrips)
                {
                    startActivity(new Intent(ViewLocations.this,ViewCityTrips.class));

                }
                else if(id==R.id.locations)
                {
                    dl.closeDrawers();

                }
                else if(id==R.id.outCityTrips)
                {
                    startActivity(new Intent(ViewLocations.this,ViewOutCTrips.class));

                }
                else if(id==R.id.stateTrips)
                {
                    startActivity(new Intent(ViewLocations.this,ViewOutSTrips.class));

                }
                return true;




            }
        });


        listView=(ListView)findViewById(R.id.locList);
        final ArrayAdapter arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,locNames);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(ViewLocations.this,MapsActivity.class);
                intent.putExtra("location",locNames.get(position));
                startActivity(intent);
                finish();


                return true;
            }
        });
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (t.onOptionsItemSelected(item)) {
            profilePic=(ImageView)findViewById(R.id.profilePic);
            Glide.with(this)
                    .load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString())
                    .into(profilePic);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
