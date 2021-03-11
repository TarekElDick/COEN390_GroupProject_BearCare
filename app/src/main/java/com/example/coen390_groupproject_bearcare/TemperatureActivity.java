package com.example.coen390_groupproject_bearcare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.coen390_groupproject_bearcare.Bluetooth.BluetoothScanner;
import com.example.coen390_groupproject_bearcare.Bluetooth.MyBluetoothService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;

public class TemperatureActivity extends AppCompatActivity {

    private TextView textViewTitleRecordTemp, textViewConfirm,textViewTempDisplay;
    private Button takeTemp, configureSensorsButton;
    private FloatingActionButton confirmButton;
    private MyBluetoothService myBluetoothService;
    private String TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);

        takeTemp = findViewById(R.id.buttonTakeTemp);
        confirmButton = findViewById(R.id.confirmButton);
        configureSensorsButton = findViewById(R.id.buttonConfigureSensor);
        textViewTempDisplay = findViewById(R.id.textView_TempDisplay);
        textViewConfirm = findViewById(R.id.textView_Confirm);
        textViewTitleRecordTemp = findViewById(R.id.textView_RecordTemp);

        TAG = "TemperatureActivity";

        myBluetoothService = new MyBluetoothService();

        try {
            // todo replace this hard-coded mac address
            myBluetoothService.connectBluetoothDevice("24:6F:28:1A:D3:26");
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }

        // on click listener for take temp button
        takeTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double tempReading = myBluetoothService.getReading();
                textViewTempDisplay.setText(Double.toString(tempReading) + " °C");
            }
        });

        configureSensorsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), BluetoothScanner.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        MyBluetoothService.close();
    }
}