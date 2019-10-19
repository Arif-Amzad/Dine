package com.arifamzad.dine.managerfragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.text.method.ScrollingMovementMethod;
import com.arifamzad.dine.ManagerActivity;
import com.arifamzad.dine.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class MealPostFragment extends Fragment {

    EditText postBox;
    Button postButton;
    TextView mName, mDate, mPost, myDineName, totalBorder, totalMoney;
    private DatabaseReference postDatabase, managerDatabase;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    ProgressDialog progress;
    String currentUserId, managerName;
    //LinearLayout postLayout;


    public static MealPostFragment newInstance(){
        MealPostFragment fragment = new MealPostFragment();
        return fragment;
    }

    public MealPostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_meal_post, container, false);

        postBox = view.findViewById(R.id.post_box);
        postButton = view.findViewById(R.id.button_post);

        mName = view.findViewById(R.id.posting_name);
        myDineName = view.findViewById(R.id.my_dine_name);
        mDate = view.findViewById(R.id.posting_date);
        //postLayout = view.findViewById(R.id.postlayout);
        mPost = view.findViewById(R.id.post);
        mPost.setMovementMethod(new ScrollingMovementMethod());
        totalBorder = view.findViewById(R.id.total_border);
        totalMoney = view.findViewById(R.id.total_money);


        mAuth = FirebaseAuth.getInstance();
        managerDatabase= FirebaseDatabase.getInstance().getReference();
        postDatabase = FirebaseDatabase.getInstance().getReference().child("post");
        postDatabase.keepSynced(true);
        currentUserId = mAuth.getCurrentUser().getUid();
        mFirestore = FirebaseFirestore.getInstance();

        progress = new ProgressDialog(getActivity());

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startingPost();
            }
        });

        showManagerDashboard();
        moneyHave();
        //totalMoney();

        return view;

    }

    public void onStart() {
        super.onStart();

    }

    public void showManagerDashboard(){

        postDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child(currentUserId).child("name").getValue().toString();
                String post = dataSnapshot.child(currentUserId).child("post").getValue().toString();
                String post_time = dataSnapshot.child(currentUserId).child("post_time").getValue().toString();

                mName.setText(name);
                mDate.setText(post_time);
                mPost.setText(post);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        managerDatabase.child("manager").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String dineName = dataSnapshot.child("dine_name").getValue().toString();
                managerName = dataSnapshot.child("manager").getValue().toString();

                myDineName.setText(dineName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        managerDatabase.child("manager").child(currentUserId).child("my_border").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                long count = dataSnapshot.getChildrenCount();
                String s = String.valueOf(count);

                totalBorder.setText(s);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void startingPost(){

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MMMM 'at' hh:mm a");

        final String postTime = simpleDateFormat.format(calendar.getTime());

        String post = postBox.getText().toString().trim();

        progress.setMessage("Posting ...");
        progress.show();

        DatabaseReference newPostRef = postDatabase.child(currentUserId);

        if(!TextUtils.isEmpty(post)){

            newPostRef.child("name").setValue(managerName);
            newPostRef.child("post").setValue(post);
            newPostRef.child("post_time").setValue(postTime);

            managerDatabase.child("manager").child(currentUserId).child("my_border").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot postData : dataSnapshot.getChildren()){
                        //String borderUid = postData.child("uid").getValue().toString();
                        String user_id = postData.getKey();

                        Map<String, Object> notificationMessage = new HashMap<>();
                        notificationMessage.put("message", post);
                        notificationMessage.put("from", currentUserId);

                        mFirestore.collection("users/" + user_id + "/Notification").add(notificationMessage).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(getActivity(), "Notification sent", Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Error"+ e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            progress.dismiss();

            AlertDialog.Builder alertDialogBuilder = new  AlertDialog.Builder(getActivity());
            // set Title
            alertDialogBuilder.setTitle("Successfully posted");
            alertDialogBuilder.setIcon(R.drawable.complete_24dp);
            //set Message
            alertDialogBuilder.setMessage("").setCancelable(false).setPositiveButton("",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id){
                    // if button is clicked ,then go to into activity
                    //Intent intent = new Intent(getActivity(), IntroActivity.class);
                    //startActivity(intent);
                }
            }).setNegativeButton("", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int id ){
                    // if button is clicked then close the alert box

                    dialog.cancel();
                }
            });
            //create alert dialog
            final AlertDialog alertDialog = alertDialogBuilder.create();
            //show alert box
            alertDialog.show();

            postBox.setText("");

            final Timer t = new Timer();
            t.schedule(new TimerTask() {
                public void run() {
                    alertDialog.dismiss(); // when the task active then close the dialog
                    // t.cancel(); // also just top the timer thread, otherwise, you may receive a crash report
                }
            }, 3000);


        }

        else{
            Toast.makeText(getActivity(), "Please write something", Toast.LENGTH_LONG).show();
            progress.dismiss();
        }

    }

    public void totalMoney(){
        Query query = managerDatabase.child("manager").child(currentUserId).child("my_border");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count =0;
                for(DataSnapshot postData : dataSnapshot.getChildren()) {

                    String paid = postData.child("paid").getValue().toString();
                    int paidInt = Integer.valueOf(paid);
                    count = count + paidInt;
                    Log.e("TAG", count + "");
                    Log.e("KEY :: ", String.valueOf(count));
                }

                totalMoney.setText(String.valueOf(count));

                DatabaseReference newRef = managerDatabase.child("manager").child(currentUserId);
                newRef.child("total_money_have").setValue(count);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void moneyHave(){
        managerDatabase.child("manager").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String m = dataSnapshot.child("total_money_have").getValue().toString();
                totalMoney.setText(String.valueOf(m));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /*

    public void onClickz()
    {
        AlertDialog.Builder alertDialogBuilder = new  AlertDialog.Builder(getActivity());
        // set Title
        alertDialogBuilder.setTitle("Disconnecting User");
        //set Message
        alertDialogBuilder.setMessage("Are You Sure You Want To Disconnect ? ").setCancelable(false).setPositiveButton("Yes",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id){
                // if button is clicked ,then go to into activity
                //Intent intent = new Intent(getActivity(), IntroActivity.class);
                //startActivity(intent);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id ){
                // if button is clicked then close the alert box

                dialog.cancel();
            }
        });
        //create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        //show alert box
        alertDialog.show();
    }
    */


}
