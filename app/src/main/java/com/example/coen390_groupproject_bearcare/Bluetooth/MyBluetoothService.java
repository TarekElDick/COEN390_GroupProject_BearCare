package com.example.coen390_groupproject_bearcare.Bluetooth;

import android.bluetooth.BluetoothSocket;
import android.icu.util.Output;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class MyBluetoothService {
    private static final String TAG = "BEAR_CARE_DEBUG_TAG";
    private Handler handler;            // get information from Bluetooth (BT) service

    public interface MessageConstants{
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;

        // THESE ARE GOING TO BE USED FOR GETTING/SENDING DATA TO THE ESP32
    }

    private class ConnectedThread extends Thread{
        private final BluetoothSocket BTSocket;
        private final InputStream BTInStream;
        private final OutputStream BTOutStream;
        private byte [] BTBuffer;

        public ConnectedThread (BluetoothSocket socket){        // constructor
            BTSocket = socket;
            InputStream tmpStreamIn = null;
            OutputStream tmpStreamOut =  null;

            try{                                              // try to assign avoids crashes when unable to do so
                tmpStreamIn = socket.getInputStream();
            }catch(IOException e){
                Log.e(TAG, "Error occurred when creating input stream.");
            }
            try{
                tmpStreamOut = socket.getOutputStream();
            }catch(IOException e){
                Log.e(TAG,"Error occurred when creating output stream.");
            }
            BTInStream = tmpStreamIn;                       // if successful initialize two objects
            BTOutStream = tmpStreamOut;

        }

        public void run(){
            BTBuffer =  new byte[1024];             // set the buffer to a KiloByte wide
            int returnedBytesSize;                  // number of bytes returned from the read function

            while(true){
                try{
                    returnedBytesSize = BTInStream.read(BTBuffer);      // read the buffer
                    Message readMessage = handler.obtainMessage(MessageConstants.MESSAGE_READ, returnedBytesSize, -1,BTBuffer);
                    readMessage.sendToTarget();
                }catch(IOException e){
                    Log.d(TAG, "Input stream was disconnected.",e);
                    break;
                }
            }

        }

        // use this in temp activity to trigger the esp32

        public void write(byte [] bytes){
            try {

                BTOutStream.write(bytes);
                Message writtenMessage = handler.obtainMessage(MessageConstants.MESSAGE_WRITE,-1,-1,bytes);
                writtenMessage.sendToTarget();
            }catch(IOException e){
                // send failure message to activity

                Log.e(TAG,"Error occurred when sending data.",e);
                Message writeErrorMsg = handler.obtainMessage(MessageConstants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast", "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                handler.sendMessage(writeErrorMsg);

            }
        }

        public void cancel(){
            try {
                BTSocket.close();
            }catch(IOException e){
                Log.e(TAG,"Could not close connect socket.",e);
            }
        }

    }

}
