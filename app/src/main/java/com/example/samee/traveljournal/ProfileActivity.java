package com.example.samee.traveljournal;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.samee.models.User;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileActivity extends AppCompatActivity {
FirebaseAuth mAuth;
EditText dob;
Spinner spinner;
EditText nameText,cityText,noText;
ImageView imageView;
DatabaseReference databaseUser;
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
private DatePickerDialog.OnDateSetListener mDateSetListener;
    private static final int CHOOSE_IMAGE = 101;
    Uri uriProfileImage;
    ProgressBar progressBar;
    String countryName;
    String profileImageUrl;
    Button saveBtn;
    ArrayList<String> countries;
ImageView profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth=FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null)
                {
                    startActivity(new Intent(ProfileActivity.this,MainActivity.class));
                    finish();
                }
            }
        });
        setContentView(R.layout.activity_profile);
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
                  dl.closeDrawers();


                }
                else if(id==R.id.cityTrips)
                {
                    startActivity(new Intent(ProfileActivity.this,ViewCityTrips.class));


                }
                else if(id==R.id.locations)
                {
                    startActivity(new Intent(ProfileActivity.this,ViewLocations.class));
                }

                else if(id==R.id.outCityTrips)
                {
                    startActivity(new Intent(ProfileActivity.this,ViewOutCTrips.class));


                }
                else if(id==R.id.stateTrips)
                {
                    startActivity(new Intent(ProfileActivity.this,ViewOutSTrips.class));


                }
                return true;




            }
        });

        saveBtn=(Button)findViewById(R.id.updateProfile);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInformation();
            }
        });
        databaseUser= FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid());
        spinner=(Spinner)findViewById(R.id.countrySpinner);
        imageView=(ImageView)findViewById(R.id.camera);
        nameText=(EditText)findViewById(R.id.nameText);
        cityText=(EditText)findViewById(R.id.cityName);
        noText=(EditText)findViewById(R.id.phoneNumber);
        progressBar=(ProgressBar)findViewById(R.id.progressBar3);
        Locale[] locale = Locale.getAvailableLocales();
        countries = new ArrayList<String>();
        String country;
        for( Locale loc : locale ){
            country = loc.getDisplayCountry();
            if( country.length() > 0 && !countries.contains(country) ){
                countries.add( country );
            }
        }
        Collections.sort(countries, String.CASE_INSENSITIVE_ORDER);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, countries);
        spinner.setAdapter(adapter);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                countryName=countries.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        dob=(EditText)findViewById(R.id.dob);
        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        ProfileActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }


        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d("ProfileActivity", "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                String date = month + "/" + day + "/" + year;
                dob.setText(date);
            }
        };
        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        loadUserInformation();

    }
    private void showData(DataSnapshot snapshot)
    {

if(snapshot.getValue()!=null) {
    nameText.setText(snapshot.getValue(User.class).getName());
    cityText.setText(snapshot.getValue(User.class).getCity());
    dob.setText(snapshot.getValue(User.class).getDob());
    for (String country : countries) {
        if (country.equals(snapshot.getValue(User.class).getCountry())) {
            spinner.setSelection(countries.indexOf(country));
        }
    }
    noText.setText(snapshot.getValue(User.class).getNumber());


}
    }
    private void loadUserInformation() {
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            if (user.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(user.getPhotoUrl().toString())
                        .into(imageView);

            }

            if (user.getDisplayName() != null) {
                nameText.setText(user.getDisplayName());
            }



                    }

            }

    private void uploadImageToFirebaseStorage() {
        StorageReference profileImageRef =
                FirebaseStorage.getInstance().getReference("profilepics/"+mAuth.getCurrentUser().getUid()+"/"+System.currentTimeMillis() + ".jpg");


        if (uriProfileImage != null) {
            progressBar.setVisibility(View.VISIBLE);
            profileImageRef.putFile(uriProfileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressBar.setVisibility(View.GONE);
                            profileImageUrl = taskSnapshot.getDownloadUrl().toString();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
public void saveUserInformation()
{
    String name=nameText.getText().toString();
    String dateob=dob.getText().toString();
    String city=cityText.getText().toString();
    String number=noText.getText().toString();
    if(TextUtils.isEmpty(name)&&TextUtils.isEmpty(dateob)&&TextUtils.isEmpty(city) && TextUtils.isEmpty(countryName) && TextUtils.isEmpty(number))
    {

        Toast.makeText(getApplicationContext(),"All data fields must be filled",Toast.LENGTH_SHORT).show();
    }
    else
    {
        FirebaseUser firebaseUser=mAuth.getCurrentUser();
        if(firebaseUser!=null)
        {
            if (firebaseUser != null && profileImageUrl != null) {
                UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .setPhotoUri(Uri.parse(profileImageUrl))
                        .build();


                firebaseUser.updateProfile(profile)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }
        User user=new User(name,city,dateob,countryName,number);
        databaseUser.setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(),"Saved",Toast.LENGTH_LONG).show();
            }
        });

    }
}


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                imageView.setImageBitmap(bitmap);

                uploadImageToFirebaseStorage();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        }

    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), CHOOSE_IMAGE);
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
