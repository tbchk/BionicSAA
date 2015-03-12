package com.example.camaleon.bionicsaa.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

import com.example.camaleon.bionicsaa.bluetooth.bluetoothCManager;

/**
 * Created by hades on 1/12/14.
 */

public class bluetoothConnectionAttempT extends Thread {


    public bluetoothConnectionAttempT(bluetoothCManager btm) {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;
        btMAN = btm;

        mmDevice = btMAN.getMmDevice();
        mBluetoothAdapter = btMAN.getmBluetoothAdapter();

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            tmp= mmDevice.createRfcommSocketToServiceRecord(mmDevice.getUuids()[0].getUuid());
        }
        catch (NullPointerException e) {
            //e.printStackTrace();
            try {
                tmp = mmDevice.createRfcommSocketToServiceRecord(DEFAULT_UUID);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        catch (IOException e){}


        mmSocket = tmp;
    }

    public void run() {

        int cStatus = btMAN.CONNECTING;
        // Cancel discovery because it will slow down the connection
        mBluetoothAdapter.cancelDiscovery();

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            cStatus = btMAN.CONNECTION_ERROR;
            try {
                mmSocket.close();
            } catch (IOException closeException) { }
            return;
        }

        if(mmSocket.isConnected())cStatus = btMAN.CONNECTED;
        // Do work to manage the connection (in a separate thread)
        btMAN.getConnected(mmSocket,cStatus);
    }

    /** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            mmSocket.close();
            btMAN.getConnected(mmSocket, btMAN.DISCONNECTED);
        } catch (IOException e) { }
    }

    //Serial Port DEFAULT UUID
    private UUID DEFAULT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private final BluetoothAdapter mBluetoothAdapter;
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;

    private bluetoothCManager btMAN;
}