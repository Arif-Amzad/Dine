package com.arifamzad.dine;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {

    private FirebaseUser currentUser;
    FirebaseDatabase firebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference borderDatabase, managerDatabase;
    private static int time=1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        FirebaseApp.initializeApp(this);
        managerDatabase= FirebaseDatabase.getInstance().getReference().child("manager");
        borderDatabase = FirebaseDatabase.getInstance().getReference().child("border");
        mAuth=FirebaseAuth.getInstance();

        currentUser= mAuth.getCurrentUser();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Intent intent=new Intent(SplashActivity.this,ManagerDrawerActivity.class);
                //startActivity(intent);
                //finish();

                checkBorderExist();
                //checkManagerExist();
            }
        }, time);
    }

    private void checkBorderExist(){
        borderDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(currentUser==null){
                    Intent lintent= new Intent(SplashActivity.this, BorderLoginActivity.class);
                    startActivity(lintent);
                    finish();
                }
                else if(dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())){

                    Intent lintent= new Intent(SplashActivity.this, BorderDrawerActivity.class);
                    startActivity(lintent);
                    finish();
                    Toast.makeText(SplashActivity.this, "You are logged in as Border", Toast.LENGTH_SHORT).show();

                }
                else{
                    checkManagerExist();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void checkManagerExist(){
        managerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())){
                    Intent lintent= new Intent(SplashActivity.this, ManagerActivity.class);
                    startActivity(lintent);
                    finish();

                    Toast.makeText(SplashActivity.this, "You are logged in as Manager", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
