package com.deep.owner;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class home extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public ArrayList<LatLng> latlngs = new ArrayList<>();
    private  Button btnViewDatabase;
    private  Button owner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //btnViewDatabase = (Button) findViewById(R.id.view_items_screen);
      //  owner = (Button)  findViewById(R.id.owner_profile);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Button mLogout = (Button) findViewById(R.id.logout);
        FirebaseOptions options = new FirebaseOptions.Builder()    //access drivers database
                .setApiKey("AIzaSyBHIi_Wx9Dmav8oOqfjb5cGlEn2aYfpymI")
                .setApplicationId("1:1048956569474:android:cdfdcc5fb3577711")
                .setDatabaseUrl("https://driver-445b4.firebaseio.com/") // Required for RTDB.
                .build();
        boolean hasBeenInitialized=false;
        Context mContext;
        List<FirebaseApp> firebaseApps = FirebaseApp.getApps(home.this);
        FirebaseApp finestayApp = null;
        for(FirebaseApp app : firebaseApps){
            if(app.getName().equals("s")){
                hasBeenInitialized=true;
                finestayApp = app;
            }
        }

        if(!hasBeenInitialized) {
            finestayApp = FirebaseApp.initializeApp(home.this, options,"s");
        }
        //FirebaseApp.initializeApp(home.this , options, "s");

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


        FirebaseApp app = FirebaseApp.getInstance("s");
        final FirebaseDatabase secondaryDatabase = FirebaseDatabase.getInstance(app);

        secondaryDatabase.getReference().child("driver").addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               mMap.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()) {     // iterate through all drivers

                    for(DataSnapshot ds :d.getChildren()){          // iterate through all info about driver
                        if (ds.getValue().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){  //see if any driver present for this owner and add in owners database
                            FirebaseDatabase.getInstance().getReference()
                                    .child("owner").child(FirebaseAuth.getInstance().getUid())
                                    .child("driver").child(d.getKey()).setValue("true");
                        }
                    }
                }

                addMarker();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }




        });

        /*owner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(home.this, database.class);
                startActivity(intent);


            }
        });*/



    }

    private void addMarker() {

        FirebaseDatabase.getInstance().getReference().child("owner")   // iterate through all drivers of this owner
            .child(FirebaseAuth.getInstance().getUid()).child("driver").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                //mMap.clear();
                for (DataSnapshot d:dataSnapshot.getChildren()) {    //get all the drivers uid
                    FirebaseApp app = FirebaseApp.getInstance("s");
                    final FirebaseDatabase secondaryDatabase = FirebaseDatabase.getInstance(app);
                    //mMap.clear();
                    secondaryDatabase.getReference().child("driver").child(d.getKey()).addValueEventListener(new ValueEventListener() {
                                        //iterate through drivers database


                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshots) {//get drivers position
                            //mMap.clear();
                            double latitude = (double) dataSnapshots.child("latitude").getValue();
                            double longitude = (double) dataSnapshots.child("longitude").getValue();
                            String driver = (String) dataSnapshots.child("f_name").getValue() +" "+ (String) dataSnapshots.child("l_name").getValue();
                            String email = (String) dataSnapshots.child("email").getValue();
                           // Driver_info u = new Driver_info();
                             //u.setMail(dataSnapshots.child("email").getValue(Driver_info.class).getMail());

                            LatLng point = new LatLng(latitude, longitude);
                            latlngs.add(point);
                            //Toast.makeText(home.this, mValueString, Toast.LENGTH_SHORT).show();
                            MarkerOptions options = new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                            options.position(point);
                            options.title(driver).snippet(email);
                            String id = d.getKey();
                            mMap.addMarker(options).setTag(id);





                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }




                    });

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
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent i = new Intent(home.this,history.class);
                i.putExtra("title",marker.getTag().toString());
                startActivity(i);
            }
        });
    }
}
