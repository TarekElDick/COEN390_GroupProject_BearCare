package com.example.coen390_groupproject_bearcare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coen390_groupproject_bearcare.Bluetooth.BluetoothScanner;
import com.example.coen390_groupproject_bearcare.Bluetooth.MyBluetoothService;
import com.example.coen390_groupproject_bearcare.Model.Temperature;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;


public class TemperatureActivity extends AppCompatActivity {

    private TextView textViewTitleRecordTemp, textViewConfirm, textViewTempDisplay;
    private Button takeTemp, configureSensorsButton, confirmTemp;
    private String TAG;
    private String childName;
    private String childId;
    private double tempReading;
    private String tempTimeStamp;
    private final FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    boolean sensorConnected;                                                                        // use this to display icons to user

    private TextView sensorConnectedTextView;
    private TextView sensorNotConnectedTextView;
    private ImageView sensConView, sensNotConView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);

        Intent intent = getIntent();
        childName = intent.getStringExtra("childName");
        childId = intent.getStringExtra("childId");

        Intent anotherIntent = new Intent(getApplicationContext(), TemperatureHistoryActivity.class);
        anotherIntent.putExtra("childId", childId);
        anotherIntent.putExtra("childName", childName);

        takeTemp = findViewById(R.id.buttonTakeTemp);
        confirmTemp = findViewById(R.id.buttonConfirmTemp);

        configureSensorsButton = findViewById(R.id.buttonConfigureSensor);
        textViewTempDisplay = findViewById(R.id.textView_TempDisplay);
        textViewTitleRecordTemp = findViewById(R.id.textView_RecordTemp);

        sensorConnectedTextView = findViewById(R.id.sensorconnectedTextView);
        sensorNotConnectedTextView = findViewById(R.id.sensornotconnectedTextView);
        sensConView = findViewById(R.id.sensorconnectedView);
        sensNotConView = findViewById(R.id.sensorNotconnectedView);

        sensorConnectedTextView.setVisibility(View.INVISIBLE);
        sensorNotConnectedTextView.setVisibility(View.INVISIBLE);
        sensConView.setVisibility(View.INVISIBLE);

        TAG = "TemperatureActivity";

        checkConnection();

        // on click listener for take temp button
        takeTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tempReading = MyBluetoothService.getReading();
                //double tempReading = 0.0;

                textViewTempDisplay.setText(tempReading + " °C");

                confirmTemp.setVisibility(View.VISIBLE);
            }
        });

        confirmTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                tempTimeStamp = DateFormat.getDateInstance().format(calendar.getTime());
                Log.d(TAG, "onClick: " + tempTimeStamp);

                Temperature temp = new Temperature(tempReading, tempTimeStamp);

                Log.d("TEMP", "Child ID of item clicked is: " + childId);
                Log.d("TEMP", "Child Name of item clicked is: " + childName);

                Log.d(TAG, "onClick: ordered by date");
                fStore.collection("Children").document(childId).collection("Temperatures").document()
                        .set(temp)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(TemperatureActivity.this, getString(R.string.temperature_recorded), Toast.LENGTH_LONG).show();
                                Log.d(TAG, "Temperature added to fireStore");
                            }
                        });
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
    protected void onStart() {
        super.onStart();
        checkConnection();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConnection();
        confirmTemp.setVisibility(View.INVISIBLE);
    }

    public void checkConnection() {
        sensorConnected = MyBluetoothService.checkConnected();
        if (sensorConnected == true) {
            sensorNotConnectedTextView.setVisibility(View.INVISIBLE);
            sensorConnectedTextView.setVisibility(View.VISIBLE);
            sensNotConView.setVisibility(View.INVISIBLE);
            sensConView.setVisibility(View.VISIBLE);
            takeTemp.setEnabled(true);
        } else {
            sensorConnectedTextView.setVisibility(View.INVISIBLE);
            sensConView.setVisibility(View.INVISIBLE);
            sensNotConView.setVisibility(View.VISIBLE);
            sensorNotConnectedTextView.setVisibility(View.VISIBLE);
            takeTemp.setEnabled(false);
            try {
                MyBluetoothService.connectAutomatically();
            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyBluetoothService.close();
    }

}