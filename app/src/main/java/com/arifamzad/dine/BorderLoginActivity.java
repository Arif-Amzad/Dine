package com.arifamzad.dine;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class BorderLoginActivity extends AppCompatActivity {

    EditText bloginEmail, bloginPassword;
    Button bloginButton;
    private DatabaseReference userDatabase;
    private FirebaseAuth mAuth;
    private ProgressDialog progress;
    private FirebaseFirestore mFirestore;

    TextView newBorder;
    LinearLayout managerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_border_login);

        bloginEmail = findViewById(R.id.blogin_email);
        bloginButton = findViewById(R.id.blogin_button);
        bloginPassword = findViewById(R.id.blogin_pass);
        newBorder = findViewById(R.id.border_new);

        managerLayout = findViewById(R.id.layout_manager);
        managerLayout.setClickable(true);

        progress= new ProgressDialog(this);

        userDatabase = FirebaseDatabase.getInstance().getReference().child("border");
        mAuth=FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        bloginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLogin();
            }
        });

        managerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(BorderLoginActivity.this, ManagerLoginActivity.class);
                startActivity(i);
            }

        });
        newBorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(BorderLoginActivity.this, BorderRegActivity.class);
                startActivity(i);
            }
        });

    }



    private void checkLogin(){
        String email= bloginEmail.getText().toString().trim();
        String password= bloginPassword.getText().toString().trim();
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
                        Toast.makeText(BorderLoginActivity.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        progress.dismiss();
                        Toast.makeText(BorderLoginActivity.this, "You have to register first", Toast.LENGTH_SHORT).show();
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

                    String name = dataSnapshot.child(user_id).child("name").getValue().toString();

                    String token_id =  FirebaseInstanceId.getInstance().getToken();
                            //String current_id = mAuth.getCurrentUser().getUid();

                            Map<String, Object> tokenMap = new HashMap<>();
                            tokenMap.put("token_id", token_id);
                            tokenMap.put("border_name", name);

                            mFirestore.collection("users").document(user_id).set(tokenMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {



                                }
                            });

                    Intent lintent= new Intent(BorderLoginActivity.this, BorderDrawerActivity.class);
                    startActivity(lintent);
                    finish();



                }
                else{

                    Toast.makeText(BorderLoginActivity.this, "You have to Register first", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public boolean onKeyDown ( int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder builder = new AlertDialog.Builder(BorderLoginActivity.this);
            builder.setMessage("Do you want to close the app?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent setIntent = new Intent(Intent.ACTION_MAIN);
                    setIntent.addCategory(Intent.CATEGORY_HOME);
                    setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(setIntent);
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            }).show();
        }
        return super.onKeyDown(keyCode, event);
    }
}
