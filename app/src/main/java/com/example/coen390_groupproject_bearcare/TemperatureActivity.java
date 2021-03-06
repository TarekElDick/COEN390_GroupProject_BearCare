package com.example.coen390_groupproject_bearcare;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TemperatureActivity extends AppCompatActivity {

    private TextView textViewTitleRecordTemp, textViewConfirm,textViewTempDisplay;
    private Button takeTemp;
    private FloatingActionButton confirmButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        takeTemp = findViewById(R.id.buttonTakeTemp);
        confirmButton = findViewById(R.id.confirmButton);
        textViewTempDisplay = findViewById(R.id.textView_TempDisplay);
        textViewConfirm = findViewById(R.id.textView_Confirm);
        textViewTitleRecordTemp = findViewById(R.id.textView_RecordTemp);
        setContentView(R.layout.activity_temperature);
    }
}