package com.example.samee.traveljournal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class CityTripsActivity extends AppCompatActivity {
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    List<Bitmap> bitmaps=new ArrayList<Bitmap>();
    ImageAdapter imageAdapter;
    private NavigationView nv;
    FirebaseAuth mAuth;
    ImageView profilePic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_trips);
        setTitle("City Trips");
        GridView gridview = (GridView) findViewById(R.id.gridviewCity);
//        bitmaps.add(BitmapFactory.decodeResource(this.getResources(),(R.drawable.lahore)));
//        bitmaps.add(BitmapFactory.decodeResource(this.getResources(),(R.drawable.lahore)));
//        bitmaps.add(BitmapFactory.decodeResource(this.getResources(),(R.drawable.lahore)));
//        bitmaps.add(BitmapFactory.decodeResource(this.getResources(),(R.drawable.lahore)));
//        bitmaps.add(BitmapFactory.decodeResource(this.getResources(),(R.drawable.lahore)));
//        bitmaps.add(BitmapFactory.decodeResource(this.getResources(),(R.drawable.lahore)));

        imageAdapter=new ImageAdapter(this,bitmaps);
        gridview.setAdapter(imageAdapter);
        mAuth=FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null)
                {
                    startActivity(new Intent(CityTripsActivity.this,MainActivity.class));
                    finish();
                }
            }
        });

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
                    startActivity(new Intent(CityTripsActivity.this,ProfileActivity.class));


                }
                else if(id==R.id.cityTrips)
                {
                    dl.closeDrawers();

                }
                else if(id==R.id.outCityTrips)
                {
                    startActivity(new Intent(CityTripsActivity.this,OutOfCityActivity.class));

                }
                else if(id==R.id.stateTrips)
                {
                    startActivity(new Intent(CityTripsActivity.this,OutOfStateActivity.class));

                }
                return true;




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
