package com.deep.customer;

import java.io.IOException;
import java.util.Arrays;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.api.Places;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
//import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
//import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class home extends FragmentActivity implements OnMapReadyCallback {
    String TAG = "placeautocomplete";
    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    private String destination, requestService;
    private LatLng destinationLatLng;
    private Location sourceLatLng;
    double a;
    public ArrayList<LatLng> latlngs = new ArrayList<>();
    FirebaseApp finestayApp = null;
    String notes = "";
    String value = "Type A";


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    Location lastknownlocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    sourceLatLng = lastknownlocation;
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


        // Initialize Places.
        Places.initialize(getApplicationContext(), "AIzaSyD6TjOKVfGpbA1Crq9Jk0grY5ohrhJ-fvs");
        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        // Specify the types of place data to return.
       autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
               // txtView.setText(place.getName() + "," + place.getId());
                Log.i("uuuuuuuuuu", "Place: " + place.getName() + ", " + place.getId());
                destinationLatLng=place.getLatLng();
                doit d=new doit();
                d.execute();

                Log.i("ddddddd",""+destinationLatLng.longitude+""+destinationLatLng.latitude);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("rororororororor", "An error occurred: " + status);
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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
                LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                sourceLatLng = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                Toast.makeText(this, Double.toString(sourceLatLng.getLatitude()), Toast.LENGTH_SHORT).show();

                // we have permission!
//                Toast.makeText(this, sourceLatLng.toString(), Toast.LENGTH_SHORT).show();
                //LatLng userlocation = new LatLng(sourceLatLng.getLatitude(), sourceLatLng.getLongitude());
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userlocation,15));

            }

        }

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseAuth.AuthStateListener firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                if (user == null) {
                    Intent intent = new Intent(home.this, login.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        LocationCallback mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for(Location location : locationResult.getLocations()){
                    if(getApplicationContext()!=null){
                        sourceLatLng= location;
                        Toast.makeText(home.this, "a", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        };

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApiKey("AIzaSyBHIi_Wx9Dmav8oOqfjb5cGlEn2aYfpymI")
                .setApplicationId("1:1048956569474:android:cdfdcc5fb3577711")
                .setDatabaseUrl("https://driver-445b4.firebaseio.com//") // Required for RTDB.
                .build();
        boolean hasBeenInitialized=false;
        List<FirebaseApp> firebaseApps = FirebaseApp.getApps(home.this);
        for(FirebaseApp app : firebaseApps){
            if(app.getName().equals("s")){
                hasBeenInitialized=true;
                finestayApp = app;
            }
        }
        if(!hasBeenInitialized) {
            finestayApp = FirebaseApp.initializeApp(home.this, options,"s");
        }

        //FirebaseDatabase secondaryDatabase = FirebaseDatabase.getInstance(finestayApp);

        //FirebaseApp.initializeApp(home.this , options, "s");

        //app = FirebaseApp.getInstance("s");


        get_driver();

        //Toast.makeText(this, latlngs.size(), Toast.LENGTH_SHORT).show();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.



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
        Button History=(Button)findViewById(R.id.history);
        History.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(home.this, history.class);
                intent.putExtra("customerOrDriver", "customer");
                startActivity(intent);
                return;
            }
        });
        //final TextView mdestination = (TextView) findViewById(R.id.destination);

        // Add marker for destination
while (destinationLatLng!=null)
{doit d=new doit();
d.execute();}


        // Request your ride
        Button mrequest = (Button) findViewById(R.id.request);
        mrequest.setOnClickListener(new View.OnClickListener() {


            //final String destination = mdestination.getText().toString();
            //final RadioGroup mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);

            final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
            // final String type = radioButton.getText().toString();
            //final String notes = mNotes.getText().toString();
            final EditText editText = (EditText) findViewById(R.id.notes);

            @Override
            public void onClick(View v) {

                RadioGroup rg =  findViewById(R.id.radioGroup);
                EditText et = findViewById(R.id.notes);
                notes = et.getText().toString();
                value =
                        ((RadioButton)findViewById(rg.getCheckedRadioButtonId()))
                                .getText().toString();
                String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Request").child(userid);
                //destinationLatLng = new LatLng(19.3,72.8);
                Log.i("fetfdetfdetf",""+destinationLatLng.latitude+""+destinationLatLng.longitude);
                ref.child("Destination").child("latitude").setValue(destinationLatLng.latitude);
                ref.child("Destination").child("longitude").setValue(destinationLatLng.longitude);
                ref.child("Type").setValue(value);
                ref.child("source").child("latitude").setValue(sourceLatLng.getLatitude());
                ref.child("source").child("longitude").setValue(sourceLatLng.getLongitude());
                ref.child("notes").setValue(notes);
               // Random rand = new Random();
                //final int id = rand.nextInt(9000) + 1000;
                //ref.child("otp").setValue(id);


                DatabaseReference href = FirebaseDatabase.getInstance().getReference("customer").child(userid).child("phone");
               /* href.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String pno = dataSnapshot.getValue(String.class);
                        Log.i("phone number","is" + pno);
                        if(pno!=null)
                        {Log.i("sent","message");


//Get the SmsManager instance and call the sendTextMessage method to send message
                            final SmsManager sms=SmsManager.getDefault();
                            sms.sendTextMessage(pno, null, "the otp is"+" "+ id,null,null);





                               /* if (checkSelfPermission(Manifest.permission.SEND_SMS)
                                        == PackageManager.PERMISSION_DENIED) {

                                    Log.d("permission", "permission denied to SEND_SMS - requesting it");
                                    String[] permissions = {Manifest.permission.SEND_SMS};

                                    requestPermissions(permissions, PERMISSION_REQUEST_CODE);
                                    }*/


                      //  }


                        //do what you want with the email
                    //}

                  /*  @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });*/

                //Toast.makeText(home.this, "aa", Toast.LENGTH_SHORT).show();

                getClosestDriver();


            }
        });


    }

    private int radius = 0;
    private Boolean driverFound = false;
    private String driverFoundID;

    GeoQuery geoQuery;
    private void getClosestDriver(){
        DatabaseReference secondaryDatabase = FirebaseDatabase.getInstance(finestayApp).getReference().child("Available_drivers").child(value);

        GeoFire geoFire = new GeoFire(secondaryDatabase);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(sourceLatLng.getLatitude(), sourceLatLng.getLongitude()), radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!driverFound){
                    //Toast.makeText(home.this, "aa", Toast.LENGTH_SHORT).show();
                    System.out.print(radius);
                    driverFound = true;
                    driverFoundID = key;
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Request").child(FirebaseAuth.getInstance().getUid());
                    ref.child("Driver").setValue(key);
                    final FirebaseDatabase secondaryDatabase = FirebaseDatabase.getInstance(finestayApp);

                    secondaryDatabase.getReference().child("driver").child(driverFoundID).child("Active Request").setValue("true");
                    secondaryDatabase.getReference().child("driver").child(driverFoundID).child("Customer").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    Intent intent = new Intent(home.this, home1.class);
                    intent.putExtra("latitude",sourceLatLng.getLatitude());
                    intent.putExtra("longitude",sourceLatLng.getLongitude());
                    intent.putExtra("driver",driverFoundID);
                    intent.putExtra("dest_latitude",destinationLatLng.latitude);
                    intent.putExtra("dest_longitude",destinationLatLng.longitude);

                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (!driverFound)
                {
                    radius++;
                    getClosestDriver();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }


    private void get_driver() {

        final FirebaseDatabase secondaryDatabase = FirebaseDatabase.getInstance(finestayApp);

        secondaryDatabase.getReference().child("Available_drivers").child("Type A").addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMap.clear();
                if(sourceLatLng!=null) {
                    updatemap(sourceLatLng);
                }
                for (DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()) {


                    double latitude = (double) uniqueKeySnapshot.child("l").child("0").getValue();
                    double longitude = (double) uniqueKeySnapshot.child("l").child("1").getValue();

                    LatLng point = new LatLng(latitude, longitude);

                    latlngs.add(point);
                }
                MarkerOptions options = new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                //Toast.makeText(home.this, latlngs.size(), Toast.LENGTH_SHORT).show();
                for (LatLng point : latlngs) {
                    options.position(point);
                        options.title("Type A Driver");

                        mMap.addMarker(options);
                    }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }




        });

        final FirebaseDatabase secondaryDatabase2 = FirebaseDatabase.getInstance(finestayApp);

        secondaryDatabase2.getReference().child("Available_drivers").child("Type C").addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMap.clear();
                if(sourceLatLng!=null) {
                    updatemap(sourceLatLng);
                }
                for (DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()) {


                    double latitude = (double) uniqueKeySnapshot.child("l").child("0").getValue();
                    double longitude = (double) uniqueKeySnapshot.child("l").child("1").getValue();

                    LatLng point = new LatLng(latitude, longitude);

                    latlngs.add(point);
                }
                MarkerOptions options = new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                //Toast.makeText(home.this, latlngs.size(), Toast.LENGTH_SHORT).show();
                for (LatLng point : latlngs) {
                    options.position(point);
                    options.title("Type B Driver");

                    mMap.addMarker(options);
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }




        });
        final FirebaseDatabase secondaryDatabase1 = FirebaseDatabase.getInstance(finestayApp);

        secondaryDatabase1.getReference().child("Available_drivers").child("Type C").addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMap.clear();
                if(sourceLatLng!=null) {
                    updatemap(sourceLatLng);
                }
                for (DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()) {


                    double latitude = (double) uniqueKeySnapshot.child("l").child("0").getValue();
                    double longitude = (double) uniqueKeySnapshot.child("l").child("1").getValue();

                    LatLng point = new LatLng(latitude, longitude);

                    latlngs.add(point);
                }
                MarkerOptions options = new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                //Toast.makeText(home.this, latlngs.size(), Toast.LENGTH_SHORT).show();
                for (LatLng point : latlngs) {
                    options.position(point);
                    options.title("Type C Driver");

                    mMap.addMarker(options);
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }




        });


    }
    public class doit extends AsyncTask<Void,Void,String> {
        String words;
        String price;
        @Override
        protected String doInBackground(Void... params){

            try {

                Document doc= Jsoup.connect("http://www.sify.com/finance/today-diesel-price/").get();
                words=doc.text();
                String[] words1=words.split(" ");
                int i= Arrays.asList(words1).indexOf("Mumbai");
                price=words1[i+1];




            } catch (IOException e) {
                e.printStackTrace();
            }return price;}
        @Override
        protected void onPostExecute(String aVoid){
            //textView.setText("="+price);
            // aVoid=price;
            final double[] slat = new double[1];
            final double[] slon = new double[1];
            final double[] dlat = new double[1];
            final double[] dlon = new double[1];
            final String[] type = new String[1];
            final double[] distance = {15};
            aVoid= aVoid.substring(3);
            a=Double.parseDouble(aVoid);
            final Double[] dist = new Double[1];
            // textView.setText("="+a);

            final double[] mileage={0};
            final double[] basecost = {0};
            String r="Type A";
            RadioGroup rg =  findViewById(R.id.radioGroup);

            String value1 =
                    ((RadioButton)findViewById(rg.getCheckedRadioButtonId()))
                            .getText().toString();

            switch(value1)

            {case "Type A": mileage[0] =15.0;

                basecost[0]=500;

                break;

                case "Type B": mileage[0] =13.0;

                    basecost[0]=680;

                    break;

                case "Type C": mileage[0] =11.0;

                    basecost[0]=900;

                    break;



                default: break;

            }
            Log.i("jjjjjjjjj",""+mileage[0]+" "+basecost[0]);
            float[] results = new float[1];
            if(destinationLatLng!=null)
            Location.distanceBetween(destinationLatLng.latitude,destinationLatLng.longitude,
                    sourceLatLng.getLatitude(),sourceLatLng.getLongitude(),
                    results);
            distance[0]=results[0]/1000;
            Log.i("tttttttttt",""+distance[0]);




            double litres= distance[0]*1.60934 / mileage[0];
            double fuelcost=litres*(double)a;
            double cost=basecost[0]+fuelcost;

            TextView v=(TextView) findViewById(R.id.cost);
            v.setText("Cost of journey"+cost);
            super.onPostExecute(aVoid);} }

    //Set user location
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //sourceLatLng = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updatemap(location);
                sourceLatLng = location;
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

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }


    //Set the marker to location
    private void updatemap(Location location) {

        LatLng userlocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.clear();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userlocation,15));
        mMap.addMarker(new MarkerOptions().position(userlocation).title("Your Location"));
    }


}
