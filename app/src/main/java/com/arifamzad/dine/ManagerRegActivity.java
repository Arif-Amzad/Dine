package com.arifamzad.dine;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ManagerRegActivity extends AppCompatActivity {

    EditText regDineName, mRegEmail, mRegPass, mRegName, regCode, mPhone, mPlace;
    Button mregButton;
    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;
    ProgressDialog progress;
    DatabaseReference userDatabase, eiinDatabase, postDatabase;
    int a;
    String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manreg);

        regDineName = findViewById(R.id.mreg_dinename);
        mRegEmail = findViewById(R.id.mreg_email);
        mRegPass = findViewById(R.id.mreg_pass);
        mregButton = findViewById(R.id.mreg_button);
        mRegName = findViewById(R.id.mreg_name);
        regCode = findViewById(R.id.mreg_code);
        mPhone = findViewById(R.id.mreg_phone);
        mPlace = findViewById(R.id.mreg_place);


        progress= new ProgressDialog(this);
        userDatabase = FirebaseDatabase.getInstance().getReference().child("manager");
        eiinDatabase = FirebaseDatabase.getInstance().getReference();
        postDatabase = FirebaseDatabase.getInstance().getReference().child("post");

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        mregButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegister();
            }
        });

    }

    private void startRegister(){

        progress.setMessage("Checking login ...");
        progress.show();

        final String dineName = regDineName.getText().toString().trim();
        final String email = mRegEmail.getText().toString().trim();
        final String password = mRegPass.getText().toString().trim();
        final String name = mRegName.getText().toString().trim();
        code = regCode.getText().toString().trim();
        final String phone = mPhone.getText().toString().trim();
        final String place = mPlace.getText().toString().trim();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        final String time = simpleDateFormat.format(calendar.getTime());

        checkEIINexist();


        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(dineName) && !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(name) && !TextUtils.isEmpty(code) && !TextUtils.isEmpty(place)){

            if(a==2){
                 mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                     @Override
                     public void onComplete(@NonNull Task<AuthResult> task) {
                         if (task.isSuccessful()) {

                             String user_id = mAuth.getCurrentUser().getUid();

                             DatabaseReference current_user_data = userDatabase.child(user_id);
                             DatabaseReference eiinRef = eiinDatabase.child("eiin");
                             DatabaseReference defPostRef = postDatabase.child(user_id);

                             current_user_data.child("email").setValue(email);
                             current_user_data.child("dine_name").setValue(dineName);
                             current_user_data.child("manager").setValue(name);
                             current_user_data.child("code").setValue(code);
                             current_user_data.child("phone").setValue(phone);
                             current_user_data.child("starting_date").setValue(time);
                             current_user_data.child("place").setValue(place);
                             current_user_data.child("total_border").setValue(0);
                             current_user_data.child("total_money_have").setValue(0);

                             eiinRef.child(code).setValue(user_id);

                             defPostRef.child("name").setValue("");
                             defPostRef.child("post").setValue("");
                             defPostRef.child("post_time").setValue("");



                             String token_id =  FirebaseInstanceId.getInstance().getToken();
                             //String user_id = mAuth.getCurrentUser().getUid();

                             Map<String, Object> tokenMap = new HashMap<>();
                             tokenMap.put("token_id", token_id);
                             tokenMap.put("manager_name", name);

                             mFirestore.collection("users").document(user_id).set(tokenMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                 @Override
                                 public void onSuccess(Void aVoid) {

                                     Intent intent = new Intent(ManagerRegActivity.this, ManagerActivity.class);
                                     startActivity(intent);
                                     finish();
                                     progress.dismiss();
                                     Toast.makeText(ManagerRegActivity.this, "You have registered as a Border", Toast.LENGTH_LONG).show();

                                 }
                             });
                         }
                         else {
                             progress.dismiss();
                             Toast.makeText(ManagerRegActivity.this, "You have already registered! Please login", Toast.LENGTH_LONG).show();

                         }
                     }
                 });
             }
             else{
                 progress.dismiss();
                 Toast.makeText(ManagerRegActivity.this, "EIIN is already taken! Choose new one", Toast.LENGTH_LONG).show();
             }
        }
        else{
            progress.dismiss();
            Toast.makeText(ManagerRegActivity.this, "Fill all value please", Toast.LENGTH_LONG).show();
        }



    }

    public void checkEIINexist(){

        eiinDatabase.child("eiin").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(code)){
                    a=1;
                }
                else{
                    a=2;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
