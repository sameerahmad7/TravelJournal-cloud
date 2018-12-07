package com.example.samee.traveljournal;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.samee.models.Posts;
import com.example.samee.models.Ratings;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class CreatePostActivity extends AppCompatActivity {
EditText tripText;
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    ImageView profilePic;

TextView locationText;
Spinner tripTypeText;
    FirebaseAuth mAuth;
java.lang.String tripName;
java.lang.String location;
java.lang.String tripType;
RatingBar ratingBar;
    ProgressBar progressBar;
ArrayList<java.lang.String> uris;
Button saveBtn;
ArrayList<java.lang.String> types;
    private StorageReference mStorage;

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int RESULT_LOAD_MAP = 2;

    private ImageButton mSelectBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
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
                    startActivity(new Intent(CreatePostActivity.this,ProfileActivity.class));


                }
                else if(id==R.id.cityTrips)
                {
                    startActivity(new Intent(CreatePostActivity.this,ViewCityTrips.class));


                }
                else if(id==R.id.outCityTrips)
                {
                    startActivity(new Intent(CreatePostActivity.this,ViewOutCTrips.class));

                }
                else if(id==R.id.stateTrips)
                {
                    startActivity(new Intent(CreatePostActivity.this,ViewOutSTrips.class));

                }
                return true;




            }
        });


        tripTypeText=(Spinner)findViewById(R.id.tripType);
        progressBar=(ProgressBar)findViewById(R.id.imageBar);
ratingBar=(RatingBar)findViewById(R.id.locRat);
        mAuth=FirebaseAuth.getInstance();
        saveBtn=(Button)findViewById(R.id.saveTrip);
        uris=new ArrayList<java.lang.String>();

        mStorage = FirebaseStorage.getInstance().getReference();
        types = new ArrayList<java.lang.String>();
        types.add("City Trip");
        types.add("Out of City Trip");
        types.add("Out of State Trip");
        ArrayAdapter<java.lang.String> adapter = new ArrayAdapter<java.lang.String>(this,android.R.layout.simple_spinner_item, types);
        tripTypeText.setAdapter(adapter);


        tripText=(EditText)findViewById(R.id.tripName);
        locationText=(TextView)findViewById(R.id.location);
        locationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreatePostActivity.this, MapsActivity.class);
                startActivityForResult(intent,RESULT_LOAD_MAP);

            }
        });
        mSelectBtn = (ImageButton) findViewById(R.id.upload_btn);
        mSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                photoPickerIntent.setType("*/*");
                photoPickerIntent.putExtra(Intent.EXTRA_MIME_TYPES, new java.lang.String[] {"image/*", "video/*"});
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMAGE);
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                java.lang.String tripName=tripText.getText().toString();
                java.lang.String location=locationText.getText().toString();
                java.lang.String type=tripTypeText.getSelectedItem().toString();
                if(TextUtils.isEmpty(tripName)&&TextUtils.isEmpty(location)&&TextUtils.isEmpty(type)&&uris.size()<1)
                {
                    Toast.makeText(getApplicationContext(),"All data fields must be filled",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    FirebaseUser firebaseUser=mAuth.getCurrentUser();
                    DatabaseReference database = null;

                    Posts post=new Posts(tripName,type,location,uris);
                    Log.d("URIs",post.getUris().get(0));
                    if(post.getTripType().equals("City Trip"))
                    {
                        database=FirebaseDatabase.getInstance().getReference("posts").child(firebaseUser.getUid()).child("City Trips").child(post.getTripName());
                    }
                    else if(post.getTripType().equals("Out of City Trip"))
                    {
                        database=FirebaseDatabase.getInstance().getReference("posts").child(firebaseUser.getUid()).child("Out of City Trips").child(post.getTripName());
                    }
                    else if(post.getTripType().equals("Out of State Trip"))
                    {
                        database=FirebaseDatabase.getInstance().getReference("posts").child(firebaseUser.getUid()).child("Out of State Trips").child(post.getTripName());
                    }

                    database.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                       Toast.makeText(getApplicationContext(),"Success!",Toast.LENGTH_SHORT).show();
                        }
                    });
                    String rating= java.lang.String.valueOf(ratingBar.getRating());
                    String name=firebaseUser.getDisplayName();
                    Ratings ratings=new Ratings(name,rating);
                    DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("locations");
                    databaseReference.child(post.getLocation()).push().setValue(ratings);

                }

            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {

                 if(data.getData()!=null)
            {

                Toast.makeText(getApplicationContext(),"Selected Single File",Toast.LENGTH_SHORT).show();
                Uri fileUri=data.getData();
                java.lang.String fileName = getFileName(fileUri);
                
                final StorageReference fileToUpload = mStorage.child("Images").child(fileName);
                progressBar.setVisibility(View.VISIBLE);
                fileToUpload.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileToUpload.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Toast.makeText(getApplicationContext(),"Image Uploaded",Toast.LENGTH_SHORT).show();
                                    Log.d("Image URL",uri.toString());
                                    progressBar.setVisibility(View.GONE);
                                    uris.add(uri.toString());
                                }
                            });


                        }
                    });
            }

        }
        else if(requestCode==RESULT_LOAD_MAP)
        {
            if(resultCode==123)
            {
                java.lang.String place=data.getExtras().getString("Location");
                Log.d("Location",place);
                locationText.setText(place);
            }

        }
    }
    public java.lang.String getFileName(Uri uri) {
        java.lang.String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
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
