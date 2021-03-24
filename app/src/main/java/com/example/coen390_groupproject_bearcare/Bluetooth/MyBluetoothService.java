package com.example.coen390_groupproject_bearcare.Bluetooth;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class MyBluetoothService {
    private static final String TAG = "MyBluetoothService";
    private static BluetoothSocket btSocket;
    //private static BluetoothAdapter BtAdapter;      // bluetooth adapter object
    private static String macAddress; // mac address is stored across all activities

    // connect to the mac address stored as a static variable in this class
    @SuppressLint("HardwareIds")
    public static void connectBluetoothDevice() throws IOException
    {
        if (btSocket!=null)
            close();

        BluetoothAdapter myBluetooth = BluetoothAdapter.getDefaultAdapter(); // get the mobile bluetooth device
        String name = "a";
        UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        BluetoothDevice device = myBluetooth.getRemoteDevice(macAddress); //connects to the device's address and checks if it's available
        btSocket = device.createInsecureRfcommSocketToServiceRecord(myUUID); //create a RFCOMM (SPP) connection

        btSocket.connect();
        Log.i(TAG, "Connected! BT Name: " + name + "\nBT Address: " + macAddress + "\nSocket: " + btSocket.toString());
    }

    public static void close() {
        try {
            if (btSocket != null)
                btSocket.close();
            Log.i("MyBluetoothService", "Disconnected");
        } catch (IOException e) {
            Log.e("close() error: ", "Could not close the connect socket", e);
        }
    }

    public static double getReading() {
        double temperatureReading = 0.0;

        try {
            if (btSocket!=null)
            {
                btSocket.getOutputStream().write('r');

                String inputString = "";

                // read in 7 bytes, which is always the length of our temperature string from the microcontroller
                for (int i = 0; i < 7; i++) {
                    int inputCharacter = (btSocket.getInputStream().read());
                    Log.i(TAG, Integer.toString(inputCharacter));
                    inputString += Character.toString((char)inputCharacter);
                }

                //.btSocket.getInputStream().;

                Log.i(TAG, inputString);
                temperatureReading = Double.parseDouble(inputString);
            }
        } catch (Exception e) {
            Log.e("Error in getReading(): ", e.getMessage());
        }

        return temperatureReading;
    }

    public static Set<BluetoothDevice> getAllPairedDevices(){       // get the paired devices
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); // get the mobile bluetooth device

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        return pairedDevices;
    }

    // automatically connect to the first BearCare device we see
    public static void connectAutomatically() throws IOException {
        //macAddress = "00:00:00:00:00:00";
        Set<BluetoothDevice> pairedDevices = getAllPairedDevices();

        for(BluetoothDevice devices : pairedDevices) {
            if (devices.getName().equals("BearCare Temperature Sensor")) {
                macAddress = devices.getAddress();
                break;
            }
        }

        // matt macAddress = "24:6F:28:1A:D3:26";
        // Ryan's ESP32 MAC ADDRESS AC:67:B2:36:13:BA

        // don't try to connect if we did not find a bearCare sensor
        // Trying to connect takes a long time for nothing
        if (macAddress != null)
            connectBluetoothDevice();

    }
}
