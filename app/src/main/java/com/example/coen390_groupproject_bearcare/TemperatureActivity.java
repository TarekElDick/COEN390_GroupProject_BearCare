package com.example.coen390_groupproject_bearcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coen390_groupproject_bearcare.Bluetooth.BluetoothScanner;
import com.example.coen390_groupproject_bearcare.Bluetooth.MyBluetoothService;
import com.example.coen390_groupproject_bearcare.Model.Temperature;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;


public class TemperatureActivity extends AppCompatActivity {

    private TextView textViewTitleRecordTemp, textViewConfirm,textViewTempDisplay;
    private Button takeTemp, configureSensorsButton;
    private FloatingActionButton confirmButton;
    private String TAG;
    private String childName;
    private String childId;
    private String tempTimeStamp;
    private final FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    boolean sensorConnected;                                                                        // use this to display icons to user

    private TextView sensorconnectedTextView;
    private TextView sensornotconnectedTextView;
    private ImageView sensconView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);

        Intent intent =  getIntent();
        childName = intent.getStringExtra("childName");
        childId = intent.getStringExtra("childId");

        takeTemp = findViewById(R.id.buttonTakeTemp);
        confirmButton = findViewById(R.id.confirmButton);
        configureSensorsButton = findViewById(R.id.buttonConfigureSensor);
        textViewTempDisplay = findViewById(R.id.textView_TempDisplay);
        textViewTitleRecordTemp = findViewById(R.id.textView_RecordTemp);

        sensorconnectedTextView = findViewById(R.id.sensorconnectedTextView);
        sensornotconnectedTextView = findViewById(R.id.sensornotconnectedTextView);
        sensconView = findViewById(R.id.sensconView);

        sensorconnectedTextView.setVisibility(View.INVISIBLE);
        sensornotconnectedTextView.setVisibility(View.INVISIBLE);
        sensconView.setVisibility(View.INVISIBLE);

        TAG = "TemperatureActivity";

        try {
            if (MyBluetoothService.getMacAddress() == null) {
                MyBluetoothService.connectAutomatically();
            }
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }

        // on click listener for take temp button
        takeTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                double tempReading = MyBluetoothService.getReading();

                Calendar calendar = Calendar.getInstance();
                tempTimeStamp = DateFormat.getDateInstance().format(calendar.getTime());
                //tempTimeStamp = System.currentTimeMillis()/1000;
                Temperature temp = new Temperature(childName ,tempReading, tempTimeStamp );

                Log.d("TEMP", "Child ID of item clicked is: " + childId);
                Log.d("TEMP", "Child Name of item clicked is: " + childName);

                fStore.collection("Temperatures").document(childId)
                        .set(temp)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(TemperatureActivity.this, "Temperature Recorded", Toast.LENGTH_LONG).show();
                                Log.d(TAG, "Temperature added to fireStore");
                            }
                        });

                textViewTempDisplay.setText(tempReading + " °C");
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


    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void checkConnection()
    {
        sensorConnected=MyBluetoothService.checkConnected();
        if (sensorConnected==true){
            sensorconnectedTextView.setVisibility(View.VISIBLE);
            sensconView.setVisibility(View.VISIBLE);
        }
        else {sensornotconnectedTextView.setVisibility(View.VISIBLE);}
    }


}