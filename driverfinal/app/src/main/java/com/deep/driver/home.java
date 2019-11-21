package com.deep.driver;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
public class home extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    int count = 0;

    String type;
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    Location lastknownlocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    updatemap(lastknownlocation);

                    return;
                }

            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseAuth.AuthStateListener firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    Intent intent = new Intent(home.this, login.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        final String userid = FirebaseAuth.getInstance().getUid();

        DatabaseReference re = FirebaseDatabase.getInstance().getReference("driver").child(userid).child("car");
        re.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getKey().equals("car")) {
                    type = dataSnapshot.getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // Logout button
        Button mLogout = (Button) findViewById(R.id.logout);
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(home.this, login.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        //Edit button
        Button mEditProfile = (Button) findViewById(R.id.editProfile);
        mEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(home.this, EditProfileActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
       Button History=(Button)findViewById(R.id.history);
        History.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(home.this, history.class);
                intent.putExtra("customerOrDriver", "Drivers");
                startActivity(intent);
                return;
            }
        });

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("driver").child(userid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    if(ds.getKey().equals("Active Request")){
                        Toast.makeText(home.this, "Ride Started", Toast.LENGTH_SHORT).show();
                    count++;
                        Intent intent = new Intent(home.this, home1.class);

                    DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Available_drivers").child(type);
                    ref1.child(userid).removeValue();
                        if(count == 1){
                            startActivity(intent);
                            finish();
                        }
                    }
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

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
                    updatemap(location);
                    String userid = FirebaseAuth.getInstance().getUid();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("driver").child(userid);
                    ref.child("latitude").setValue(location.getLatitude());
                    ref.child("longitude").setValue(location.getLongitude());
                    ref = FirebaseDatabase.getInstance().getReference("Available_drivers");
                    ref.child(type).setValue("true");
                    ref = FirebaseDatabase.getInstance().getReference("Available_drivers").child(type);
                    GeoFire geoFire = new GeoFire(ref);
                    geoFire.setLocation(userid, new GeoLocation(location.getLatitude(), location.getLongitude()));
//                    ref.child(userid).child("type").setValue(type);

                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        //Check Permission
        if (Build.VERSION.SDK_INT < 23) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        } else {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                // ask for permission

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);


            } else {

                // we have permission!

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location lastknownlocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (lastknownlocation != null) {
                    updatemap(lastknownlocation);
                }
            }

        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }


    //Set the marker to location
    private void updatemap(final Location location) {
        final String userid = FirebaseAuth.getInstance().getUid();

        DatabaseReference re = FirebaseDatabase.getInstance().getReference("driver").child(userid).child("car");
        re.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getKey().equals("car")) {
                    type = dataSnapshot.getValue().toString();
                    LatLng userlocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.clear();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userlocation, 15));
                    mMap.addMarker(new MarkerOptions().position(userlocation).title("Your Location"));
                           // .flat(true).anchor(0.5f,0.5f).rotation(90.0f).icon(BitmapDescriptorFactory.fromResource(R.drawable.truck)));
                    //String userid = FirebaseAuth.getInstance().getUid();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("driver").child(userid);
                    ref.child("latitude").setValue(location.getLatitude());
                    ref.child("longitude").setValue(location.getLongitude());
                    ref = FirebaseDatabase.getInstance().getReference("Available_drivers");
                    ref.child(type).setValue("true");
                    ref = FirebaseDatabase.getInstance().getReference("Available_drivers").child(type);
                    GeoFire geoFire = new GeoFire(ref);
                    geoFire.setLocation(userid,new GeoLocation(location.getLatitude(), location.getLongitude()));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


 //       ref.child(userid).child("type").setValue(type);

    }
}