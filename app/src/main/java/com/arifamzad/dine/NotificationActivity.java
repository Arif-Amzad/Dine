package com.arifamzad.dine;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class NotificationActivity extends AppCompatActivity {

    TextView mNotifData, mNotifName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        String dataMessage = getIntent().getStringExtra("message"); //name as taken from data in node.js
        String dataFrom = getIntent().getStringExtra("from_name"); ////name as taken from data in node.js

        mNotifData = findViewById(R.id.notif_text);
        mNotifName = findViewById(R.id.notif_manager);

        mNotifName.setTextColor(Color.parseColor("CE552B"));

        mNotifName.setText(dataFrom);
        mNotifData.setText(dataMessage);
    }
}
