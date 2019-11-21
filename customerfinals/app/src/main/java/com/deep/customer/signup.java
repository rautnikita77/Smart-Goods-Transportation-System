package com.deep.customer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class signup extends AppCompatActivity {
    private EditText mEmail, mPassword, mFName,mLName,mPhone, mAadhar;
    private Button mRegistration;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    //defining AwesomeValidation object
    private AwesomeValidation awesomeValidation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_signup);
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        mAuth = FirebaseAuth.getInstance();

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){
                    Intent intent = new Intent(signup.this, home.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };
        mRegistration = (Button) findViewById(R.id.registration);
        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        mFName = (EditText) findViewById(R.id.first_name);
        mLName = (EditText) findViewById(R.id.last_name);
        mPhone = (EditText) findViewById(R.id.mobile);
        mAadhar = (EditText) findViewById(R.id.aadhar);

        //adding validation to edittexts
        awesomeValidation.addValidation(this, R.id.first_name, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.nameerror);
        awesomeValidation.addValidation(this, R.id.last_name, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.nameerror);
        awesomeValidation.addValidation(this, R.id.email, Patterns.EMAIL_ADDRESS, R.string.emailerror);
        awesomeValidation.addValidation(this, R.id.password, "^[A-Za-z0-9\\s]{1,}[\\.]{0,1}[A-Za-z0-9\\s]{0,}$", R.string.passworderror);
        awesomeValidation.addValidation(this, R.id.mobile, "^[2-9]{2}[0-9]{8}$", R.string.mobileerror);
        awesomeValidation.addValidation(this, R.id.aadhar, "^[0-9]{12}$", R.string.aadharerror);


        mRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (awesomeValidation.validate()) {
                    Toast.makeText(signup.this, "Registration Successfull", Toast.LENGTH_LONG).show();

                    //process the data further
                    final String email = mEmail.getText().toString();
                    final String password = mPassword.getText().toString();
                    final String Fname = mFName.getText().toString();
                    final String Lname = mLName.getText().toString();
                    final String Phone = mPhone.getText().toString();
                    final String aadhar = mAadhar.getText().toString();


                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(signup.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(signup.this, "sign up error", Toast.LENGTH_SHORT).show();
                            }else{
                                User user = new User(
                                        email,
                                        Fname,
                                        Lname,
                                        Phone,
                                        aadhar
                                );
                                FirebaseDatabase.getInstance().getReference("customer")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(signup.this,"Registered", Toast.LENGTH_LONG).show();
                                        } else {
                                            //display a failure message
                                        }
                                    }
                                });


                                // mAuth.createUserWithEmailAndPassword(email,password).

                                //String user_id = mAuth.getCurrentUser().getUid();
                                //DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(user_id);
                                //current_user_db.setValue(true);

                            }
                        }
                    });
                }

            }
        });
        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(signup.this,login.class);
                startActivity(intent);
                finish();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }
    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }
}
