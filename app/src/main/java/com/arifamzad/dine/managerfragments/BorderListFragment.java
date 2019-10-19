package com.arifamzad.dine.managerfragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arifamzad.dine.BorderLoginActivity;
import com.arifamzad.dine.ManagerActivity;
import com.arifamzad.dine.R;
import com.arifamzad.dine.patternManagement.BorderReq;
import com.arifamzad.dine.patternManagement.BorderReqCustomAdapter;
import com.arifamzad.dine.patternManagement.MyBorder;
import com.arifamzad.dine.patternManagement.MyBorderAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.Intent.getIntent;

/**
 * A simple {@link Fragment} subclass.
 */
public class BorderListFragment extends Fragment {

    LinearLayout mbView;
    RecyclerView borderListRecycle;
    DatabaseReference mbDatabase;
    FirebaseAuth mAuth;
    public String managerId;

    //private List<MyBorder> mbList = new ArrayList<>();
    private List<MyBorder> mbList = new ArrayList<MyBorder>();
    private MyBorderAdapter mbAdapter;

    public static BorderListFragment newInstance(){
        BorderListFragment fragment = new BorderListFragment();
        return fragment;
    }

    public BorderListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_border_list, container, false);

        mbView = view.findViewById(R.id.my_border_view);
        borderListRecycle = view.findViewById(R.id.border_list);

        mAuth = FirebaseAuth.getInstance();
        managerId = mAuth.getCurrentUser().getUid();

        mbDatabase = FirebaseDatabase.getInstance().getReference().child("manager").child(managerId);
        mbDatabase.keepSynced(true);

        mbAdapter = new MyBorderAdapter(mbList, getActivity());
        borderListRecycle.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        //layoutManager.setReverseLayout(true);
        //layoutManager.setStackFromEnd(true);
        borderListRecycle.setLayoutManager(layoutManager);

        borderShow();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //borderShow();

        mbList.clear();

    }

    public void borderShow() {

        Query query = mbDatabase.child("my_border");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                mbDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {


                        mbList.clear();
                        borderListRecycle.removeAllViews();

                        if(dataSnapshot2.hasChild("my_border")) {



                            for (DataSnapshot postData : dataSnapshot.getChildren()) {

                                String status = postData.child("status").getValue().toString();
                                String on = "ON";

                                if(status.matches("ON")){

                                    Calendar calendar = Calendar.getInstance();
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
                                    SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
                                    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

                                    final int currentDate = Integer.parseInt(dateFormat.format(calendar.getTime()));
                                    final int currentMonth = Integer.parseInt(monthFormat.format(calendar.getTime()));
                                    //final int currentYear = Integer.parseInt(yearFormat.format(calendar.getTime()));

                                    int off_varriable = Integer.parseInt(String.valueOf(postData.child("off_varriable").getValue()));
                                    int days_on = Integer.parseInt(String.valueOf(postData.child("days_on").getValue()));
                                    int date = Integer.parseInt(String.valueOf(postData.child("date").getValue()));

                                    int d= days_on;
                                    //int p = paid;
                                    if(currentDate - date < 0){
                                        if(currentMonth==1 || currentMonth==3 ||currentMonth==5 ||currentMonth==7 ||currentMonth==8 ||currentMonth==10 ||currentMonth==12){
                                            d= 31-date+currentDate;
                                            d= d + days_on-off_varriable;
                                            //p = d*60;
                                            off_varriable = 0;
                                            date = currentDate;
                                        }
                                        else if(currentMonth==2){
                                            d= 28-date + currentDate;
                                            d= d+ days_on - off_varriable;
                                            //p = d*60;
                                            off_varriable = 0;
                                            date = currentDate;
                                        }
                                        else{
                                            d= 30-date + currentDate;
                                            d= d+ days_on - off_varriable;
                                            //p = d*60;
                                            off_varriable = 0;
                                            date = currentDate;
                                        }

                                        DatabaseReference newRef = mbDatabase.child("my_border").child(postData.getKey());
                                        newRef.child("date").setValue(currentDate);
                                        newRef.child("off_varriable").setValue(0);
                                        newRef.child("days_on").setValue(d);
                                    }

                                    else if(currentDate-date>0){

                                        d= currentDate - date;
                                        d= d+ days_on - off_varriable;
                                        //p = d*60;
                                        off_varriable = 0;
                                        date = currentDate;

                                        DatabaseReference newRef = mbDatabase.child("my_border").child(postData.getKey());
                                        newRef.child("date").setValue(currentDate);
                                        newRef.child("off_varriable").setValue(0);
                                        newRef.child("days_on").setValue(d);



                                    }
                                    //Log.e(postData.child("name").getValue().toString(), "-----------------------ON------------------------------------");

                                }
                                else{
                                    //statusShow.setText("OFF");
                                    //updating will resume until user turn status ON
                                    //Log.e(postData.child("name").getValue().toString(), "-------------------------OFF----------------------------------");
                                    //Log.e(postData.child("status").getValue().toString(), "-------------------------OFF----------------------------------");
                                }

                                MyBorder myBorder = new MyBorder();

                                myBorder.setName(postData.child("name").getValue().toString());
                                myBorder.setPhone(postData.child("phone").getValue().toString());
                                myBorder.setUid(postData.child("uid").getValue().toString());
                                myBorder.setDays_on(postData.child("days_on").getValue().toString());
                                myBorder.setPaid(postData.child("paid").getValue().toString());
                                myBorder.setStatus(postData.child("status").getValue().toString());

                                mbList.add(myBorder);

                                //adapter
                                borderListRecycle.setAdapter(mbAdapter);

                                mbAdapter.notifyDataSetChanged();

                                //Toast.makeText(getActivity(), "You have "+mbList.size()+" borders", Toast.LENGTH_LONG).show();

                            }
                        }

                        else{
                            Toast.makeText(getActivity(), "You have no border", Toast.LENGTH_LONG);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


    }

}
