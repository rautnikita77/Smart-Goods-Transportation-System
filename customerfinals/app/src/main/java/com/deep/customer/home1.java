package com.deep.customer;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.location.Location;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.GeoApiContext;


public class home1 extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private GeoApiContext mGeoApiContext = null;
    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    private static final int LOCATION_UPDATE_INTERVAL =3000;
    LatLng src;
    LatLng dest;
    int count = 0;
    Double lat,lon;
    String driver_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home1);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseAuth.AuthStateListener firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    Intent intent = new Intent(home1.this, login.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        dispotp();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (getIntent().getExtras() != null) {
            lat = getIntent().getDoubleExtra("latitude", 1.0);
            lon = getIntent().getDoubleExtra("longitude", 1.0);
            Toast.makeText(this, lat.toString(), Toast.LENGTH_SHORT).show();
            driver_id = getIntent().getStringExtra("driver");
            try{
                dest = new LatLng(lat,lon);
                MarkerOptions options = new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                //Toast.makeText(this,dest.toString(), Toast.LENGTH_SHORT).show();
                options.position(dest);
                options.title("Customer");
                mMap.addMarker(options);}
            catch (NullPointerException e){
                //Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }
            //
        }

        driver();



    }

    private void dispotp() {
        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Request");
        ref.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("otp").exists()){
                    TextView otp = (TextView)findViewById(R.id.otpNum);
                    otp.setText(dataSnapshot.child("otp").getValue().toString());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    private void driver() {
        final FirebaseOptions options = new FirebaseOptions.Builder()
                .setApiKey("AIzaSyBHIi_Wx9Dmav8oOqfjb5cGlEn2aYfpymI")
                .setApplicationId("1:1048956569474:android:cdfdcc5fb3577711")
                .setDatabaseUrl("https://driver-445b4.firebaseio.com//") // Required for RTDB.
                .build();
        FirebaseApp.initializeApp(home1.this , options, "sim");

        FirebaseApp app = FirebaseApp.getInstance("sim");
        FirebaseDatabase secondaryDatabase = FirebaseDatabase.getInstance(app);

        secondaryDatabase.getReference().child("driver").child(driver_id).addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double latitude = 0,longitude = 0;

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    mMap.clear();

                    if(ds.getKey().equals("latitude")) {
                        latitude = (double) ds.getValue();
                    }if(ds.getKey().equals("longitude")) {
                        longitude = (double) ds.getValue();
                    }

                    }
                src = new LatLng(latitude, longitude);
                Toast.makeText(home1.this, src.toString(), Toast.LENGTH_SHORT).show();
                MarkerOptions options = new MarkerOptions()
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                options.position(src);
                options.title("Driver");
                mMap.addMarker(options);

                MarkerOptions option = new MarkerOptions()
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                option.title("Customer");
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(src,18));

                option.position(dest);
                option.position(dest);
                mMap.addMarker(option);
                try{
                    String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Request")
                            .child(userid).child("otp");
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(count == 0){if (dataSnapshot.exists()){
                            if(dataSnapshot.getValue().equals("valid")){
                                count++;
                                Intent intent = new Intent(home1.this, home2.class);
                                intent.putExtra("driver",driver_id);
                                finish();
                                startActivity(intent);}
                            }}
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    ;}
                catch (NullPointerException e){

                }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}