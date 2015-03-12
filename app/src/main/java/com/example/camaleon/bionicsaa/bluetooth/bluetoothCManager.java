package com.example.camaleon.bionicsaa.bluetooth;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;

import java.util.Set;


/**
 * Created by hades on 1/12/14.
 */
public class bluetoothCManager {

    //Un cambio
    // Puedo conectar solo a dispositivos emparejados, emparejo por MAC o por nombre,
    // De preferencia usar MAC pues es un identificador único
    public bluetoothCManager(String remoteID, int Type)
    {

        pairedBy = Type;
        if (pairedBy == pairedByName){
            mmName = remoteID;
            mmMAC = "";
        }else if(pairedBy == pairedByMAC){
            mmName = "";
            mmMAC = remoteID;
        }

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            btExistStatus = BT_NOT_EXIST;
            btOnStatus = BT_TURNED_OFF;
            pairedStatus = DEVICE_UNPAIRED;
        }else {
            btExistStatus = BT_EXIST;

            if(mBluetoothAdapter.isEnabled()){
                btOnStatus = BT_TURNED_ON;
                mmDevice = getPaired(remoteID,  Type);
                if(mmDevice!=null) pairedStatus = DEVICE_PAIRED;
                else pairedStatus = DEVICE_UNPAIRED;
            }
            else {
                btOnStatus = BT_TURNED_OFF;
                pairedStatus = DEVICE_UNPAIRED;
            }

        }
        mmSocket = null;
        connectionStatus = DISCONNECTED;

    }


    public int connect(){
        if (getPairedStatus() == DEVICE_UNPAIRED)return DEVICE_UNPAIRED;
        if(connectionStatus == CONNECTED) return CONNECTED;
        if(connectionStatus == CONNECTING) return CONNECTING;

        //connection Thread
        tCon = new bluetoothConnectionAttempT(this);
        tCon.start();

        connectionStatus = CONNECTING;
        //tCon.run();

        return CONNECTING;
    }

    public void disconnect(){
        tCon.cancel();
    }

    //Manejo de datos
    public void send(byte[] bytes){
        communicationT.write(bytes);
    }

    public void dataReceived(int status,int len, byte[] buffer){

    }


    public void getConnected(BluetoothSocket S, int conStatus){
        connectionStatus = conStatus;
        mmSocket = S;

        communicationT = new bluetoothConnectedT(mmSocket,this);
        communicationT.start();

        Log.e("Comunicacion","Conecto");
    }

    public int getConnectionStatus(){


        if (mmSocket == null) {
            connectionStatus = DISCONNECTED;
            return DISCONNECTED;
        }

        if (mmSocket.isConnected())connectionStatus = CONNECTED;

        return connectionStatus;
    }


    private BluetoothDevice getPaired(String remoteID, int Type){
        if (pairedBy == pairedByMAC) return getPairedDeviceByMAC();
        if (pairedBy == pairedByName) return getPairedDeviceByName();

        return null;
    }

    public int getPairedStatus(){


        if(pairedStatus == DEVICE_PAIRED) return DEVICE_PAIRED;
        if(btOnStatus == BT_TURNED_OFF) return DEVICE_UNPAIRED;

        if(pairedBy == pairedByMAC)
        mmDevice = getPaired(mmMAC,  pairedBy);
        else if (pairedBy == pairedByName)
            mmDevice = getPaired(mmName,pairedBy);


        if(mmDevice!=null) pairedStatus = DEVICE_PAIRED;
        else pairedStatus = DEVICE_UNPAIRED;

        return pairedStatus;
    }

    //Get Declarations
    private BluetoothDevice getPairedDeviceByMAC()
    {
        BluetoothDevice tmp = null;
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView

                if(device.getAddress().equals(mmMAC)){
                    mmName = device.getName();
                    Log.e("Name",mmName);
                    Log.e("MAC",device.getAddress());
                    pairedStatus = DEVICE_PAIRED;
                    return device;
                }

            }
        }

        pairedStatus = DEVICE_UNPAIRED;
        return null;
    }




    private BluetoothDevice getPairedDeviceByName()
    {
        BluetoothDevice tmp = null;
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                if(device.getName().equals(mmName)){
                    mmMAC = device.getAddress();
                    Log.e("Name",device.getName());
                    Log.e("MAC",mmMAC);

                    pairedStatus = DEVICE_PAIRED;
                    return device;
                }

            }
        }

        pairedStatus = DEVICE_UNPAIRED;
        return null;
    }

    public BluetoothDevice getMmDevice(){
        return mmDevice;
    }

    public BluetoothAdapter getmBluetoothAdapter(){
        return mBluetoothAdapter;
    }


    //SET DECLARATIONS
    public int setBluetoothOn(Activity A)
    {
        if(!isBluetoothHw())return BT_NOT_EXIST;
        if (isBluetoothOn()) return BT_TURNED_ON;

        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        A.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);


        // FALTA AGREGAR MAS CONTROL SOBRE LA RESPUESTA y CAMBIOS MANUALES AL ESTADO DEL BT
        // Por ahora tomo por defecto que encendio el BT
        return BT_TURNED_ON;
    }


    // IS_DECLARATIONS
    public boolean isBluetoothHw()
    {
        if(btExistStatus == BT_EXIST)return true;
        else return false;
    }

    public boolean isBluetoothOn()
    {
        if (!isBluetoothHw()) return false;

        if(mBluetoothAdapter.isEnabled()){
            btOnStatus = BT_TURNED_ON;
            return true;
        }
        else{
            btOnStatus = BT_TURNED_OFF;
            return false;
        }
    }

    //Cliente BT
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mmSocket;

    //Hilos
    bluetoothConnectionAttempT tCon;
    bluetoothConnectedT communicationT;


    //Servidor BT
    private String mmMAC;
    private String mmName;
    private BluetoothDevice mmDevice;

    //Control variables
    private int btExistStatus;
    private int btOnStatus;
    private int pairedStatus;
    private int connectionStatus;

    private int REQUEST_ENABLE_BT=0;


    private int pairedBy;
    public static int pairedByName= 0;
    public static int pairedByMAC= 1;


    //State Declarations
    //Hardware Bluetooth status
    public static int BT_NOT_EXIST = 0;
    public static int BT_EXIST = 1;


    //On/Off bt status
    public static int BT_TURNED_OFF = 2;
    public static int BT_TURNED_ON = 3;


    //Paired/Unpaired Status
    public static int DEVICE_UNPAIRED = 4;
    public static int DEVICE_PAIRED = 5;

    //Connection Status
    public static int DISCONNECTED = 6;
    public static int CONNECTING = 7;
    public static int CONNECTED = 8;
    public static int CONNECTION_ERROR = 9;

    //Transmission Status
    public static int MESSAGE_READ = 10;

}
