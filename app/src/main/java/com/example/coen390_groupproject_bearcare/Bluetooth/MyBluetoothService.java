package com.example.coen390_groupproject_bearcare.Bluetooth;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.Toast;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class MyBluetoothService {
    private final String TAG = "MyBluetoothService";
    private static BluetoothSocket btSocket;

    @SuppressLint("HardwareIds")
    public void connectBluetoothDevice() throws IOException
    {

        BluetoothAdapter myBluetooth;
        String address;
        String name = "a";
        UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        address = myBluetooth.getAddress();
        Set<BluetoothDevice> pairedDevices = myBluetooth.getBondedDevices();
        if (pairedDevices.size()>0)
        {
            for(BluetoothDevice bt : pairedDevices)
            {
                address=bt.getAddress();
                name = bt.getName();
                //Toast.makeText(getApplicationContext(),"Connected", Toast.LENGTH_SHORT).show();
            }
        }

        myBluetooth = BluetoothAdapter.getDefaultAdapter(); //get the mobile bluetooth device
        BluetoothDevice device = myBluetooth.getRemoteDevice(address); //connects to the device's address and checks if it's available
        btSocket = device.createInsecureRfcommSocketToServiceRecord(myUUID); //create a RFCOMM (SPP) connection

        btSocket.connect();
        Log.i(TAG, "Connected! BT Name: " + name + "\nBT Address: " + address + "\nSocket: " + btSocket.toString());
        //btSocket.getOutputStream().write('r');
    }

    public static void close() {
        try {
            btSocket.close();
        } catch (IOException e) {
            Log.e("Error in close(): ", e.getMessage());
        }
    }

    public double getReading() {
        double temperatureReading = 0.0;

        try {
            if (btSocket!=null)
            {
                btSocket.getOutputStream().write('r');

                String inputString = "";

                // read in 5 bytes, which is the length of our temperature string from the microcontroller
                for (int i = 0; i < 5; i++) {
                    int inputCharacter = (btSocket.getInputStream().read());
                    Log.i(TAG, Integer.toString(inputCharacter));
                    inputString += Character.toString((char)inputCharacter);
                }

                Log.i(TAG, inputString);
                temperatureReading = Double.parseDouble(inputString);
            }
        } catch (Exception e) {
            Log.e("Error in getReading(): ", e.getMessage());
        }

        return temperatureReading;
    }


}
