package com.example.coen390_groupproject_bearcare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ChildProfileActivity extends AppCompatActivity {

    private Button buttonTakeTemp;

    private String childId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_profile);

        // Back Buttton takes you back to child directory
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = new Intent();
        childId = intent.getStringExtra("childId");
        setUpUI();

    }

    private void setUpUI() {

        // Connections
        buttonTakeTemp = findViewById(R.id.buttonTakeTemp_childProfileActivity);

        buttonTakeTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Go to takeTempActivity
                Intent intent = new Intent(getApplicationContext(), TemperatureActivity.class);
                intent.putExtra("childId", childId);
                startActivity(intent);

            }
        });

    }
}