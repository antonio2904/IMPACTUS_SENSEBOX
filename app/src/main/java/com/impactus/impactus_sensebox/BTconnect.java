package com.impactus.impactus_sensebox;


import android.os.Handler;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.IOException;
import java.text.DateFormat;

import java.util.Date;
import java.util.UUID;


import static com.impactus.impactus_sensebox.R.id.map;


public class BTconnect extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;


    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;
    TextView list;
    Button beginner;
    Button stopper;
    String currentDateTimeString;
    ImpactusDataBaseAdapter impactusadapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btconnect);
        list = (TextView) findViewById(R.id.lister);
        beginner = (Button) findViewById(R.id.beginbt);
        stopper = (Button) findViewById(R.id.stopbt);

        stopper.setEnabled(false);

        impactusadapter=new ImpactusDataBaseAdapter(this);
        impactusadapter=impactusadapter.open();



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

        beginner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                try {
                    beginner.setEnabled(false);
                    stopper.setEnabled(true);
                    openBT();

                } catch (IOException ex) {
                }

            }
        });
        stopper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                try{
                    stopper.setEnabled(false);
                    beginner.setEnabled(true);
                    closeBT();
                }catch (IOException ex) {
                }
            }
        });
    }

    void openBT() throws IOException
    {
        UUID uuid=UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        BLUETOOTHCLASS.getInstance().btsocket=BLUETOOTHCLASS.getInstance().btdevice.createRfcommSocketToServiceRecord(uuid);
        BLUETOOTHCLASS.getInstance().btsocket.connect();
        BLUETOOTHCLASS.getInstance().btinput=BLUETOOTHCLASS.getInstance().btsocket.getInputStream();
        if(BLUETOOTHCLASS.getInstance().btsocket.isConnected()) {
            Toast.makeText(getApplicationContext(), "SENSEBOX Linked", Toast.LENGTH_SHORT).show();
            listenfordata();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Connection Dropped",Toast.LENGTH_SHORT).show();
        }
    }
    void listenfordata() {

        final Handler handler = new Handler();
        final byte delimiter = 10;
        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        int bytesAvailable = BLUETOOTHCLASS.getInstance().btinput.available();
                        if (bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];
                            BLUETOOTHCLASS.getInstance().btinput.read(packetBytes);
                            for (int i = 0; i < bytesAvailable; i++) {
                                byte b = packetBytes[i];
                                if (b == delimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable() {
                                        public void run() {
                                            if(data.length()>34) {
                                                list.setText(data);
                                                currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                                                impactusadapter.insertEntry((String.valueOf(GPSCLASS.getInstance().latitude)).substring(0, 9),
                                                        (String.valueOf(GPSCLASS.getInstance().longitude)).substring(0, 9), currentDateTimeString,
                                                        (String.valueOf(data).substring(0, 2)), (String.valueOf(data).substring(13, 15)), (String.valueOf(data).substring(31, 35)), GPSCLASS.getInstance().location_name);
                                            }
                                        }
                                    });
                                } else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    } catch (IOException ex) {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }//
    void closeBT() throws IOException
    {
        stopWorker = true;
        BLUETOOTHCLASS.getInstance().btinput.close();
        BLUETOOTHCLASS.getInstance().btsocket.close();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng loc = new LatLng(GPSCLASS.getInstance().latitude, GPSCLASS.getInstance().longitude);
        mMap.addMarker(new MarkerOptions().position(loc).title("Current Location:"+GPSCLASS.getInstance().location_name)).showInfoWindow();
        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(loc, 15F);
        mMap.animateCamera(cu);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close The Database
        impactusadapter.close();
    }
    @Override
    protected void onStop() {
        super.onStop();
        // Close The Database
        impactusadapter.close();
    }

    @Override
    protected void onPause(){
        super.onPause();
        impactusadapter.close();
    }

    @Override
    protected void onResume(){
        super.onResume();
        impactusadapter.open();
    }

}

