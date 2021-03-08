package com.example.coen390_groupproject_bearcare.Bluetooth;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.coen390_groupproject_bearcare.R;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothScanner extends AppCompatActivity {


    private BluetoothAdapter bluetoothAdapter;      // bluetooth adapter object
    private Set<BluetoothDevice> pairedDevices;          // paired devices
    private int REQUEST_ENABLE_BLUETOOTH = 1;       // REQUEST BLUETOOTH VALUE
    private ListView deviceList;
    private String EXTRA_ADDRESS = "Device_Address";    // device address to be shared between activities
    private AdapterView.OnItemClickListener deviceClicked;
    private Button refreshDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_scanner);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        deviceList = findViewById(R.id.device_list);

        if(bluetoothAdapter == null)
        {
            Toast.makeText(this,"This device does not support Bluetooth.",Toast.LENGTH_SHORT).show();
        }
        checkBluetoothOn(null);
        list(null);


    }


    public void checkBluetoothOn(View v){           // check if bluetooth is on
        if (!bluetoothAdapter.isEnabled()) {        // its not enabled
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); // this constant is used to request to turn on BS services
            startActivityForResult(turnOn, 0);
            Toast.makeText(getApplicationContext(), "Please turn on Bluetooth.",Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(getApplicationContext(), "Bluetooth services are enabled.", Toast.LENGTH_LONG).show();
        }
    }



    public  void visible(View v){
        Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(getVisible, 0);
    }


    public void list(View v){       // get the paired devices names and MAC address
        pairedDevices = bluetoothAdapter.getBondedDevices();

        ArrayList list = new ArrayList();

        for(BluetoothDevice devices : pairedDevices) {

            String deviceName = devices.getName();          // get the name store it in a string
            String devicesAddress = devices.getAddress(); // get the MAC address store it in a string
            String nameAddress = deviceName + "\n" + "Device Address: " + devicesAddress;     // concatenate the two strings
            list.add(nameAddress);

        }
        Toast.makeText(getApplicationContext(), "Showing Paired Devices",Toast.LENGTH_SHORT).show();

        final ArrayAdapter adapter = new  ArrayAdapter(this,android.R.layout.simple_list_item_1, list);

        deviceList.setAdapter(adapter);
    }

}