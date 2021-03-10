package com.example.coen390_groupproject_bearcare.Bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import java.io.IOException;
import java.util.UUID;

public class AcceptThread extends Thread {
    private final BluetoothServerSocket mmServerSocket;
    private final BluetoothAdapter bluetoothAdapter;
    private final String TAG;
    private final BluetoothDevice bluetoothDevice;

    public AcceptThread(BluetoothAdapter adapter, String macAddress) {
        // Use a temporary object that is later assigned to mmServerSocket
        // because mmServerSocket is final.
        BluetoothServerSocket tmp = null;

        this.bluetoothAdapter = adapter;

        bluetoothDevice = bluetoothAdapter.getRemoteDevice(macAddress);
        UUID myUUID = bluetoothDevice.getUuids()[0].getUuid();
        TAG = "AcceptThread";

        Log.i(TAG, myUUID.toString());

        try {
            // MY_UUID is the app's UUID string, also used by the client code.
            // TODO replace the "bearcare" string with reference to our stored string
            tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("BearCare", myUUID);
        } catch (IOException e) {
            Log.e(TAG, "Socket's listen() method failed", e);
        }
        mmServerSocket = tmp;
    }

    public void run() {
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned.
        while (true) {
            try {
                socket = mmServerSocket.accept(5000); // around 5 second timeout
            } catch (IOException e) {
                Log.e(TAG, "Socket's accept() method failed", e);
                break;
            }

            if (socket != null) {
                // A connection was accepted. Perform work associated with
                // the connection in a separate thread.
                Log.i(TAG, "BT connection was accepted");
                // TODO uncomment the below line or do something useful with socket
                //manageMyConnectedSocket(socket);

                try {
                    mmServerSocket.close();
                } catch (IOException e) {
                    Log.e(TAG, "Socket's close() method failed", e);
                }

                break;
            }


        }
    }

    // Closes the connect socket and causes the thread to finish.
    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the connect socket", e);
        }
    }
}
