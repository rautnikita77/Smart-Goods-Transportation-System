package com.deep.driver;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfileActivity extends AppCompatActivity {

    String mF_name,mL_name,mPhone;
    ImageView close;
    TextView save;
    EditText firstname, lastname,phone;
    String uid;
    //FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        close = findViewById(R.id.close);
        //save = findViewById(R.id.save);
        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        phone=findViewById(R.id.phone);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseAuth.AuthStateListener firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    Intent intent = new Intent(EditProfileActivity.this, login.class);
                    startActivity(intent);
                    finish();
                }

            }
        };



           uid = FirebaseAuth.getInstance().getCurrentUser().getUid();




       // Toast.makeText(this, uid, Toast.LENGTH_SHORT).show();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("driver").child(uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    {
                        if (ds.getKey().equals("f_name")) {
                            mF_name = ds.getValue().toString();
                        }
                        if (ds.getKey().equals("l_name")) {
                            mL_name = ds.getValue().toString();
                        }
                        if (ds.getKey().equals("phone")) {
                            mPhone = ds.getValue().toString();
                        }

                    }


//                    lastname.setText(user.l_name);
                }
                firstname.setText(mF_name);
                lastname.setText(mL_name);
                phone.setText(mPhone);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        close.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(EditProfileActivity.this, home.class);

                startActivity(intent);
                finish();
            }

    });
        save = findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                updateProfile(firstname.getText().toString(),lastname.getText().toString(),phone.getText().toString());


            }

        });



    }

    private void updateProfile(String firstname, String lastname,String phone) {
        FirebaseDatabase.getInstance().getReference("driver").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("f_name").setValue(firstname);
        FirebaseDatabase.getInstance().getReference("driver").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("l_name").setValue(lastname);
        FirebaseDatabase.getInstance().getReference("driver").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("phone").setValue(phone);

    }
}
