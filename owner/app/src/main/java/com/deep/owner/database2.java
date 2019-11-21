package com.deep.owner;

import android.support.v7.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class database2 extends AppCompatActivity {
    private ListView mListView;


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mRef;
    private String UserID;
    private FirebaseDatabase mFirebaseDatabase;
    //private FirebaseAuth mAuth;
    //private FirebaseAuth.AuthStateListener ;
    //private DatabaseReference mRef;
   // private String UserID;
    //private ListView mListView;
    public ArrayList<String> array = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_database_layout);
        //FirebaseUser user = mAuth.getCurrentUser();
       // UserID = user.getUid();
        /*
        FirebaseOptions options = new FirebaseOptions.Builder()    //access drivers database
                .setApiKey("AIzaSyBHIi_Wx9Dmav8oOqfjb5cGlEn2aYfpymI")
                .setApplicationId("1:1048956569474:android:cdfdcc5fb3577711")
                .setDatabaseUrl("https://driver-445b4.firebaseio.com/") // Required for RTDB.
                .build();

        FirebaseApp.initializeApp(database2.this, options, "driver");

        FirebaseApp app = FirebaseApp.getInstance("driver");
        final FirebaseDatabase secondaryDatabase = FirebaseDatabase.getInstance(app);

        DatabaseReference ref = secondaryDatabase.getReference();

        DatabaseReference temp = ref.child("driver");
        UserID = temp.getKey();
        */
        boolean hasBeenInitialized = false;
        List<FirebaseApp> fbsLcl = FirebaseApp.getApps(this);
        for (FirebaseApp app1 : fbsLcl) {
            if (app1.getName().equals("driver")) {
                hasBeenInitialized = true;
            }
        }

        FirebaseOptions options = new FirebaseOptions.Builder()    //access drivers database
                .setApiKey("AIzaSyBHIi_Wx9Dmav8oOqfjb5cGlEn2aYfpymI")
                .setApplicationId("1:1048956569474:android:cdfdcc5fb3577711")
                .setDatabaseUrl("https://driver-445b4.firebaseio.com/") // Required for RTDB.
                .build();

        if (!hasBeenInitialized)
             FirebaseApp.initializeApp(getApplicationContext(), options, "driver"/*""*/);
        else {
            FirebaseApp.getInstance("driver");
        }

        //fbDB = FirebaseDatabase.getInstance(fbApp);


        // <!------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------!>
        mListView = (ListView) findViewById(R.id.listview);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();



        mRef.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                showData(dataSnapshot);
            }
            @Override
            public void onCancelled(DatabaseError databaseError){


            }
        });


        // <!------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------!>

    }
    private void showData(DataSnapshot dataSnapshot) {
        FirebaseDatabase.getInstance().getReference().child("owner")   // iterate through all drivers of this owner
                .child(FirebaseAuth.getInstance().getUid()).child("driver").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                for (DataSnapshot d:dataSnapshot.getChildren()) {    //get all the drivers uid
                    FirebaseApp app = FirebaseApp.getInstance("s");
                    final FirebaseDatabase secondaryDatabase = FirebaseDatabase.getInstance(app);

                    secondaryDatabase.getReference().child("driver").child(d.getKey()).addValueEventListener(new ValueEventListener() {
                        //iterate through drivers database

                        @Override
                        public void onDataChange(@NonNull final DataSnapshot dataSnapshots ) {//get drivers position


                                Driver_info u = new Driver_info();
                              // u.setMail(dataSnapshots.child("email").getValue(Driver_info.class).getMail());
                                String driver = (String) dataSnapshots.child("f_name").getValue();
                                //boolean abc = true;
                                //Toast.makeText(database2.this,driver , Toast.LENGTH_SHORT).show();
                                //u.setF_name(ds.child(UserID).getValue(userInfo.class).getF_name());
                                //u.setL_name(ds.child(UserID).getValue(userInfo.class).getL_name());
                                //u.setPhone(ds.child(UserID).getValue(userInfo.class).getPhone());


                                ArrayList<String> array = new ArrayList<>();

                                array.add(driver);


                                ArrayAdapter adapter = new ArrayAdapter(database2.this, android.R.layout.simple_list_item_1, array);
                                mListView.setAdapter(adapter);



                                //array.add(u.getF_name());
                                //array.add(u.getL_name());
                                //array.add(u.getPhone());
                               // ArrayAdapter adapter = new ArrayAdapter(database2.this, android.R.layout.simple_list_item_1, array);
                                //mListView.setAdapter(adapter);






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











    }


