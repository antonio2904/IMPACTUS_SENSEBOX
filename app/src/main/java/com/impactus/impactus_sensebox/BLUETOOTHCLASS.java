package com.impactus.impactus_sensebox;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;


import java.io.InputStream;


public class BLUETOOTHCLASS {
    BluetoothDevice btdevice;
    BluetoothAdapter btadptr;
    BluetoothSocket btsocket;
    InputStream btinput;
    private static BLUETOOTHCLASS instance=null;
    protected BLUETOOTHCLASS(){}
    public static BLUETOOTHCLASS getInstance()
    {
        if(null==instance){
            instance=new BLUETOOTHCLASS();
        }
        return instance;
    }

}
