package com.example.samee.traveljournal;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.samee.models.Posts;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class CreatePostActivity extends AppCompatActivity {
EditText tripText;
TextView locationText;
Spinner tripTypeText;
    FirebaseAuth mAuth;
String tripName;
String location;
String tripType;
    ProgressBar progressBar;
ArrayList<String> uris;
Button saveBtn;
ArrayList<String> types;
    private StorageReference mStorage;

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int RESULT_LOAD_MAP = 2;

    private ImageButton mSelectBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        tripTypeText=(Spinner)findViewById(R.id.tripType);
        progressBar=(ProgressBar)findViewById(R.id.imageBar);

        mAuth=FirebaseAuth.getInstance();
        saveBtn=(Button)findViewById(R.id.saveTrip);
        uris=new ArrayList<String>();

        mStorage = FirebaseStorage.getInstance().getReference();
        types = new ArrayList<String>();
        types.add("City Trip");
        types.add("Out of City Trip");
        types.add("Out of State Trip");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, types);
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
                photoPickerIntent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {"image/*", "video/*"});
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMAGE);
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tripName=tripText.getText().toString();
                String location=locationText.getText().toString();
                String type=tripTypeText.getSelectedItem().toString();
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
                    DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("locations");
                    databaseReference.setValue(post.getLocation());

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
                String fileName = getFileName(fileUri);
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
                String place=data.getExtras().getString("Location");
                Log.d("Location",place);
                locationText.setText(place);
            }

        }
    }
    public String getFileName(Uri uri) {
        String result = null;
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

}
