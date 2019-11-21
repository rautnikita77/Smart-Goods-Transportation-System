package com.deep.driver;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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

public class login extends AppCompatActivity {
    private EditText mEmail, mPassword;
    private Button mLogin;
    private AwesomeValidation awesomeValidation;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //FirebaseApp.initializeApp(this);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        mAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){
                    Intent intent = new Intent(login.this, home.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };
        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        mLogin = (Button) findViewById(R.id.login);
        awesomeValidation.addValidation(this, R.id.email, "^(\\s|\\S)*(\\S)+(\\s|\\S)*$", R.string.emailerror1);
        awesomeValidation.addValidation(this, R.id.password, "^(\\s|\\S)*(\\S)+(\\s|\\S)*$", R.string.passworderror1);
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (awesomeValidation.validate()) {
                    final String email = mEmail.getText().toString();
                    final String password = mPassword.getText().toString();
                    if (TextUtils.isEmpty(email)) {
                        Toast.makeText(login.this, "Please enter email", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (TextUtils.isEmpty(password)) {
                        Toast.makeText(login.this, "Please enter password", Toast.LENGTH_LONG).show();
                        return;
                    }

                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(login.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(login.this, "sign in error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        findViewById(R.id.registration).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(login.this,signup.class);
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

