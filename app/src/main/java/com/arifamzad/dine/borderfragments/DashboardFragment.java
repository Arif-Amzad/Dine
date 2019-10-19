package com.arifamzad.dine.borderfragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.arifamzad.dine.ManagerLoginActivity;
import com.arifamzad.dine.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment {

    EditText searchBox;
    DatabaseReference managerDatabase,eiinDatabase, borderDatabase, borderReqDatabase;
    String uidd, code;
    LinearLayout click;
    LinearLayout layoutDine, dashShow, dineNameLayout;
    ScrollView dashScroll;
    TextView dine, mName, dPlace, mPhone;
    TextView empty, dashDineName, dashManagerName, dashDineCode, dashContact, dashPayment, dashMealOn, dashPost, dashPlace,dashPostTime, dashPostMan;
    Button reqButton;
    FirebaseAuth mAuth;
    String currentBorderId, my_manager, dineName;
    NavigationView navigationView;

    public static DashboardFragment newInstance(){
        DashboardFragment fragment = new DashboardFragment();
        return fragment;
    }

    public DashboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_dashboard, container, false);

        searchBox = view.findViewById(R.id.box_code);
        click = view.findViewById(R.id.click_search);
        dine = view.findViewById(R.id.dine);
        mName = view.findViewById(R.id.name_manager);
        mPhone = view.findViewById(R.id.phone_manager);
        dPlace = view.findViewById(R.id.location_dine);
        layoutDine= view.findViewById(R.id.layout_dine);
        reqButton= view.findViewById(R.id.button_req);
        dineNameLayout = view.findViewById(R.id.dash_name_layout);


        dashShow = view.findViewById(R.id.dash_show);
        dashDineName = view.findViewById(R.id.dash_dine_name);
        dashManagerName = view.findViewById(R.id.dash_manager_name);
        dashDineCode = view.findViewById(R.id.dash_dine_code);
        dashContact = view.findViewById(R.id.dash_contact);
        dashPayment = view.findViewById(R.id.dash_payment);
        dashMealOn = view.findViewById(R.id.dash_meal_on);
        dashPost = view.findViewById(R.id.dash_post);
        empty = view.findViewById(R.id.empty);
        dashPlace = view.findViewById(R.id.dash_place);
        dashScroll = view.findViewById(R.id.dash_scroll);
        dashPostTime = view.findViewById(R.id.dash_post_time);
        dashPostMan = view.findViewById(R.id.dash_post_manager);

        //navigationView = (NavigationView) view.findViewById(R.id.nav_view);


        managerDatabase = FirebaseDatabase.getInstance().getReference();
        eiinDatabase = FirebaseDatabase.getInstance().getReference().child("eiin");
        borderDatabase = FirebaseDatabase.getInstance().getReference();
        borderReqDatabase = FirebaseDatabase.getInstance().getReference().child("request");

        mAuth = FirebaseAuth.getInstance();
        currentBorderId = mAuth.getUid();

        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSearching();
            }
        });

        reqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                reqToManager();
                layoutDine.setVisibility(View.INVISIBLE);
                //layoutDine.setVisibility(View.INVISIBLE);


            }
        });

        dashShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                layoutDine.setVisibility(View.INVISIBLE);
            }
        });

        dashBoard();

        return view;
    }

    public void dashBoard(){

        borderDatabase.child("border").child(currentBorderId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("my_manager")){

                    //empty.setVisibility(View.INVISIBLE);
                    dashShow.setVisibility(View.VISIBLE);

                    my_manager = dataSnapshot.child("my_manager").getValue().toString();

                    borderDatabase.child("post").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            String post = dataSnapshot.child(my_manager).child("post").getValue().toString();
                            String postTime = dataSnapshot.child(my_manager).child("post_time").getValue().toString();
                            dashPost.setText(post);
                            dashPostTime.setText(postTime);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    borderDatabase.child("manager").child(my_manager).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            dineName = dataSnapshot.child("dine_name").getValue().toString();
                            String mName = dataSnapshot.child("manager").getValue().toString();
                            String code = dataSnapshot.child("code").getValue().toString();
                            String phone = dataSnapshot.child("phone").getValue().toString();
                            String place = dataSnapshot.child("place").getValue().toString();

                            dashDineName.setText(dineName);
                            dashManagerName.setText(mName);
                            dashDineCode.setText(code);
                            dashContact.setText(phone);
                            dashPlace.setText(place);

                            dashPostMan.setText(mName);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    borderDatabase.child("manager").child(my_manager).child("my_border").child(currentBorderId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String daysOn = dataSnapshot.child("days_on").getValue().toString();
                            String payment = dataSnapshot.child("paid").getValue().toString();

                            dashPayment.setText(payment);
                            dashMealOn.setText(daysOn);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
                else{
                    empty.setVisibility(View.VISIBLE);
                    dashShow.setVisibility(View.INVISIBLE);
                    dineNameLayout.setVisibility(View.INVISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void startSearching(){
        code = searchBox.getText().toString().trim();
        if(code.length()!= 0){
            eiinDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild(code)){
                        uidd = dataSnapshot.child(code).getValue().toString();

                        Toast.makeText(getActivity(), "This Dine is available", Toast.LENGTH_LONG).show();
                        chechkManager();
                    }
                    else{

                        layoutDine.setVisibility(View.INVISIBLE);
                        Toast.makeText(getActivity(), "Invalid EIIN", Toast.LENGTH_LONG).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else{
            Toast.makeText(getActivity(), "search panle is empty", Toast.LENGTH_LONG).show();
        }


    }
    public void chechkManager(){
        managerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("manager").child(uidd).child("dine_name").getValue().toString();
                String loc = dataSnapshot.child("manager").child(uidd).child("place").getValue().toString();
                String manName = dataSnapshot.child("manager").child(uidd).child("manager").getValue().toString();
                String phone = dataSnapshot.child("manager").child(uidd).child("phone").getValue().toString();

                layoutDine.setVisibility(View.VISIBLE);

                dine.setText(name);
                dPlace.setText(loc);
                mName.setText(manName);
                mPhone.setText(phone);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void reqToManager(){

        currentBorderId= mAuth.getCurrentUser().getUid();

        borderDatabase.child("border").child(currentBorderId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild("my_manager")){
                    Toast.makeText(getActivity(), "You are already connected with "+dineName, Toast.LENGTH_LONG).show();
                }
                else{

                    AlertDialog.Builder alertDialogBuilder = new  AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setTitle("Request sent");
                    alertDialogBuilder.setMessage("");
                    alertDialogBuilder.setIcon(R.drawable.complete_24dp);

                    final AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                    final Timer t = new Timer();
                    t.schedule(new TimerTask() {
                        public void run() {
                            alertDialog.dismiss(); // when the task active then close the dialog
                            t.cancel(); // also just top the timer thread, otherwise, you may receive a crash report
                        }
                    }, 3000);

                    String name = dataSnapshot.child("name").getValue().toString();
                    String phone = dataSnapshot.child("phone").getValue().toString();

                    DatabaseReference newRef = managerDatabase.child("manager").child(uidd).child("request").child(currentBorderId);

                    newRef.child("name").setValue(name);
                    newRef.child("phone").setValue(phone);
                    newRef.child("uid").setValue(currentBorderId);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
