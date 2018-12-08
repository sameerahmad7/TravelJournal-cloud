package com.example.samee.traveljournal;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.samee.models.PlaceInfo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private Boolean mLocationPermissionGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFused;
    private AutoCompleteTextView mText;
    private ImageView mGPS;
    private PlaceAutocompleteAdapter mAdapter;
    private GoogleApiClient mGoogleApiClient;
    Location currentLocation;
    String loc;

    int count = 0;
    Marker marker;
    private PlaceInfo mPlace;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));





    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mText = (AutoCompleteTextView) findViewById(R.id.input_search);
        mGPS = (ImageView) findViewById(R.id.ic_gps);
        getLocationPermission();

    }
    public void Select(View view)
    {

        if(mPlace!=null) {
            Log.d(TAG,"Location Selected");
            Intent i = getIntent();
            i.putExtra("Location", mPlace.getName());
            this.setResult(123, i);
            super.finish();
        }
        else if(mText.getText()!=null)
        {
            Intent i = getIntent();
            i.putExtra("Location", mText.getText().toString());
            this.setResult(123, i);
            super.finish();


        }
        else {
            Toast.makeText(this,"Enter a location!",Toast.LENGTH_LONG).show();
        }
    }

    private void init() {
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, LAT_LNG_BOUNDS, null);
        mText.setOnItemClickListener(mAutolistener);
        mText.setAdapter(mAdapter);

        mText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENTER
                        ) {
                    geoLocate();

                }
                return false;
            }
        });
        HideSoftKeyboard();
        mGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
getDeviceLocation();
            }
        });

    }

    private void geoLocate() {
        String searchString = mText.getText().toString();
        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);

        } catch (IOException e) {
            Log.e(TAG, "geolocate: IOException" + e.getMessage());
        }
        if (list.size() > 0) {
            Address address = list.get(0);

            Log.d(TAG, address.toString());
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), 15f, address.getAddressLine(0));
        }
    }

    private void getDeviceLocation() {
        Log.d(TAG, "Get Device Location");

        mFused = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
            try{
                if(mLocationPermissionGranted){

                    final Task location = mFused.getLastLocation();
                    location.addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful()) {
                                Log.d(TAG, "onComplete: found location!");
                                Location currentLocation = (Location) task.getResult();

                                    moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                            15f,
                                            "My Location");
                                }

                            else{
                                Log.d(TAG, "onComplete: current location is null");
                                Toast.makeText(MapsActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }catch (SecurityException e){
                Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
            }

        }




    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
               strAdd=returnedAddress.getAddressLine(0);
                Log.d("Current loction address", strAdd);
            } else {
                Log.d("Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Current loction address", "Canont get Address!");
        }
        return strAdd;
    }

    private void moveCamera(LatLng latLng, float zoom,String title) {
        Log.d(TAG, "moving camera to lat: " + latLng.latitude + " long: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        if(title.equals("My Location"))
        {

            mText.setText(getCompleteAddressString(latLng.latitude,latLng.longitude));
            Log.d(TAG,getCompleteAddressString(latLng.latitude,latLng.longitude));
        }

            MarkerOptions markerOptions=new MarkerOptions().position(latLng).title(title);
            if(marker!=null)
            {
                marker.remove();
            }
            marker=mMap.addMarker(markerOptions);

        HideSoftKeyboard();
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Toast.makeText(getApplicationContext(), "Map is ready", Toast.LENGTH_LONG).show();
                mMap = googleMap;
                if (mLocationPermissionGranted) {
                    if(getIntent().hasExtra("location"))
                    {
                        mText.setText(getIntent().getStringExtra("location"));
                        geoLocate();
                    }
                    else
                    getDeviceLocation();
                    if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    mMap.setMyLocationEnabled(true);
                    mMap.getUiSettings().setMyLocationButtonEnabled(false); //disables my location button
                    init();
                }

            }
        });
    }

    private void getLocationPermission()
    {
        String[] Permissions={Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
        {
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED)
            {

                mLocationPermissionGranted=true;
                initMap();

            }
            else {
                ActivityCompat.requestPermissions(this,Permissions,LOCATION_PERMISSION_REQUEST_CODE);
            }

        }
        else {
            ActivityCompat.requestPermissions(this,Permissions,LOCATION_PERMISSION_REQUEST_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted=false;
        switch (requestCode)
        {
            case LOCATION_PERMISSION_REQUEST_CODE:
            {
                if(grantResults.length>0)
                {
                    for (int i=0;i<grantResults.length;i++) {
                        if(grantResults[i]!=PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGranted=true;
                    initMap();
                }
            }
        }
    }
    private void HideSoftKeyboard()
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mText.getWindowToken(), 0);
    }


    private AdapterView.OnItemClickListener mAutolistener =new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            HideSoftKeyboard();
            final AutocompletePrediction item=mAdapter.getItem(position);
            final String placeId=item.getPlaceId();
            PendingResult<PlaceBuffer> placeResult=Places.GeoDataApi.getPlaceById(mGoogleApiClient,placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback=new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if(!places.getStatus().isSuccess())
            {
                Log.d(TAG,"Place query not completed successfully");
                places.release();
                return;
            }
            final Place place=places.get(0);
            try {
                mPlace=new PlaceInfo();
                mPlace.setName(place.getName().toString());
                mPlace.setAddress(place.getAddress().toString());
                mPlace.setPhoneNumber(place.getPhoneNumber().toString());
                mPlace.setId(place.getId().toString());
                mPlace.setLatLng(place.getLatLng());
                mPlace.setRating(place.getRating());
                mPlace.setUri(place.getWebsiteUri());

                Log.d(TAG,"OnResult: Place Details: "+mPlace.toString());


            }
            catch (NullPointerException e)
            {
                Log.e(TAG,"Null Pointer Exception"+e.getMessage());

            }
            moveCamera(new LatLng(place.getViewport().getCenter().latitude,place.getViewport().getCenter().longitude),
                    15f,mPlace.getName());
            places.release();




        }
    };


}
