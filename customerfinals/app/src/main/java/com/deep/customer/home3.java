package com.deep.customer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class home3 extends AppCompatActivity {
    private RatingBar rBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home3);
        rBar = (RatingBar) findViewById(R.id.rating);

        Button rate =(Button) findViewById(R.id.rating1);
        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(home3.this, ""+rBar.getRating(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(home3.this, home.class);

                startActivity(intent);
                finish();
            }
        });
        FirebaseAuth.AuthStateListener firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    Intent intent = new Intent(home3.this, login.class);
                    startActivity(intent);
                    finish();
                }
            }
        };



    }
}
