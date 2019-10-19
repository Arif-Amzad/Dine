package com.arifamzad.dine.patternManagement;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.arifamzad.dine.R;
import com.arifamzad.dine.managerfragments.BorderListFragment;
import com.arifamzad.dine.managerfragments.MealPostFragment;
import com.arifamzad.dine.managerfragments.PendingBorderFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyBorderAdapter extends RecyclerView.Adapter<MyBorderAdapter.myBorderHolder> {

    private List<MyBorder> mbList = new ArrayList<>();
    private Context context;
    private FirebaseAuth mAuth;
    DatabaseReference myBorderDatabase;
    DatabaseReference borderDatabase;
    String managerId;
    int newMoney;
    int totalMoneyHaveINT;
    int i,k=0;
    //private DatabaseReference myBorderDatabase, requestDatabase;
    //private FirebaseAuth mAuth;


    public MyBorderAdapter(List<MyBorder>mbList,Context context){
        this.mbList= mbList;
        this.context=context;

    }
    @NonNull
    @Override
    public MyBorderAdapter.myBorderHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View view = LayoutInflater.from(context).inflate(R.layout.pattern_my_border,parent,false);

        mAuth = FirebaseAuth.getInstance();
        managerId= mAuth.getCurrentUser().getUid();
        myBorderDatabase = FirebaseDatabase.getInstance().getReference().child("manager");
        //mAuth = FirebaseAuth.getInstance();
        //String managerId= mAuth.getCurrentUser().getUid();
        //myBorderDatabase = FirebaseDatabase.getInstance().getReference().child("manager").child(managerId).child("my_border");

        myBorderHolder myBorderHolder = new myBorderHolder(view);

        return myBorderHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyBorderAdapter.myBorderHolder myBorderHolder, final int position) {

        final MyBorder myBorder = mbList.get(position);

        //myBorderHolder.textViewName.setText(myBorder.name);
        //myBorderHolder.textViewPhone.setText(myBorder.phone);
        //myBorderHolder.textUid.setText(myBorder.uid);

        myBorderHolder.forExpand(myBorder);

        myBorderHolder.itemView.setOnClickListener(v -> {

            boolean expanded = myBorder.isExpanded();
            myBorder.setExpanded(!expanded);
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return mbList.size();
    }

    public class myBorderHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView textViewName;
        TextView textViewPhone;
        TextView textUid;
        TextView textDayOn;
        TextView textDayOff;
        TextView textPayment;
        TextView statusShow;
        LinearLayout layoutStatus;
        LinearLayout layoutDayOn;
        LinearLayout layoutDayOff;
        LinearLayout layoutPaid;
        EditText updatePaymentBox;
        Button updatePayment;


        public myBorderHolder(@NonNull View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.show_name);
            textViewPhone = itemView.findViewById(R.id.show_phone);
            textUid = itemView.findViewById(R.id.invisible_uid);
            textDayOn = itemView.findViewById(R.id.day_on);
            textPayment = itemView.findViewById(R.id.payment);
            statusShow = itemView.findViewById(R.id.status);
            //textDayOff = itemView.findViewById(R.id.day_off);

            layoutStatus = itemView.findViewById(R.id.show_status);
            layoutPaid = itemView.findViewById(R.id.show_paid);
            layoutDayOn = itemView.findViewById(R.id.show_day_on);
            //layoutDayOff = itemView.findViewById(R.id.show_day_off);

            updatePaymentBox = itemView.findViewById(R.id.update_payment_box);
            updatePayment = itemView.findViewById(R.id.button_update_payment);

            updatePayment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    MyBorder myBorder = mbList.get(getAdapterPosition());

                    String paid = myBorder.paid;
                    String uid = myBorder.uid;


                    String regexStr = "^[0-9]*$";
                    //
                    String minus = "-";
                    String plus = "+";

                    String n = updatePaymentBox.getText().toString().trim();

                    if(n.length()==0 || n==minus || n==plus){

                    }
                    else if(n.length()!= 0){

                        i = Integer.parseInt(String.valueOf(n));
                        int j = Integer.parseInt(String.valueOf(paid));
                        int sum = i+j;

                        newMoney = i;

                        String sumint = String.valueOf(sum);

                        myBorder.setPaid(sumint);

                        if(n.matches("")){

                        }
                        else{
                            moneyAdd();
                            updatePaymentBox.setText("");
                        }






                        DatabaseReference newRef = myBorderDatabase.child(managerId).child("my_border").child(uid);
                        newRef.child("paid").setValue(sumint);

                    }
                    //moneyAdd();
                }

            });

            itemView.setOnClickListener(this);
        }

        private void forExpand(MyBorder myBorder){

            boolean expanded = myBorder.isExpanded();
            //textUid.setVisibility(expanded ? View.VISIBLE : View.GONE);
            layoutStatus.setVisibility(expanded ? View.VISIBLE : View.GONE);
            layoutDayOn.setVisibility(expanded ? View.VISIBLE : View.GONE);
            //layoutDayOff.setVisibility(expanded ? View.VISIBLE : View.GONE);
            layoutPaid.setVisibility(expanded ? View.VISIBLE : View.GONE);

            textViewName.setText(myBorder.name);
            textViewPhone.setText(myBorder.phone);
            textUid.setText(myBorder.uid);
            textDayOn.setText(myBorder.days_on);
            textPayment.setText(myBorder.paid);
            statusShow.setText(myBorder.status);

            if(myBorder.status.matches("ON")){
                statusShow.setTextColor(Color.parseColor("#4CAF50"));
            }
            else{
                statusShow.setTextColor(Color.parseColor("#FFF44336"));
            }
        }

        @Override
        public void onClick(View view) {

        }
    }

    public void moneyAdd(){

        final int[] c = {0};

        myBorderDatabase.child(managerId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String moneyHave = dataSnapshot.child("total_money_have").getValue().toString();
                int moneyHaveInt = Integer.parseInt(moneyHave);

                int sum = newMoney + moneyHaveInt;

                DatabaseReference moneyRef = myBorderDatabase.child(managerId);

                if (c[0] ==0){
                    moneyRef.child("total_money_have").setValue(sum);
                    c[0]++;
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

}
