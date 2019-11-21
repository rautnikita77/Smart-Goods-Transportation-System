package com.deep.driver;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class home2 extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Double latitude,longitude;
    final String[] cust_id = new String[1];
    LocationManager locationManager;
    LocationListener locationListener;
    Double a;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
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

        final FirebaseOptions options = new FirebaseOptions.Builder()
                .setApiKey("AIzaSyA1dUvvOoLkQhUhpoLWVyAOJKEICbz6eew")
                .setApplicationId("1:619591266046:android:cdfdcc5fb3577711")
                .setDatabaseUrl("https://customer-fe4b3.firebaseio.com/") // Required for RTDB.
                .build();
        boolean hasBeenInitialized=false;
        Context mContext;
        List<FirebaseApp> firebaseApps = FirebaseApp.getApps(home2.this);
        FirebaseApp finestayApp = null;
        for(FirebaseApp app : firebaseApps){
            if(app.getName().equals("sii")){
                hasBeenInitialized=true;
                finestayApp = app;
            }
        }

        if(!hasBeenInitialized) {
            finestayApp = FirebaseApp.initializeApp(home2.this, options,"sii");
        }
        //FirebaseApp.initializeApp(home2.this, options, "sii");

      //  FirebaseApp app = FirebaseApp.getInstance("sii");
        final FirebaseDatabase secondaryDatabase = FirebaseDatabase.getInstance(finestayApp);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button mEndTrip =(Button) findViewById(R.id.ride);
        mEndTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(cust_id[0]!=null) {
                    final FirebaseOptions options1 = new FirebaseOptions.Builder()
                            .setApiKey("AIzaSyA1dUvvOoLkQhUhpoLWVyAOJKEICbz6eew")
                            .setApplicationId("1:619591266046:android:cdfdcc5fb3577711")
                            .setDatabaseUrl("https://customer-fe4b3.firebaseio.com/") // Required for RTDB.
                            .build();
                    boolean hasBeenInitialized1 = false;
                    // Context mContext;
                    List<FirebaseApp> firebaseApps1 = FirebaseApp.getApps(home2.this);
                    FirebaseApp finestayApp1 = null;
                    for (FirebaseApp app : firebaseApps1) {
                        if (app.getName().equals("siickk")) {
                            hasBeenInitialized1 = true;
                            finestayApp1 = app;
                        }
                    }

                    if (!hasBeenInitialized1) {
                        finestayApp1 = FirebaseApp.initializeApp(home2.this, options1, "siickk");
                    }
                    //FirebaseApp.initializeApp(home2.this, options, "sii");

                    //  FirebaseApp app = FirebaseApp.getInstance("sii");
                    final FirebaseDatabase secondaryDatabase1 = FirebaseDatabase.getInstance(finestayApp1);


                    final String[] latt = new String[1];
                    final String[] lon = new String[1];
                    final ArrayList<String> abc = new ArrayList<String>(2);
                    Log.i("cust", "cis" + cust_id[0]);

                    DatabaseReference cdata = secondaryDatabase.getReference().child("Request").child(cust_id[0]).child("Destination");
                    cdata.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Log.i("inside the loop", "yes  varun");

                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                Log.i("inside the loop", "yes  varun");


                               // Toast.makeText(home2.this, "donedonedone", Toast.LENGTH_LONG).show();

                                if (ds.getKey().equals("latitude")) {
                                   // Toast.makeText(home2.this, "hey1", Toast.LENGTH_SHORT).show();
                                    double a = (double) ds.getValue();
                                    String b = String.valueOf(a);
                                    abc.add(b);
                                }
                                if (ds.getKey().equals("longitude")) {
                                    longitude = (double) ds.getValue();
                                   // Toast.makeText(home2.this, "hey2", Toast.LENGTH_SHORT).show();
                                    abc.add(ds.child("longitude").getValue(String.class));
                                }


                               // Toast.makeText(home2.this, "donedonedone", Toast.LENGTH_LONG).show();
                                Log.i("the dest is ", abc.toString());


                            }
                        }

                        //   DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();


                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

/*
                    String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
                    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd-MM-yyyy");
                    String format = simpleDateFormat.format(new Date());
                    String format1 = simpleDateFormat1.format(new Date());
                    //System.out.println("date : "+ format);
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("driver").child(uid).child("history").child(format).setValue(true);
                mDatabase.child("driver").child(uid).child("history").child(format).child("customer id").setValue(cust_id[0]);
                    mDatabase.child("driver").child(uid).child("history").child(format).child("cost").setValue("2000");
                    mDatabase.child("driver").child(uid).child("history").child(format).child("Date").setValue(format1);
                    secondaryDatabase.getReference().child("customer").child(cust_id[0]).child("history").child(format).setValue(true);
                    secondaryDatabase.getReference().child("customer").child(cust_id[0]).child("history").child(format).child("driver id").setValue(uid);
                   // secondaryDatabase.getReference().child("customer").child(cust_id[0]).child("history").child(format).child("cost").setValue("2000");
                    secondaryDatabase.getReference().child("customer").child(cust_id[0]).child("history").child(format).child("date").setValue(format1);


                    //  mDatabase.child("driver").child(uid).child("history").child(cust_id[0]).child("Destination").child("latitude").setValue(abc.get(0));
                    //mDatabase.child("driver").child(uid).child("history").child(cust_id[0]).child("Destination").child("latitude").setValue(abc.get(1));
                    mDatabase.child("driver").child(uid).child("Active Request").removeValue();
                    mDatabase.child("driver").child(uid).child("Customer").removeValue();*/
                    doit c = new doit();
                    c.execute();


                    //secondaryDatabase.getReference().child("Request").child(cust_id[0]).removeValue();
                }
                    Intent intent = new Intent(home2.this, home3.class);

                    startActivity(intent);
                    finish();



            }
        });


        FirebaseDatabase.getInstance().getReference().child("driver")
                .child(FirebaseAuth.getInstance().getUid()).child("Customer").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
if (dataSnapshot.getValue()!=null)
                cust_id[0] = dataSnapshot.getValue().toString();
                if(cust_id[0]!=null) {;

                    secondaryDatabase.getReference().child("Request").child(cust_id[0]).child("Destination").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                Toast.makeText(home2.this, ds.getKey(), Toast.LENGTH_SHORT).show();
                                if (ds.getKey().equals("latitude")) {
                                    latitude = (Double) ds.getValue();
                                }
                                if (ds.getKey().equals("longitude")) {
                                    longitude = (Double) ds.getValue();
                                }
                                if(latitude!=null && longitude!=null){
                                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
                                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                    mapIntent.setPackage("com.google.android.apps.maps");
                                    startActivity(mapIntent);

                                }
                            }
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
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
                    String userid = FirebaseAuth.getInstance().getUid();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("driver").child(userid);
                    ref.child("latitude").setValue(location.getLatitude());
                    ref.child("longitude").setValue(location.getLongitude());

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

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;}

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
                final FirebaseOptions options = new FirebaseOptions.Builder()
                        .setApiKey("AIzaSyA1dUvvOoLkQhUhpoLWVyAOJKEICbz6eew")
                        .setApplicationId("1:619591266046:android:cdfdcc5fb3577711")
                        .setDatabaseUrl("https://customer-fe4b3.firebaseio.com/") // Required for RTDB.
                        .build();
                boolean hasBeenInitialized=false;
                Context mContext;
                List<FirebaseApp> firebaseApps = FirebaseApp.getApps(home2.this);
                FirebaseApp finestayApp1 = null;
                for(FirebaseApp app : firebaseApps){
                    if(app.getName().equals("sii")){
                        hasBeenInitialized=true;
                        finestayApp1 = app;
                    }
                }

                if(!hasBeenInitialized) {
                    finestayApp1 = FirebaseApp.initializeApp(home2.this, options,"siiw");
                }
                //FirebaseApp.initializeApp(home2.this, options, "sii");

                //  FirebaseApp app = FirebaseApp.getInstance("sii");
                final FirebaseDatabase secondaryDatabase2 = FirebaseDatabase.getInstance(finestayApp1);
                DatabaseReference cdata1=secondaryDatabase2.getReference().child("Request").child(cust_id[0]);
                cdata1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot dc : dataSnapshot.getChildren()) {


                            Log.i("inside the loop", "yes  varun");
                            if (dc.getKey().equals("Destination")) {

                                for (DataSnapshot ds : dc.getChildren()) {
                                    Log.i("inside the loop", "yes  varun");


                                  //  Toast.makeText(home2.this, "donedonedone", Toast.LENGTH_LONG).show();

                                    if (ds.getKey().equals("latitude")) {
                                       // Toast.makeText(home2.this, "hey1", Toast.LENGTH_SHORT).show();
                                        slat[0] = (double) ds.getValue();
                                        //String b= String.valueOf(a);
                                        //abc.add(b);
                                    }
                                    if (ds.getKey().equals("longitude")) {
                                        slon[0] = (double) ds.getValue();
                                        //Toast.makeText(home2.this, "hey2", Toast.LENGTH_SHORT).show();
                                        //abc.add(ds.child("longitude").getValue(String.class))
                                        ;
                                    }
                                }


                               // Toast.makeText(home2.this, "donedonedone", Toast.LENGTH_LONG).show();
                                Log.i("the dest is ", "" + slat[0] + slon[0]);


                            }
                            else if (dc.getKey().equals("source")) {

                                for (DataSnapshot dr : dc.getChildren()) {
                                    Log.i("inside the loop", "yes  varun");


                                   // Toast.makeText(home2.this, "donedonedone", Toast.LENGTH_LONG).show();

                                    if (dr.getKey().equals("latitude")) {
                                       // Toast.makeText(home2.this, "hey1", Toast.LENGTH_SHORT).show();
                                        dlat[0] = (double) dr.getValue();
                                        //String b= String.valueOf(a);
                                        //abc.add(b);
                                    }
                                    if (dr.getKey().equals("longitude")) {
                                        dlon[0] = (double) dr.getValue();
                                      //  Toast.makeText(home2.this, "hey2", Toast.LENGTH_SHORT).show();
                                        //abc.add(ds.child("longitude").getValue(String.class))
                                        ;
                                    }
                                }


                               // Toast.makeText(home2.this, "donedonedone", Toast.LENGTH_LONG).show();
                                Log.i("bbbbbbbbb ", "" + dlat[0] + dlon[0]);


                            }
                          else if(dc.getKey().equals("Type")){
                                type[0] =dc.getValue().toString();}
                        }
                        switch(type[0])

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


                        /*    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                    .permitAll().build();
                            StrictMode.setThreadPolicy(policy);
                            //your codes here
                            StringBuilder stringBuilder = new StringBuilder();
                             //dist[0] = 0.0;
                            try {

                                //  destinationAddress = destinationAddress.replaceAll(" ","%20");
                                Log.i("mmmmmmmmm",slat[0]+slon[0]+dlat[0]+dlon[0]+" ");
                                String url = "http://maps.googleapis.com/maps/api/directions/json?origin=" + dlat[0] + "," + dlon[0] + "&destination=" + slat[0] + "," + slon[0] + "&mode=driving&sensor=false";

                                HttpPost httppost = new HttpPost(url);

                                HttpClient client = new DefaultHttpClient();
                                HttpResponse response;
                                stringBuilder = new StringBuilder();


                                response = client.execute(httppost);
                                HttpEntity entity = response.getEntity();
                                InputStream stream = entity.getContent();
                                int b;
                                while ((b = stream.read()) != -1) {
                                    stringBuilder.append((char) b);
                                }
                            } catch (ClientProtocolException e) {
                            } catch (IOException e) {
                            }

                            JSONObject jsonObject = new JSONObject();
                            try {

                                jsonObject = new JSONObject(stringBuilder.toString());

                                JSONArray array = jsonObject.getJSONArray("routes");

                                JSONObject routes = array.getJSONObject(0);

                                JSONArray legs = routes.getJSONArray("legs");

                                JSONObject steps = legs.getJSONObject(0);

                                JSONObject distance = steps.getJSONObject("distance");

                                Log.i("eeeee", distance.toString());
                                dist[0] = Double.parseDouble(distance.getString("text").replaceAll("[^\\.0123456789]","") );

                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }



                        /*StringBuilder stringBuilder = new StringBuilder();
                        Double dist = 0.0;
                        try {

                            //  destinationAddress = destinationAddress.replaceAll(" ","%20");
                            String url = "http://maps.googleapis.com/maps/api/directions/json?origin=" + dlat + "," + dlon + "&destination=" + slat + "," + slon + "&mode=driving&sensor=false";

                            HttpPost httppost = new HttpPost(url);

                            HttpClient client = new DefaultHttpClient();
                            HttpResponse response;
                            stringBuilder = new StringBuilder();


                            response = client.execute(httppost);
                            HttpEntity entity = response.getEntity();
                            InputStream stream = entity.getContent();
                            int b;
                            while ((b = stream.read()) != -1) {
                                stringBuilder.append((char) b);
                            }
                        } catch (ClientProtocolException e) {
                        } catch (IOException e) {
                        }

                        JSONObject jsonObject = new JSONObject();
                        try {

                            jsonObject = new JSONObject(stringBuilder.toString());

                            JSONArray array = jsonObject.getJSONArray("routes");

                            JSONObject routes = array.getJSONObject(0);

                            JSONArray legs = routes.getJSONArray("legs");

                            JSONObject steps = legs.getJSONObject(0);

                            JSONObject distance = steps.getJSONObject("distance");

                            Log.i("Distance", distance.toString());
                            dist = Double.parseDouble(distance.getString("text").replaceAll("[^\\.0123456789]","") );

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }*/



                        //distance[0]= dist[0];
                        float[] results = new float[1];
                        Location.distanceBetween(dlat[0],dlon[0],
                                slat[0], slon[0],
                                results);
                        distance[0]=results[0]/1000;
                        Log.i("tttttttttt",""+distance[0]);




                        double litres= distance[0]*1.60934 / mileage[0];
                        double fuelcost=litres*(double)a;
                        double cost=basecost[0]+fuelcost;
                        Log.i("costcostcost","is"+cost);
                        String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
                        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd-MM-yyyy");
                        String format = simpleDateFormat.format(new Date());
                        String format1 = simpleDateFormat1.format(new Date());
                        //System.out.println("date : "+ format);
                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                       // DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                        mDatabase.child("driver").child(uid).child("history").child(format).setValue(true);
                        mDatabase.child("driver").child(uid).child("history").child(format).child("customer id").setValue(cust_id[0]);
                        mDatabase.child("driver").child(uid).child("history").child(format).child("cost").setValue(cost);
                        mDatabase.child("driver").child(uid).child("history").child(format).child("Date").setValue(format1);
                        mDatabase.child("driver").child(uid).child("history").child(format).child("Source").child("latitude").setValue(dlat[0]);
                        mDatabase.child("driver").child(uid).child("history").child(format).child("Source").child("longitude").setValue(dlon[0]);
                        mDatabase.child("driver").child(uid).child("history").child(format).child("Destination").child("latitude").setValue(slat[0]);
                        mDatabase.child("driver").child(uid).child("history").child(format).child("Destination").child("longitude").setValue(slon[0]);



                        secondaryDatabase2.getReference().child("customer").child(cust_id[0]).child("history").child(format).setValue(true);
                        secondaryDatabase2.getReference().child("customer").child(cust_id[0]).child("history").child(format).child("driver id").setValue(uid);
                        secondaryDatabase2.getReference().child("customer").child(cust_id[0]).child("history").child(format).child("cost").setValue(cost);
                        secondaryDatabase2.getReference().child("customer").child(cust_id[0]).child("history").child(format).child("date").setValue(format1);
                        secondaryDatabase2.getReference().child("customer").child(cust_id[0]).child("history").child(format).child("Source").child("latitude").setValue(dlat[0]);
                        secondaryDatabase2.getReference().child("customer").child(cust_id[0]).child("history").child(format).child("Source").child("longitude").setValue(dlon[0]);
                        secondaryDatabase2.getReference().child("customer").child(cust_id[0]).child("history").child(format).child("Destination").child("latitude").setValue(slat[0]);
                        secondaryDatabase2.getReference().child("customer").child(cust_id[0]).child("history").child(format).child("Destination").child("longitude").setValue(slon[0]);

                        secondaryDatabase2.getReference().child("Request").child(cust_id[0]).removeValue();
                        mDatabase.child("driver").child(uid).child("Active Request").removeValue();
                        mDatabase.child("driver").child(uid).child("Customer").removeValue();







                    }

                    //   DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();


                    @Override
                    public void onCancelled (DatabaseError databaseError){
                    }
                });








                super.onPostExecute(aVoid);} }



    }

