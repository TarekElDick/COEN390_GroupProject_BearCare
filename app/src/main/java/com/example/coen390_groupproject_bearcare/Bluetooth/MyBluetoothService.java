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
    private static final String TAG = "MyBluetoothService";
    private static BluetoothSocket btSocket;
    private static BluetoothAdapter BtAdapter;      // bluetooth adapter object
    public static String macAddress; // todo make this private

    @SuppressLint("HardwareIds")
    public static void connectBluetoothDevice() throws IOException
    {
        if (btSocket!=null)
            close();

        BluetoothAdapter myBluetooth;
        String name = "a";
        UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        myBluetooth = BluetoothAdapter.getDefaultAdapter(); //get the mobile bluetooth device
        BluetoothDevice device = myBluetooth.getRemoteDevice(macAddress); //connects to the device's address and checks if it's available
        btSocket = device.createInsecureRfcommSocketToServiceRecord(myUUID); //create a RFCOMM (SPP) connection

        btSocket.connect();
        Log.i(TAG, "Connected! BT Name: " + name + "\nBT Address: " + macAddress + "\nSocket: " + btSocket.toString());
    }

    public static void close() {
        try {
            btSocket.close();
            Log.i("MyBluetoothService", "Disconnected");
        } catch (IOException e) {
            Log.e("close() error: ", "Could not close the connect socket", e);
        }
    }

    public double getReading() {
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


}
