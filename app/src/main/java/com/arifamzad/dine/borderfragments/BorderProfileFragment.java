package com.arifamzad.dine.borderfragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arifamzad.dine.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class BorderProfileFragment extends Fragment {

    TextView borderName, borderPhone, borderEmail;
    DatabaseReference borderDatabase;
    FirebaseAuth mAuth;

    public static BorderProfileFragment newInstance(){
        BorderProfileFragment fragment = new BorderProfileFragment();
        return fragment;
    }

    public BorderProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_border_profile, container, false);

        borderName = view.findViewById(R.id.profile_border_name);
        borderEmail = view.findViewById(R.id.profile_border_email);
        borderPhone = view.findViewById(R.id.profile_border_contact);

        borderDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        borderProfile();

        return view;
    }

    public void borderProfile(){
        String currentUserId = mAuth.getCurrentUser().getUid();

        borderDatabase.child("border").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String bName = dataSnapshot.child("name").getValue().toString();
                String bEmail = dataSnapshot.child("email").getValue().toString();
                String bPhone = dataSnapshot.child("phone").getValue().toString();

                borderName.setText(bName);
                borderEmail.setText(bEmail);
                borderPhone.setText(bPhone);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
