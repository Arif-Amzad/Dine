package com.arifamzad.dine.borderfragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.arifamzad.dine.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class MealOffFragment extends Fragment {

    TextView digitOne, digitTwo, currentMonth, selectedMonth, mealOfWrite;
    TextView digitThree, digitFour;
    int todayDate, thisMonth, desireDate, desireMonth, totalOff;
    LinearLayout clickCalender;
    Button turnOffBtn;
    FirebaseAuth mAuth;
    DatabaseReference borderDatabase;
    String bUid, mUid, currentStatus;
    int i;

    private CalendarView calendarView;

    public static MealOffFragment newInstance(){
        MealOffFragment fragment = new MealOffFragment();
        return fragment;
    }


    public MealOffFragment() {
        //Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_meal_off, container, false);

        digitOne = view.findViewById(R.id.digit_1);
        digitTwo = view.findViewById(R.id.digit_2);
        digitThree = view.findViewById(R.id.digit_3);
        digitFour = view.findViewById(R.id.digit_4);
        currentMonth = view.findViewById(R.id.current_month);
        selectedMonth = view.findViewById(R.id.selected_month);
        turnOffBtn = view.findViewById(R.id.button_turn_off);
        mealOfWrite = view.findViewById(R.id.meal_off_writing);
        calendarView = view.findViewById(R.id.calender);

        borderDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        bUid = mAuth.getCurrentUser().getUid();

        settingTime();
        mealStat();

        turnOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                i=0;

                AlertDialog.Builder alertDialogBuilder = new  AlertDialog.Builder(getActivity());

                if(currentStatus.matches("OFF")){
                    alertDialogBuilder.setTitle("Do you really want to turn ON your meal?");
                    alertDialogBuilder.setIcon(R.drawable.ic_decission);
                    alertDialogBuilder.setCancelable(false).setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id){
                            turnONOFF();
                            //Intent intent = new Intent(getActivity(), DashboardFragment.newInstance().getClass());
                            //startActivity(intent);
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id ){
                            dialog.cancel();
                        }
                    });
                    final AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }

                else if(currentStatus.matches("ON")){
                    if(desireDate - todayDate>=0 || (desireMonth-thisMonth<2 && desireMonth-thisMonth>=0)){
                        alertDialogBuilder.setTitle("Do you really want to turn OFF your meal?");
                        alertDialogBuilder.setIcon(R.drawable.ic_decission);
                        alertDialogBuilder.setCancelable(false).setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id){
                                turnONOFF();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id ){
                                dialog.cancel();
                            }
                        });
                        final AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                }
            }
        });


        return view;
    }

    public void turnONOFF(){
        DatabaseReference databaseReference = borderDatabase.child("manager").child(mUid).child("my_border").child(bUid);

        if(currentStatus.matches("ON")&& i==0){
            databaseReference.child("status").setValue("OFF");
            i++;
        }
        else if(currentStatus.matches("OFF") && i==0){
            databaseReference.child("status").setValue("ON");
            i++;
        }




    }


    public void mealStat(){

        borderDatabase.child("border").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {

                if(dataSnapshot2.child(bUid).hasChild("my_manager")){
                    mUid = dataSnapshot2.child(bUid).child("my_manager").getValue().toString();

                    borderDatabase.child("manager").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            currentStatus = dataSnapshot.child(mUid).child("my_border").child(bUid).child("status").getValue().toString();

                            if(currentStatus.matches("ON")){
                                mealOfWrite.setText("Your regular meal is ON now");
                                mealOfWrite.setTextColor(Color.parseColor("#4CAF50"));
                                turnOffBtn.setText("Turn off meal (temporary)");

                            }
                            else if(currentStatus.matches("OFF")) {
                                //mealOfWrite.setVisibility(View.VISIBLE);
                                mealOfWrite.setText("Your regular meal is OFF today");
                                mealOfWrite.setTextColor(Color.parseColor("#FFF44336"));
                                turnOffBtn.setText("Click to turn meal ON again");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else turnOffBtn.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void settingTime(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM 'at' hh:mm a");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        SimpleDateFormat monthFormatTEXT = new SimpleDateFormat("MMMM");

        todayDate = Integer.parseInt(dateFormat.format(calendar.getTime()));
        thisMonth = Integer.parseInt(monthFormat.format(calendar.getTime()));
        String monthTEXT = String.valueOf(monthFormatTEXT.format(calendar.getTime()));
        todayDate++;
        String dateStr = String.valueOf(todayDate);

        digitOne.setText(dateStr.substring(0,1));
        digitTwo.setText(dateStr.substring(1));
        digitThree.setText(dateStr.substring(0,1));
        digitFour.setText(dateStr.substring(1));
        currentMonth.setText(monthTEXT);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int i, int i1, int i2) {
                i1++;
                String selectedDay = i +"/"+ i1+"/"+i2;

                desireDate = i2;
                int month = i1;
                int year = i;

                String dateStr = String.valueOf(desireDate);

                if(desireDate>=todayDate-1 && month==thisMonth){
                    if(dateStr.length()==1){
                        digitThree.setText("0");
                        digitFour.setText(dateStr.substring(0,1));
                    }
                    else{
                        digitThree.setText(dateStr.substring(0,1));
                        digitFour.setText(dateStr.substring(1));
                    }
                }
                else if(month>thisMonth){
                    if(dateStr.length()==1){
                        digitThree.setText("0");
                        digitFour.setText(dateStr.substring(0,1));
                    }
                    else{
                        digitThree.setText(dateStr.substring(0,1));
                        digitFour.setText(dateStr.substring(1));
                    }
                }
                HashMap<Integer, String>mp = new HashMap<Integer, String>();

                mp.put(1,"January");
                mp.put(2,"February");
                mp.put(3,"March");
                mp.put(4,"April");
                mp.put(5,"May");
                mp.put(6,"June");
                mp.put(7,"July");
                mp.put(8,"August");
                mp.put(9,"September");
                mp.put(10,"October");
                mp.put(11,"November");
                mp.put(12,"December");

                selectedMonth.setText(mp.get(month));

                if(thisMonth==month){
                    if(desireDate- todayDate>1){
                        totalOff = desireDate - todayDate;
                        totalOff++;
                        turnOffBtn.setText("Turn off meal for "+totalOff+ " days");
                    }
                    else if(desireDate- todayDate==1){
                        totalOff = desireDate - todayDate;
                        totalOff++;
                        turnOffBtn.setText("Turn off meal for "+totalOff+ " day");
                    }
                    else if(desireDate- todayDate==0){
                        turnOffBtn.setText("Turn off meal for 1 day");
                    }
                    else if(desireDate-todayDate== -1){
                        turnOffBtn.setText("You can't off meal for today");
                    }
                    else{
                        turnOffBtn.setText("Past is past, forget it!");
                    }
                }
                else if(month - thisMonth ==1){
                    if(month==1 || month==3 ||month==5 ||month==7 ||month==8 ||month==10 ||month==12){
                        totalOff = 31-todayDate+1 + desireDate;
                        totalOff++;
                        turnOffBtn.setText("Turn off meal for "+totalOff+ " days");
                    }
                    else if(month ==2){
                        totalOff = 28-todayDate+1 + desireDate;
                        totalOff++;
                        turnOffBtn.setText("Turn off meal for "+totalOff+ " days");
                    }
                    else{
                        totalOff = 30-todayDate+1 + desireDate;
                        totalOff++;
                        turnOffBtn.setText("Turn off meal for "+totalOff+ " days");
                    }
                }
                else{
                    turnOffBtn.setText("Permission denied");
                }

            }
        });


    }

}
