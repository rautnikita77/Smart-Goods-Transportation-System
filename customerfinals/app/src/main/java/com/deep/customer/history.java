package com.deep.customer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class history extends AppCompatActivity {
    ListView simpleList;
    final ArrayList resultsHistory = new ArrayList<String>();
    final ArrayList resultsHistory1 = new ArrayList<String>();
    final ArrayList resultsHistory2 = new ArrayList<String>();

    String ab,abc,abd;
   // String [] ee=new String[2];
    int i=0;
    int count=2;
    @Override

protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_history);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseAuth.AuthStateListener firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    Intent intent = new Intent(history.this, login.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
      simpleList = (ListView)findViewById(R.id.simpleListView);

        DatabaseReference userHistoryDatabase = FirebaseDatabase.getInstance().getReference().child("customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("history");
        userHistoryDatabase.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 count=(int)dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                                                               }}
            );



        userHistoryDatabase.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                simpleList = (ListView)findViewById(R.id.simpleListView);
                count=(int)dataSnapshot.getChildrenCount();
                final String[] ee=new String[count];
                Log.i("wwwwwwww","count is"+ count);

                if(dataSnapshot.exists()){i=0;
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        for(DataSnapshot d: ds.getChildren()){
                 if(d.exists())
                 {if(d.getKey().equals("driver id"))
                 {resultsHistory.add("driver Id=" +" "+d.getValue().toString()+"\n ");
                     ab= "driver Id=" +" "+d.getValue().toString()+"\n ";

                        Toast.makeText(history.this,ab,Toast.LENGTH_LONG).show();
                     Log.i("ab",ab);
                      }
                     if(d.getKey().equals("date"))
                     {
                         resultsHistory1.add("date=" +" "+d.getValue().toString()+"\n ");
                         abc= "date=" +" "+d.getValue().toString()+"\n ";

                         Toast.makeText(history.this,abc,Toast.LENGTH_LONG).show();

                         Log.i("abc",abc);
                         }
                         if(d.getKey().equals("cost"))
                         {//double cost=(double)d.getValue();
                             resultsHistory2.add(  abd= "Cost=" +" "+d.getValue().toString()+"\n ");
                             abd= "Cost=" +" "+d.getValue().toString()+"\n ";
                             Toast.makeText(history.this,abd,Toast.LENGTH_LONG).show();
                           Log.i("abd",abd);
                             }
                             //resultsHistory.add(abc+ab+abd);
                 }



                               }
                            Log.i("final history","is"+ee[i]);

                            Log.i("dddddd","i is"+i);
                             //   i++;


                        Log.i("hhhh",ab+abc+abd);
                        if(abc!=null && ab!=null && abd!=null){
                            ee[i]=abc+ab+abd;
                            i++;}
                            Log.i("ccccccc","as"+resultsHistory.size());


                                //int g=0;
                                //String[] gm=new String[3];
                                //if ((!resultsHistory.get(y).equals("0") && !resultsHistory1.equals("0") && !resultsHistory2.equals(0)))
                               // gm[0]=abc;
                                 //   gm[1]=ab;
                                  //  gm[2]=abd;












                 // String[] finals=new String[i+1];
                   //     for(int x=0;x<=i;x++)
                     //   {
                       //     finals[x]=ee[x];
                         //   Log.i("aaaaaaaa","gg"+finals[x]);
                        //}
                      //  String


                        // simpleList = (ListView)findViewById(R.id.simpleListView);
                        //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(history.this, R.layout.activity_listview, R.id.textView1,finals );
                        //simpleList.setAdapter(arrayAdapter);

                }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(history.this, R.layout.activity_listview, R.id.textView1,ee );
                    simpleList.setAdapter(arrayAdapter);





                }


                /*Object [] def=resultsHistory.toArray();
                String [] rrr=new String[def.length];
                int i=0;
                for(Object obj: def)
                {rrr[i]=(String)obj;
                    i++;
                }
                Object [] ghi=resultsHistory1.toArray();
                String [] rrrr=new String[ghi.length];
                int x=0;
                for(Object obj: ghi)
                {rrrr[x]=(String)obj;
                    x++;
                }
                Object [] jkl=resultsHistory2.toArray();
                String [] rrrrr=new String[jkl.length];
                int z=0;
                for(Object obj: jkl)
                {rrrrr[z]=(String)obj;
                    z++;
                }

                ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(history.this, R.layout.activity_listview, R.id.textView2,rrrr );
                ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(history.this, R.layout.activity_listview, R.id.textView3,rrrrr );

                simpleList.setAdapter(arrayAdapter1);
                simpleList.setAdapter(arrayAdapter2);*/

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });



}}
