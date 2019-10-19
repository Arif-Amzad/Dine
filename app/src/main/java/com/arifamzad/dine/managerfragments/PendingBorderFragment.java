package com.arifamzad.dine.managerfragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.arifamzad.dine.BorderDrawerActivity;
import com.arifamzad.dine.BorderLoginActivity;
import com.arifamzad.dine.R;
import android.support.v4.app.Fragment;

import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.arifamzad.dine.patternManagement.BorderReq;
import com.arifamzad.dine.patternManagement.BorderReqCustomAdapter;
import com.arifamzad.dine.patternManagement.MyBorder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class PendingBorderFragment extends Fragment {

    LinearLayout reqView;
    RecyclerView reqList;
    DatabaseReference reqDatabase, requestDatabase, myBorderDatabase, myManagerDatabase;
    FirebaseAuth mAuth;
    public String managerId;
    public Button acceptBtn, decline;
    boolean b;

    //private List<BorderReq>list =new ArrayList<>();
    private List<BorderReq> list = new ArrayList<BorderReq>();
    private BorderReqCustomAdapter reqAdapter;

    public static PendingBorderFragment newInstance(){
        PendingBorderFragment fragment= new PendingBorderFragment();
        return fragment;
    }

    public PendingBorderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_pending_border, container, false);

        //FragmentTransaction ft = getFragmentManager().beginTransaction();
        //ft.detach(this).attach(this).commit();


        reqView = view.findViewById(R.id.reqView);
        reqList = view.findViewById(R.id.request_list);
        acceptBtn = view.findViewById(R.id.accept);
        decline = view.findViewById(R.id.cancel_req);


        mAuth = FirebaseAuth.getInstance();
        managerId = mAuth.getCurrentUser().getUid();

        reqDatabase = FirebaseDatabase.getInstance().getReference().child("manager").child(managerId);
        reqDatabase.keepSynced(true);

        myBorderDatabase = FirebaseDatabase.getInstance().getReference().child("manager").child(managerId).child("my_border");
        requestDatabase = FirebaseDatabase.getInstance().getReference().child("manager").child(managerId).child("request");
        myManagerDatabase = FirebaseDatabase.getInstance().getReference().child("border");

        reqAdapter = new BorderReqCustomAdapter(list, getActivity());
        reqList.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        //layoutManager.setReverseLayout(true);
        //layoutManager.setStackFromEnd(true);
        reqList.setLayoutManager(layoutManager);

        reqChildExist();
        reqPostShow();

        //int k=0;

        //int moneyGet =getArguments().getInt("money_get");



        return view;
    }

    @Override
    public void onResume() {
        super.onResume();


        //reqPostShow();
        list.clear();

    }

    public void reqPostShow(){

        Query query = reqDatabase.child("request");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                if(b==true) {

                    for (DataSnapshot postData : dataSnapshot.getChildren()) {

                        BorderReq borderReq = new BorderReq();

                        borderReq.setName(postData.child("name").getValue().toString());
                        borderReq.setPhone(postData.child("phone").getValue().toString());
                        borderReq.setUid(postData.child("uid").getValue().toString());

                        list.add(borderReq);

                        reqList.setAdapter(reqAdapter);

                        reqAdapter.notifyDataSetChanged();
                        //Log.e("NAME :: ", postData.child("name").getValue().toString());
                        //Log.e("KEY :: ", postData.getKey().toString());

                        //Toast.makeText(getActivity(), "You have "+list.size()+" border requests", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        /*

        FirebaseRecyclerAdapter<BorderReq, PostViewHolder>adapter = new FirebaseRecyclerAdapter<BorderReq, PostViewHolder>(

                BorderReq.class,
                R.layout.pattern_request,
                PostViewHolder.class,
                reqDatabase.child("request")
        ) {
            protected void populateViewHolder(final PostViewHolder viewHolder, BorderReq model, int position) {

                viewHolder.setName(model.getName());
                viewHolder.setPhone(model.getPhone());
                viewHolder.setUid(model.getUid());

            }
        };
        reqList.setAdapter(adapter);
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        View pView;

        DatabaseReference databaseReference;
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            pView= itemView;
        }

        public void setName(String name){
            TextView post_name = pView.findViewById(R.id.reqShow_name);
            post_name.setText(name);
        }

        public void setPhone(String phone){
            TextView phonep = pView.findViewById(R.id.reqShow_phone);
            phonep.setText(phone);
        }
       public void setUid(String uid){
            TextView phonep = pView.findViewById(R.id.invisible_uid);
            phonep.setText(uid);
        }
        */
    }

    public void reqChildExist(){
        reqDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {

                if(dataSnapshot2.hasChild("request")) {
                    b=true;
                }
                else{
                    b=false;
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }





}
