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

    private BluetoothAdapter bluetoothAdapter;                                                      // bluetooth adapter object
    ArrayList<BluetoothDevice> discoveredDevices = new ArrayList<>();                               // array list to hold discovered devices
    ArrayList<BluetoothDevice> addedBluetoothDevices = new ArrayList<>();                           // this array is used to ensure that the index and size of paired devices matches discoveredDevices
    private int REQUEST_ENABLE_LOCATION = 1;                                                        // REQUEST LOCATION VALUE
    private int REQUEST_CODE_CHECK_SETTINGS = 2;                                                    // THIS IS USED TO CHECK THE RESULTS OF LOCATION SERVICES ENABLED
    private boolean locationStatus;                                                                 // this boolean will be used to determine if location services are turned on
    private ListView deviceList;
    private static final String TAG =  "BluetoothScanner";
    private Button discoverButton;                                                                  // used for discovering BearCare hardware
    boolean bluetoothOn;                                                                            // to fix the issue where the paired list isn't showing up if bluetooth is enabled on entering activity
    boolean locationOn;                                                                             // to fix the issue where discovery isn't notifying users

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

        IntentFilter pairFilter =  new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);     // this filter needs to be used when pairing to a device
        registerReceiver(pairReceiver,pairFilter);                                                  // register the receiver

        IntentFilter foundFilter = new IntentFilter((BluetoothDevice.ACTION_FOUND));                // this filter needs to be created for using the discovery function
        registerReceiver(btReceiver,foundFilter);                                                   // register the receiver

        IntentFilter discoveryDoneFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);    // this filter is used to determine if bluetooth discovery is done
        registerReceiver(btDiscoveryDone,discoveryDoneFilter);                                      // register the receiver

        IntentFilter connectFilter = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);        // this filter is used to determine if the bluetooth device is connected
        registerReceiver(connectReceiver,connectFilter);                                            // register the receiver

        if(bluetoothAdapter == null)                                                                // the device does not support bluetooth if this statement is true
        {
            Toast.makeText(this,"This device does not support Bluetooth.",Toast.LENGTH_SHORT).show();
        }
        checkBluetoothOn(null);                                                                  // check if BT is enabled

        if(bluetoothOn == true)                                                                     // boolean is used to determine if list() should be called
            list(null);

        discoverButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)                                               // needed for the checking of location status minimum API must be maintained for use
            @Override
            public void onClick(View v) {
                checkLocationPermission(null);
                checkLocationStatus();

                titleText.setText("Discovered Bluetooth Devices");

                discoveredDevices.clear();                                                          // clear the list, otherwise we will have device doubles in the listView

                bluetoothAdapter.startDiscovery();
                if(locationOn == true)
                    Toast.makeText(getApplicationContext(),"Bluetooth discovery started, this will take 12 seconds.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {                                                                    // on destroy unregister all receivers
        super.onDestroy();
        unregisterReceiver(btReceiver);
        unregisterReceiver(pairReceiver);
        unregisterReceiver(btDiscoveryDone);
        unregisterReceiver(connectReceiver);
    }


    private final BroadcastReceiver connectReceiver = new BroadcastReceiver() {                     // this is to check connection status of bluetooth device
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);       // create local Bluetooth device object
            if(BluetoothDevice.ACTION_ACL_CONNECTED.equals(action) ){

                if(device.getBondState() == BluetoothDevice.BOND_BONDED)                            // make sure that this message only displays if the device is paired to the phone
                    Toast.makeText(getApplicationContext(), "Connected to device: " + device.getName(), Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplicationContext(),"Connection to " + device.getName() + " failed or cancelled.",Toast.LENGTH_SHORT).show();
            }
        }
    };

    private final   BroadcastReceiver btDiscoveryDone = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                Toast.makeText(getApplicationContext(),"Bluetooth discovery finished.",Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getApplicationContext(),"Paired to: " + localBluetoothObject.getName(), Toast.LENGTH_SHORT).show();
                }
                if(localBluetoothObject.getBondState() == BluetoothDevice.BOND_BONDING)     // 2: create a pair
                {
                    Log.d(TAG1, "onReceive: BOND_PAIRING");
                    Toast.makeText(getApplicationContext(),"Pairing to: " + localBluetoothObject.getName(), Toast.LENGTH_SHORT).show();

                }
                if (localBluetoothObject.getBondState() == BluetoothDevice.BOND_NONE)      // 3: pair is broken
                {
                    Log.d(TAG1, "onReceive: BOND_BROKEN");
                    Toast.makeText(getApplicationContext(),"Pair broken with: " + localBluetoothObject.getName(), Toast.LENGTH_SHORT).show();

                }

            }
        }
    };

    public void checkBluetoothOn(View v){                                                           // check if bluetooth is on
        if (!bluetoothAdapter.isEnabled()) {                                                        // its not enabled
            bluetoothOn = false;                                                                    // bt is not on set to false
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);                     // this constant is used to request to turn on BS services
            startActivityForResult(turnOn, 1);                                          // this is used to check if turning on bluetooth was successful
            Toast.makeText(getApplicationContext(), "Please turn on Bluetooth.",Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(getApplicationContext(), "Bluetooth services are enabled.", Toast.LENGTH_LONG).show();
            bluetoothOn = true;                                                                     // set the boolean to true - bluetooth is enabled
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
                Toast.makeText(this,"Location services enabled. Click on discover to find BearCare Hardware.",Toast.LENGTH_SHORT).show();
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

    public void requestLocationPermission()                                                         // this function will be used to request permission from the user to access location
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission Needed")
                    .setMessage("To discover BearCare Sensors, location service permission must be granted")
                    .setPositiveButton("Grant Permission", new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.P)
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

    public void buildAlertMessageLocation(boolean locationService)                                  // this is going to be used to turn location services on
    {
        if(locationService == true)                                                                 // if the boolean is true do nothing
        {
            locationOn = true;                                                                      // location service is on
            return;                                                                                 // return
        }
        else{
            new AlertDialog.Builder(this)                                                   // creates a new alert dialog
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

    @RequiresApi(api = Build.VERSION_CODES.P)                                                       // this API check is required to use location services
    public void checkLocationStatus() {                                                             // does the initial check to see if location services are enabled
        Context context = getApplicationContext();                                                  // local context
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

    public void listDiscovered()                                                                    // making a separate list function for discovered devices as a test for now please review for efficiency 03/20/21 RH
    {
        ArrayList<String> foundDevices = new ArrayList<>();

        for (BluetoothDevice devices: discoveredDevices)
        {
            String nameAddress = devices.getName() + "\n" + "Device Address: " + devices.getAddress();
            foundDevices.add(nameAddress);
            Log.d(TAG, "listDiscovered: " + nameAddress);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, foundDevices);

        deviceList.setAdapter(adapter);

    }

    public void list(View v){                                                                       // get the paired devices and list them on screen
        Set<BluetoothDevice> pairedDevices = MyBluetoothService.getAllPairedDevices();

        ArrayList<String> pairedDevicesStrings = new ArrayList<>();                                 // this is going to be used to hold all paired devices

        for(BluetoothDevice devices : pairedDevices) {

            if(devices.getName() != null){
                if(devices.getName().equals("BearCare Temperature Sensor")) {
                    pairedDevicesStrings.add(devices.getName() + "\n" + "Device Address: " + devices.getAddress());
                    addedBluetoothDevices.add(devices);
                }

            }

        }

        final ArrayAdapter<String> adapter = new  ArrayAdapter(this,android.R.layout.simple_list_item_1,pairedDevicesStrings);

        deviceList.setAdapter(adapter);
    }
                                                                                                    // define what happens when we click an item in the list
    private final AdapterView.OnItemClickListener messageClickedHandler = new AdapterView.OnItemClickListener() {

        public void onItemClick(AdapterView parent, View v, int position, long id) {

            try {
                pairOrConnect(position);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };

public void pairOrConnect(int position) throws IOException {
    BluetoothDevice bdDevice,bdDevice1;                                                             // create local bluetooth device
    if(discoveredDevices.size() > 0)
        bdDevice = discoveredDevices.get(position);                                                 // set it equal to the device selected from the list
    else
    {
        bdDevice = addedBluetoothDevices.get(position);                                             // if this statement is hit then the paired list is shown connect to the paired device
    }
    if(bdDevice.getBondState() != BluetoothDevice.BOND_BONDED)                                      // 1. Check if the device is already paired to phone if not pair
    {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {                            // this checks if the phone has the correct api to use the method below
            Log.d(TAG1, "onItemClick: Trying to pair with: " + bdDevice.getAddress());
            bdDevice.createBond();
        }
    }
    else if(bdDevice.getBondState() == BluetoothDevice.BOND_BONDED)                                 // 2. The device is paired connect
    {
        Toast.makeText(getApplicationContext(),"Attempting to connect with: " + bdDevice.getName(),Toast.LENGTH_SHORT).show();
        try {
            MyBluetoothService.connectBluetoothDevice(bdDevice.getAddress());                       // connect to device using overloaded connect function
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}

}