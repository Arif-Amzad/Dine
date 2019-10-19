package com.arifamzad.dine;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
//import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.arifamzad.dine.borderfragments.BorderProfileFragment;
import com.arifamzad.dine.borderfragments.DashboardFragment;
import com.arifamzad.dine.borderfragments.MealOffFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.util.HashMap;
import java.util.Map;

public class BorderDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    String currentId;
    DatabaseReference borderDatabase;
    TextView nhName, nhEmail;
    NavigationView mNavigationView;

    ProgressDialog progress;

    //@SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //fab.setOnClickListener(new View.OnClickListener() {
            //@Override
            //public void onClick(View view) {
          //      Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
              //          .setAction("Action", null).show();
            //}
        //});

        //FirebaseMessaging.getInstance().setAutoInitEnabled(true);


        //mNavigationView = (NavigationView) findViewById(R.id.nav_view);


        //nhName = findViewById(R.id.nav_header_name);
        //nhEmail = findViewById(R.id.nav_header_email);


        borderDatabase = FirebaseDatabase.getInstance().getReference();
        progress= new ProgressDialog(this);
        mAuth= FirebaseAuth.getInstance();
        currentId = mAuth.getCurrentUser().getUid();


        mFirestore = FirebaseFirestore.getInstance();
/*
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        mFirestore.setFirestoreSettings(settings);


*/
        setContentView(R.layout.activity_border_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //toolbar.setTitleTextColor(Color.parseColor("#4CAF50"));

        //toggle.getDrawerArrowDrawable().setColor(Color.parseColor("#4CAF50"));
        //toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        nhEmail =navigationView.getHeaderView(0).findViewById(R.id.nav_header_email);
        nhName = navigationView.getHeaderView(0).findViewById(R.id.nav_header_name);

        Fragment fragment = null;
        Class fragmentClass = null;

        fragmentClass = BorderProfileFragment.class;

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //FragmentManager fragmentManager = getSupportFragmentManager();
        //fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        Fragment selectedFragment=new DashboardFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.flContent, selectedFragment);
        transaction.commit();


        navDataLoad();
    }

    public void onStart(){
        super.onStart();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.border_drawer, menu);
        return true;
    }
/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;mbList.clear();
                borderListRecycle.removeAllViews();
        }

        return super.onOptionsItemSelected(item);
    }
*/
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //Fragment selectedFragment = null;
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment=new Fragment();
        Class fragmentClass = null;


        if (id == R.id.nav_profile) {
            fragmentClass = BorderProfileFragment.class;

        }
        else if (id == R.id.nav_dashboard) {
            fragmentClass = DashboardFragment.class;
        }
        else if (id == R.id.nav_calculator) {

        }
        else if (id == R.id.nav_logout) {
            progress.setMessage("Logging out ...");
            progress.show();

            Map<String, Object> tokenMapRemove = new HashMap<>();
            tokenMapRemove.put("token_id", FieldValue.delete());

            mFirestore.collection("users").document(currentId).update(tokenMapRemove).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    logout();
                    progress.dismiss();
                    Intent out = new Intent(BorderDrawerActivity.this, BorderLoginActivity.class);
                    startActivity(out);
                }
            });

        }
        else if(id == R.id.nav_meal_off){
            fragmentClass = MealOffFragment.class;
        }
        else if(id == R.id.nav_share){


            /*ApplicationInfo api = getApplicationContext().getApplicationInfo();
            String apkPath = api.sourceDir;

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("application/vnd.android.package-archive");

            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(apkPath)));
            startActivity(Intent.createChooser(intent, "Share via"));

            */

            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_TEXT, "Download 'Dine' app from my Google Drive. linke is here: https://drive.google.com/open?id=1GFoIne8e1907d-MVeEguKHht1EVchGV6");
            startActivity(Intent.createChooser(i,"Share Dine app Via"));

        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void logout(){
        mAuth.signOut();
    }

    public boolean onKeyDown ( int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder builder = new AlertDialog.Builder(BorderDrawerActivity.this);
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

    public void navDataLoad(){

        borderDatabase.child("border").child(currentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();

                nhName.setText(name);
                nhEmail.setText(email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}