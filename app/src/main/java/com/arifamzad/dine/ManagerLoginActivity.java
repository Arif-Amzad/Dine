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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class ManagerLoginActivity extends AppCompatActivity {

    EditText mLoginEmail, mLoginPass;
    Button mLoginButton;
    private FirebaseAuth mAuth;
    private DatabaseReference userDatabase;
    private ProgressDialog progress;
    private FirebaseFirestore mFirestore;
    TextView createNew;
    String currentId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_login);

        mLoginEmail = findViewById(R.id.mlogin_email);
        mLoginPass = findViewById(R.id.mlogin_pass);
        mLoginButton = findViewById(R.id.mlogin_button);
        createNew = findViewById(R.id.create_new);

        progress= new ProgressDialog(this);

        userDatabase = FirebaseDatabase.getInstance().getReference().child("manager");

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLogin();
            }
        });

        createNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ManagerLoginActivity.this, ManagerRegActivity.class);
                startActivity(i);
            }
        });
    }


    private void checkLogin(){
        String email= mLoginEmail.getText().toString().trim();
        String password= mLoginPass.getText().toString().trim();
        progress.setMessage("Checking login ...");
        progress.show();

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){

                        checkUserExist();

                        progress.dismiss();

                    } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        progress.dismiss();
                        Toast.makeText(ManagerLoginActivity.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        progress.dismiss();
                        Toast.makeText(ManagerLoginActivity.this, "You have to register first", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
        else{
            progress.dismiss();
        }


    }

    private void checkUserExist(){
        String user_id= mAuth.getCurrentUser().getUid();
        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(user_id)){

                    String name = dataSnapshot.child(user_id).child("manager").getValue().toString();


                    String token_id =  FirebaseInstanceId.getInstance().getToken();
                            //currentId = mAuth.getCurrentUser().getUid();

                    Map<String, Object> tokenMap = new HashMap<>();
                    tokenMap.put("token_id", token_id);
                    tokenMap.put("manager_name", name);

                    mFirestore.collection("users").document(user_id).update(tokenMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    });

                    Intent lintent= new Intent(ManagerLoginActivity.this, ManagerActivity.class);
                    startActivity(lintent);
                    finish();

                }
                else{

                    Toast.makeText(ManagerLoginActivity.this, "You have to Register first", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
