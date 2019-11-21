package com.deep.customer;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
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
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;

import java.util.ArrayList;
import java.util.List;

public class home2 extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    Double s_lat,s_lon,d_lat,d_lon;
    private String TAG = "so47492459";
    private String driver_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);
        FirebaseAuth.AuthStateListener firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    Intent intent = new Intent(home2.this, login.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Button mEndTrip =(Button) findViewById(R.id.button);
        mEndTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(home2.this, home3.class);

                startActivity(intent);
                finish();
            }
        });

        if (getIntent().getExtras() != null) {
            driver_id = getIntent().getStringExtra("driver");
        }
        Button mLogout = (Button) findViewById(R.id.logout);
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(home2.this, login.class);
                startActivity(intent);
                finish();
                return;
            }
        });
        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Request").child(userid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot d:dataSnapshot.getChildren()){
                    if(d.getKey().equals("Destination")){
                        for (DataSnapshot ds:d.getChildren()){
                            if(ds.getKey().equals("latitude")){
                                d_lat = (Double) ds.getValue();
                            }
                            if(ds.getKey().equals("longitude")){
                                d_lon = (Double) ds.getValue();
                            }
                        }
                        ///Copy after this line
                        final FirebaseOptions options = new FirebaseOptions.Builder()
                                .setApiKey("AIzaSyBHIi_Wx9Dmav8oOqfjb5cGlEn2aYfpymI")
                                .setApplicationId("1:1048956569474:android:cdfdcc5fb3577711")
                                .setDatabaseUrl("https://driver-445b4.firebaseio.com//") // Required for RTDB.
                                .build();
                        //FirebaseApp.initializeApp(home2.this , options, "sis")
                        FirebaseApp finestayApp = null;
                        boolean hasBeenInitialized=false;
                        List<FirebaseApp> firebaseApps = FirebaseApp.getApps(home2.this);
                        for(FirebaseApp app : firebaseApps){
                            if(app.getName().equals("sis")){
                                hasBeenInitialized=true;
                                finestayApp = app;
                            }
                        }
                        if(!hasBeenInitialized) {
                            finestayApp = FirebaseApp.initializeApp(home2.this, options,"sis");
                        }

                        FirebaseDatabase secondaryDatabase = FirebaseDatabase.getInstance(finestayApp);

                        secondaryDatabase.getReference().child("driver").child(driver_id).addValueEventListener(new ValueEventListener(){
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {


                                for(DataSnapshot dataSnapshot2:dataSnapshot1.getChildren()){

                                    if(dataSnapshot2.getKey().equals("latitude")){
                                        s_lat = (Double) dataSnapshot2.getValue();
                                    }
                                    if(dataSnapshot2.getKey().equals("longitude")){
                                        s_lon = (Double) dataSnapshot2.getValue();
                                    }

                                }
                            poly();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });



                        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home, 15));

                        Uri gmmIntentUri = Uri.parse("google.navigation:q="+d_lat+","+d_lon);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);

                    }

                }

            }

            private void poly() {
                final List<LatLng> path = new ArrayList();
                mMap.clear();
                if(s_lat!=0 && s_lon!=0){
                    GeoApiContext context = new GeoApiContext.Builder()
                            .apiKey("AIzaSyD6TjOKVfGpbA1Crq9Jk0grY5ohrhJ-fvs")
                            .build();
                    String destination = '"'+String.valueOf(d_lat)+","+String.valueOf(d_lon)+'"';
                    String source = '"'+String.valueOf(s_lat)+","+String.valueOf(s_lon)+'"';
                    mMap.addMarker(new MarkerOptions().position(new LatLng(s_lat,s_lon)).title("Source"));
                    mMap.addMarker(new MarkerOptions().position(new LatLng(d_lat,d_lon)).title("Destination"));

                    Toast.makeText(home2.this, source+destination, Toast.LENGTH_SHORT).show();
                    DirectionsApiRequest req = DirectionsApi.getDirections(context, source,destination);
                    try {
                        DirectionsResult res = req.await();

                        //Loop through legs and steps to get encoded polylines of each step
                        if (res.routes != null && res.routes.length > 0) {
                            DirectionsRoute route = res.routes[0];

                            if (route.legs !=null) {
                                for(int i=0; i<route.legs.length; i++) {
                                    DirectionsLeg leg = route.legs[i];
                                    if (leg.steps != null) {
                                        for (int j=0; j<leg.steps.length;j++){
                                            DirectionsStep step = leg.steps[j];
                                            if (step.steps != null && step.steps.length >0) {
                                                for (int k=0; k<step.steps.length;k++){
                                                    DirectionsStep step1 = step.steps[k];
                                                    EncodedPolyline points1 = step1.polyline;
                                                    if (points1 != null) {
                                                        //Decode polyline and add points to list of route coordinates
                                                        List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
                                                        for (com.google.maps.model.LatLng coord1 : coords1) {
                                                            path.add(new LatLng(coord1.lat, coord1.lng));
                                                        }
                                                    }
                                                }
                                            } else {
                                                EncodedPolyline points = step.polyline;
                                                if (points != null) {
                                                    //Decode polyline and add points to list of route coordinates
                                                    List<com.google.maps.model.LatLng> coords = points.decodePath();
                                                    for (com.google.maps.model.LatLng coord : coords) {
                                                        path.add(new LatLng(coord.lat, coord.lng));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch(Exception ex) {
                        Toast.makeText(home2.this, ex.toString(), Toast.LENGTH_LONG).show();
                    }

                    //Draw the polyline
                    if (path.size() > 0) {
                        PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLUE).width(5);
                        mMap.addPolyline(opts);
                    }

                    mMap.getUiSettings().setZoomControlsEnabled(true);
                    CameraUpdate center=
                            CameraUpdateFactory.newLatLng(new LatLng(s_lat,s_lon));
                    CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);

                    mMap.moveCamera(center);
                    mMap.animateCamera(zoom);
                }
                else{
                    //Toast.makeText(home2.this, s_lat.toString()+s_lon.toString(), Toast.LENGTH_SHORT).show();
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

}
