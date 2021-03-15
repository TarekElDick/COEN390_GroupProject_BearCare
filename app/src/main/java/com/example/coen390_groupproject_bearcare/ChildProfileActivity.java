package com.example.coen390_groupproject_bearcare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ChildProfileActivity extends AppCompatActivity {

    private TextView textViewChildName;
    private Button buttonTakeTemp;

    private String childId;
    private String childName;

    String TAG = "debug_childprofile";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_profile);

        // Back Button takes you back to child directory
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        childId = intent.getStringExtra("childId");
        childName = intent.getStringExtra("childName");

        Log.d(TAG, "Child ID of item clicked is: " + childId);
        Log.d(TAG, "Child Name of item clicked is: " + childName);

        setUpUI();

    }

    private void setUpUI() {

        // Connections
        buttonTakeTemp = findViewById(R.id.buttonTakeTemp_childProfileActivity);
        textViewChildName = findViewById(R.id.textViewChildName_childProfile);

        buttonTakeTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Go to takeTempActivity
                Intent intent = new Intent(getApplicationContext(), TemperatureActivity.class);
                intent.putExtra("childId", childId);
                intent.putExtra("childName", childName);
                Log.d(TAG, "Child ID of item clicked is: " + childId);
                Log.d(TAG, "Child Name of item clicked is: " + childName);
                startActivity(intent);

            }
        });

        textViewChildName.setText(childName);

    }
}