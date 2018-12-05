package com.example.samee.traveljournal;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;

import com.example.samee.models.Posts;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ViewCityTrips extends AppCompatActivity {
ListView cityLists;
    FirebaseAuth mAuth=FirebaseAuth.getInstance();
    ArrayList<DataSnapshot> snapshots=new ArrayList<DataSnapshot>();
    ArrayList<String> tripNames=new ArrayList<String>();
    FloatingActionButton actionButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_city_trips);
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
                            FirebaseUser firebaseUser=mAuth.getCurrentUser();
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
        FirebaseUser firebaseUser=mAuth.getCurrentUser();
      FirebaseDatabase.getInstance().getReference("posts").child(firebaseUser.getUid())
              .child("City Trips").addChildEventListener(new ChildEventListener() {
                  @Override
                  public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                      if(dataSnapshot.getValue()!=null)
                      {
                          snapshots.add(dataSnapshot);
                          tripNames.add(dataSnapshot.getValue(Posts.class).getTripName());
                          arrayAdapter.notifyDataSetChanged();
                      }

                  }

                  @Override
                  public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
                  public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                  }

                  @Override
                  public void onCancelled(DatabaseError databaseError) {

                  }
              });





    }
}

