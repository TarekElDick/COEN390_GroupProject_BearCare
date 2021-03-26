package com.example.coen390_groupproject_bearcare.Bluetooth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coen390_groupproject_bearcare.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Handler;

public class BluetoothScanner extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;                              // bluetooth adapter object
    ArrayList<BluetoothDevice> discoveredDevices = new ArrayList<>();       // array list to hold discovered devices
    private int REQUEST_ENABLE_LOCATION = 1;                           // REQUEST LOCATION VALUE
    private int REQUEST_CODE_CHECK_SETTINGS = 2;
    private boolean locationStatus;                                    // this boolean will be used to determine if location services are turned on
    private ListView deviceList;
    private static final String TAG =  "BluetoothScanner";
    private ArrayList<String> macList;                                // list of all mac addresses, in the order they are presented on screen
    private Button discoverButton;                                    // used for discovering BearCare hardware
    boolean bluetoothOn;                                              // to fix the issue where the paired list isn't showing up if bluetooth is enabled on entering activity



    private  static final String TAG1 = "Bluetooth Scanner";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_scanner);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        deviceList = findViewById(R.id.device_list);
        discoverButton = findViewById(R.id.button_Discover);
        TextView titleText = (TextView) findViewById(R.id.textViewPairedDevices);
        deviceList.setOnItemClickListener(messageClickedHandler);

        macList = new ArrayList<>();    // this list holds the mac addresses of the paired or discovered bearcare devices, depending on context

        IntentFilter pairFilter =  new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);     // this filter needs to be used when pairing to a device
        registerReceiver(pairReceiver,pairFilter);

        IntentFilter intentFilter = new IntentFilter((BluetoothDevice.ACTION_FOUND));           // this filter needs to be created for using the discovery function
        registerReceiver(btReceiver,intentFilter);                                               // register the receiver

        IntentFilter discoveryDoneFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(btDiscoveryDone,discoveryDoneFilter);



        if(bluetoothAdapter == null)
        {
            Toast.makeText(this,"This device does not support Bluetooth.",Toast.LENGTH_SHORT).show();
        }
        checkBluetoothOn(null);

        if(bluetoothOn == true)
            list(null);

        discoverButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)                       // needed for the checking of location status minimum API must be maintained for use
            @Override
            public void onClick(View v) {
                checkLocationPermission(null);
                checkLocationStatus();

                titleText.setText("Discovered Bluetooth Devices");

                discoveredDevices.clear(); // clear the list, otherwise we will have device doubles in the listview

                bluetoothAdapter.startDiscovery();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(btReceiver);
        unregisterReceiver(pairReceiver);
        unregisterReceiver(btDiscoveryDone);
    }


    private final   BroadcastReceiver btDiscoveryDone = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                listDiscovered();
            }
        }
    };

    private final BroadcastReceiver btReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action))
            {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, "onReceive: " + device.getName() + ": Device Address " + device.getAddress());

                if (device.getName() != null)
                {
                    if (device.getName().equals("BearCare Temperature Sensor")) {
                        discoveredDevices.add(device);
                    }
                }

            }
        }

    };
    
    BroadcastReceiver pairReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {             // this used to pair objects from the discovered list
            final String action = intent.getAction();
            if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action))
            {   // will use 3 if statements now
                BluetoothDevice localBluetoothObject = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(localBluetoothObject.getBondState() == BluetoothDevice.BOND_BONDED)      // 1: device is already paired
                {
                    Log.d(TAG1, "onReceive: BOND_BONDED");

                }
                if(localBluetoothObject.getBondState() == BluetoothDevice.BOND_BONDING)     // 2: create a pair
                {
                    Log.d(TAG1, "onReceive: BOND_PAIRING");

                }
                if (localBluetoothObject.getBondState() == BluetoothDevice.BOND_NONE)      // 3: pair is broken
                {
                    Log.d(TAG1, "onReceive: BOND_BROKEN");

                }

            }
        }
    };

    public void checkBluetoothOn(View v){           // check if bluetooth is on
        if (!bluetoothAdapter.isEnabled()) {        // its not enabled
            bluetoothOn = false;                    // bt is not on set to false
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); // this constant is used to request to turn on BS services
            startActivityForResult(turnOn, 1);
            Toast.makeText(getApplicationContext(), "Please turn on Bluetooth.",Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(getApplicationContext(), "Bluetooth services are enabled.", Toast.LENGTH_LONG).show();
            bluetoothOn = true;                 // set the boolean to true - bluetooth is enabled
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {           // this is called when StartActivityForResult is done - calls the list function so its not empty on activity
                                                                                                        // entry
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Bluetooth enabled", Toast.LENGTH_LONG).show();
                list(null);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "User canceled", Toast.LENGTH_LONG).show();
            }
        }
        else if(requestCode ==2)
        {
            if(REQUEST_CODE_CHECK_SETTINGS == requestCode){
                Log.d(TAG, "onActivityResult: REACHED REQUEST CODE 2");
                bluetoothAdapter.startDiscovery();
                Toast.makeText(this,"Location Enabled Begin Scanning for Bluetooth Devices.",Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "onActivityResult: REACH REQUEST CODE 2 CANCELLED");
                Toast.makeText(this, "User canceled", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void checkLocationPermission(View v){            // check if location services are on and request permission for access
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(this,"You have granted BearCare permission to access your location.", Toast.LENGTH_SHORT).show();
        }else{
            requestLocationPermission();
        }
    }

    public void requestLocationPermission()         // this function will be used to request permission from the user to access location
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission Needed")
                    .setMessage("To discover BearCare Sensors, location service permission must be granted")
                    .setPositiveButton("Grant Permission", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setNegativeButton("Deny Permission", new DialogInterface.OnClickListener() {       // if the user does not want to give permission dismiss
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ENABLE_LOCATION);
        }

    }

    public void buildAlertMessageLocation(boolean locationService)         // this is going to be used to turn location services on
    {
        if(locationService == true)                                        // if the boolean is true do nothing
            return;                                                        // return
        else{
            new AlertDialog.Builder(this)                         // creates a new alert dialog
                    .setTitle("Enable Location Services")
                    .setMessage("To discover BearCare Sensors, location services must be turned on")
                    .setPositiveButton("Turn on Location Services", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent enableLocation = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(enableLocation, 2);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)           // this is required to use location services
    public void checkLocationStatus() {                 // does the initial check to see if location services are enabled
        Context context = getApplicationContext();      // local context
        LocationManager x = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);   // location manager object x declared
        locationStatus = x.isLocationEnabled();                                                     // returns true or false depending on if location services are enabled
        buildAlertMessageLocation(locationStatus);                                                  // call the function buildAlertMessageLocation with the  boolean locationStatus
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {       // this is the result of the requestLocationPermission function
        if(requestCode == REQUEST_ENABLE_LOCATION)
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else
            {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void listDiscovered()        // making a separate list function for discovered devices as a test for now please review for efficiency 03/20/21 RH
    {
        ArrayList<String> foundDevices = new ArrayList<>();       // testing delete if it doesnt work

        for (BluetoothDevice devices: discoveredDevices)
        {
            String nameAddress = devices.getName() + "\n" + "Device Address: " + devices.getAddress();
            foundDevices.add(nameAddress);
            Log.d(TAG, "listDiscovered: " + nameAddress);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, foundDevices);
        deviceList.setAdapter(adapter);
    }

    public void list(View v){       // get the paired devices and list them on screen
        Set<BluetoothDevice> pairedDevices = MyBluetoothService.getAllPairedDevices();

        ArrayList<String> pairedDevicesStrings = new ArrayList<>();         // this is going to be used to hold all paired devices

        for(BluetoothDevice devices : pairedDevices) {

            String deviceName = devices.getName();          // get the name store it in a string
            String devicesAddress = devices.getAddress(); // get the MAC address store it in a string
            macList.add(devicesAddress);    // store this for later when we click an element in the devices list

            String nameAddress = deviceName + "\n" + "Device Address: " + devicesAddress;     // concatenate the two strings
            if (nameAddress.contains("BearCare"))
                pairedDevicesStrings.add(nameAddress);
        }

        final ArrayAdapter<String> adapter = new  ArrayAdapter(this,android.R.layout.simple_list_item_1,pairedDevicesStrings);

        deviceList.setAdapter(adapter);
    }

    // define what happens when we click an item in the list
    private final AdapterView.OnItemClickListener messageClickedHandler = new AdapterView.OnItemClickListener() {

        public void onItemClick(AdapterView parent, View v, int position, long id) {

            BluetoothDevice bdDevice;                   // use this local object to pair to the selected BT device
            bluetoothAdapter.cancelDiscovery();         // cancel discovery every time an attempt to make a connection is made
            String macAddress = macList.get((int)id);
            Log.i(TAG, "MAC address clicked: " + macAddress);
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {            // this checks if the phone has the correct api to use the method below
                Log.d(TAG1, "onItemClick: Trying to pair with: " + macAddress);
               bdDevice= discoveredDevices.get(position);
               bdDevice.createBond();
            }

        }
    };

}