package com.deep.owner;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class database extends AppCompatActivity{
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mRef;
    private String UserID;
    private ListView mListView;
    private String email,f_name,l_name,phone;
    private String driver_email;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_database_layout);



        mListView = (ListView) findViewById(R.id.listview);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        UserID = user.getUid();

        mRef.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                showData(dataSnapshot);
            }
             @Override
              public void onCancelled(DatabaseError databaseError){


            }
        });

    }
    private void showData(DataSnapshot dataSnapshot){
        for(DataSnapshot ds:dataSnapshot.getChildren()){
            userInfo u = new userInfo();
            u.setEmail(ds.child(UserID).getValue(userInfo.class).getEmail());
            u.setF_name(ds.child(UserID).getValue(userInfo.class).getF_name());
            u.setL_name(ds.child(UserID).getValue(userInfo.class).getL_name());
            u.setPhone(ds.child(UserID).getValue(userInfo.class).getPhone());
            ArrayList<String> array = new ArrayList<>();
            array.add(u.getEmail());
            array.add(u.getF_name());
            array.add(u.getL_name());
            array.add(u.getPhone());
            ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,array);
            mListView.setAdapter(adapter);

        }
    }

}
