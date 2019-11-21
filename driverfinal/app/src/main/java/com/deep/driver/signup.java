package com.deep.driver;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
    private EditText mEmail, mPassword, mFName, mLName, mOName, mCar, mReg_no, mPhone, mLicense;
    private Button mRegistration;
    private AwesomeValidation awesomeValidation;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);


        getSupportActionBar().hide();
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
        mFName = (EditText) findViewById(R.id.f_name);
        mLName = (EditText) findViewById(R.id.l_name);
        mPhone = (EditText) findViewById(R.id.phone);
        mOName = (EditText) findViewById(R.id.owner);
        final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        mReg_no = (EditText) findViewById(R.id.reg_no);
        mLicense = (EditText) findViewById(R.id.licence_no);

        //adding validation to edittexts
        awesomeValidation.addValidation(this, R.id.f_name, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.nameerror);
        awesomeValidation.addValidation(this, R.id.l_name, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.nameerror);
        awesomeValidation.addValidation(this, R.id.email, Patterns.EMAIL_ADDRESS, R.string.emailerror);
        awesomeValidation.addValidation(this, R.id.owner, Patterns.EMAIL_ADDRESS, R.string.emailerror);

        awesomeValidation.addValidation(this, R.id.password, "^[A-Za-z0-9\\s]{1,}[\\.]{0,1}[A-Za-z0-9\\s]{0,}$", R.string.passworderror);
        awesomeValidation.addValidation(this, R.id.phone, "^[2-9]{2}[0-9]{8}$", R.string.mobileerror);
        awesomeValidation.addValidation(this, R.id.reg_no, "^[0-9]{5}$", R.string.registrationerror);
        awesomeValidation.addValidation(this, R.id.licence_no, "^[A-Za-z0-9]{15}$", R.string.licenseerror);

        mRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (awesomeValidation.validate()) {
                    Toast.makeText(signup.this, "Registration Successfull", Toast.LENGTH_LONG).show();

                    //process the data further
                    RadioGroup rg =  findViewById(R.id.radioGroup);
                    final String email = mEmail.getText().toString();
                    final String password = mPassword.getText().toString();
                    final String f_name = mFName.getText().toString();
                    final String l_name = mLName.getText().toString();
                    final String owner = mOName.getText().toString();
                    final String car = ((RadioButton)findViewById(rg.getCheckedRadioButtonId()))
                            .getText().toString();
                    final String reg_no = mReg_no.getText().toString();
                    final String phone = mPhone.getText().toString();
                    final String license = mLicense.getText().toString();
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(signup.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(signup.this, "sign up error", Toast.LENGTH_SHORT).show();
                            }else{
                                User user = new User(
                                        email, f_name, l_name, phone, car, owner, reg_no, license
                                );
                                FirebaseDatabase.getInstance().getReference("driver")
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