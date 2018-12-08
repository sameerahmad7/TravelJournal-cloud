package com.example.samee.traveljournal;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.samee.models.Posts;
import com.example.samee.models.Ratings;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class ViewCityTrips extends AppCompatActivity {
ListView cityLists;
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    ImageView profilePic;
    FirebaseAuth mAuth=FirebaseAuth.getInstance();
    ArrayList<DataSnapshot> snapshots=new ArrayList<DataSnapshot>();
    ArrayList<java.lang.String> tripNames=new ArrayList<java.lang.String>();
    ArrayList<Posts>posts=new ArrayList<Posts>();
    FloatingActionButton actionButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_city_trips);
        setTitle("City Trips");
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
                    startActivity(new Intent(ViewCityTrips.this,ProfileActivity.class));


                }
                else if(id==R.id.cityTrips)
                {
                    dl.closeDrawers();

                }
                else if(id==R.id.locations)
                {
                    startActivity(new Intent(ViewCityTrips.this,ViewLocations.class));
                }
                else if(id==R.id.outCityTrips)
                {
                    startActivity(new Intent(ViewCityTrips.this,ViewOutCTrips.class));

                }
                else if(id==R.id.stateTrips)
                {
                    startActivity(new Intent(ViewCityTrips.this,ViewOutSTrips.class));

                }
                return true;




            }
        });

        cityLists=(ListView)findViewById(R.id.viewCityTrips);
        cityLists.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final DataSnapshot snapshot=snapshots.get(position);
                PopupMenu popupMenu=new PopupMenu(ViewCityTrips.this,cityLists);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId()==R.id.delete)
                        {
                            final FirebaseUser firebaseUser=mAuth.getCurrentUser();
                            FirebaseDatabase.getInstance().getReference("locations")
                                    .child(snapshot.getValue(Posts.class).getLocation()).addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    if(dataSnapshot.getValue()!=null)
                                    {
                                        if(dataSnapshot.getValue(Ratings.class).getUser().equals(firebaseUser.getDisplayName()))
                                        {
                                         String key=dataSnapshot.getKey();
                                            FirebaseDatabase.getInstance().getReference("locations")
                                                    .child(snapshot.getValue(Posts.class).getLocation()).child(key).removeValue();
                                        }
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

                            for(String url:snapshot.getValue(Posts.class).getUris())
                            {
                                FirebaseStorage.getInstance().getReferenceFromUrl(url).delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getApplicationContext(),"File deleted",Toast.LENGTH_SHORT).show();
                                            }
                                        });


                            }

                            FirebaseDatabase.getInstance().getReference("posts").child(firebaseUser.getUid())
                                    .child("City Trips").child(snapshot.getKey()).removeValue();

                            return true;
                        }
                        return false;

                    }
                });
                popupMenu.show();


                return false;
            }
        });
        actionButton=(FloatingActionButton)findViewById(R.id.addCity);
        final ArrayAdapter arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,tripNames);
        cityLists.setAdapter(arrayAdapter);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewCityTrips.this,CreatePostActivity.class));
            }
        });
        cityLists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(ViewCityTrips.this,TripDetails.class);
                intent.putExtra("post",posts.get(position));
                startActivity(intent);


            }
        });
        FirebaseUser firebaseUser=mAuth.getCurrentUser();
      FirebaseDatabase.getInstance().getReference("posts").child(firebaseUser.getUid())
              .child("City Trips").addChildEventListener(new ChildEventListener() {
                  @Override
                  public void onChildAdded(DataSnapshot dataSnapshot, java.lang.String s) {
                      if(dataSnapshot.getValue()!=null)
                      {
                          posts.add(dataSnapshot.getValue(Posts.class));
                          snapshots.add(dataSnapshot);
                          tripNames.add(dataSnapshot.getValue(Posts.class).getTripName());
                          arrayAdapter.notifyDataSetChanged();
                      }

                  }

                  @Override
                  public void onChildChanged(DataSnapshot dataSnapshot, java.lang.String s) {

                  }

                  @Override
                  public void onChildRemoved(DataSnapshot dataSnapshot) {
                      int index=0;
                      for(DataSnapshot snapshot:snapshots)
                      {
                          if(snapshot.getKey().toString().equals(dataSnapshot.getKey().toString()))
                          {
                              tripNames.remove(index);
                              snapshots.remove(index);


                          }
                          index++;
                      }
                      arrayAdapter.notifyDataSetChanged();


                  }

                  @Override
                  public void onChildMoved(DataSnapshot dataSnapshot, java.lang.String s) {

                  }

                  @Override
                  public void onCancelled(DatabaseError databaseError) {

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



