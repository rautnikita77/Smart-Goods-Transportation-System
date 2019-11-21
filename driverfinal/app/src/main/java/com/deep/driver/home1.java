package com.deep.driver;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class home1 extends FragmentActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    Double latitude, longitude;
    EditText[] otpETs = new EditText[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home1);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        final String[] cust_id = new String[1];


        FirebaseDatabase.getInstance().getReference().child("driver")
                .child(FirebaseAuth.getInstance().getUid()).child("Customer").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final FirebaseOptions options = new FirebaseOptions.Builder()
                        .setApiKey("AIzaSyA1dUvvOoLkQhUhpoLWVyAOJKEICbz6eew")
                        .setApplicationId("1:619591266046:android:cdfdcc5fb3577711")
                        .setDatabaseUrl("https://customer-fe4b3.firebaseio.com/") // Required for RTDB.
                        .build();
                List<FirebaseApp> firebaseApps = FirebaseApp.getApps(home1.this);
                FirebaseApp finestayApp = null;
                boolean hasBeenInitialized=false;
                for(FirebaseApp app : firebaseApps){
                    if(app.getName().equals("si")){
                        hasBeenInitialized=true;
                        finestayApp = app;
                    }
                }
                if(!hasBeenInitialized) {
                    finestayApp = FirebaseApp.initializeApp(home1.this, options,"si");
                }

                if(dataSnapshot.exists())
                    cust_id[0] = dataSnapshot.getValue().toString();

                //   FirebaseApp.initializeApp(home1.this, options, "si");

                FirebaseApp app = FirebaseApp.getInstance("si");
                FirebaseDatabase secondaryDatabase = FirebaseDatabase.getInstance(app);
                if(cust_id[0]!=null) {
                    secondaryDatabase.getReference().child("Request").child(cust_id[0]).child("source").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                Toast.makeText(home1.this, ds.getKey(), Toast.LENGTH_SHORT).show();
                                if (ds.getKey().equals("latitude")) {
                                    Toast.makeText(home1.this, "hey1", Toast.LENGTH_SHORT).show();
                                    latitude = (Double) ds.getValue();
                                }
                                if (ds.getKey().equals("longitude")) {
                                    longitude = (Double) ds.getValue();
                                    Toast.makeText(home1.this, "hey2", Toast.LENGTH_SHORT).show();
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

        final FirebaseOptions options = new FirebaseOptions.Builder()
                .setApiKey("AIzaSyA1dUvvOoLkQhUhpoLWVyAOJKEICbz6eew")
                .setApplicationId("1:619591266046:android:cdfdcc5fb3577711")
                .setDatabaseUrl("https://customer-fe4b3.firebaseio.com/") // Required for RTDB.
                .build();
        List<FirebaseApp> firebaseApps = FirebaseApp.getApps(home1.this);
        FirebaseApp finestayApp1 = null;
        boolean hasBeenInitialized1=false;
        for(FirebaseApp app : firebaseApps){
            if(app.getName().equals("sia")){
                hasBeenInitialized1=true;
                finestayApp1 = app;
            }
        }
        if(!hasBeenInitialized1) {
            finestayApp1 = FirebaseApp.initializeApp(home1.this, options,"sia");
        }

        //   FirebaseApp.initializeApp(home1.this, options, "sia");

        //final FirebaseApp appa = FirebaseApp.getInstance("sia");
        Button mLogout = (Button) findViewById(R.id.ride);

        otpETs[0] = findViewById(R.id.otpET1);
        otpETs[1] = findViewById(R.id.otpET2);
        otpETs[2] = findViewById(R.id.otpET3);
        otpETs[3] = findViewById(R.id.otpET4);
        otpETs[0].addTextChangedListener(new GenericTextWatcher(otpETs[0]));
        otpETs[1].addTextChangedListener(new GenericTextWatcher(otpETs[1]));
        otpETs[2].addTextChangedListener(new GenericTextWatcher(otpETs[2]));
        otpETs[3].addTextChangedListener(new GenericTextWatcher(otpETs[3]));


        //final FirebaseApp finalFinestayApp1 = finestayApp1;
        final FirebaseApp finalFinestayApp = finestayApp1;
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final String otp = Helpers.rS(otpETs[0]) + Helpers.rS(otpETs[1]) +
                        Helpers.rS(otpETs[2]) + Helpers.rS(otpETs[3]);

                if (TextUtils.isEmpty(otp)) {
                    Toast.makeText(home1.this, "Please enter OTP", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    final FirebaseDatabase secondaryDatabase = FirebaseDatabase.getInstance(finalFinestayApp);
                    if (cust_id[0] != null) {
                        secondaryDatabase.getReference().child("Request").child(cust_id[0]).child("otp").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String id = dataSnapshot.getValue().toString();
                                if (otp.equals(id)) {
                                    secondaryDatabase.getReference().child("Request")
                                            .child(cust_id[0]).child("otp").setValue("valid");
                                    Intent intent = new Intent(home1.this, home2.class);
                                    finish();
                                    startActivity(intent);

                                }
                                else
                                {
                                    Toast.makeText(home1.this, "Enter correct OTP", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }

                        });
                    }
                }
            }
        });


    }


    public class GenericTextWatcher implements TextWatcher
    {
        private View view;
        private GenericTextWatcher(View view)
        {
            this.view = view;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // TODO Auto-generated method stub
            String text = editable.toString();
            switch(view.getId())
            {

                case R.id.otpET1:
                    if(text.length()==1)
                        otpETs[1].requestFocus();
                    break;
                case R.id.otpET2:
                    if(text.length()==1)
                        otpETs[2].requestFocus();
                    else if(text.length()==0)
                        otpETs[0].requestFocus();
                    break;
                case R.id.otpET3:
                    if(text.length()==1)
                        otpETs[3].requestFocus();
                    else if(text.length()==0)
                        otpETs[1].requestFocus();
                    break;
                case R.id.otpET4:
                    if(text.length()==0)
                        otpETs[2].requestFocus();
                    break;
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
        }
    }
    public static class Helpers {

        public static String rS(EditText editText) {
            return editText.getText().toString().trim();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
}