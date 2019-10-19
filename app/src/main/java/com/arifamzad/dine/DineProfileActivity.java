package com.arifamzad.dine;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DineProfileActivity extends AppCompatActivity {

    TextView dineName, mName, sDate, tBorder, dCode, pNumber, mEmail;
    private FirebaseAuth mAuth;
    private DatabaseReference userDatabase;
    Button closeDine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dine_profile);

        dineName = findViewById(R.id.profile_dine_name);
        mName = findViewById(R.id.profile_manager_name);
        sDate = findViewById(R.id.profile_date);
        dCode  = findViewById(R.id.profile_code);
        pNumber = findViewById(R.id.profile_contact);
        mEmail = findViewById(R.id.profile_email);

        closeDine = findViewById(R.id.dine_close);

        userDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        dineProfile();

        closeDine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialogBuilder = new  AlertDialog.Builder(DineProfileActivity.this);

                alertDialogBuilder.setTitle("Do you really want to close your Dine permanently?");
                alertDialogBuilder.setIcon(R.drawable.ic_decission);
                alertDialogBuilder.setCancelable(false).setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id){
                        //setCloseDine();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id ){
                        dialog.cancel();
                    }
                });
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                setCloseDine();
            }
        });
    }

    public void setCloseDine(){



    }

    public void dineProfile(){
        String currentUserId = mAuth.getCurrentUser().getUid();
        userDatabase.child("manager").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String dName = dataSnapshot.child("dine_name").getValue().toString();
                String maName = dataSnapshot.child("manager").getValue().toString();
                String stDate = dataSnapshot.child("starting_date").getValue().toString();
                String phone = dataSnapshot.child("phone").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();
                String code = dataSnapshot.child("code").getValue().toString();
                //String toBorder = dataSnapshot.child("total_border").getValue().toString();

                dineName.setText(dName);
                mName.setText(maName);
                sDate.setText(stDate);
                dCode.setText(code);
                pNumber.setText(phone);
                mEmail.setText(email);
                //tBorder.setText(toBorder);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
