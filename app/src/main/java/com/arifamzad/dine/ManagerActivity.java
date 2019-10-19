package com.arifamzad.dine;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.arifamzad.dine.managerfragments.MealPostFragment;
import com.arifamzad.dine.managerfragments.BorderListFragment;
import com.arifamzad.dine.managerfragments.PendingBorderFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ManagerActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    ProgressDialog progress;
    private FirebaseFirestore mFirestore;
    String currentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);

        mAuth = FirebaseAuth.getInstance();
        currentId = mAuth.getCurrentUser().getUid();
        mFirestore = FirebaseFirestore.getInstance();
        progress = new ProgressDialog(this);

        Fragment selectedFragment=new MealPostFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, selectedFragment);
        transaction.commit();

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.border_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.pending_border:
                                selectedFragment = PendingBorderFragment.newInstance();
                                break;
                            case R.id.border_list:
                                selectedFragment = BorderListFragment.newInstance();
                                break;
                            case R.id.post_meal:
                                selectedFragment = MealPostFragment.newInstance();
                                break;
                        }
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, selectedFragment);
                        transaction.commit();
                        return true;
                    }
                });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.manager_dot_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id==R.id.action_dine_profile){
            Intent i = new Intent(ManagerActivity.this, DineProfileActivity.class);
            startActivity(i);
        }
        else if (id == R.id.action_logout) {
            progress.setMessage("Logging out ...");
            progress.show();

            Map<String, Object> tokenMapRemove = new HashMap<>();
            tokenMapRemove.put("token_id", FieldValue.delete());
            mFirestore.collection("users").document(currentId).update(tokenMapRemove).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    logout();
                    progress.dismiss();
                    Intent out = new Intent(ManagerActivity.this, BorderLoginActivity.class);
                    startActivity(out);
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    public void logout(){
        mAuth.signOut();
    }

    public boolean onKeyDown ( int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ManagerActivity.this);
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
