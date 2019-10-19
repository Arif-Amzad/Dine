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

import com.arifamzad.dine.managerfragments.BorderListFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class BorderRegActivity extends AppCompatActivity {


    EditText bregName, bregEmail, bregPassword,bregPhone;
    Button bregButton;
    DatabaseReference userDatabase;
    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_border_reg);

        bregName = findViewById(R.id.breg_name);
        bregEmail = findViewById(R.id.breg_email);
        bregButton = findViewById(R.id.breg_button);
        bregPassword = findViewById(R.id.breg_pass);
        bregPhone = findViewById(R.id.breg_phone);

        progress= new ProgressDialog(this);

        userDatabase = FirebaseDatabase.getInstance().getReference().child("border");
        mAuth=FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        //FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
          //      .setTimestampsInSnapshotsEnabled(true)
            //    .build();
        //mFirestore.setFirestoreSettings(settings);

        bregButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegister();
            }
        });


    }

    private void startRegister(){

        progress.setMessage("Registering ...");
        progress.show();

        final String name = bregName.getText().toString().trim();
        final String email = bregEmail.getText().toString().trim();
        final String password = bregPassword.getText().toString().trim();
        final String phone = bregPhone.getText().toString().trim();

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(name)){

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        String user_id = mAuth.getCurrentUser().getUid();

                        DatabaseReference current_user_data= userDatabase.child(user_id);

                        current_user_data.child("email").setValue(email);
                        current_user_data.child("name").setValue(name);
                        current_user_data.child("phone").setValue(phone);



                        String token_id =  FirebaseInstanceId.getInstance().getToken();

                        Map<String, Object> tokenMap = new HashMap<>();
                        tokenMap.put("token_id", token_id);
                        tokenMap.put("border_name", name);

                        mFirestore.collection("users").document(user_id).set(tokenMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Intent registerintent= new Intent(BorderRegActivity.this, BorderDrawerActivity.class);
                                startActivity(registerintent);
                                finish();
                                progress.dismiss();
                                Toast.makeText(BorderRegActivity.this, "You have registered as a Border", Toast.LENGTH_LONG).show();

                            }
                        });




                    }

                    else{
                        progress.dismiss();
                        Toast.makeText(BorderRegActivity.this, "You have already registered! Please login", Toast.LENGTH_LONG).show();

                    }
                }
            });
        }
        else{
            progress.dismiss();
            Toast.makeText(BorderRegActivity.this, "Fill all value please", Toast.LENGTH_LONG).show();

        }
    }
}
